/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import java.util.ArrayList;
import kawaii.module.Module;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.setting.Setting;
import kawaii.ui.Animation;
import kawaii.ui.astolfo.AstolfoPalette;
import kawaii.ui.astolfo.BooleanComponent;
import kawaii.ui.astolfo.CategoryPanel;
import kawaii.ui.astolfo.ModeComponent;
import kawaii.ui.astolfo.SettingComponent;
import kawaii.ui.astolfo.SliderComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ModuleButton
implements AstolfoPalette {
    public Module module;
    public boolean expanded = false;
    public CategoryPanel categoryPanel;
    public Animation expandAnimation;
    public Animation hoverAnimation;
    public double height;
    public double width;
    public double posX;
    public double settingsHeight = 0.0;
    public double posY;
    public ArrayList<SettingComponent> components = new ObjectList();

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.height)) {
            switch (mouseButton) {
                case 0: {
                    this.module.toggle();
                    break;
                }
                case 1: {
                    if (this.components.size() <= 0) break;
                    this.expanded = !this.expanded;
                }
            }
        } else if (this.expanded) {
            for (SettingComponent settingComponent : this.components) {
                if (((Setting)settingComponent.setting).isVisible()) continue;
                settingComponent.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public double drawModule(int mouseX, int mouseY, double yOffset) {
        this.expandAnimation.setEasing(200).setReversed(!this.expanded).update();
        this.posY = this.categoryPanel.posY + this.categoryPanel.headerHeight + yOffset;
        boolean hovered = GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.height);
        this.posX = this.categoryPanel.posX;
        this.height = 17.0;
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.height, this.module.isEnabled() ? (hovered ? new ColorWrapper(this.categoryPanel.category.getId()).darkerColor().getRGB() : this.categoryPanel.category.getId()) : (hovered ? new ColorWrapper(this.getDisabledColor()).brighterColor().getRGB() : this.getDisabledColor()));
        FontLoader.sf20.drawString(this.module.getName().toLowerCase(), this.posX + 3.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f), -1);
        if (this.module.getKeyBind() != 0) {
            FontLoader.sf20.drawString("[" + Keyboard.getKeyName(this.module.getKeyBind()) + "]", this.posX + this.width - 3.0 - (double)FontLoader.sf20.getStringWidth("[" + Keyboard.getKeyName(this.module.getKeyBind()) + "]"), this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f), -7237231);
        }
        double settingsDrawnHeight = 0.0;
        Gui.drawRect(this.posX, this.posY + this.height, this.posX + this.width, this.posY + this.height + this.expandAnimation.getValue() / 100.0 * this.settingsHeight, this.getAccentColor());
        GL11.glEnable(3089);
        RenderUtils.applyScissor(this.posX, this.posY + this.height - 1.0, this.posX + this.width, this.posY + this.height + this.expandAnimation.getValue() / 100.0 * this.settingsHeight);
        for (SettingComponent settingComponent : this.components) {
            if (((Setting)settingComponent.setting).isVisible()) continue;
            settingsDrawnHeight += settingComponent.drawComponent(mouseX, mouseY, settingsDrawnHeight);
        }
        GL11.glDisable(3089);
        this.settingsHeight = settingsDrawnHeight;
        return this.height + this.expandAnimation.getValue() / 100.0 * this.settingsHeight;
    }

    public ModuleButton(Module module, int buttonHeight, CategoryPanel categoryPanel) {
        this.expandAnimation = new Animation();
        this.hoverAnimation = new Animation();
        this.module = module;
        this.height = buttonHeight;
        this.categoryPanel = categoryPanel;
        this.width = this.categoryPanel.width;
        for (Setting setting : module.getSettings()) {
            if (setting instanceof ModeSetting) {
                this.components.add(new ModeComponent((ModeSetting)setting, this));
            }
            if (setting instanceof NumberSetting) {
                this.components.add(new SliderComponent((NumberSetting)setting, this));
            }
            if (setting instanceof BooleanSetting) {
                this.components.add(new BooleanComponent((BooleanSetting)setting, this));
            }
            if (!(setting instanceof MultiBooleanSetting)) continue;
            BooleanSetting[] booleanSettingArray = ((MultiBooleanSetting)setting).getValues();
            int booleanCount = booleanSettingArray.length;
            for (int i = 0; i < booleanCount; ++i) {
                BooleanSetting booleanSetting = booleanSettingArray[i];
                this.components.add(new BooleanComponent(booleanSetting, this));
            }
        }
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.expanded) {
            for (SettingComponent settingComponent : this.components) {
                settingComponent.onMouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }
}

