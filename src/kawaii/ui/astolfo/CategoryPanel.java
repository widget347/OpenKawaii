/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.ui.astolfo.AstolfoPalette;
import kawaii.ui.astolfo.ClickGui;
import kawaii.ui.astolfo.ModuleButton;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;

public class CategoryPanel
implements AstolfoPalette {
    public double width;
    public boolean dragging;
    public ClickGui parentGui;
    public Category category;
    public double dragOffsetY;
    public double headerHeight;
    public ArrayList<ModuleButton> modulePanels = new ObjectList();
    public double contentHeight;
    public double posY;
    public double posX;
    public double dragOffsetX;

    public void onKeyTyped(char typedChar, int keyCode) {
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        for (ModuleButton moduleButton : this.modulePanels) {
            moduleButton.onMouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight);
    }

    public void drawPanel(int mouseX, int mouseY) {
        double d = 2.0;
        if (this.dragging) {
            this.posX = this.dragOffsetX + (double)mouseX;
            this.posY = this.dragOffsetY + (double)mouseY;
        }
        Gui.drawRect(this.posX - d, this.posY - d, this.posX + this.width + d, this.posY + this.contentHeight + this.headerHeight + d, this.category.getId());
        Gui.drawRect(this.posX - 1.5, this.posY - 1.5, this.posX + this.width + 1.5, this.posY + this.headerHeight + 1.5 + this.contentHeight, this.getAccentColor());
        FontLoader.sf20.drawString(this.category.getDisplayName().toLowerCase(), this.posX + 5.0, this.posY + this.headerHeight / d - (double)((float)FontLoader.sf20.getHeight() / 2.0f), -1);
        double moduleOffsetY = 0.0;
        for (ModuleButton moduleButton : this.modulePanels) {
            moduleOffsetY += moduleButton.drawModule(mouseX, mouseY, moduleOffsetY);
        }
        this.contentHeight = moduleOffsetY;
    }

    public boolean onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.dragging = this.isHovered(mouseX, mouseY);
        this.dragOffsetX = this.posX - (double)mouseX;
        this.dragOffsetY = this.posY - (double)mouseY;
        for (ModuleButton moduleButton : this.modulePanels) {
            moduleButton.onMouseClicked(mouseX, mouseY, mouseButton);
        }
        return this.dragging;
    }

    public CategoryPanel(double x, double y, ClickGui clickGui, Category category) {
        this.posX = x;
        this.posY = y;
        this.category = category;
        this.width = 115.0;
        this.parentGui = clickGui;
        this.headerHeight = 17.0;
        for (Module module : Kawaii.INSTANCE.getModuleManager().getModulesInCategory(category, FontLoader.popMed20)) {
            this.modulePanels.add(new ModuleButton(module, 20, this));
        }
    }
}

