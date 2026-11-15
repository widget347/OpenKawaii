/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import kawaii.Kawaii;
import kawaii.render.RenderUtils;
import kawaii.ui.Button;
import kawaii.ui.GuiAltLogin;
import kawaii.ui.GuiTheme;
import kawaii.ui.Panel;
import kawaii.ui.ServerShortcut;
import kawaii.ui.Widget;
import kawaii.ui.font.FontLoader;
import kawaii.util.RunnableQueue;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;

public class MainMenu
extends GuiScreen
implements RunnableQueue {
    public Panel shortcutPanel = new Panel(new ResourceLocation("kawaii/images/shortcut.png"), 25.0, "Server Shortcuts", new ServerShortcut(new ResourceLocation("kawaii/images/servers/HypixelLogo.png"), "Hypixel", "mc.hypixel.net"), new ServerShortcut(new ResourceLocation("kawaii/images/servers/mmcLogo.png"), "MinemenClub", "minemen.club:25565"), new ServerShortcut(new ResourceLocation("kawaii/images/servers/stitchLogo.png"), "Stitch AC Testing", "mc.stitch.best"), new ServerShortcut(new ResourceLocation("kawaii/images/servers/invadedLogo.png"), "InvadedLands", "play.invadedlands.net"));

    public void openSingleplayer() {
        this.mc.displayGuiScreen(new GuiSelectWorld(this));
    }

    @Override
    public void initGui() {
        this.clearTasks();
        super.initGui();
    }

    @Override
    public void mouseClicked(int cursorX, int cursorY, int mouseButton) {
        if (mouseButton != 0) {
            return;
        }
        this.updateMouseOver(cursorX, cursorY);
        this.shortcutPanel.handleMouseClicked(cursorX, cursorY, mouseButton);
        super.mouseClicked(cursorX, cursorY, mouseButton);
    }

    public void openAltManager() {
        this.mc.displayGuiScreen(new GuiAltLogin(this));
    }

    public void openMultiplayer() {
        this.mc.displayGuiScreen(new GuiMultiplayer(this));
    }

    public void openSettings() {
        this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
    }

    @Override
    public void drawScreen(int cursorX, int cursorY, float partialTicks) {
        float halfDivisor = 2.0f;
        Gui.drawRect(0.0, 0.0, this.width, this.height, -13487566);
        RenderUtils.drawImageWithAlpha(0.0, 0.0, this.width, this.height, new ResourceLocation("kawaii/images/background.png"), 1.0);
        this.shortcutPanel.render(5.0, 5.0, cursorX, cursorY);
        int index = 0;
        for (Widget widget : tasks) {
            double widgetX = (double)((float)this.width / halfDivisor) - widget.getWidth() + (index % 2 == 1 ? widget.getWidth() + 5.0 : 0.0) - 2.0;
            widget.onMouseDrag(widgetX, (double)((float)this.height / halfDivisor) + (double)((int)((float)index / halfDivisor)) * (widget.getHeight() + 5.0), cursorX, cursorY);
            ++index;
        }
        FontLoader.latoBlack40.drawCenteredString(Kawaii.INSTANCE.getName(), (float)this.width / halfDivisor, (float)this.height / halfDivisor - 50.0f, -1);
    }

    @Override
    public void onCleared() {
        tasks.add(new Button("Singleplayer", 150.0, 40.0, GuiTheme.light, new Thread(this::openSingleplayer)));
        tasks.add(new Button("Multiplayer", 150.0, 40.0, GuiTheme.darkUnderline, new Thread(this::openMultiplayer)));
        tasks.add(new Button("Alt Manager", 150.0, 40.0, GuiTheme.darkUnderline, new Thread(this::openAltManager)));
        tasks.add(new Button("Settings", 150.0, 40.0, GuiTheme.darkUnderline, new Thread(this::openSettings)));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

