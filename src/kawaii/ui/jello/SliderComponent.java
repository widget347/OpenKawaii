/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import kawaii.render.RenderUtils;
import kawaii.setting.NumberSetting;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.ModuleSettingsPanel;
import kawaii.ui.jello.SettingComponent;
import kawaii.util.GuiUtil;

public class SliderComponent
extends SettingComponent {
    public boolean dragging;

    public boolean isDragging() {
        return this.dragging;
    }

    @Override
    public double render(int mouseX, int mouseY, double yOffset) {
        double halfFactor = 2.0;
        super.render(mouseX, mouseY, yOffset);
        this.posY = this.screen.getBoxY() + yOffset + 40.0;
        this.posX = this.screen.getBoxX();
        this.height = 20.0;
        FontLoader.sf20.drawString("", this.posX + 11.0, this.posY + 15.0, -1);
        FontLoader.sf20.drawString(((NumberSetting)this.value).getName(), this.posX + 10.0, this.posY + this.height / halfFactor - (double)((float)FontLoader.sfLight22.getHeight() / 2.0f), -16777216);
        double sliderWidth = 70.0;
        double sliderX = this.posX + this.screen.getBoxWidth() - 10.0 - sliderWidth;
        double sliderHeight = 3.0;
        double sliderY = this.posY + this.height / halfFactor - sliderHeight / halfFactor;
        RenderUtils.drawRoundedRect(sliderX, sliderY, sliderWidth, sliderHeight, sliderHeight / halfFactor, -1891920129);
        RenderUtils.drawRoundedRect(sliderX + (((NumberSetting)this.value).getValue() - ((NumberSetting)this.value).getIncrement()) / (((NumberSetting)this.value).getMax() - ((NumberSetting)this.value).getIncrement()) * sliderWidth - 4.5 - 1.0, sliderY + sliderHeight / halfFactor - 4.5 - 1.0, 11.0, 11.0, 11.0, -2236963);
        RenderUtils.drawRoundedRect(sliderX + (((NumberSetting)this.value).getValue() - ((NumberSetting)this.value).getIncrement()) / (((NumberSetting)this.value).getMax() - ((NumberSetting)this.value).getIncrement()) * sliderWidth - 4.5, sliderY + sliderHeight / halfFactor - 4.5, 9.0, 9.0, 9.0, -1);
        FontLoader.sf20.drawString("", 0.0f, 0.0f, -1);
        if (GuiUtil.isInside(mouseX, mouseY, sliderX - halfFactor, sliderY - halfFactor, sliderX + sliderWidth + 4.0, sliderY + sliderHeight + 4.0) || this.dragging) {
            String valueText = Math.floor(((NumberSetting)this.value).getValue() * 1000.0) / 1000.0 + ((NumberSetting)this.value).getName();
            FontLoader.sf18.drawString(valueText, sliderX - 4.0 - (double)FontLoader.sf18.getStringWidth(valueText), sliderY + (sliderHeight / halfFactor - (double)((float)FontLoader.sf18.getHeight() / 2.0f)), -6710887);
        }
        if (this.dragging) {
            double dragOffset = (double)mouseX - sliderX;
            ((NumberSetting)this.value).setValue(((NumberSetting)this.value).getIncrement() + (((NumberSetting)this.value).getMax() - ((NumberSetting)this.value).getIncrement()) / sliderWidth * dragOffset);
            System.out.println(((NumberSetting)this.value).getValue());
        }
        return this.height;
    }

    public SliderComponent(NumberSetting numberSetting, ModuleSettingsPanel moduleSettingsPanel) {
        this.value = numberSetting;
        this.screen = moduleSettingsPanel;
        this.height = 20.0;
    }

    @Override
    public boolean handleMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        super.handleMouseReleased(mouseX, mouseY, mouseButton);
        return false;
    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.handleMouseClicked(mouseX, mouseY, mouseButton);
        double sliderWidth = 70.0;
        double sliderX = this.posX + this.screen.getBoxWidth() - 10.0 - sliderWidth;
        double sliderHeight = 5.0;
        double sliderY = this.posY + this.height / 2.0 - sliderHeight / 2.0;
        this.dragging = GuiUtil.isInside(mouseX, mouseY, sliderX, sliderY, sliderX + sliderWidth, sliderY + sliderHeight);
        return this.dragging;
    }
}

