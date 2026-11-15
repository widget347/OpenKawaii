/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.awt.Color;
import kawaii.module.render.Hud;
import kawaii.render.ColorWrapper;

public class ColorThemes {
    public static int getThemeColor(int offset) {
        switch (Hud.colorMode.getMode()) {
            case "Kawaii": {
                return -6134276;
            }
            case "Custom": {
                switch (Hud.colorTheme.getMode()) {
                    case "Rose": {
                        return ColorThemes.pulseThemeColor(-35477, 100, offset * 3);
                    }
                    case "Amethyst": {
                        return ColorThemes.pulseThemeColor(-6198273, 10, offset * 3);
                    }
                    case "Sapphire": {
                        return ColorThemes.pulseThemeColor(-9732097, 10, offset * 3);
                    }
                    case "Mint": {
                        return ColorThemes.pulseThemeColor(-5832791, 10, offset * 3);
                    }
                }
            }
        }
        return ColorThemes.getRainbowColor(4000, offset * 5).getRGB();
    }

    public static Color pulseColor(Color color, int phaseIndex, int phaseCount) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float wave = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)phaseIndex / (float)phaseCount * 2.0f) % 2.0f - 1.0f);
        wave = 0.5f + 0.5f * wave;
        hsb[2] = wave % 2.0f;
        return new ColorWrapper(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }

    public static Color withAlpha(Color color, int alpha) {
        return new ColorWrapper(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }

    public static int getStaticThemeColor(int offset) {
        switch (Hud.colorMode.getMode()) {
            case "Kawaii": {
                return -6134276;
            }
            case "Custom": {
                switch (Hud.colorTheme.getMode()) {
                    case "Rose": {
                        return -35477;
                    }
                    case "Amethyst": {
                        return -6198273;
                    }
                    case "Sapphire": {
                        return -9732097;
                    }
                    case "Mint": {
                        return -5832791;
                    }
                }
            }
        }
        return ColorThemes.getRainbowColor(4000, offset * 5).getRGB();
    }

    public static int pulseThemeColor(int themeRgb, int phaseIndex, int phaseCount) {
        ColorWrapper colorWrapper = new ColorWrapper(themeRgb);
        float[] hsb = new float[3];
        Color.RGBtoHSB(colorWrapper.redValue(), colorWrapper.greenValue(), colorWrapper.blueValue(), hsb);
        float wave = Math.abs(((float)(System.currentTimeMillis() % 2000L) / 1000.0f + (float)phaseIndex / (float)phaseCount * 2.0f) % 2.0f - 1.0f);
        wave = 0.7f + 0.3f * wave;
        hsb[2] = wave % 2.0f;
        return new ColorWrapper(Color.HSBtoRGB(hsb[0], hsb[1] * wave, hsb[2])).rgbValue();
    }

    public static Color getRainbowColor(int periodMillis, float phaseOffset) {
        float hue;
        float rainbowPeriod = 4000.0f;
        for (hue = (float)(System.currentTimeMillis() % (long)periodMillis) + phaseOffset; hue > rainbowPeriod; hue -= rainbowPeriod) {
        }
        if ((double)(hue /= rainbowPeriod) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.getHSBColor(hue += 0.5f, 0.5f, 1.0f);
    }
}

