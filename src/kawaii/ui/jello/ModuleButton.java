/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import kawaii.module.Module;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.CategoryFrame;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;

public class ModuleButton {
    public Animation toggleAnimation;
    public double posX;
    public double posY;
    public CategoryFrame parent;
    public double height;
    public Module module;

    public double getY() {
        return this.posY;
    }

    public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        switch (mouseButton) {
            case 0: {
                if (!GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.getWidth(), this.posY + this.height)) break;
                this.module.toggle();
                break;
            }
            case 1: {
                if (!GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.getWidth(), this.posY + this.height)) break;
                this.parent.getScreen().openSettings(this.module);
            }
        }
    }

    public void handleKeyTyped(char typedChar, int keyCode) {
    }

    public double getX() {
        return this.posX;
    }

    public Animation getToggleAnimation() {
        return this.toggleAnimation;
    }

    public CategoryFrame getParent() {
        return this.parent;
    }

    public ModuleButton(Module module, CategoryFrame categoryFrame) {
        this.module = module;
        this.height = 15.0;
        this.toggleAnimation = new Animation();
        this.parent = categoryFrame;
    }

    public double render(int mouseX, int mouseY, double offsetY) {
        this.posX = this.parent.getPosX();
        this.posY = this.parent.getPosY() + offsetY + this.parent.getHeaderHeight();
        boolean hovered = !this.parent.getScreen().isSettingsOpen() && GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.parent.getWidth(), this.posY + this.height);
        boolean enabled = this.module.isEnabled();
        this.toggleAnimation.setReversed(!enabled).setEasing(100).update();
        Gui.drawRect(this.posX, this.posY, this.posX + this.parent.getWidth(), this.posY + this.height, enabled ? (hovered ? -9194241 : -12871937) : (hovered ? -34607121 : -1));
        this.height = 18.0;
        FontLoader.sf20.drawString(this.module.getName(), this.posX + 10.0 + this.toggleAnimation.getValue() / 100.0 * 5.0, this.posY + this.height / 2.0 - (double)((float)FontLoader.sf20.getHeight() / 2.0f), enabled ? -1 : -822083584);
        return this.height;
    }

    public void handleMouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public double getHeight() {
        return this.height;
    }

    public Module getModule() {
        return this.module;
    }
}

