/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import kawaii.event.CancellableEvent;
import kawaii.event.PacketEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class Velocity
extends Module {
    public ModeSetting modeSetting = new ModeSetting("Mode", "Edit", "Cancel");
    public NumberSetting verticalSetting;
    public NumberSetting xSetting = new NumberSetting("X", 50.0, 1.0, 0.0, 200.0, "%");

    public Velocity() {
        super("Velocity", "Allows you to edit velocity", Category.combat);
        this.verticalSetting = new NumberSetting("Y", 50.0, 1.0, 0.0, 200.0, "%");
        this.addSettings(this.modeSetting, this.xSetting, this.verticalSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        this.setSuffix((int)this.xSetting.getValue() + "% | " + (int)this.verticalSetting.getValue() + "%");
        if (Velocity.mc.thePlayer == null) {
            return;
        }
        if (cancellableEvent instanceof PacketEvent) {
            PacketEvent packetEvent = (PacketEvent)cancellableEvent;
            if (packetEvent.packet instanceof S12PacketEntityVelocity) {
                S12PacketEntityVelocity s12PacketEntityVelocity = (S12PacketEntityVelocity)packetEvent.packet;
                if (s12PacketEntityVelocity.entityID == Velocity.mc.thePlayer.getEntityId()) {
                    switch (this.modeSetting.getMode()) {
                        case "Edit": {
                            s12PacketEntityVelocity.pxp = (int)((double)(s12PacketEntityVelocity.pxp / 100) * this.xSetting.getValue());
                            s12PacketEntityVelocity.motionY = (int)((double)(s12PacketEntityVelocity.motionY / 100) * this.verticalSetting.getValue());
                            s12PacketEntityVelocity.pxg = (int)((double)(s12PacketEntityVelocity.pxg / 100) * this.xSetting.getValue());
                            break;
                        }
                        case "Cancel": {
                            packetEvent.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.xSetting.setVisible(!this.modeSetting.getMode().equalsIgnoreCase("Edit"));
        this.verticalSetting.setVisible(!this.modeSetting.getMode().equalsIgnoreCase("Edit"));
    }
}

