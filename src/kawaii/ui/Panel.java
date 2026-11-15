/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.ArrayList;
import java.util.Arrays;
import kawaii.render.ColorThemes;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.ServerShortcut;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import kawaii.util.ObjectList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Panel {
    public ArrayList<ServerShortcut> children;
    public Animation labelAnimation = new Animation();
    public double positionY;
    public ResourceLocation icon;
    public double positionX;
    public double size;
    public Animation expandAnimation = new Animation();
    public String name;
    public boolean expanded;

    public void render(double x, double y, int mouseX, int mouseY) {
        double padding = 2.0;
        this.positionX = x;
        this.positionY = y;
        boolean hovered = GuiUtil.isInside(mouseX, mouseY, x, y, x + this.size, y + this.size);
        this.expandAnimation.setEnd(1.0).setEasing(400).setReversed(!this.expanded).update();
        RenderUtils.drawRoundedRect(x, y, this.size, this.size + this.expandAnimation.getValue() / 100.0 * ((double)this.children.size() * this.size), this.size, Integer.MIN_VALUE);
        RenderUtils.drawImage(x + this.size / padding / padding, y + this.size / padding / padding, (int)(this.size / 1.75), (int)(this.size / 1.75), this.icon, hovered ? new ColorWrapper(ColorThemes.getStaticThemeColor((int)y)) : new ColorWrapper(255, 255, 255, 255));
        this.labelAnimation.setEnd(1.0).setReversed(!hovered).setEasing(400).update();
        GL11.glEnable(3089);
        RenderUtils.applyScissor(x, y, this.size + padding, this.size + this.expandAnimation.getValue() / 100.0 * ((double)this.children.size() * this.size) + padding);
        double rowIndex = 0.0;
        for (ServerShortcut serverShortcut : this.children) {
            serverShortcut.renderIcon(mouseX, mouseY, x, y + this.size + rowIndex * this.size, this.size);
            rowIndex += 1.0;
        }
        GL11.glDisable(3089);
        rowIndex = 0.0;
        if (this.expanded) {
            for (ServerShortcut serverShortcut : this.children) {
                serverShortcut.renderLabel(mouseX, mouseY, x, y + this.size + rowIndex * this.size, this.size);
                rowIndex += 1.0;
            }
        }
        GL11.glEnable(3089);
        RenderUtils.applyScissor(x + this.size + 2.5, y + this.size / padding - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - padding, x + this.size + 2.5 + this.labelAnimation.getValue() / 100.0 * (double)(FontLoader.popMed20.getStringWidth(this.name) + 5), y + this.size / padding - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - padding + (double)FontLoader.popMed20.getHeight() + padding);
        RenderUtils.drawRoundedRect(x + this.size + 2.5, y + this.size / padding - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - padding, FontLoader.popMed20.getStringWidth(this.name) + 5, FontLoader.popMed20.getHeight() + 2, 5.0, Integer.MIN_VALUE);
        FontLoader.popMed20.drawString(this.name, x + this.size + 5.0, y + this.size / padding - (double)((float)FontLoader.popMed20.getHeight() / 2.0f), -1);
        GL11.glDisable(3089);
    }

    public Panel(ResourceLocation resourceLocation, double panelSize, String panelName, ServerShortcut ... shortcuts) {
        this.children = new ObjectList(Arrays.asList(shortcuts));
        this.name = panelName;
        this.size = panelSize;
        this.icon = resourceLocation;
    }

    public void handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.positionX, this.positionY, this.positionX + this.size, this.positionY + this.size)) {
            this.expanded = !this.expanded;
        }
        for (ServerShortcut serverShortcut : this.children) {
            if (!this.expanded) continue;
            serverShortcut.onClick(mouseX, mouseY, mouseButton);
        }
    }
}

