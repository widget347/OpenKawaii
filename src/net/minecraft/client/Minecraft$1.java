/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.stats.IStatStringFormat;

public class Minecraft$1
implements IStatStringFormat {
    public Minecraft field_74532_a;

    public Minecraft$1(Minecraft mc) {
        this.field_74532_a = mc;
    }

    @Override
    public String formatString(String formatPattern) {
        try {
            return String.format(formatPattern,GameSettings.getKeyDisplayString(this.field_74532_a.gameSettings.keyBindInventory.getKeyCode()));
        }
        catch (Exception error) {
            return "Error: " + error.getLocalizedMessage();
        }
    }
}

