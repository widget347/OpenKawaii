/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.Score;

public class GuiIngame$1
implements Predicate<Score> {
    public static String toc = "CL_00001958";
    public GuiIngame this$0;

    @Override
    public boolean apply(Score score) {
        return score.getPlayerName() != null && !score.getPlayerName().startsWith("#");
    }

    public GuiIngame$1(GuiIngame guiIngame) {
        this.this$0 = guiIngame;
    }
}

