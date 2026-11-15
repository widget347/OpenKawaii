/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import kawaii.render.ColorThemes;
import kawaii.setting.NumberSetting;
import kawaii.ui.clean.ModuleComponent;
import kawaii.ui.clean.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;

public class SliderComponent
extends SettingComponent {
    public double posX;
    public NumberSetting numberSetting;
    public double height = 12.0;
    public Boolean dragging = false;
    public double posY;

    public SliderComponent(NumberSetting numberSetting, ModuleComponent moduleComponent) {
        super(numberSetting, moduleComponent);
        this.numberSetting = numberSetting;
    }

    @Override
    public void mouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (GuiUtil.isInside(cursorX, cursorY, this.posX, this.posY, this.posX + this.moduleComponent.width, this.posY + this.height)) {
            this.dragging = true;
        }
    }

    @Override
    public double render(int cursorX, int cursorY, double yOffset) {
        this.posX = this.moduleComponent.posX;
        this.posY = this.moduleComponent.posY + this.moduleComponent.height + yOffset;
        if (this.dragging.booleanValue()) {
            double mouseOffsetX = (double)cursorX - this.posX;
            this.numberSetting.setValue(this.numberSetting.getIncrement() + (this.numberSetting.getMax() - this.numberSetting.getIncrement()) / this.moduleComponent.width * mouseOffsetX);
        }
        Gui.drawRect(this.posX, this.posY, this.posX + (this.numberSetting.getValue() - this.numberSetting.getIncrement()) / (this.numberSetting.getMax() - this.numberSetting.getIncrement()) * this.moduleComponent.width, this.posY + this.height, ColorThemes.getStaticThemeColor((int)this.posY));
        Gui.drawRect(this.posX, this.posY, this.posX + (this.numberSetting.getValue() - this.numberSetting.getIncrement()) / (this.numberSetting.getMax() - this.numberSetting.getIncrement()) * this.moduleComponent.width, this.posY + this.height, 0x40000000);
        FontLoader.sf18.drawString(this.numberSetting.getName(), this.posX + 4.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        FontLoader.sf18.drawString(Math.floor(this.numberSetting.getValue() * 1000.0) / 1000.0 + this.numberSetting.getName(), this.posX + this.moduleComponent.width - 4.0 - (double)FontLoader.sf18.getStringWidth(Math.floor(this.numberSetting.getValue() * 1000.0) / 1000.0 + this.numberSetting.getName()), this.posY + this.height / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return this.height;
    }

    @Override
    public void mouseReleased(int cursorX, int cursorY, int mouseButton) {
        this.dragging = false;
    }
}

