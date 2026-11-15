/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

public class GuiUtil {
    public static boolean isInside(int mouseX, int mouseY, double left, double top, double right, double bottom) {
        return left < (double)mouseX && right > (double)mouseX && top < (double)mouseY && bottom > (double)mouseY;
    }
}

