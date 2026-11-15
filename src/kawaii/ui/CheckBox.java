/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.Widget;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CheckBox
extends Widget {
    public boolean checked = false;
    public Animation animation = new Animation();
    public double y;
    public String label;
    public double x;

    public boolean isHovered(int mouseX, int mouseY) {
        return GuiUtil.isInside(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + this.height);
    }

    public double getY() {
        return this.y;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public double getX() {
        return this.x;
    }

    public CheckBox(String labelText, double boxWidth, double boxHeight) {
        this.label = labelText;
        this.width = boxWidth;
        this.height = boxHeight;
    }

    @Override
    public void onMouseDrag(double posX, double posY, int mouseX, int mouseY) {
        double divisor = 2.0;
        this.x = posX;
        this.y = posY;
        boolean hovered = this.isHovered(mouseX, mouseY);
        RenderUtils.drawRoundedRect(posX, posY, this.width, this.height, 4.0, -14606047);
        RenderUtils.drawRoundedRect(posX + 1.0, posY + 1.0, this.height - divisor, this.height - divisor, 4.0, -13553359);
        FontLoader.popMed20.drawString(this.label, posX + this.height + divisor, posY + this.height / divisor - (double)((float)FontLoader.popMed20.getHeight() / 2.0f), -1);
        this.animation.setEnd(1.0).setReversed(!this.checked).setEasing(100).update();
        this.height = 20.0;
        GL11.glPushMatrix();
        GL11.glTranslated(posX + (this.height - divisor) / divisor + 1.0, posY + (this.height - divisor) / divisor + 1.0, 0.0);
        GL11.glScaled(this.animation.getValue() / 100.0, this.animation.getValue() / 100.0, 0.0);
        GL11.glTranslated(-(posX + (this.height - divisor) / divisor + 1.0), -(posY + (this.height - divisor) / divisor + 1.0), 0.0);
        RenderUtils.drawImageWithAlpha(posX + 1.0, posY + 1.0, (int)(this.height - divisor), (int)(this.height - divisor), new ResourceLocation("kawaii/images/Check.png"), this.animation.getValue() / 100.0);
        GL11.glPopMatrix();
    }

    public String getLabel() {
        return this.label;
    }

    @Override
    public boolean isMouseOver(int mouseX, int mouseY) {
        if (this.isHovered(mouseX, mouseY)) {
            this.checked = !this.checked;
            return true;
        }
        return false;
    }
}

