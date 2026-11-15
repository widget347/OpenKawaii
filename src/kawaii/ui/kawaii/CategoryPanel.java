/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import kawaii.ui.kawaii.ClickGui;
import kawaii.ui.kawaii.ModuleButton;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class CategoryPanel {
    public double width;
    public double contentHeight;
    public Category category;
    public ClickGui gui;
    public Boolean dragging = false;
    public double dragOffsetY = 0.0;
    public double headerHeight;
    public double posX;
    public double posY;
    public boolean expanded = true;
    public Animation expandAnimation;
    public double dragOffsetX = 0.0;
    public ArrayList<ModuleButton> moduleButtons = new ObjectList();

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.dragging = false;
        if (this.expanded) {
            for (ModuleButton moduleButton : this.moduleButtons) {
                moduleButton.onMouseReleased(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight)) {
            switch (mouseButton) {
                case 0: {
                    this.dragging = true;
                    this.dragOffsetX = (double)mouseX - this.posX;
                    this.dragOffsetY = (double)mouseY - this.posY;
                    break;
                }
                case 1: {
                    boolean nowExpanded = this.expanded = !this.expanded;
                }
            }
        }
        if (this.expanded) {
            for (ModuleButton moduleButton : this.moduleButtons) {
                moduleButton.onMouseClicked(mouseX, mouseY, mouseButton);
            }
        }
    }

    public void onKeyTyped(char typedChar, int keyCode) {
        if (this.expanded) {
            for (ModuleButton moduleButton : this.moduleButtons) {
                moduleButton.onKeyTyped(typedChar, keyCode);
            }
        }
    }

    public CategoryPanel(int startX, int startY, Category category, ClickGui clickGui) {
        this.expandAnimation = new Animation();
        this.posX = startX;
        this.posY = startY;
        this.category = category;
        this.gui = clickGui;
        this.width = 120.0;
        this.headerHeight = 15.0;
        int buttonCount = 0;
        for (Module module : Kawaii.INSTANCE.getModuleManager().getModulesInCategory(category, FontLoader.sf24)) {
            this.moduleButtons.add(new ModuleButton(20.0, this, module));
            ++buttonCount;
        }
    }

    public void render(int mouseX, int mouseY) {
        if (this.dragging.booleanValue()) {
            this.posX = (double)mouseX - this.dragOffsetX;
            this.posY = (double)mouseY - this.dragOffsetY;
        }
        Gui.drawRect(this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight, ClickGui.getEnabledColor());
        FontLoader.popBold20.drawString(this.category.getDisplayName(), this.posX + 2.0, this.posY + this.headerHeight / 2.0 - ((double)((float)FontLoader.sf24.getHeight() / 2.0f) - 0.5), -1);
        double renderedHeight = 0.0;
        this.expandAnimation.setEnd(1.0).setReversed(!this.expanded).setEasing(200).update();
        GL11.glEnable(3089);
        RenderUtils.applyScissor(this.posX, this.posY, this.posX + this.width, this.posY + this.headerHeight + this.expandAnimation.getValue() / 100.0 * this.contentHeight);
        for (ModuleButton moduleButton : this.moduleButtons) {
            renderedHeight += moduleButton.renderButton(mouseX, mouseY, renderedHeight, !this.expandAnimation.isFinished());
        }
        this.contentHeight = renderedHeight;
        GL11.glDisable(3089);
        RenderUtils.drawRoundedOutline(this.posX, this.posY, this.width, this.headerHeight + renderedHeight + 1.0, 2.0, 4.0f, ClickGui.getEnabledColor());
        RenderUtils.drawRoundedOutline(this.posX + 1.0, this.posY + this.headerHeight, this.width - 2.0, renderedHeight, 0.0, 1.0f, new ColorWrapper(ClickGui.getEnabledColor()).darkerColor().getRGB());
    }
}

