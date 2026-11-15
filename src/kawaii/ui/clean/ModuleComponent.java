/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import java.util.ArrayList;
import kawaii.module.Module;
import kawaii.render.ColorThemes;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.setting.Setting;
import kawaii.ui.Animation;
import kawaii.ui.clean.CategoryPanel;
import kawaii.ui.clean.CheckboxComponent;
import kawaii.ui.clean.CleanPalette;
import kawaii.ui.clean.ModeComponent;
import kawaii.ui.clean.SettingComponent;
import kawaii.ui.clean.SliderComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ModuleComponent
implements CleanPalette {
    public boolean settingsOpen = false;
    public ArrayList<SettingComponent> settingComponents = new ObjectList();
    public double posY;
    public CategoryPanel categoryPanel;
    public Animation settingsAnimation = new Animation();
    public Module module;
    public double height;
    public double settingsHeight = 0.0;
    public double posX;
    public double width;
    public Animation arrowAnimation = new Animation();

    public void mouseReleased(int cursorX, int cursorY, int mouseButton) {
        if (this.settingsOpen) {
            for (SettingComponent settingComponent : this.settingComponents) {
                settingComponent.mouseReleased(cursorX, cursorY, mouseButton);
            }
        }
    }

    public double render(int cursorX, int cursorY, double yOffset) {
        double animationMax = 100.0;
        double padding = 5.0;
        float halfScale = 2.0f;
        double scaleFactor = 2.0;
        this.settingsAnimation.setEasing(200).setReversed(!this.settingsOpen).update();
        this.arrowAnimation.setEasing(200).setReversed(!this.settingsOpen).update();
        this.posY = this.categoryPanel.posY + this.categoryPanel.headerHeight + yOffset;
        this.posX = this.categoryPanel.posX;
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, this.getBackgroundColor());
        FontLoader.popMed20.drawString(this.module.getName(), this.posX + padding, this.posY + this.height / scaleFactor - (double)((float)FontLoader.popMed20.getHeight() / halfScale), this.module.isEnabled() ? ColorThemes.getStaticThemeColor((int)this.posY) : -1);
        if (this.settingComponents.size() > 0) {
            GlStateManager.pushMatrix();
            GL11.glTranslated(this.posX + this.width - padding - (double)((float)FontLoader.popMed20.getStringWidth("v") / halfScale) - padding, this.posY + this.height / scaleFactor - (double)((float)FontLoader.popMed20.getHeight() / halfScale) + (double)((float)FontLoader.popMed20.getHeight() / halfScale), 0.0);
            if (this.arrowAnimation.getValue() / animationMax * scaleFactor - 1.0 < 0.0) {
                GL11.glRotatef(180.0f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glScaled(1.0, Math.abs((float)(this.arrowAnimation.getValue() / animationMax * scaleFactor) - 1.0f), 1.0);
            FontLoader.popMed20.drawString("v", (float)(-FontLoader.popMed20.getStringWidth("v")) / halfScale, (float)(-FontLoader.popMed20.getHeight()) / halfScale, -1);
            GL11.glScaled(1.0, 1.0, 1.0);
            if (this.arrowAnimation.getValue() / animationMax * scaleFactor - 1.0 < 0.0) {
                GL11.glRotatef(-180.0f, 0.0f, 0.0f, 1.0f);
            }
            GL11.glTranslated(-(this.posX + this.width - padding - (double)((float)FontLoader.popMed20.getStringWidth("v") / halfScale) - padding), -(this.posY + this.height / scaleFactor - (double)((float)FontLoader.popMed20.getHeight() / halfScale) + (double)((float)FontLoader.popMed20.getHeight() / halfScale)), 0.0);
            GlStateManager.popMatrix();
        }
        double totalSettingsHeight = 0.0;
        Gui.drawRect(this.posX, this.posY + this.height, this.posX + this.width, this.posY + this.height + this.settingsAnimation.getValue() / animationMax * this.settingsHeight, this.getAccentColor());
        GL11.glEnable(3089);
        RenderUtils.applyScissor(this.posX, this.posY + this.height - 1.0, this.posX + this.width, this.posY + this.height + this.settingsAnimation.getValue() / animationMax * this.settingsHeight);
        for (SettingComponent settingComponent : this.settingComponents) {
            if (((Setting)settingComponent.setting).isVisible()) continue;
            totalSettingsHeight += settingComponent.render(cursorX, cursorY, totalSettingsHeight);
        }
        GL11.glDisable(3089);
        this.settingsHeight = totalSettingsHeight;
        return this.height + this.settingsAnimation.getValue() / animationMax * this.settingsHeight;
    }

    public ModuleComponent(Module module, int componentHeight, CategoryPanel categoryPanel) {
        this.module = module;
        this.height = componentHeight;
        this.categoryPanel = categoryPanel;
        this.width = this.categoryPanel.width;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof ModeSetting) {
                this.settingComponents.add(new ModeComponent((ModeSetting)setting, this));
            }
            if (setting instanceof NumberSetting) {
                this.settingComponents.add(new SliderComponent((NumberSetting)setting, this));
            }
            if (!(setting instanceof BooleanSetting)) continue;
            this.settingComponents.add(new CheckboxComponent((BooleanSetting)setting, this));
        }
    }

    public void mouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (GuiUtil.isInside(cursorX, cursorY, this.posX, this.posY, this.posX + this.width, this.posY + this.height)) {
            switch (mouseButton) {
                case 0: {
                    this.module.toggle();
                    break;
                }
                case 1: {
                    this.settingsOpen = !this.settingsOpen;
                }
            }
        } else if (this.settingsOpen) {
            for (SettingComponent settingComponent : this.settingComponents) {
                if (((Setting)settingComponent.setting).isVisible()) continue;
                settingComponent.mouseClicked(cursorX, cursorY, mouseButton);
            }
        }
    }
}

