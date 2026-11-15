/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import kawaii.setting.ModeSetting;
import kawaii.ui.clean.CleanPalette;
import kawaii.ui.clean.ModuleComponent;
import kawaii.ui.clean.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;

public class ModeComponent
extends SettingComponent
implements CleanPalette {
    public double posY;
    public double posX;
    public static double componentHeight = 15.0;

    public ModeComponent(ModeSetting modeSetting, ModuleComponent moduleComponent) {
        super(modeSetting, moduleComponent);
        this.setting = modeSetting;
    }

    @Override
    public double render(int mouseX, int mouseY, double yOffset) {
        this.posX = this.moduleComponent.posX;
        this.posY = this.moduleComponent.posY + this.moduleComponent.height + yOffset;
        FontLoader.sf18.drawString(((ModeSetting)this.setting).getName(), this.posX + 4.0, this.posY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(((ModeSetting)this.setting).getMode(), this.posX + this.moduleComponent.width - 4.0 - (double)FontLoader.sf18.getStringWidth(((ModeSetting)this.setting).getMode()), this.posY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return componentHeight;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.moduleComponent.width, this.posY + componentHeight)) {
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
}

