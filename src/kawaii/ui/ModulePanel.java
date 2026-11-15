/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.module.Module;
import kawaii.render.RenderUtils;
import kawaii.ui.PanelRegistry;
import kawaii.util.GuiUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ModulePanel {
    public double y;
    public int offsetX = 0;
    public boolean visible = true;
    public int positionX;
    public double dragOffsetX;
    public int positionY;
    public Module module;
    public double x;
    public double dragOffsetY;
    public int offsetY = 0;
    public Boolean enabled = false;
    public String name;
    public boolean dragging;

    public boolean isVisible() {
        return this.visible;
    }

    public ModulePanel(String panelName, Module module, int panelX, int panelY, double width, double height) {
        this.module = module;
        this.positionX = panelX;
        this.positionY = panelY;
        this.x = width;
        this.y = height;
        this.name = panelName;
        PanelRegistry.register(this);
    }

    public void beginRender() {
        this.enabled = this.module != null && !this.module.isEnabled();
        if (this.enabled.booleanValue()) {
            return;
        }
        GlStateManager.pushMatrix();
        if (this.visible) {
            GL11.glEnable(3089);
        }
        RenderUtils.applyScissor((float)this.positionX + (float)this.offsetX, (float)this.positionY + (float)this.offsetY, (float)((double)this.positionX + this.x + (double)this.offsetX), (float)((double)this.positionY + this.y + (double)this.offsetY));
        GlStateManager.translate(this.positionX, this.positionY, 0.0f);
    }

    public void setPosition(double newX, double newY) {
        this.x = newX;
        this.y = newY;
    }

    public int getOffsetX() {
        return this.offsetX;
    }

    public boolean isHovered(int cursorX, int cursorY) {
        this.enabled = this.module != null && !this.module.isEnabled();
        if (this.enabled.booleanValue()) {
            return false;
        }
        if (this.dragging) {
            this.positionX = cursorX - (int)this.dragOffsetX;
            this.positionY = cursorY - (int)this.dragOffsetY;
        }
        if (GuiUtil.isInside(cursorX, cursorY, this.positionX + this.offsetX, this.positionY + this.offsetY, (double)this.positionX + this.x + (double)this.offsetX, (double)this.positionY + this.y + (double)this.offsetY)) {
            Gui.drawRect(this.positionX + this.offsetX, this.positionY + this.offsetY, (double)this.positionX + this.x + (double)this.offsetX, (double)this.positionY + this.y + (double)this.offsetY, Integer.MIN_VALUE);
            return true;
        }
        return false;
    }

    public int getPositionX() {
        return this.positionX;
    }

    public String getName() {
        return this.name;
    }

    public int getPositionY() {
        return this.positionY;
    }

    public double getHeight() {
        return this.y;
    }

    public boolean mouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (this.enabled.booleanValue()) {
            return false;
        }
        if (GuiUtil.isInside(cursorX, cursorY, this.positionX + this.offsetX, this.positionY + this.offsetY, (double)this.positionX + this.x + (double)this.offsetX, (double)this.positionY + this.y + (double)this.offsetY)) {
            if (mouseButton == 0) {
                this.dragOffsetX = cursorX - this.positionX;
                this.dragOffsetY = cursorY - this.positionY;
                this.dragging = true;
            }
            return true;
        }
        return false;
    }

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void endRender() {
        if (this.enabled.booleanValue()) {
            return;
        }
        if (this.visible) {
            GL11.glDisable(3089);
        }
        GlStateManager.translate(-this.positionX, -this.positionY, 0.0f);
        GlStateManager.popMatrix();
    }

    public double getWidth() {
        return this.x;
    }

    public double getDragOffsetY() {
        return this.dragOffsetY;
    }

    public int getOffsetY() {
        return this.offsetY;
    }

    public void mouseReleased(int cursorX, int cursorY, int mouseButton) {
        this.dragging = false;
    }

    public boolean isDragging() {
        return this.dragging;
    }

    public double getDragOffsetX() {
        return this.dragOffsetX;
    }

    public void setVisible(boolean visibleState) {
        this.visible = visibleState;
    }

    public ModulePanel(String panelName, int panelX, int panelY, double width, double height, int initialOffsetX, int initialOffsetY) {
        this.name = panelName;
        this.offsetX = initialOffsetX;
        this.offsetY = initialOffsetY;
        this.positionX = panelX;
        this.positionY = panelY;
        this.x = width;
        this.y = height;
        PanelRegistry.register(this);
    }

    public Module getModule() {
        return this.module;
    }
}

