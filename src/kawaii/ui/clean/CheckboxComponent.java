/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.ui.Animation;
import kawaii.ui.clean.ModuleComponent;
import kawaii.ui.clean.SettingComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CheckboxComponent
extends SettingComponent {
    public double posY;
    public Animation toggleAnimation = new Animation();
    public static double checkboxHeight = 15.0;
    public double posX;
    public BooleanSetting booleanSetting;

    public CheckboxComponent(BooleanSetting booleanSetting, ModuleComponent moduleComponent) {
        super(booleanSetting, moduleComponent);
        this.booleanSetting = booleanSetting;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.moduleComponent.width, this.posY + checkboxHeight)) {
            this.booleanSetting.toggle();
            this.toggleAnimation.setReversed(this.booleanSetting.getValue() == false);
            this.toggleAnimation.restart();
        }
    }

    @Override
    public double render(int mouseX, int mouseY, double yOffset) {
        this.posX = this.moduleComponent.posX;
        this.posY = this.moduleComponent.posY + this.moduleComponent.height + yOffset;
        this.toggleAnimation.setEnd(1.0).setReversed(this.booleanSetting.getValue() == false).setEasing(100).update();
        Gui.drawRect(this.posX + this.moduleComponent.width - 14.0, this.posY + 2.5, this.posX + this.moduleComponent.width - 4.0, this.posY + checkboxHeight - 2.5, -12434878);
        GL11.glPushMatrix();
        GL11.glTranslated(this.posX + this.moduleComponent.width - 14.0 + 5.0, this.posY + 2.5 + 5.0, 0.0);
        GL11.glScaled(this.toggleAnimation.getValue() / 100.0, this.toggleAnimation.getValue() / 100.0, 0.0);
        RenderUtils.drawImageWithAlpha(-5.0, -5.0, 10, 10, new ResourceLocation("kawaii/images/Check.png"), this.toggleAnimation.getValue() / 100.0);
        GL11.glTranslated(-(this.posX + this.moduleComponent.width - 14.0), -(this.posY + 2.5), 0.0);
        GL11.glPopMatrix();
        FontLoader.sf18.drawString(this.booleanSetting.getName(), this.posX + 4.0, this.posY + checkboxHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return checkboxHeight;
    }
}

