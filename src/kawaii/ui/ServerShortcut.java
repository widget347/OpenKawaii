/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.ui.Animation;
import kawaii.ui.MainMenu;
import kawaii.ui.UiElement;
import kawaii.ui.font.FontLoader;
import kawaii.util.GuiUtil;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ServerShortcut
implements UiElement {
    public String address;
    public String name;
    public ResourceLocation icon;
    public double positionY;
    public Animation labelAnimation = new Animation();
    public double positionX;
    public double size;

    public void onClick(int mouseX, int mouseY, int mouseButton) {
        if (GuiUtil.isInside(mouseX, mouseY, this.positionX, this.positionY, this.positionX + this.size, this.positionY + this.size)) {
            mc.displayGuiScreen(new GuiConnecting(new MainMenu(), mc, new ServerData("server", this.address, false)));
        }
    }

    public double renderLabel(int mouseX, int mouseY, double iconX, double iconY, double iconSize) {
        double halfDivisor = 2.0;
        boolean hovered = GuiUtil.isInside(mouseX, mouseY, iconX, iconY, iconX + iconSize, iconY + iconSize);
        this.labelAnimation.setEnd(1.0).setEasing(400).setReversed(!hovered).update();
        GL11.glEnable(3089);
        RenderUtils.applyScissor(iconX + iconSize + 2.5, iconY + iconSize / halfDivisor - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - halfDivisor, iconX + iconSize + 2.5 + this.labelAnimation.getValue() / 100.0 * (double)(FontLoader.popMed20.getStringWidth(this.name) + 5), iconY + iconSize / halfDivisor - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - halfDivisor + (double)FontLoader.popMed20.getHeight() + halfDivisor);
        RenderUtils.drawRoundedRect(iconX + iconSize + 2.5, iconY + iconSize / halfDivisor - (double)((float)FontLoader.popMed20.getHeight() / 2.0f) - halfDivisor, FontLoader.popMed20.getStringWidth(this.name) + 5, FontLoader.popMed20.getHeight() + 2, 5.0, Integer.MIN_VALUE);
        FontLoader.popMed20.drawString(this.name, iconX + iconSize + 5.0, iconY + iconSize / halfDivisor - (double)((float)FontLoader.popMed20.getHeight() / 2.0f), -1);
        GL11.glDisable(3089);
        return iconSize;
    }

    public ServerShortcut(ResourceLocation iconLocation, String serverName, String serverAddress) {
        this.icon = iconLocation;
        this.name = serverName;
        this.address = serverAddress;
    }

    public double renderIcon(int mouseX, int mouseY, double iconX, double iconY, double iconSize) {
        this.positionX = iconX;
        this.positionY = iconY;
        this.size = iconSize;
        boolean hovered = GuiUtil.isInside(mouseX, mouseY, iconX, iconY, iconX + iconSize, iconY + iconSize);
        RenderUtils.drawRoundedRect(iconX + iconSize / 5.0 - 1.0, iconY + iconSize / 5.0 - 1.0, (int)(iconSize / 1.5) + 2, (int)(iconSize / 1.5) + 2, (int)(iconSize / 1.5), hovered ? -14671840 : -15592942);
        RenderUtils.drawImage(iconX + iconSize / 5.0, iconY + iconSize / 5.0, (int)(iconSize / 1.5), (int)(iconSize / 1.5), this.icon, new ColorWrapper(255, 255, 255, 255));
        return iconSize;
    }
}

