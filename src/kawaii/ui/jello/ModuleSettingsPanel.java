/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import java.util.ArrayList;
import kawaii.module.Module;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.setting.Setting;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.CheckboxComponent;
import kawaii.ui.jello.ClickGui;
import kawaii.ui.jello.ModeComponent;
import kawaii.ui.jello.SettingComponent;
import kawaii.ui.jello.SliderComponent;
import kawaii.util.ObjectList;
import net.minecraft.client.renderer.GlStateManager;

public class ModuleSettingsPanel {
    public ArrayList<SettingComponent> components = new ObjectList();
    public double boxHeight;
    public Module module;
    public ClickGui screen;
    public boolean closing = false;
    public double boxY;
    public double boxWidth;
    public double boxX;
    public Animation animation = new Animation();

    public void handleMouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (SettingComponent settingComponent : this.components) {
            if (!settingComponent.handleMouseReleased(mouseX, mouseY, mouseButton)) continue;
            return;
        }
    }

    public double getBoxHeight() {
        return this.boxHeight;
    }

    public boolean isClosing() {
        return this.closing;
    }

    public Module getModule() {
        return this.module;
    }

    public double getBoxWidth() {
        return this.boxWidth;
    }

    public double getBoxX() {
        return this.boxX;
    }

    public Animation getAnimation() {
        return this.animation;
    }

    public ArrayList<SettingComponent> getComponents() {
        return this.components;
    }

    public ModuleSettingsPanel(Module module, ClickGui clickGui) {
        this.module = module;
        this.screen = clickGui;
        this.boxWidth = 500.0;
        this.boxHeight = clickGui.height - 100;
        this.boxY = 0.0;
        this.boxX = 0.0;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof ModeSetting) {
                this.components.add(new ModeComponent((ModeSetting)setting, this));
            }
            if (setting instanceof NumberSetting) {
                this.components.add(new SliderComponent((NumberSetting)setting, this));
            }
            if (setting instanceof MultiBooleanSetting) {
                for (BooleanSetting booleanSetting : ((MultiBooleanSetting)setting).getValues()) {
                    this.components.add(new CheckboxComponent(booleanSetting, this));
                }
            }
            if (!(setting instanceof BooleanSetting)) continue;
            this.components.add(new CheckboxComponent((BooleanSetting)setting, this));
        }
        this.animation.restart();
    }

    public ClickGui getScreen() {
        return this.screen;
    }

    public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (SettingComponent settingComponent : this.components) {
            if (!settingComponent.handleMouseClicked(mouseX, mouseY, mouseButton)) continue;
            return;
        }
    }

    public double getBoxY() {
        return this.boxY;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        float centerDivisor = 2.0f;
        this.animation.setReversed(this.closing).setEasing(200).update();
        if (this.closing && this.animation.isFinished()) {
            this.screen.closeSettings();
        }
        this.boxWidth = (float)this.screen.width / 5.0f + 100.0f;
        this.boxHeight = (float)this.screen.height / centerDivisor + 50.0f;
        this.boxX = (double)((float)this.screen.width / centerDivisor) - this.boxWidth / 2.0;
        this.boxY = (double)((float)this.screen.height / centerDivisor) - this.boxHeight / 2.0;
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)this.screen.width / centerDivisor, (float)this.screen.height / centerDivisor, 0.0f);
        GlStateManager.scale(this.animation.getValue() / 100.0, this.animation.getValue() / 100.0, 1.0);
        GlStateManager.translate(-((float)this.screen.width / centerDivisor), -((float)this.screen.height / centerDivisor), 0.0f);
        FontLoader.popMed40.drawString(this.module.getName(), this.boxX, this.boxY - 25.0, -1);
        RenderUtils.drawRoundedRect(this.boxX, this.boxY, this.boxWidth, this.boxHeight, 10.0, -1);
        FontLoader.sf20.drawString(this.module.getDescription(), this.boxX + 11.0, this.boxY + 15.0, -1895825408);
        double offsetY = 0.0;
        for (SettingComponent settingComponent : this.components) {
            if (((Setting)settingComponent.value).isVisible()) continue;
            offsetY += settingComponent.render(mouseX, mouseY, offsetY);
        }
        offsetY = 0.0;
        for (SettingComponent settingComponent : this.components) {
            if (((Setting)settingComponent.value).isVisible()) continue;
            offsetY += settingComponent.renderOptions(mouseX, mouseY, offsetY);
        }
        GlStateManager.scale(-(this.animation.getValue() / 100.0), -(this.animation.getValue() / 100.0), 1.0);
        GlStateManager.translate(-((float)this.screen.width / centerDivisor), -((float)this.screen.height / centerDivisor), 0.0f);
        GlStateManager.popMatrix();
    }
}

