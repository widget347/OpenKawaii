/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import kawaii.setting.BooleanSetting;
import kawaii.ui.Animation;
import kawaii.ui.astolfo.ModuleButton;
import kawaii.ui.astolfo.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;

public class BooleanComponent
extends SettingComponent {
    public Animation animation = new Animation();
    public BooleanSetting setting;
    public double posX;
    public static double componentHeight = 14.0;
    public double posY;

    @Override
    public double drawComponent(int cursorX, int cursorY, double yOffset) {
        this.posX = this.modulePanel.posX;
        this.posY = this.modulePanel.posY + this.modulePanel.height + yOffset;
        FontLoader.sf18.drawString(this.setting.getName(), this.posX + 6.0, this.posY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), this.setting.getValue() != false ? this.modulePanel.categoryPanel.category.getId() : -7237231);
        return componentHeight;
    }

    @Override
    public void onMouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (GuiUtil.isInside(cursorX, cursorY, this.posX, this.posY, this.posX + this.modulePanel.width, this.posY + componentHeight)) {
            this.setting.toggle();
            this.animation.setReversed(this.setting.getValue() == false);
            this.animation.restart();
        }
    }

    public BooleanComponent(BooleanSetting booleanSetting, ModuleButton moduleButton) {
        super(booleanSetting, moduleButton);
        this.setting = booleanSetting;
    }
}

