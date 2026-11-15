/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.ui.font.FontLoader;
import kawaii.ui.jello.ClickGui;
import kawaii.ui.jello.ModuleButton;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;

public class CategoryFrame {
    public ClickGui screen;
    public double posY;
    public double width;
    public double headerHeight;
    public ArrayList<ModuleButton> modules = new ObjectList();
    public double posX;
    public Category category;

    public double getPosY() {
        return this.posY;
    }

    public void render(int mouseX, int mouseY) {
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight, -135665175);
        FontLoader.sf24.drawString(this.category.getDisplayName(), this.posX + 10.0, this.posY + this.headerHeight / 2.0 - (double)((float)FontLoader.sf24.getHeight() / 2.0f), Integer.MIN_VALUE);
        this.headerHeight = 28.0;
        double moduleOffsetY = 0.0;
        for (ModuleButton moduleButton : this.modules) {
            moduleOffsetY += moduleButton.render(mouseX, mouseY, moduleOffsetY);
        }
    }

    public double getPosX() {
        return this.posX;
    }

    public ArrayList<ModuleButton> getModules() {
        return this.modules;
    }

    public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (ModuleButton moduleButton : this.modules) {
            moduleButton.handleMouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public CategoryFrame(int x, int y, Category category, ClickGui clickGui) {
        this.posX = x;
        this.posY = y;
        this.category = category;
        this.screen = clickGui;
        this.width = 110.0;
        this.headerHeight = 30.0;
        for (Module module : Kawaii.INSTANCE.getModuleManager().getModulesInCategory(category, FontLoader.sf20)) {
            this.modules.add(new ModuleButton(module, this));
        }
    }

    public void handleKeyTyped(char typedChar, int keyCode) {
        for (ModuleButton moduleButton : this.modules) {
            moduleButton.handleKeyTyped(typedChar, keyCode);
        }
    }

    public double getHeaderHeight() {
        return this.headerHeight;
    }

    public void handleMouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (ModuleButton moduleButton : this.modules) {
            moduleButton.handleMouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public double getWidth() {
        return this.width;
    }

    public ClickGui getScreen() {
        return this.screen;
    }

    public Category getCategory() {
        return this.category;
    }
}

