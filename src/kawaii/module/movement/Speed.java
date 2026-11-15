/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import kawaii.Kawaii;
import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.movement.Flight;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.Notification;
import kawaii.util.MovementUtil;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class Speed
extends Module {
    public ModeSetting modeSetting = new ModeSetting("Mode", "Vanilla", "VanillaLow", "Hypixel", "Verus");
    public NumberSetting speedSetting = new NumberSetting("Speed", 0.5, 0.01, 0.2, 1.0);

    public Speed() {
        super("Speed", "Player go faster", Category.movement);
        this.addSettings(this.modeSetting, this.speedSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        this.setSuffix(this.modeSetting.getMode());
        if (cancellableEvent instanceof ImageInitEvent) {
            switch (this.modeSetting.getMode()) {
                case "Vanilla": {
                    Speed.mc.thePlayer.setForwardSpeed(0.0f);
                    if (!MovementUtil.isMoving()) break;
                    if (Speed.mc.thePlayer.onGround) {
                        Speed.mc.thePlayer.jump();
                    }
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    break;
                }
                case "Hypixel": {
                    Speed.mc.thePlayer.setForwardSpeed(0.0f);
                    if (!MovementUtil.isMoving()) break;
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    if (Speed.mc.thePlayer.motionY > 0.0) {
                        Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue() + 0.02);
                    }
                    if (!Speed.mc.thePlayer.onGround) break;
                    Speed.mc.thePlayer.jumpWithHeight(0.36);
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue() + 0.01);
                    break;
                }
                case "VanillaLow": {
                    Speed.mc.thePlayer.setForwardSpeed(0.0f);
                    if (!MovementUtil.isMoving()) break;
                    if (Speed.mc.thePlayer.onGround) {
                        Speed.mc.thePlayer.jumpWithHeight(0.3);
                    }
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    break;
                }
                case "Verus": {
                    Speed.mc.thePlayer.setForwardSpeed(0.0f);
                    if (!MovementUtil.isMoving()) break;
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue());
                    if (!Speed.mc.thePlayer.onGround) break;
                    Speed.mc.thePlayer.setForwardSpeedDouble(this.speedSetting.getValue() + 0.02);
                    Speed.mc.thePlayer.jumpWithHeight(0.42);
                }
            }
        }
    }

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
    }

    @Override
    public void onEnable() {
        if (Kawaii.INSTANCE.getModuleManager().getModule(Flight.class).isEnabled()) {
            Module flightModule = Kawaii.INSTANCE.getModuleManager().getModule(Flight.class);
            flightModule.setEnabled(false);
            Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Disabled Flight", "Disabled Flight to prevent flags", 1500.0, C19PacketResourcePackStatus.Action.INFORMATION));
        }
        super.onEnable();
    }
}

