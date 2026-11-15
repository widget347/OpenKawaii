/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.misc;

import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.event.ImageInitEvent;
import kawaii.event.PacketEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import kawaii.util.ConcurrentArrayList;
import kawaii.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;

public class Disabler
extends Module {
    public CopyOnWriteArrayList<Packet> queuedPackets;
    public ModeSetting modeSetting;
    public Timer timer = new Timer();

    @Override
    public void onEvent(PacketEvent packetEvent) {
        if (Disabler.mc.thePlayer == null) {
            return;
        }
        this.setSuffix("Watchdog");
        if (packetEvent.packet instanceof C03PacketPlayer && Disabler.mc.thePlayer.ticksExisted < 70 || packetEvent.packet instanceof C0BPacketEntityAction) {
            packetEvent.setCancelled(true);
        }
        if (mc.getNetHandler() != null && Disabler.mc.getNetHandler().doneLoadingTerrain && !packetEvent.isCancelled() && (packetEvent.packet instanceof C03PacketPlayer || packetEvent.packet instanceof C0FPacketConfirmTransaction || packetEvent.packet instanceof C00PacketKeepAlive)) {
            packetEvent.setCancelled(true);
            this.queuedPackets.add(packetEvent.packet);
        }
        super.onEvent(packetEvent);
    }

    @Override
    public void onDisable() {
        this.queuedPackets.clear();
    }

    @Override
    public void onEvent(ImageInitEvent imageInitEvent) {
        if (this.timer.hasTimePassed(120L)) {
            for (Packet packet : this.queuedPackets) {
                Disabler.mc.thePlayer.sendQueue.addToSendQueue(packet);
            }
            this.queuedPackets.clear();
            this.timer.reset();
        }
        if (Disabler.mc.thePlayer != null && Disabler.mc.thePlayer.ticksExisted == 7) {
            Disabler.mc.thePlayer.sendQueue.addToSendQueue(new C0BPacketEntityAction(Disabler.mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING));
        }
        super.onEvent(imageInitEvent);
    }

    public Disabler() {
        super("Disabler", "Disabler Serber da pai", Category.exploit);
        this.queuedPackets = new ConcurrentArrayList();
        this.modeSetting = new ModeSetting("Mode", "Watchdog");
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }
}

