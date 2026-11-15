/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.movement;

import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.util.BlockPos;

public class AntiVoid
extends Module {
    public NumberSetting distanceSetting;
    public NumberSetting motionSetting;
    public int jumpStartY = -1;
    public ModeSetting modeSetting = new ModeSetting("Mode", "Float", "Jump");

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.motionSetting.setVisible(!this.modeSetting.getMode().equalsIgnoreCase("Jump"));
    }

    public AntiVoid() {
        super("AntiVoid", "Forces server to lag you back while in void", Category.player);
        this.distanceSetting = new NumberSetting("Distance", 3.0, 1.0, 0.0, 7.0);
        this.motionSetting = new NumberSetting("Motion", 0.5, 0.01, 0.0, 1.0);
        this.addSettings(this.modeSetting, this.distanceSetting, this.motionSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent) {
            boolean isVoid = true;
            for (int i = (int)Math.ceil(AntiVoid.mc.thePlayer.posY); i > -1; --i) {
                Block block = AntiVoid.mc.theWorld.getBlockState(new BlockPos(AntiVoid.mc.thePlayer.posX, (double)i, AntiVoid.mc.thePlayer.posZ)).getBlock();
                if (!(block instanceof BlockAir)) {
                    isVoid = false;
                }
            }
            switch (this.modeSetting.getMode()) {
                case "Float": {
                    if (!((double)AntiVoid.mc.thePlayer.fallDistance > this.distanceSetting.getValue()) || AntiVoid.mc.thePlayer.capabilities.allowFlying || !isVoid) break;
                    AntiVoid.mc.thePlayer.motionY = 0.0;
                    AntiVoid.mc.thePlayer.motionX = 0.0;
                    AntiVoid.mc.thePlayer.motionZ = 0.0;
                    AntiVoid.mc.thePlayer.setPosition(AntiVoid.mc.thePlayer.posX, AntiVoid.mc.thePlayer.posY, AntiVoid.mc.thePlayer.posZ);
                    break;
                }
                case "Jump": {
                    if (!isVoid) {
                        this.jumpStartY = -1;
                    }
                    if (!(this.jumpStartY != -1 ? (double)this.jumpStartY >= AntiVoid.mc.thePlayer.posY : (double)AntiVoid.mc.thePlayer.fallDistance > this.distanceSetting.getValue()) || AntiVoid.mc.thePlayer.capabilities.allowFlying || !isVoid) break;
                    AntiVoid.mc.thePlayer.fallDistance = 0.0f;
                    this.jumpStartY = (int)AntiVoid.mc.thePlayer.posY;
                    AntiVoid.mc.thePlayer.motionY = this.motionSetting.getValue();
                }
            }
        }
    }
}

