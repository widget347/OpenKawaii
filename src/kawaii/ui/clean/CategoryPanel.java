/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.ui.clean.CleanPalette;
import kawaii.ui.clean.ClickGui;
import kawaii.ui.clean.ModuleComponent;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;

public class CategoryPanel
implements CleanPalette {
    public boolean dragging;
    public ArrayList<ModuleComponent> moduleComponents = new ObjectList();
    public ClickGui parentScreen;
    public double posY;
    public double headerHeight;
    public Category category;
    public double width;
    public double dragOffsetY;
    public double dragOffsetX;
    public double posX;

    public boolean isMouseOver(int mouseX, int mouseY) {
        return GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight);
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        for (ModuleComponent moduleComponent : this.moduleComponents) {
            moduleComponent.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.dragging = this.isMouseOver(mouseX, mouseY);
        this.dragOffsetX = this.posX - (double)mouseX;
        this.dragOffsetY = this.posY - (double)mouseY;
        for (ModuleComponent moduleComponent : this.moduleComponents) {
            moduleComponent.mouseClicked(mouseX, mouseY, mouseButton);
        }
        return this.dragging;
    }

    public CategoryPanel(double startX, double startY, ClickGui clickGui, Category category) {
        this.posX = startX;
        this.posY = startY;
        this.category = category;
        this.width = 125.0;
        this.parentScreen = clickGui;
        this.headerHeight = 20.0;
        for (Module module : Kawaii.INSTANCE.getModuleManager().getModulesInCategory(category, FontLoader.popMed20)) {
            this.moduleComponents.add(new ModuleComponent(module, 20, this));
        }
    }

    public void renderPanel(int mouseX, int mouseY) {
        if (this.dragging) {
            this.posX = this.dragOffsetX + (double)mouseX;
            this.posY = this.dragOffsetY + (double)mouseY;
        }
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight, this.getAccentColor());
        FontLoader.popMed25.drawCenteredString(this.category.getDisplayName(), this.posX + this.width / 2.0, this.posY + this.headerHeight / 2.0 - (double)((float)FontLoader.popMed25.getHeight() / 2.0f), -1);
        double moduleOffsetY = 0.0;
        for (ModuleComponent moduleComponent : this.moduleComponents) {
            moduleOffsetY += moduleComponent.render(mouseX, mouseY, moduleOffsetY);
        }
    }
}

