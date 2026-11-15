/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.world;

import kawaii.event.ImageInitEvent;
import kawaii.event.PacketEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import net.minecraft.network.play.server.S03PacketTimeUpdate;

public class CustomTime
extends Module {
    public ModeSetting timeModeSetting = new ModeSetting("Time", 2, "Day", "Dusk", "Night", "Dawn");

    public CustomTime() {
        super("Atmosphere", "Changes the look of the world", Category.visuals);
        this.addSettings(this.timeModeSetting);
    }

    @Override
    public void onEvent(PacketEvent packetEvent) {
        if (packetEvent.packet instanceof S03PacketTimeUpdate) {
            packetEvent.setCancelled(true);
        }
        super.onEvent(packetEvent);
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onEvent(ImageInitEvent imageInitEvent) {
        switch (this.timeModeSetting.getMode()) {
            case "Day": {
                CustomTime.mc.theWorld.setWorldTime(6000L);
                break;
            }
            case "Dusk": {
                CustomTime.mc.theWorld.setWorldTime(12500L);
                break;
            }
            case "Night": {
                CustomTime.mc.theWorld.setWorldTime(18000L);
                break;
            }
            case "Dawn": {
                CustomTime.mc.theWorld.setWorldTime(23500L);
            }
        }
        super.onEvent(imageInitEvent);
    }
}

