/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.event.ImageInitEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.BooleanSetting;
import kawaii.setting.NumberSetting;

public class Blur
extends Module {
    public static BooleanSetting blurWatermark;
    public static BooleanSetting blurArrayList;
    public static Boolean blurActive;
    public static BooleanSetting blurInfoElements;
    public static NumberSetting blurRadius;
    public static BooleanSetting blurRadar;
    public static BooleanSetting blurTabGui;
    public static BooleanSetting blurHotbar;

    static {
        blurActive = false;
        blurRadius = new NumberSetting("Radius", 10.0, 1.0, 2.0, 25.0);
        blurArrayList = new BooleanSetting("Blur Arraylist", true);
        blurRadar = new BooleanSetting("Blur Radar", true);
        blurHotbar = new BooleanSetting("Blur Hotbar", true);
        blurTabGui = new BooleanSetting("Blur TabGUI", true);
        blurWatermark = new BooleanSetting("Blur Watermark", true);
        blurInfoElements = new BooleanSetting("Blur InfoElements", true);
    }

    @Override
    public void onEvent(ImageInitEvent imageInitEvent) {
        blurActive = true;
        super.onEvent(imageInitEvent);
    }

    public Blur() {
        super("Blur", "Enables Blur [PERFORMANCE HEAVY]", Category.visuals);
        this.addSettings(blurRadius, blurArrayList, blurRadar, blurHotbar, blurTabGui, blurWatermark, blurInfoElements);
    }

    @Override
    public void onEnable() {
        blurActive = true;
    }

    @Override
    public void onDisable() {
        blurActive = false;
    }
}

