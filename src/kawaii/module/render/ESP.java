/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.BooleanSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;

public class ESP
extends Module {
    public static NumberSetting outlineSizeSetting;
    public static BooleanSetting invisiblesSetting;
    public static BooleanSetting animalsSetting;
    public static BooleanSetting playersSetting;
    public static BooleanSetting mobsSetting;

    static {
        playersSetting = new BooleanSetting("Players", true);
        animalsSetting = new BooleanSetting("Animals", true);
        mobsSetting = new BooleanSetting("Mobs", true);
        invisiblesSetting = new BooleanSetting("Invisibles", false);
        outlineSizeSetting = new NumberSetting("Outline Size", 5.0, 0.5, 0.5, 15.0);
    }

    public ESP() {
        super("ESP", "Allows you to see entities through walls", Category.visuals);
        this.addSettings(new MultiBooleanSetting("Targets", playersSetting, animalsSetting, mobsSetting, invisiblesSetting), outlineSizeSetting);
    }
}

