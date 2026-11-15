/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.Setting;
import kawaii.ui.font.FontLoader;
import kawaii.ui.kawaii.ModuleButton;
import kawaii.ui.kawaii.SettingComponent;
import kawaii.util.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

public class MultiSettingComponent
extends SettingComponent {
    public MultiBooleanSetting multiSetting;
    public double height = 15.0;
    public double posY;
    public double posX;
    public boolean expanded;

    public MultiSettingComponent(Setting setting, ModuleButton moduleButton) {
        super(setting, moduleButton);
        this.multiSetting = (MultiBooleanSetting)setting;
    }

    @Override
    public double render(int mouseX, int mouseY, double x, double y) {
        double padding = 4.0;
        double halfFactor = 2.0;
        this.posX = x;
        this.posY = y;
        Gui.drawRect(x, y, x + this.parent.width, y + this.height, this.getBackgroundColor());
        this.height = this.expanded ? (double)(this.multiSetting.getValues().length * 15 + 10) : 15.0;
        RenderUtils.drawRoundedRect(x + padding, y + 1.0, this.parent.width - 8.0, this.height - halfFactor, 3.0, new ColorWrapper(this.getBackgroundColor()).brighterColor().getRGB());
        FontLoader.sf18.drawString(this.multiSetting.getName(), x + this.parent.width / halfFactor - (double)((float)FontLoader.sf18.getStringWidth(this.multiSetting.getName()) / 2.0f), y + padding, -1);
        FontLoader.sf18.drawString(this.expanded ? "-" : "+", x + this.parent.width - (double)FontLoader.sf18.getStringWidth(this.expanded ? "-" : "+") - 8.0, y + padding, -1);
        Gui.drawRect(x + this.parent.width / halfFactor + (double)((float)FontLoader.sf18.getStringWidth(this.multiSetting.getName()) / 2.0f) + halfFactor, y + padding + (double)FontLoader.sf18.getHeight() + 0.5, x + this.parent.width / halfFactor - (double)((float)FontLoader.sf18.getStringWidth(this.multiSetting.getName()) / 2.0f) - halfFactor, y + padding + (double)FontLoader.sf18.getHeight() + 1.0, -1);
        int index = 0;
        if (this.expanded) {
            for (BooleanSetting booleanSetting : this.multiSetting.getValues()) {
                Minecraft.getMinecraft().fontRendererObj.drawString("\u2022", (int)(x + 10.0), (int)(y + 13.0 - (double)((float)FontLoader.sf18.getHeight() / 4.0f) + (double)(15 * index)), booleanSetting.getValue() != false ? -1 : -1879048193);
                FontLoader.sf18.drawString(booleanSetting.getName(), x + 14.0, y + 13.0 + (double)(15 * index), booleanSetting.getValue() != false ? -1 : -1879048193);
                ++index;
            }
        }
        return this.height;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.width, this.posY + this.height)) {
            switch (mouseButton) {
                case 0: {
                    int rowIndex = 0;
                    if (!this.expanded) break;
                    BooleanSetting[] settingValues = this.multiSetting.getValues();
                    int valueCount = settingValues.length;
                    for (int i = 0; i < valueCount; ++i) {
                        BooleanSetting booleanSetting = settingValues[i];
                        if (GuiUtil.isInside(mouseX, mouseY, this.posX + 4.0, this.posY + 13.0 + (double)(15 * rowIndex) - 7.5, this.posX + this.parent.width - 4.0, this.posY + 13.0 + (double)(15 * rowIndex) + 7.5)) {
                            booleanSetting.toggle();
                        }
                        ++rowIndex;
                    }
                    break;
                }
                case 1: {
                    this.expanded = !this.expanded;
                }
            }
        }
    }
}

