/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import kawaii.render.RenderUtils;
import kawaii.setting.ModeSetting;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.ModuleSettingsPanel;
import kawaii.ui.jello.SettingComponent;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class ModeComponent
extends SettingComponent {
    public Animation dropdownAnimation = new Animation();
    public boolean expanded;

    @Override
    public double renderOptions(int mouseX, int mouseY, double offsetY) {
        double verticalOffset = 3.5;
        double rightPadding = 10.0;
        float fontDivisor = 2.0f;
        double heightDivisor = 2.0;
        double dropdownWidth = 70.0;
        double optionHeight = FontLoader.sf20.getHeight() + 7;
        if (this.dropdownAnimation.getValue() > 0.0) {
            RenderUtils.drawRoundedRect(this.posX + this.screen.getBoxWidth() - rightPadding - dropdownWidth, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - verticalOffset + optionHeight, dropdownWidth, this.dropdownAnimation.getValue() / 100.0 * ((double)((ModeSetting)this.value).getModes().length * optionHeight), heightDivisor, -1);
        }
        if (this.expanded) {
            RenderUtils.drawRoundedOutline(this.posX + this.screen.getBoxWidth() - rightPadding - dropdownWidth - 0.5, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - verticalOffset - 0.5, dropdownWidth + 1.5, optionHeight + 1.0 + this.dropdownAnimation.getValue() / 100.0 * ((double)((ModeSetting)this.value).getModes().length * optionHeight), 3.0, 1.0f, -4473925);
        }
        int rowIndex = 0;
        double optionX = this.posX + this.screen.getBoxWidth() - rightPadding - dropdownWidth;
        double optionY = this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - verticalOffset;
        GL11.glEnable(3089);
        RenderUtils.applyScissor(this.posX + this.screen.getBoxWidth() - rightPadding - dropdownWidth, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - verticalOffset + optionHeight, this.posX + this.screen.getBoxWidth() - rightPadding - dropdownWidth + dropdownWidth, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - verticalOffset + optionHeight + this.dropdownAnimation.getValue() / 100.0 * ((double)((ModeSetting)this.value).getModes().length * optionHeight));
        String[] modeNames = ((ModeSetting)this.value).getModes();
        int modeCount = modeNames.length;
        for (int i = 0; i < modeCount; i += 2) {
            String modeName = modeNames[i];
            boolean hovered = GuiUtil.isInside(mouseX, mouseY, optionX, optionY + (double)rowIndex * optionHeight + optionHeight, optionX + dropdownWidth, optionY + (double)rowIndex * optionHeight + optionHeight + optionHeight);
            if (hovered) {
                Gui.drawRect(optionX, optionY + (double)rowIndex * optionHeight + optionHeight, optionX + dropdownWidth, optionY + (double)rowIndex * optionHeight + optionHeight + optionHeight, -1776412);
            }
            FontLoader.sf20.drawString("", 0.0f, 0.0f, -1);
            FontLoader.sf20.drawString(modeName, optionX + 5.0, optionY + (double)rowIndex * optionHeight + optionHeight + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor), -16777216);
            ++rowIndex;
            --i;
        }
        GL11.glDisable(3089);
        return this.height;
    }

    public ModeComponent(ModeSetting modeSetting, ModuleSettingsPanel moduleSettingsPanel) {
        this.value = modeSetting;
        this.screen = moduleSettingsPanel;
    }

    @Override
    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        double dropdownWidth = 70.0;
        double optionHeight = FontLoader.sf20.getHeight() + 7;
        if (GuiUtil.isInside(mouseX, mouseY, this.posX + this.screen.getBoxWidth() - 10.0 - dropdownWidth, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f) - 3.5, this.posX + this.screen.getBoxWidth() - 10.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f) - 3.5 + 15.0)) {
            if (!this.expanded) {
                this.screen.getComponents().forEach(this::collapseOther);
            }
            this.expanded = !this.expanded;
            return true;
        }
        int modeIndex = 0;
        double optionX = this.posX + this.screen.getBoxWidth() - 10.0 - dropdownWidth;
        double optionY = this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f) - 3.5;
        if (this.expanded) {
            for (String modeName : ((ModeSetting)this.value).getModes()) {
                if (GuiUtil.isInside(mouseX, mouseY, optionX, optionY + (double)modeIndex * optionHeight + optionHeight, optionX + dropdownWidth, optionY + (double)modeIndex * optionHeight + optionHeight + optionHeight)) {
                    ((ModeSetting)this.value).setModeIndex(modeIndex);
                    return true;
                }
                ++modeIndex;
            }
        }
        return false;
    }

    @Override
    public double render(int mouseX, int mouseY, double offsetY) {
        double horizontalPadding = 10.0;
        float fontDivisor = 2.0f;
        double heightDivisor = 2.0;
        this.posY = this.screen.getBoxY() + offsetY + 40.0;
        this.posX = this.screen.getBoxX();
        this.height = 20.0;
        this.dropdownAnimation.setReversed(!this.expanded).setEasing(200).update();
        FontLoader.sf20.drawString("", this.posX + 11.0, this.posY + 15.0, -1);
        FontLoader.sf20.drawString(((ModeSetting)this.value).getName(), this.posX + horizontalPadding, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sfLight22.getHeight() / fontDivisor), -16777216);
        double dropdownWidth = 70.0;
        double optionHeight = FontLoader.sf20.getHeight() + 7;
        RenderUtils.drawRoundedOutline(this.posX + this.screen.getBoxWidth() - horizontalPadding - dropdownWidth - 0.5, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - 3.5 - 0.5, dropdownWidth + 1.5, optionHeight + 1.0 + this.dropdownAnimation.getValue() / 100.0 * ((double)((ModeSetting)this.value).getModes().length * optionHeight), 3.0, 1.0f, -4473925);
        RenderUtils.drawRoundedRect(this.posX + this.screen.getBoxWidth() - horizontalPadding - dropdownWidth, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) - 3.5, dropdownWidth, optionHeight + this.dropdownAnimation.getValue() / 100.0 * ((double)((ModeSetting)this.value).getModes().length * optionHeight), 3.0, -1);
        FontLoader.sf20.drawString("", this.posX + 11.0, this.posY + 15.0, -1);
        GL11.glTranslated(this.posX + this.screen.getBoxWidth() - horizontalPadding - (double)((float)FontLoader.sf20.getStringWidth("V") / fontDivisor) - 5.0, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) + (double)((float)FontLoader.sf20.getHeight() / fontDivisor), 0.0);
        GL11.glRotatef(-((float)(this.dropdownAnimation.getValue() / 100.0 * -90.0)) + 90.0f, 0.0f, 0.0f, 1.0f);
        FontLoader.sf20.drawString("v", (float)(-FontLoader.sf20.getStringWidth("v")) / fontDivisor, (float)(-FontLoader.sf20.getHeight()) / fontDivisor, -1895825408);
        GL11.glRotatef(-(-((float)(this.dropdownAnimation.getValue() / 100.0 * -90.0)) + 90.0f), 0.0f, 0.0f, 1.0f);
        GL11.glTranslated(-(this.posX + this.screen.getBoxWidth() - horizontalPadding - (double)((float)FontLoader.sf20.getStringWidth("V") / fontDivisor) - 5.0), -(this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor) + (double)((float)FontLoader.sf20.getHeight() / fontDivisor)), 0.0);
        FontLoader.sf20.drawString("", this.posX + 11.0, this.posY + 15.0, -1);
        FontLoader.sf20.drawString(((ModeSetting)this.value).getMode(), this.posX + this.screen.getBoxWidth() - horizontalPadding - dropdownWidth + 5.0, this.posY + this.height / heightDivisor - (double)((float)FontLoader.sf20.getHeight() / fontDivisor), -15527149);
        return this.height;
    }

    public void collapseOther(SettingComponent settingComponent) {
        if (settingComponent instanceof ModeComponent && settingComponent != this) {
            ((ModeComponent)settingComponent).expanded = false;
        }
    }
}

