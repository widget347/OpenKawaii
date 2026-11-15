/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

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

public class MultiBooleanComponent
extends SettingComponent {
    public double x;
    public MultiBooleanSetting module;
    public boolean expanded;
    public double height = 15.0;
    public double y;

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.x, this.y, this.x + this.parent.width, this.y + this.height)) {
            switch (mouseButton) {
                case 0: {
                    int rowIndex = 0;
                    if (!this.expanded) break;
                    for (BooleanSetting booleanSetting : this.module.getValues()) {
                        if (GuiUtil.isInside(mouseX, mouseY, this.x + 4.0, this.y + 13.0 + (double)(15 * rowIndex) - 7.5, this.x + this.parent.width - 4.0, this.y + 13.0 + (double)(15 * rowIndex) + 7.5)) {
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

    public MultiBooleanComponent(Setting setting, ModuleButton moduleButton) {
        super(setting, moduleButton);
        this.module = (MultiBooleanSetting)setting;
    }

    @Override
    public double render(int mouseX, int mouseY, double posX, double posY) {
        double padding = 4.0;
        double gap = 2.0;
        this.x = posX;
        this.y = posY;
        Gui.drawRect(posX, posY, posX + this.parent.width, posY + this.height, this.getBackgroundColor());
        this.height = this.expanded ? (double)(this.module.getValues().length * 15 + 10) : 15.0;
        RenderUtils.drawRoundedRect(posX + padding, posY + 1.0, this.parent.width - 8.0, this.height - gap, 3.0, new ColorWrapper(this.getBackgroundColor()).brighterColor().getRGB());
        FontLoader.sf18.drawString(this.module.getName(), posX + this.parent.width / gap - (double)((float)FontLoader.sf18.getStringWidth(this.module.getName()) / 2.0f), posY + padding, -1);
        FontLoader.sf18.drawString(this.expanded ? "-" : "+", posX + this.parent.width - (double)FontLoader.sf18.getStringWidth(this.expanded ? "-" : "+") - 8.0, posY + padding, -1);
        Gui.drawRect(posX + this.parent.width / gap + (double)((float)FontLoader.sf18.getStringWidth(this.module.getName()) / 2.0f) + gap, posY + padding + (double)FontLoader.sf18.getHeight() + 0.5, posX + this.parent.width / gap - (double)((float)FontLoader.sf18.getStringWidth(this.module.getName()) / 2.0f) - gap, posY + padding + (double)FontLoader.sf18.getHeight() + 1.0, -1);
        int rowIndex = 0;
        if (this.expanded) {
            for (BooleanSetting booleanSetting : this.module.getValues()) {
                Minecraft.getMinecraft().fontRendererObj.drawString("\u2022", (int)(posX + 10.0), (int)(posY + 13.0 - (double)((float)FontLoader.sf18.getHeight() / 4.0f) + (double)(15 * rowIndex)), booleanSetting.getValue() != false ? -1 : -1879048193);
                FontLoader.sf18.drawString(booleanSetting.getName(), posX + 14.0, posY + 13.0 + (double)(15 * rowIndex), booleanSetting.getValue() != false ? -1 : -1879048193);
                ++rowIndex;
            }
        }
        return this.height;
    }
}

