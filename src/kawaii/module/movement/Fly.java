/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import java.util.ArrayList;
import kawaii.event.PacketEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.network.PacketDirection;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.util.ObjectList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;

public class Fly
extends Module {
    public ArrayList<Packet> packetQueue;
    public BooleanSetting damageSetting = new BooleanSetting("Damage", true);
    public ModeSetting mode = new ModeSetting("Mode", "AirBoost", "Freecam");

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.damageSetting.setVisible(this.mode.getMode().equalsIgnoreCase("Freecam"));
        super.onEvent(settingsUpdateEvent);
    }

    public Fly() {
        super("Teleport", "Allows the player to teleport", Category.exploit);
        this.packetQueue = new ObjectList();
        this.addSettings(this.mode, this.damageSetting);
    }

    @Override
    public void onEvent(PacketEvent packetEvent) {
        if (this.mode.getMode().equalsIgnoreCase("Freecam")) {
            if (packetEvent.getType().equals((Object)PacketDirection.Oxc)) {
                packetEvent.setCancelled(true);
                this.packetQueue.add(packetEvent.packet);
            }
        } else {
            Fly.mc.thePlayer.setPosition(Fly.mc.thePlayer.posX, Fly.mc.thePlayer.posY + 0.5, Fly.mc.thePlayer.posZ);
            Fly.mc.thePlayer.setForwardSpeed(-2.0f);
        }
    }

    @Override
    public void onDisable() {
        boolean damageEnabled = this.damageSetting.getValue();
        double lastX = Fly.mc.thePlayer.posX;
        double lastY = Fly.mc.thePlayer.posY;
        double lastZ = Fly.mc.thePlayer.posZ;
        if (damageEnabled) {
            double stepCount = 23.0;
            int i = 0;
            while ((double)i < stepCount) {
                Fly.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY + 3.0 / stepCount + 0.01, lastZ, false));
                Fly.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY, lastZ, false));
                ++i;
            }
            Fly.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(lastX, lastY, lastZ, true));
        }
        for (Packet packet : this.packetQueue) {
            Fly.mc.thePlayer.sendQueue.addToSendQueue(packet);
        }
        this.packetQueue.clear();
        super.onDisable();
    }
}

