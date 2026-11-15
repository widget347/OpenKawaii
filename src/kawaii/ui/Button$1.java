/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.ui.GuiTheme;

public class Button$1 {
    public static int[] $SwitchMap$kawaii$ui$GuiTheme = new int[GuiTheme.values().length];

    static {
        try {
            Button$1.$SwitchMap$kawaii$ui$GuiTheme[GuiTheme.darkUnderline.ordinal()] = 1;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
        try {
            Button$1.$SwitchMap$kawaii$ui$GuiTheme[GuiTheme.light.ordinal()] = 2;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
        try {
            Button$1.$SwitchMap$kawaii$ui$GuiTheme[GuiTheme.darkFlat.ordinal()] = 3;
        }
        catch (NoSuchFieldError ignored) {
            // empty catch block
        }
    }
}

