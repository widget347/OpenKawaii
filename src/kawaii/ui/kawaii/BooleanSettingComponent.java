/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.Setting;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import kawaii.ui.kawaii.ModuleButton;
import kawaii.ui.kawaii.SettingComponent;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BooleanSettingComponent
extends SettingComponent {
    public static double componentHeight = 15.0;
    public double posX;
    public BooleanSetting booleanSetting;
    public Animation toggleAnimation = new Animation();
    public double posY;

    @Override
    public void mouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (GuiUtil.isInside(cursorX, cursorY, this.posX, this.posY, this.posX + this.parent.width, this.posY + componentHeight)) {
            this.booleanSetting.toggle();
            this.toggleAnimation.setReversed(this.booleanSetting.getValue() == false);
            this.toggleAnimation.restart();
        }
    }

    public BooleanSettingComponent(Setting setting, ModuleButton moduleButton) {
        super(setting, moduleButton);
        this.booleanSetting = (BooleanSetting)setting;
    }

    @Override
    public double render(int cursorX, int cursorY, double componentX, double componentY) {
        this.posX = componentX;
        this.posY = componentY;
        this.toggleAnimation.setEnd(1.0).setReversed(this.booleanSetting.getValue() == false).setEasing(100).update();
        Gui.drawRect(componentX, componentY, componentX + this.parent.width, componentY + componentHeight, this.getBackgroundColor());
        Gui.drawRect(componentX + this.parent.width - 14.0, componentY + 2.5, componentX + this.parent.width - 4.0, componentY + componentHeight - 2.5, -12434878);
        GL11.glPushMatrix();
        GL11.glTranslated(componentX + this.parent.width - 14.0 + 5.0, componentY + 2.5 + 5.0, 0.0);
        GL11.glScaled(this.toggleAnimation.getValue() / 100.0, this.toggleAnimation.getValue() / 100.0, 0.0);
        RenderUtils.drawImageWithAlpha(-5.0, -5.0, 10, 10, new ResourceLocation("kawaii/images/Check.png"), this.toggleAnimation.getValue() / 100.0);
        GL11.glTranslated(-(componentX + this.parent.width - 14.0), -(componentY + 2.5), 0.0);
        GL11.glPopMatrix();
        FontLoader.sf18.drawString(this.booleanSetting.getName(), componentX + 4.0, componentY + componentHeight / 2.0 - (double)((float)FontLoader.sf18.getHeight() / 2.0f), -1);
        return componentHeight;
    }
}

