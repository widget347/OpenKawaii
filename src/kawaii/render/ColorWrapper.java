/*
 * Decompiled with CFR 0.152.
 */
package kawaii.render;

import java.awt.Color;

public class ColorWrapper
extends Color {
    public int greenValue() {
        return super.getGreen();
    }

    public int blueValue() {
        return super.getBlue();
    }

    public Color darkerColor() {
        return super.darker();
    }

    public int redValue() {
        return super.getRed();
    }

    public Color brighterColor() {
        return super.brighter();
    }

    public int rgbValue() {
        return super.getRGB();
    }

    public int alphaValue() {
        return super.getAlpha();
    }

    public ColorWrapper(int red, int green, int blue) {
        super(red, green, blue);
    }

    public ColorWrapper(int rgb) {
        super(rgb);
    }

    public ColorWrapper(int red, int green, int blue, int alpha) {
        super(red, green, blue, alpha);
    }

    public ColorWrapper(float red, float green, float blue, float alpha) {
        super(red, green, blue, alpha);
    }
}

