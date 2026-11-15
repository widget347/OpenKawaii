/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import kawaii.setting.NumberSetting;
import kawaii.setting.Setting;
import kawaii.ui.font.FontLoader;
import kawaii.ui.kawaii.ClickGui;
import kawaii.ui.kawaii.ModuleButton;
import kawaii.ui.kawaii.SettingComponent;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;

public class NumberSettingComponent
extends SettingComponent {
    public double posY;
    public double height = 12.0;
    public double posX;
    public NumberSetting numberSetting;
    public Boolean dragging = false;

    @Override
    public double render(int mouseX, int mouseY, double startX, double startY) {
        this.posX = startX;
        this.posY = startY;
        if (this.dragging.booleanValue()) {
            double dragX = (double)mouseX - startX;
            this.numberSetting.setValue(this.numberSetting.getIncrement() + (this.numberSetting.getMax() - this.numberSetting.getIncrement()) / this.parent.width * dragX);
        }
        Gui.drawRect(startX, startY, startX + this.parent.width, startY + this.height, this.getBackgroundColor());
        Gui.drawRect(startX, startY, startX + (this.numberSetting.getValue() - this.numberSetting.getIncrement()) / (this.numberSetting.getMax() - this.numberSetting.getIncrement()) * this.parent.width, startY + this.height, ClickGui.getEnabledColor());
        FontLoader.sf18.drawString(this.numberSetting.getName(), startX + 4.0, startY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(Math.floor(this.numberSetting.getValue() * 1000.0) / 1000.0 + this.numberSetting.getName(), startX + this.parent.width - 4.0 - (double)FontLoader.sf18.getStringWidth(Math.floor(this.numberSetting.getValue() * 1000.0) / 1000.0 + this.numberSetting.getName()), startY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return this.height;
    }

    public NumberSettingComponent(Setting setting, ModuleButton moduleButton) {
        super(setting, moduleButton);
        this.numberSetting = (NumberSetting)setting;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.width, this.posY + this.height)) {
            this.dragging = true;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }
}

