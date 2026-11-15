/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import java.awt.Color;
import java.util.Date;
import java.util.Iterator;
import kawaii.Kawaii;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.SuffixStyle;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.render.ColorThemes;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.font.CFontRenderer;
import kawaii.ui.font.CustomFontRenderer;
import kawaii.ui.font.FontLoader;
import kawaii.util.CustomDate;
import kawaii.util.DateFormatter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;

public class Hud
extends Module {
    public static ModeSetting watermarkMode;
    public static MultiBooleanSetting elements;
    public static NumberSetting bgOpacity;
    public static ModeSetting arrayListOutline;
    public static BooleanSetting newChat;
    public static ModeSetting colorMode;
    public static BooleanSetting arrayListBg;
    public static ModeSetting colorTheme;
    public static ModeSetting arrayListFont;
    public static ModeSetting hotbarStyle;

    public static Color getHudColor() {
        return new ColorWrapper(0.0f, 0.0f, 0.0f, (float)bgOpacity.getValue() / 255.0f);
    }

    static {
        elements = new MultiBooleanSetting("Elements", new BooleanSetting("FPS", true), new BooleanSetting("BPS", true), new BooleanSetting("Watermark", true), new BooleanSetting("UID", false));
        bgOpacity = new NumberSetting("BG Opacity", 100.0, 1.0, 50.0, 200.0);
        watermarkMode = new ModeSetting("Mode", "Kawaii", "Basic");
        colorTheme = new ModeSetting("Color Theme", "Rose", "Amethyst", "Sapphire", "Mint");
        colorMode = new ModeSetting("Color", "Kawaii", "Astolfo", "Custom");
        arrayListOutline = new ModeSetting("Outline", "Full", "Left", "Right", "None");
        hotbarStyle = new ModeSetting("Hotbar", "Filled", "Float", "Outlined");
        arrayListFont = new ModeSetting("Arraylist Font", "Basic", "Rough", "Smooth");
        arrayListBg = new BooleanSetting("ArrayList BG", true);
        newChat = new BooleanSetting("New Chat", false);
    }

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        colorTheme.setVisible(!colorMode.getMode().equalsIgnoreCase("Custom"));
        super.onEvent(settingsUpdateEvent);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        double padding = 2.0;
        if (!this.isEnabled()) {
            return;
        }
        if (cancellableEvent instanceof EventRender2D) {
            Object watermarkText;
            Object font;
            ScaledResolution scaledResolution = ((EventRender2D)cancellableEvent).scaledResolution;
            if (elements.findByName("Watermark").getValue().booleanValue()) {
                if (watermarkMode.getMode().equalsIgnoreCase("Basic")) {
                    Object currentDate = new CustomDate();
                    font = "hh\u00a77:\u00a76mm";
                    DateFormatter dateFormatter = new DateFormatter((String)font);
                    String formattedTime = dateFormatter.format((Date)currentDate);
                    watermarkText = Kawaii.INSTANCE.getName() + " " + Kawaii.INSTANCE.getVersion() + " - " + Kawaii.INSTANCE.getIntentAccount().username;
                    if (cancellableEvent.getEventType() == EventType.pre) {
                        if (Blur.blurActive.booleanValue() && Blur.blurWatermark.getValue().booleanValue()) {
                            BlurRenderer.queueBlur(5.0, 3.0, 10 + FontLoader.popMed20.getStringWidth((String)watermarkText) + 5, FontLoader.popMed20.getHeight() + 7 + 3, padding);
                        }
                        RenderUtils.drawRoundedRect(5.0, 3.0, 10 + FontLoader.popMed20.getStringWidth((String)watermarkText), FontLoader.popMed20.getHeight() + 7, padding, Hud.getHudColor().getRGB());
                    }
                    FontLoader.popMed20.drawThemedString((String)watermarkText, 10.0, 7.0, 5);
                }
                if (cancellableEvent.getEventType() != EventType.pre) {
                    switch (watermarkMode.getMode()) {
                        case "Kawaii": {
                            FontLoader.val32.drawString(Kawaii.INSTANCE.getName(), 3.0f, 5.0f, -2649601);
                            FontLoader.val16.drawString(Kawaii.INSTANCE.getVersion(), FontLoader.val32.getStringWidth(Kawaii.INSTANCE.getName()) + 4, 2 + FontLoader.val32.getHeight() - FontLoader.val16.getHeight(), -3694081);
                        }
                    }
                }
                if (elements.findByName("UID").getValue().booleanValue()) {
                    FontLoader.popMed20.drawString("UID: \u00a7e", scaledResolution.getScaledWidth() - FontLoader.popMed20.getStringWidth("UID: " + Kawaii.INSTANCE.getIntentAccount().client_uid) - 4, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight() - FontLoader.popMed20.getHeight() - 2, -1);
                    FontLoader.popMed20.drawThemedString(Kawaii.INSTANCE.getIntentAccount().client_uid + "", scaledResolution.getScaledWidth() - FontLoader.popMed20.getStringWidth("" + Kawaii.INSTANCE.getIntentAccount().client_uid) - 4, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight() - FontLoader.popMed20.getHeight() - 2, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight() - FontLoader.popMed20.getHeight() - 2);
                }
                if (elements.findByName("BPS").getValue().booleanValue()) {
                    double bps = Math.floor(Math.hypot(Math.abs(Hud.mc.thePlayer.motionX) * 20.0 * (double)Hud.mc.timer.timerSpeed, Math.abs(Hud.mc.thePlayer.motionZ) * 20.0 * (double)Hud.mc.timer.timerSpeed) * 100.0) / 100.0;
                    FontLoader.popMed20.drawString("BPS: \u00a7e", scaledResolution.getScaledWidth() - FontLoader.popMed20.getStringWidth("BPS: " + bps) - 4, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight(), -1);
                    FontLoader.popMed20.drawThemedString(bps + "", scaledResolution.getScaledWidth() - FontLoader.popMed20.getStringWidth("" + bps) - 4, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight(), scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight());
                }
                if (elements.findByName("FPS").getValue().booleanValue()) {
                    FontLoader.popMed20.drawString("FPS: \u00a7e", 4.0f, scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight(), -1);
                    FontLoader.popMed20.drawThemedString(Minecraft.getDebugFPS() + "", 4 + FontLoader.popMed20.getStringWidth("FPS: "), scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight(), scaledResolution.getScaledHeight() - 2 - FontLoader.popMed20.getHeight());
                }
            }
            int moduleIndex = 0;
            if (!Blur.blurArrayList.getValue().booleanValue() && cancellableEvent.getEventType() == EventType.pre) {
                return;
            }
            double moduleSpacing = 0.0;
            switch (arrayListFont.getMode()) {
                case "Basic": {
                    font = FontLoader.sf24;
                    moduleSpacing = 4.0;
                    break;
                }
                case "Smooth": {
                    font = FontLoader.roboReg24;
                    moduleSpacing = 3.0;
                    break;
                }
                default: {
                    font = FontLoader.popMed20;
                    moduleSpacing = padding;
                }
            }
            java.util.List<Module> sortedModules = Kawaii.INSTANCE.getModuleManager().getSortedEnabledModules((CFontRenderer)font);
            Iterator moduleIterator = sortedModules.iterator();
            while (moduleIterator.hasNext()) {
                Module module = (Module)moduleIterator.next();
                double rowTop = (double)moduleIndex * ((double)((CustomFontRenderer)font).getHeight() + moduleSpacing);
                double rowBottom = rowTop + (double)((CustomFontRenderer)font).getHeight() + moduleSpacing;
                String displayName = module.getDisplayName(SuffixStyle.bracketedSuffix);
                double rowLeft = scaledResolution.getScaledWidth() - ((CFontRenderer)font).getStringWidth(displayName) - 6;
                double rowRight = scaledResolution.getScaledWidth();
                int startColor = ColorThemes.getThemeColor((int)rowTop + 1);
                switch (arrayListOutline.getMode()) {
                    case "Left": {
                        Module nextModule;
                        rowLeft += 1.5;
                        if (cancellableEvent.getEventType() == EventType.post && arrayListBg.getValue().booleanValue()) {
                            Gui.drawRect(rowLeft, rowTop, rowRight, rowBottom, Hud.getHudColor().getRGB());
                        }
                        Gui.drawGradientRect(rowLeft - 1.0, rowTop, rowLeft, rowBottom, startColor, ColorThemes.getThemeColor((int)rowBottom));
                        Module nextModuleRef = nextModule = sortedModules.size() > moduleIndex + 1 ? (Module)sortedModules.get(moduleIndex + 1) : null;
                        if (nextModule != null) {
                            Gui.drawRect(rowLeft - 1.0, rowBottom + 1.0, rowRight - 6.0 + 1.5 - (double)((CFontRenderer)font).getStringWidth(nextModule.getDisplayName(SuffixStyle.bracketedSuffix)), rowBottom, ColorThemes.getThemeColor((int)rowBottom));
                            break;
                        }
                        Gui.drawRect(rowLeft - 1.0, rowBottom, rowRight, rowBottom + 1.0, ColorThemes.getThemeColor((int)rowBottom));
                        break;
                    }
                    case "Full": {
                        Module nextModule;
                        if (cancellableEvent.getEventType() == EventType.post && arrayListBg.getValue().booleanValue()) {
                            Gui.drawRect(rowLeft, rowTop, rowRight, rowBottom, Hud.getHudColor().getRGB());
                        }
                        Gui.drawGradientRect(rowLeft - 1.0, rowTop, rowLeft, rowBottom, startColor, ColorThemes.getThemeColor((int)rowBottom));
                        Module nextModuleRef = nextModule = sortedModules.size() > moduleIndex + 1 ? (Module)sortedModules.get(moduleIndex + 1) : null;
                        if (nextModule != null) {
                            Gui.drawRect(rowLeft - 1.0, rowBottom + 1.0, rowRight - 6.0 - (double)((CFontRenderer)font).getStringWidth(nextModule.getDisplayName(SuffixStyle.bracketedSuffix)), rowBottom, ColorThemes.getThemeColor((int)rowBottom));
                        } else {
                            Gui.drawRect(rowLeft - 1.0, rowBottom, rowRight, rowBottom + 1.0, ColorThemes.getThemeColor((int)rowBottom));
                        }
                        Gui.drawGradientRect(scaledResolution.getScaledWidth() - 1, rowTop, scaledResolution.getScaledWidth(), rowBottom, startColor, ColorThemes.getThemeColor((int)rowBottom));
                        break;
                    }
                    case "Right": {
                        if (cancellableEvent.getEventType() == EventType.post && arrayListBg.getValue().booleanValue()) {
                            Gui.drawRect(rowLeft, rowTop, rowRight, rowBottom, Hud.getHudColor().getRGB());
                        }
                        Gui.drawGradientRect(scaledResolution.getScaledWidth() - 1, rowTop, scaledResolution.getScaledWidth(), rowBottom, startColor, ColorThemes.getThemeColor((int)rowBottom));
                        break;
                    }
                    default: {
                        rowLeft += 0.5;
                        if (cancellableEvent.getEventType() != EventType.post || !arrayListBg.getValue().booleanValue()) break;
                        Gui.drawRect(rowLeft, rowTop, rowRight, rowBottom, Hud.getHudColor().getRGB());
                    }
                }
                if (arrayListBg.getValue().booleanValue()) {
                    ((CFontRenderer)font).drawString(displayName, rowLeft + padding, rowTop + 2.5, startColor);
                } else if (cancellableEvent.getEventType() == EventType.post) {
                    ((CFontRenderer)font).drawString(displayName, rowLeft + padding, rowTop + 2.5, startColor);
                }
                if (Blur.blurActive.booleanValue() && cancellableEvent.getEventType() == EventType.post && arrayListBg.getValue().booleanValue()) {
                    BlurRenderer.queueRect(rowLeft, rowTop, rowRight, rowBottom);
                }
                ++moduleIndex;
            }
        }
    }

    public Hud() {
        super("Hud", "Displays HUD", Category.visuals);
        this.setEnabled(true);
        this.addSettings(watermarkMode, colorMode, colorTheme, arrayListOutline, arrayListFont, bgOpacity, hotbarStyle, arrayListBg, newChat, elements);
    }

    public static Color getHudColorFromRgb(int rgb) {
        ColorWrapper colorWrapper = new ColorWrapper(rgb);
        return new ColorWrapper((float)colorWrapper.redValue(), (float)colorWrapper.greenValue(), (float)colorWrapper.blueValue(), (float)bgOpacity.getValue() / 255.0f);
    }
}

