/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import kawaii.setting.NumberSetting;
import kawaii.ui.astolfo.ModuleButton;
import kawaii.ui.astolfo.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;

public class SliderComponent
extends SettingComponent {
    public double height = 14.0;
    public NumberSetting setting;
    public double posY;
    public Boolean dragging = false;
    public double posX;

    @Override
    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
    }

    public SliderComponent(NumberSetting numberSetting, ModuleButton moduleButton) {
        super(numberSetting, moduleButton);
        this.setting = numberSetting;
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.modulePanel.width, this.posY + this.height)) {
            this.dragging = true;
        }
    }

    @Override
    public double drawComponent(int mouseX, int mouseY, double yOffset) {
        this.posX = this.modulePanel.posX;
        this.posY = this.modulePanel.posY + this.modulePanel.height + yOffset;
        if (this.dragging.booleanValue()) {
            double relativeMouseX = (double)mouseX - this.posX;
            this.setting.setValue(this.setting.getIncrement() + (this.setting.getMax() - this.setting.getIncrement()) / this.modulePanel.width * relativeMouseX);
        }
        Gui.drawRect(this.posX, this.posY, this.posX + (this.setting.getValue() - this.setting.getIncrement()) / (this.setting.getMax() - this.setting.getIncrement()) * this.modulePanel.width, this.posY + this.height, this.modulePanel.categoryPanel.category.getId());
        Gui.drawRect(this.posX, this.posY, this.posX + (this.setting.getValue() - this.setting.getIncrement()) / (this.setting.getMax() - this.setting.getIncrement()) * this.modulePanel.width, this.posY + this.height, 0x40000000);
        FontLoader.sf18.drawString(this.setting.getName(), this.posX + 6.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(Math.floor(this.setting.getValue() * 1000.0) / 1000.0 + this.setting.getName(), this.posX + this.modulePanel.width - 6.0 - (double)FontLoader.sf18.getStringWidth(Math.floor(this.setting.getValue() * 1000.0) / 1000.0 + this.setting.getName()), this.posY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -5263441);
        return this.height;
    }
}

