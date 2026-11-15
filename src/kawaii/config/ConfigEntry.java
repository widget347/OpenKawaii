/*
 * Decompiled with CFR 0.152.
 */
package kawaii.config;

import kawaii.config.ConfigFile;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.ui.ConfigManagerGui;
import kawaii.ui.font.FontLoader;
import kawaii.util.CustomDate;
import kawaii.util.DateUtils;
import kawaii.util.GuiUtil;
import net.minecraft.util.ResourceLocation;

public class ConfigEntry {
    public double width;
    public double x;
    public double y;
    public double height;
    public ConfigFile configFile;
    public ConfigManagerGui screen;

    public void render(double renderX, double renderY, int mouseX, int mouseY) {
        this.x = renderX;
        this.y = renderY;
        this.width = 150.0;
        RenderUtils.drawRoundedRect(renderX, renderY, this.width, this.height, 2.0, -15132391);
        FontLoader.popMed20.drawString(this.configFile.getName(), renderX + 5.0, renderY + this.height / 2.0 - (double)((float)FontLoader.popMed25.getHeight() / 2.0f) - 1.0, -1);
        FontLoader.sf18.drawString("Last Saved: " + DateUtils.formatDate(new CustomDate(this.configFile.getFile().lastModified())), renderX + 5.0, renderY + this.height - (double)FontLoader.sf18.getHeight() - 2.0, -2130706433);
        if (this.screen.getSelectedConfig() == this) {
            RenderUtils.drawImage(renderX + this.width - 12.0, renderY + 2.0, 10, 10, new ResourceLocation("kawaii/images/Check.png"), new ColorWrapper(-11206782));
        }
    }

    public ConfigFile getConfigFile() {
        return this.configFile;
    }

    public ConfigManagerGui getScreen() {
        return this.screen;
    }

    public boolean handleClick(int mouseX, int mouseY, int mouseButton) {
        return GuiUtil.isInside(mouseX, mouseY, this.x, this.y, this.x + this.width, this.y + this.height);
    }

    public double getY() {
        return this.y;
    }

    public ConfigEntry(ConfigFile configFile, ConfigManagerGui configScreen) {
        this.configFile = configFile;
        this.y = 0.0;
        this.x = 0.0;
        this.width = 75.0;
        this.height = 25.0;
        this.screen = configScreen;
    }

    public double getHeight() {
        return this.height;
    }

    public double getX() {
        return this.x;
    }

    public double getWidth() {
        return this.width;
    }
}

