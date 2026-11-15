/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.config.ConfigEntry;
import kawaii.config.ConfigFile;
import kawaii.render.RenderUtils;
import kawaii.ui.Button;
import kawaii.ui.CheckBox;
import kawaii.ui.GuiTheme;
import kawaii.ui.Widget;
import kawaii.ui.WidgetRegistry;
import kawaii.ui.font.FontLoader;
import kawaii.util.ObjectList;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ConfigManagerGui
extends GuiScreen
implements WidgetRegistry {
    public double scrollOffset = 0.0;
    public ArrayList<ConfigEntry> configEntries = new ObjectList();
    public double headerHeight;
    public CheckBox keybindsToggle = new CheckBox("Load Keybinds", 100.0, 20.0);
    public double panelWidth = 200.0;
    public double panelX;
    public ConfigEntry selectedConfig = null;
    public double contentHeight;
    public double panelY;
    public double panelHeight = 125.0;

    public double getContentHeight() {
        return this.contentHeight;
    }

    public double getPanelY() {
        return this.panelY;
    }

    public ArrayList<ConfigEntry> getConfigEntries() {
        return this.configEntries;
    }

    @Override
    public void handleMouseInput() {
        super.handleMouseInput();
        int wheelDelta = Mouse.getEventDWheel();
        if (wheelDelta != 0) {
            if (wheelDelta > 1) {
                wheelDelta = 1;
            }
            if (wheelDelta < -1) {
                wheelDelta = -1;
            }
            if ((wheelDelta *= 7) > 0 ? this.scrollOffset + (double)wheelDelta < 5.0 : this.scrollOffset + (double)wheelDelta > -(this.contentHeight - 100.0)) {
                this.scrollOffset += (double)wheelDelta;
            }
        }
    }

    public ConfigEntry getSelectedConfig() {
        return this.selectedConfig;
    }

    public double getPanelHeight() {
        return this.panelHeight;
    }

    public void reloadConfigs() {
        this.configEntries.clear();
        for (ConfigFile configFile : Kawaii.INSTANCE.getConfigManager().getConfigs()) {
            if (configFile.getName().contains("$default")) continue;
            this.configEntries.add(new ConfigEntry(configFile, this));
            System.out.println(configFile.getName());
        }
    }

    public void loadSelected() {
        Kawaii.INSTANCE.getConfigManager().deleteConfig(this.selectedConfig.getConfigFile().getName());
        this.reloadConfigs();
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    public void loadSelectedKeybinds() {
        Kawaii.INSTANCE.getConfigManager().loadConfig(this.selectedConfig.getConfigFile().getName(), this.keybindsToggle.isChecked());
    }

    public double getHeaderHeight() {
        return this.headerHeight;
    }

    public double getScrollOffset() {
        return this.scrollOffset;
    }

    @Override
    public void onClear() {
        entries.add(new Button("Load Selected", 100.0, 20.0, GuiTheme.darkFlat, this::loadSelectedKeybinds));
        entries.add(this.keybindsToggle);
        entries.add(new Button("Delete", 75.0, 20.0, GuiTheme.darkFlat, this::loadSelected));
    }

    public ConfigManagerGui() {
        this.reloadConfigs();
        this.clearAll();
    }

    public double getPanelX() {
        return this.panelX;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        double padding = 5.0;
        this.panelWidth = 350.0;
        this.panelHeight = 200.0;
        this.headerHeight = 25.0;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        this.panelX = (double)((float)scaledResolution.getScaledWidth() / 2.0f) - this.panelWidth / 2.0;
        this.panelY = (double)((float)scaledResolution.getScaledHeight() / 2.0f) - this.panelHeight / 2.0;
        RenderUtils.drawRoundedRect(this.panelX, this.panelY, this.panelWidth, this.panelHeight, 10.0, -15395563);
        Gui.drawRect(this.panelX, this.panelY + this.headerHeight, this.panelX + this.panelWidth, this.panelY + this.panelHeight, -14474461);
        FontLoader.popMed30.drawString("Config Manager", this.panelX + padding, this.panelY + this.headerHeight / 2.0 - (double)((float)FontLoader.popMed30.getHeight() / 2.0f), -1);
        int index = 0;
        this.contentHeight = 0.0;
        GL11.glEnable(3089);
        RenderUtils.applyScissor(this.panelX, this.panelY + this.headerHeight, this.panelX + this.panelWidth, this.panelY + this.panelHeight);
        for (ConfigEntry configEntry : this.configEntries) {
            configEntry.render(this.panelX + (index % 2 != 0 ? this.panelWidth - configEntry.getWidth() - 15.0 : 15.0), this.panelY + this.scrollOffset + (double)((int)((float)index / 2.0f)) * (configEntry.getHeight() + padding) + this.headerHeight + padding, mouseX, mouseY);
            ++index;
            this.contentHeight += configEntry.getHeight() + padding;
        }
        this.contentHeight /= 2.0;
        GL11.glDisable(3089);
        Gui.drawRect(this.panelX, this.panelY + this.panelHeight - 30.0, this.panelX + this.panelWidth, this.panelY + this.panelHeight, -15592942);
        double buttonOffsetX = 0.0;
        for (Widget widget : entries) {
            widget.onMouseDrag(this.panelX + padding + buttonOffsetX, this.panelY + this.panelHeight - 25.0, mouseX, mouseY);
            buttonOffsetX += widget.getWidth() + padding;
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public CheckBox getKeybindsToggle() {
        return this.keybindsToggle;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.anyEntryMatches(mouseX, mouseY)) {
            return;
        }
        for (ConfigEntry configEntry : this.configEntries) {
            if (!configEntry.handleClick(mouseX, mouseY, mouseButton)) continue;
            this.selectedConfig = configEntry;
            return;
        }
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    public double getPanelWidth() {
        return this.panelWidth;
    }
}

