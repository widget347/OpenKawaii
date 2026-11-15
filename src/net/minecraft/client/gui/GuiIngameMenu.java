/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.MainMenu;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;

public class GuiIngameMenu
extends GuiScreen {
    public int saveStep;
    public int visibleTime;

    @Override
    public void updateScreen() {
        super.updateScreen();
        ++this.visibleTime;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, this.width, this.height);
            Gui.drawRect(0.0, 0.0, this.width, this.height, -2145509857);
        } else {
            this.drawDefaultBackground();
        }
        FontLoader.val24.drawCenteredString(I18n.format("menu.game", new Object[0]), (float)this.width / 2.0f, 40.0f, 0xFFFFFF);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) {
        switch (guiButton.id) {
            case 0: {
                this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
                break;
            }
            case 1: {
                boolean java64bit = this.mc.isJava64bit();
                boolean fullScreenMode = this.mc.isFullScreen();
                guiButton.enabled = false;
                this.mc.theWorld.sendQuittingDisconnectingPacket();
                this.mc.loadWorld(null);
                if (java64bit) {
                    this.mc.displayGuiScreen(new MainMenu());
                    break;
                }
                if (fullScreenMode) {
                    RealmsBridge realmsBridge = new RealmsBridge();
                    realmsBridge.switchToRealms(new MainMenu());
                    break;
                }
                this.mc.displayGuiScreen(new GuiMultiplayer(new MainMenu()));
            }
            default: {
                break;
            }
            case 4: {
                this.mc.displayGuiScreen(null);
                this.mc.setIngameFocus();
                break;
            }
            case 5: {
                this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            }
            case 6: {
                this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
                break;
            }
            case 7: {
                this.mc.displayGuiScreen(new GuiShareToLan(this));
            }
        }
    }

    @Override
    public void initGui() {
        this.saveStep = 0;
        this.buttonList.clear();
        int buttonWidth = 98;
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + -16, I18n.format("menu.returnToMenu", new Object[0])));
        if (!this.mc.isJava64bit()) {
            ((GuiButton)this.buttonList.get((int)0)).displayString = I18n.format("menu.disconnect", new Object[0]);
        }
        this.buttonList.add(new GuiButton(4, this.width / 2 - 100, this.height / 4 + 24 + -16, I18n.format("menu.returnToGame", new Object[0])));
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.options", new Object[0])));
        GuiButton guiButton = new GuiButton(7, this.width / 2 + 2, this.height / 4 + 96 + -16, 98, 20, I18n.format("menu.shareToLan", new Object[0]));
        this.buttonList.add(guiButton);
        this.buttonList.add(new GuiButton(5, this.width / 2 - 100, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.achievements", new Object[0])));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, this.height / 4 + 48 + -16, 98, 20, I18n.format("gui.stats", new Object[0])));
        guiButton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().$f();
    }
}

