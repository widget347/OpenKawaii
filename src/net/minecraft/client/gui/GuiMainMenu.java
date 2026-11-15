/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import kawaii.Kawaii;
import kawaii.render.RenderUtils;
import kawaii.ui.GuiAltLogin;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

public class GuiMainMenu
extends GuiScreen
implements GuiYesNoCallback {
    @Override
    public void initGui() {
        int buttonsY = this.height / 4 + 48;
        this.addSingleplayerMultiplayerButtons(buttonsY);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, buttonsY + 72 + 12, 98, 20, I18n.format("menu.options", new Object[0])));
        this.buttonList.add(new GuiButton(4, this.width / 2 + 2, buttonsY + 72 + 12, 98, 20, I18n.format("menu.quit", new Object[0])));
        this.mc.setConnectedToRealms(false);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        if (guiButton.id == 4) {
            this.mc.shutdown();
        }
        if (guiButton.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }
        if (guiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }
        if (guiButton.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (guiButton.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (guiButton.id == 14) {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    public void addSingleplayerMultiplayerButtons(int y) {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, y, I18n.format("menu.singleplayer", new Object[0])));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 100, y + 24, I18n.format("menu.multiplayer", new Object[0])));
        this.buttonList.add(new GuiButton(14, this.width / 2 - 100, y + 48, "Alt Manager"));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        RenderUtils.drawImageWithAlpha(0.0, 0.0, this.mc.displayWidth / 2, this.mc.displayHeight / 2, new ResourceLocation("kawaii/images/background.png"), 1.0);
        String nameAndVersion = Kawaii.INSTANCE.getName() + " " + Kawaii.INSTANCE.getVersion();
        FontLoader.val32.drawString(Kawaii.INSTANCE.getName(), (float)this.width / 2.0f - (float)FontLoader.val32.getStringWidth(Kawaii.INSTANCE.getName()) / 2.0f, (float)this.height / 4.0f, -337409);
        this.drawString(this.fontRendererObj, nameAndVersion, 2, this.height - 10, -1);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

