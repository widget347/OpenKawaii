/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.ModuleSettingsPanel;
import kawaii.ui.jello.SettingComponent;
import kawaii.util.GuiUtil;

public class CheckboxComponent
extends SettingComponent {
    @Override
    public double render(int mouseX, int mouseY, double yOffset) {
        double boxSize = 10.0;
        this.posY = this.screen.getBoxY() + yOffset + 40.0;
        this.posX = this.screen.getBoxX();
        this.height = 17.0;
        FontLoader.sf20.drawString("", this.posX + 11.0, this.posY + 15.0, -1);
        FontLoader.sf20.drawString(((BooleanSetting)this.value).getName(), this.posX + boxSize, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f), -16777216);
        RenderUtils.drawRoundedRect(this.posX + this.screen.getBoxWidth() - boxSize - 12.0, this.posY + this.height / 2.0 - 6.0, boxSize, boxSize, boxSize, ((BooleanSetting)this.value).getValue() != false ? -1891920129 : -2236963);
        return this.height;
    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX + this.screen.getBoxWidth() - 10.0 - 12.0, this.posY + this.height / 2.0 - 6.0, this.posX + this.screen.getBoxWidth() - 10.0 - 12.0 + 10.0, this.posY + this.height / 2.0 - 6.0 + 10.0)) {
            ((BooleanSetting)this.value).toggle();
            return true;
        }
        return false;
    }

    public CheckboxComponent(BooleanSetting booleanSetting, ModuleSettingsPanel moduleSettingsPanel) {
        this.value = booleanSetting;
        this.screen = moduleSettingsPanel;
    }
}

