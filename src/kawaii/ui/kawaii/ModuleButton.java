/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

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
import kawaii.ui.kawaii.BooleanSettingComponent;
import kawaii.ui.kawaii.CategoryPanel;
import kawaii.ui.kawaii.ClickGui;
import kawaii.ui.kawaii.ModeSettingComponent;
import kawaii.ui.kawaii.MultiSettingComponent;
import kawaii.ui.kawaii.NumberSettingComponent;
import kawaii.ui.kawaii.SettingComponent;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;

public class ModuleButton {
    public double width;
    public Animation expandAnimation;
    public double posY;
    public double height;
    public ArrayList<SettingComponent> components = new ObjectList();
    public boolean expanded = false;
    public Module module;
    public CategoryPanel panel;
    public double settingsHeight = 0.0;
    public double posX;
    public boolean binding = false;

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.binding) {
            if (keyCode == 1) {
                this.binding = false;
                this.panel.gui.isBinding = false;
                return;
            }
            this.module.setKeyBind(keyCode);
            this.binding = false;
            this.panel.gui.isBinding = false;
        }
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.height)) {
            switch (mouseButton) {
                case 0: {
                    this.module.toggle();
                    break;
                }
                case 1: {
                    this.expanded = !this.expanded;
                    break;
                }
                case 2: {
                    this.binding = true;
                    this.panel.gui.isBinding = true;
                }
            }
        }
        if (this.expanded) {
            for (SettingComponent settingComponent : this.components) {
                settingComponent.mouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public ModuleButton(double buttonHeight, CategoryPanel categoryPanel, Module module) {
        this.expandAnimation = new Animation();
        this.height = buttonHeight;
        this.panel = categoryPanel;
        this.module = module;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof BooleanSetting) {
                this.components.add(new BooleanSettingComponent(setting, this));
            }
            if (setting instanceof ModeSetting) {
                this.components.add(new ModeSettingComponent(setting, this));
            }
            if (setting instanceof NumberSetting) {
                this.components.add(new NumberSettingComponent(setting, this));
            }
            if (!(setting instanceof MultiBooleanSetting)) continue;
            this.components.add(new MultiSettingComponent(setting, this));
        }
    }

    public double renderButton(int mouseX, int mouseY, double offsetY, boolean useScissor) {
        this.posX = this.panel.posX;
        this.posY = this.panel.posY + offsetY + this.panel.headerHeight;
        this.width = this.panel.width;
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, this.module.isEnabled() ? ClickGui.getEnabledColor() : ClickGui.getDisabledColor());
        FontLoader.popMed20.drawString(this.binding ? "Binding..." : this.module.getName(), this.posX + 5.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf24.getHeight() / 2.0f), -1);
        if (this.components.size() > 0) {
            FontLoader.popMed20.drawString(this.expanded ? "-" : "+", this.posX - 5.0 + this.width - (double)FontLoader.sf24.getStringWidth(this.expanded ? "-" : "+"), this.posY + this.height / 2.0 - (double)((float)FontLoader.sf24.getHeight() / 2.0f), -1);
        }
        double componentsHeight = 0.0;
        this.expandAnimation.setEasing(200).setEnd(1.0).setReversed(!this.expanded).update();
        if (useScissor) {
            RenderUtils.applyScissor(this.posX, this.posY, this.posX + this.width, this.posY + 1.0 + this.height + this.expandAnimation.getValue() / 100.0 * this.settingsHeight);
        }
        for (SettingComponent settingComponent : this.components) {
            if (settingComponent.setting.isVisible()) continue;
            componentsHeight += settingComponent.render(mouseX, mouseY, this.posX, this.posY + this.height + componentsHeight);
        }
        double previousSettingsHeight = this.settingsHeight;
        this.settingsHeight = componentsHeight;
        return this.height + this.expandAnimation.getValue() / 100.0 * this.settingsHeight;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.expanded) {
            for (SettingComponent settingComponent : this.components) {
                settingComponent.mouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }
}

