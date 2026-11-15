/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import java.util.List;
import kawaii.Kawaii;
import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.event.PacketEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.movement.Speed;
import kawaii.network.PacketDirection;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.Notification;
import kawaii.util.MovementUtil;
import kawaii.util.ObjectList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.util.Vec3;

public class Flight
extends Module {
    public Vec3 enablePosition;
    public Vec3 lastPosition;
    public ModeSetting modeSetting = new ModeSetting("Mode", "Vanilla", "Vanilla Damage", "Smooth", "MMC", "Verus");
    public List<Packet> delayedPackets;
    public NumberSetting speedSetting = new NumberSetting("Speed", 0.5, 0.01, 0.2, 10.0);

    public Flight() {
        super("Flight", "Allows you to fly!", Category.movement);
        this.delayedPackets = new ObjectList();
        this.addSettings(this.modeSetting, this.speedSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent) {
            this.setSuffix(this.modeSetting.getMode());
            Flight.mc.thePlayer.capabilities.allowFlying = false;
            switch (this.modeSetting.getMode()) {
                case "Vanilla Damage": 
                case "Vanilla": {
                    Flight.mc.thePlayer.setForwardSpeed(0.0f);
                    if (MovementUtil.isMoving()) {
                        Flight.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    }
                    double verticalSpeed = 0.0;
                    if (Flight.mc.gameSettings.keyBindJump.pressed) {
                        verticalSpeed += 0.4;
                    }
                    if (Flight.mc.gameSettings.keyBindSneak.pressed) {
                        verticalSpeed -= 0.4;
                    }
                    Flight.mc.thePlayer.motionY = verticalSpeed;
                    break;
                }
                case "MMC": {
                    if (MovementUtil.isMoving()) {
                        // empty if block
                    }
                    Flight.mc.thePlayer.motionY = -0.11;
                    break;
                }
                case "Smooth": {
                    double verticalSpeed = Flight.mc.thePlayer.motionY / 1.2 + 0.065;
                    if (MovementUtil.isMoving()) {
                        Flight.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    }
                    if (Flight.mc.gameSettings.keyBindJump.pressed) {
                        verticalSpeed += 0.2;
                    }
                    if (Flight.mc.gameSettings.keyBindSneak.pressed) {
                        verticalSpeed -= 0.2;
                    }
                    Flight.mc.thePlayer.motionY = verticalSpeed;
                    break;
                }
                case "Verus": {
                    Flight.mc.thePlayer.motionY = -0.14;
                    Flight.mc.thePlayer.setForwardSpeed(0.28f);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        switch (this.modeSetting.getMode()) {
            case "Smooth": {
                break;
            }
            case "MMC": {
                for (Packet packet : this.delayedPackets) {
                    Flight.mc.thePlayer.sendQueue.addToSendQueue(packet);
                }
                this.delayedPackets.clear();
                Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY, Flight.mc.thePlayer.posZ, 0.0f, 0.0f, false));
                Flight.mc.thePlayer.setMotionInts(0, 0, 0);
                break;
            }
            default: {
                Flight.mc.thePlayer.setMotionInts(0, 0, 0);
            }
        }
    }

    @Override
    public void onEnable() {
        if (Kawaii.INSTANCE.getModuleManager().getModule(Speed.class).isEnabled()) {
            Module module = Kawaii.INSTANCE.getModuleManager().getModule(Speed.class);
            module.setEnabled(false);
            Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Disabled Speed", "Disabled Speed to prevent flags", 1500.0, C19PacketResourcePackStatus.Action.INFORMATION));
        }
        boolean isVanillaDamage = this.modeSetting.getMode().equalsIgnoreCase("Vanilla Damage");
        double startX = Flight.mc.thePlayer.posX;
        double startY = Flight.mc.thePlayer.posY;
        double startZ = Flight.mc.thePlayer.posZ;
        if (isVanillaDamage) {
            double packetSteps = 50.0;
            int i = 0;
            while ((double)i < packetSteps) {
                Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(startX, startY + 3.0 / packetSteps + 0.01, startZ, false));
                Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(startX, startY, startZ, false));
                ++i;
            }
            Flight.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(startX, startY, startZ, true));
        }
        if (this.modeSetting.getMode().equalsIgnoreCase("MMC")) {
            Flight.mc.thePlayer.setPosition(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY + 0.25, Flight.mc.thePlayer.posZ);
        }
        this.lastPosition = new Vec3(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY, Flight.mc.thePlayer.posZ);
        this.enablePosition = new Vec3(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY, Flight.mc.thePlayer.posZ);
    }

    @Override
    public void onEvent(PacketEvent packetEvent) {
        if (Flight.mc.thePlayer == null) {
            return;
        }
        if (packetEvent.getType() == PacketDirection.Oxc && this.modeSetting.getMode().equalsIgnoreCase("MMC")) {
            if (packetEvent.packet instanceof C03PacketPlayer) {
                packetEvent.setCancelled(true);
                if (Flight.mc.thePlayer.getDistance(this.lastPosition.xCoord, this.lastPosition.rea, this.lastPosition.zCoord) > 7.0) {
                    this.lastPosition = new Vec3(Flight.mc.thePlayer.posX, Flight.mc.thePlayer.posY, Flight.mc.thePlayer.posZ);
                    this.delayedPackets.add(packetEvent.packet);
                }
            } else if (packetEvent.packet instanceof C00PacketKeepAlive || packetEvent.packet instanceof C0FPacketConfirmTransaction) {
                packetEvent.setCancelled(true);
                this.delayedPackets.add(packetEvent.packet);
            }
        }
        super.onEvent(packetEvent);
    }
}

