/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.player;

import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.event.CancellableEvent;
import kawaii.event.MotionEvent;
import kawaii.event.PacketEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.network.PacketDirection;
import kawaii.setting.BooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.util.ConcurrentArrayList;
import kawaii.util.Timer;
import net.minecraft.network.Packet;

public class Blink
extends Module {
    public BooleanSetting pulseSetting;
    public NumberSetting delaySetting;
    public BooleanSetting serverPacketsSetting;
    public CopyOnWriteArrayList<Packet> delayedServerPackets;
    public CopyOnWriteArrayList<Packet> delayedClientPackets;
    public Timer pulseTimer = new Timer();

    @Override
    public void onDisable() {
        for (Packet packet : this.delayedClientPackets) {
            Blink.mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
        for (Packet packet : this.delayedServerPackets) {
            packet.processPacket(mc.getNetHandler());
        }
        this.delayedServerPackets.clear();
        this.delayedClientPackets.clear();
    }

    public Blink() {
        super("Blink", "Delays all packets", Category.exploit);
        this.pulseSetting = new BooleanSetting("Pulse", false);
        this.serverPacketsSetting = new BooleanSetting("Server Packets", false);
        this.delaySetting = new NumberSetting("Delay", 1000.0, 1.0, 100.0, 2500.0, "ms");
        this.delayedClientPackets = new ConcurrentArrayList();
        this.delayedServerPackets = new ConcurrentArrayList();
        this.addSettings(this.serverPacketsSetting, this.pulseSetting, this.delaySetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (Blink.mc.thePlayer == null || Blink.mc.thePlayer.ticksExisted < 40) {
            return;
        }
        if (cancellableEvent instanceof PacketEvent) {
            PacketEvent packetEvent = (PacketEvent)cancellableEvent;
            if (packetEvent.getType() == PacketDirection.Oxc) {
                packetEvent.setCancelled(true);
                this.delayedClientPackets.add(packetEvent.packet);
            } else {
                if (!this.serverPacketsSetting.getValue().booleanValue()) {
                    return;
                }
                packetEvent.setCancelled(true);
                this.delayedServerPackets.add(packetEvent.packet);
            }
        } else if (cancellableEvent instanceof MotionEvent && this.pulseSetting.getValue().booleanValue() && this.pulseTimer.hasTimePassed(this.delaySetting.getValue())) {
            if (!this.delayedServerPackets.isEmpty()) {
                for (Packet packet : this.delayedServerPackets) {
                    packet.processPacket(mc.getNetHandler());
                }
            }
            if (!this.delayedClientPackets.isEmpty()) {
                for (Packet packet : this.delayedClientPackets) {
                    Blink.mc.thePlayer.sendQueue.addToSendQueue(packet);
                }
            }
            this.delayedClientPackets.clear();
            this.delayedServerPackets.clear();
            this.pulseTimer.reset();
        }
    }

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.delaySetting.setVisible(this.pulseSetting.getValue() == false);
    }
}

