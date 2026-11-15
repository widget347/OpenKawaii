/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import kawaii.render.ColorThemes;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiButton
extends Gui {
    public static ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public boolean enabled = true;
    public int width = 200;
    public boolean pressed;
    public int yPosition;
    public boolean hovered;
    public int id;
    public boolean visible = true;
    public Animation hoverAnimation = new Animation();
    public int xPosition;
    public String displayString;
    public int height = 20;

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public int getHoverState(boolean mouseOver) {
        int hoverState = 1;
        if (!this.enabled) {
            hoverState = 0;
        } else if (mouseOver) {
            hoverState = 2;
        }
        return hoverState;
    }

    public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY) {
        return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
    }

    public void mouseDragged(Minecraft minecraft, int mouseX, int mouseY) {
    }

    public void drawButton(Minecraft minecraft, int mouseX, int mouseY) {
        float halfDivisor = 2.0f;
        if (this.visible) {
            FontRenderer fontRenderer = minecraft.fontRendererObj;
            minecraft.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            this.mouseDragged(minecraft, mouseX, mouseY);
            int textColor = 0xE0E0E0;
            if (!this.enabled) {
                textColor = 0xA0A0A0;
            } else if (this.hovered) {
                textColor = ColorThemes.getStaticThemeColor((int)((float)this.yPosition + (float)this.width / halfDivisor));
            }
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, Integer.MIN_VALUE);
            this.hoverAnimation.setReversed(this.hovered).setEasing(500).setEnd(1.0).update();
            Gui.drawRect((float)this.xPosition + (float)this.width / halfDivisor, this.yPosition + this.height - 1, (double)this.xPosition + this.hoverAnimation.getValue() / 100.0 * (double)((float)this.width / halfDivisor), this.yPosition + this.height, ColorThemes.getStaticThemeColor(this.yPosition + this.height));
            Gui.drawRect((float)this.xPosition + (float)this.width / halfDivisor, this.yPosition + this.height - 1, (double)(this.xPosition + this.width) - this.hoverAnimation.getValue() / 100.0 * (double)((float)this.width / halfDivisor), this.yPosition + this.height, ColorThemes.getStaticThemeColor(this.yPosition + this.height));
            FontLoader.popMed20.drawCenteredString(this.displayString, (float)this.xPosition + (float)this.width / halfDivisor, (float)this.yPosition + (float)this.height / halfDivisor - (float)FontLoader.popMed20.getHeight() / halfDivisor + 1.0f, textColor);
        }
    }

    public void setWidth(int newWidth) {
        this.width = newWidth;
    }

    public GuiButton(int buttonId, int x, int y, int buttonWidth, int buttonHeight, String buttonText) {
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.displayString = buttonText;
    }

    public int getButtonWidth() {
        return this.width;
    }

    public void playPressSound(SoundHandler soundHandler) {
        soundHandler.t_(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0f));
    }

    public boolean isMouseOver() {
        return this.hovered;
    }
}

