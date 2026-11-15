/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.render.ColorThemes;
import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.Button$1;
import kawaii.ui.GuiTheme;
import kawaii.ui.Widget;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class Button
extends Widget {
    public double y;
    public GuiTheme theme;
    public Animation animation = new Animation();
    public double x;
    public String label;
    public Runnable action;

    public String getLabel() {
        return this.label;
    }

    public Runnable getAction() {
        return this.action;
    }

    public double getY() {
        return this.y;
    }

    public GuiTheme getTheme() {
        return this.theme;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        if (this.isHovered(mouseX, mouseY)) {
            this.action.run();
            return true;
        }
        return false;
    }

    public Button(String text, double buttonWidth, double buttonHeight, GuiTheme guiTheme, Runnable clickAction) {
        this.theme = guiTheme;
        this.label = text;
        this.width = buttonWidth;
        this.height = buttonHeight;
        this.action = clickAction;
    }

    public double getX() {
        return this.x;
    }

    @Override
    public void onMouseDrag(double originX, double originY, int mouseX, int mouseY) {
        double animationScale = 100.0;
        float halfDivisor = 2.0f;
        double two = 2.0;
        this.x = originX;
        this.y = originY;
        boolean hovered = this.isHovered(mouseX, mouseY);
        this.animation.setEnd(1.0).setReversed(!hovered).setEasing(150).update();
        GlStateManager.pushMatrix();
        GlStateManager.translate(originX + this.width / two, originY + this.height / two, 0.0);
        GlStateManager.scale(0.95 + this.animation.getValue() / animationScale * 0.05, 0.95 + this.animation.getValue() / animationScale * 0.05, 1.0);
        GlStateManager.translate(-(originX + this.width / two), -(originY + this.height / two), 0.0);
        switch (Button$1.$SwitchMap$kawaii$ui$GuiTheme[this.theme.ordinal()]) {
            case 1: {
                RenderUtils.drawRoundedRect(originX, originY, this.width, this.height, two, hovered ? -15658735 : -15461356);
                FontLoader.popMed25.drawString(this.label, originX + this.width / two - (double)((float)FontLoader.popMed25.getStringWidth(this.label) / halfDivisor), originY + this.height / two - (double)((float)FontLoader.popMed25.getHeight() / halfDivisor), hovered ? ColorThemes.getStaticThemeColor((int)originY) : -1);
                if (!(this.animation.getValue() > 0.0)) break;
                Gui.drawRect(originX + this.width / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor), originX + this.width / two + this.animation.getValue() / animationScale * (double)FontLoader.popBold20.getStringWidth(this.label) / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor) + two, -1);
                Gui.drawRect(originX + this.width / two + this.animation.getValue() / animationScale * (double)((float)(-FontLoader.popBold20.getStringWidth(this.label)) / halfDivisor), originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor), originX + this.width / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor) + two, -1);
                break;
            }
            case 2: {
                RenderUtils.drawRoundedRect(originX, originY, this.width, this.height, two, hovered ? -1184275 : -1);
                FontLoader.popMed25.drawString(this.label, originX + this.width / two - (double)((float)FontLoader.popMed25.getStringWidth(this.label) / halfDivisor), originY + this.height / two - (double)((float)FontLoader.popMed25.getHeight() / halfDivisor), 0);
                if (!(this.animation.getValue() > 0.0)) break;
                Gui.drawRect(originX + this.width / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor), originX + this.width / two + this.animation.getValue() / animationScale * (double)FontLoader.popBold20.getStringWidth(this.label) / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor) + two, -15066598);
                Gui.drawRect(originX + this.width / two + this.animation.getValue() / animationScale * (double)((float)(-FontLoader.popBold20.getStringWidth(this.label)) / halfDivisor), originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor), originX + this.width / two, originY + this.height / two + (double)((float)FontLoader.popMed30.getHeight() / halfDivisor) + two, -15066598);
                break;
            }
            case 3: {
                RenderUtils.drawRoundedRect(originX, originY, this.width, this.height, two, hovered ? -14737633 : -14606047);
                FontLoader.popMed25.drawString(this.label, originX + this.width / two - (double)((float)FontLoader.popMed25.getStringWidth(this.label) / halfDivisor), originY + this.height / two - (double)((float)FontLoader.popMed25.getHeight() / halfDivisor), hovered ? ColorThemes.getStaticThemeColor((int)originY) : -1);
            }
        }
        GlStateManager.popMatrix();
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return GuiUtil.isInside(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + this.height);
    }
}

