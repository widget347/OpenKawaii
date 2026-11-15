/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.BooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.input.Mouse;

public class AutoClicker
extends Module {
    public Timer clickTimer = new Timer();
    public NumberSetting minCpsSetting = new NumberSetting("Min CPS", 9.0, 1.0, 1.0, 20.0);
    public BooleanSetting rightClickSetting;
    public NumberSetting maxCpsSetting = new NumberSetting("Max CPS", 14.0, 1.0, 1.0, 20.0);
    public BooleanSetting breakBlocksSetting = new BooleanSetting("Break Blocks", true);

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent) {
            this.setSuffix((int)this.minCpsSetting.getValue() + " - " + (int)this.maxCpsSetting.getValue());
            if (this.breakBlocksSetting.getValue().booleanValue() && AutoClicker.mc.objectMouseOver != null && AutoClicker.mc.objectMouseOver.getBlockPos() != null) {
                if (AutoClicker.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    if (AutoClicker.mc.objectMouseOver.getBlockPos() != null) {
                        return;
                    }
                }
            }
            if (Minecraft.getMinecraft().currentScreen == null && Mouse.isButtonDown(0)) {
                if (this.clickTimer.hasTimePassed(1000 / RandomUtils.nextInt((int)this.minCpsSetting.getValue(), (int)this.maxCpsSetting.getValue()))) {
                    KeyBinding.setKeyBindState(-100, true);
                    KeyBinding.onTick(-100);
                    this.clickTimer.reset();
                } else {
                    KeyBinding.setKeyBindState(-100, false);
                }
            }
        }
    }

    public AutoClicker() {
        super("AutoClicker", "Automatically Clicks", Category.combat);
        this.rightClickSetting = new BooleanSetting("Right Click", true);
        this.addSettings(this.minCpsSetting, this.maxCpsSetting, this.breakBlocksSetting, this.rightClickSetting);
    }
}

