/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import kawaii.setting.ModeSetting;
import kawaii.setting.Setting;
import kawaii.ui.font.FontLoader;
import kawaii.ui.kawaii.ModuleButton;
import kawaii.ui.kawaii.SettingComponent;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;

public class ModeSettingComponent
extends SettingComponent {
    public double posX;
    public ModeSetting modeSetting;
    public double posY;
    public static double rowHeight = 15.0;

    @Override
    public double render(int mouseX, int mouseY, double startX, double startY) {
        double two = 2.0;
        this.posX = startX;
        this.posY = startY;
        Gui.drawRect(startX, startY, startX + this.parent.width, startY + rowHeight, this.getBackgroundColor());
        double segmentWidth = (float)FontLoader.sf18.getStringWidth(this.modeSetting.getMode()) / (float)this.modeSetting.getModes().length + 1.0f;
        for (int i = 0; i < this.modeSetting.getModes().length; ++i) {
            double barX = startX + this.parent.width - 4.0 - (double)FontLoader.sf18.getStringWidth(this.modeSetting.getMode()) - 0.5;
            double barY = startY + rowHeight / two + (double)((float)FontLoader.sf18.getHeight() / 2.0f) + 1.0;
            Gui.drawRect(barX + segmentWidth * (double)i + 1.0, barY, barX + segmentWidth * (double)(i + 1) - two, barY + two, this.modeSetting.getModeIndex() == i ? -1 : -12369085);
        }
        FontLoader.sf18.drawString(this.modeSetting.getName(), startX + 4.0, startY + rowHeight / two - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(this.modeSetting.getMode(), startX + this.parent.width - 4.0 - (double)FontLoader.sf18.getStringWidth(this.modeSetting.getMode()), startY + rowHeight / two - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return rowHeight;
    }

    public ModeSettingComponent(Setting setting, ModuleButton moduleButton) {
        super(setting, moduleButton);
        this.modeSetting = (ModeSetting)setting;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.width, this.posY + rowHeight)) {
            switch (mouseButton) {
                case 0: {
                    if (this.modeSetting.getModeIndex() + 1 < this.modeSetting.getModes().length) {
                        this.modeSetting.setModeIndex(this.modeSetting.getModeIndex() + 1);
                        break;
                    }
                    this.modeSetting.setModeIndex(0);
                    break;
                }
                case 1: {
                    if (this.modeSetting.getModeIndex() > 0) {
                        this.modeSetting.setModeIndex(this.modeSetting.getModeIndex() - 1);
                        break;
                    }
                    this.modeSetting.setModeIndex(this.modeSetting.getModes().length - 1);
                }
            }
        }
    }
}

