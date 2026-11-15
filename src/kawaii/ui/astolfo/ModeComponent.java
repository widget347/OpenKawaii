/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import kawaii.setting.ModeSetting;
import kawaii.ui.astolfo.AstolfoPalette;
import kawaii.ui.astolfo.ModuleButton;
import kawaii.ui.astolfo.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;

public class ModeComponent
extends SettingComponent
implements AstolfoPalette {
    public double posY;
    public static double componentHeight = 14.0;
    public double posX;

    @Override
    public double drawComponent(int mouseX, int mouseY, double offsetY) {
        this.posX = this.modulePanel.posX;
        this.posY = this.modulePanel.posY + this.modulePanel.height + offsetY;
        FontLoader.sf18.drawString(((ModeSetting)this.setting).getName(), this.posX + 6.0, this.posY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(((ModeSetting)this.setting).getMode().toUpperCase(), this.posX + this.modulePanel.width - 6.0 - (double)FontLoader.sf18.getStringWidth(((ModeSetting)this.setting).getMode().toUpperCase()), this.posY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -5263441);
        return componentHeight;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.modulePanel.width, this.posY + componentHeight)) {
            switch (mouseButton) {
                case 0: {
                    if (((ModeSetting)this.setting).getModeIndex() + 1 < ((ModeSetting)this.setting).getModes().length) {
                        ((ModeSetting)this.setting).setModeIndex(((ModeSetting)this.setting).getModeIndex() + 1);
                        break;
                    }
                    ((ModeSetting)this.setting).setModeIndex(0);
                    break;
                }
                case 1: {
                    if (((ModeSetting)this.setting).getModeIndex() > 0) {
                        ((ModeSetting)this.setting).setModeIndex(((ModeSetting)this.setting).getModeIndex() - 1);
                        break;
                    }
                    ((ModeSetting)this.setting).setModeIndex(((ModeSetting)this.setting).getModes().length - 1);
                }
            }
        }
    }

    public ModeComponent(ModeSetting modeSetting, ModuleButton moduleButton) {
        super(modeSetting, moduleButton);
        this.setting = modeSetting;
    }
}

