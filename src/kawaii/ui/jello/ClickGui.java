/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.Animation;
import kawaii.ui.jello.CategoryFrame;
import kawaii.ui.jello.ModuleSettingsPanel;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class ClickGui
extends GuiScreen {
    public static ClickGui INSTANCE;
    public ArrayList<CategoryFrame> frames;
    public boolean blockDefaultKeys = false;
    public Minecraft minecraft = Minecraft.getMinecraft();
    public ModuleSettingsPanel settingsScreen;
    public Animation openAnimation;

    public ClickGui() {
        this.frames = new ObjectList();
        this.openAnimation = new Animation();
        int frameIndex = 0;
        for (Category category : Category.values()) {
            this.frames.add(new CategoryFrame(frameIndex * 115 + 10, 10, category, this));
            ++frameIndex;
        }
    }

    public void openSettings(Module module) {
        this.settingsScreen = new ModuleSettingsPanel(module, this);
        this.openAnimation.restart();
    }

    public static int getEnabledColor() {
        return -7984641;
    }

    public static void open() {
        if (INSTANCE == null) {
            INSTANCE = new ClickGui();
        }
        ClickGui.INSTANCE.minecraft.displayGuiScreen(INSTANCE);
        ClickGui.INSTANCE.openAnimation.restart();
    }

    @Override
    public void onGuiClosed() {
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (this.settingsScreen != null && keyCode == 1) {
            this.settingsScreen.closing = true;
            return;
        }
        if (!this.blockDefaultKeys) {
            super.keyTyped(typedChar, keyCode);
        }
        for (CategoryFrame categoryFrame : this.frames) {
            categoryFrame.handleKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static int getDisabledColor() {
        return -13487555;
    }

    public boolean isSettingsOpen() {
        return this.settingsScreen != null;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        if (this.settingsScreen != null) {
            Gui.drawRect(0.0, 0.0, this.width, this.height, 0x60000000);
            this.settingsScreen.handleMouseReleased(mouseX, mouseY, mouseButton);
            return;
        }
        for (CategoryFrame categoryFrame : this.frames) {
            categoryFrame.handleMouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        double animationDivisor = 100.0;
        float centerDivisor = 2.0f;
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, this.width, this.height);
        } else {
            Gui.drawRect(0.0, 0.0, this.width, this.height, 0x30000000);
        }
        boolean hasSettingsScreen = this.settingsScreen != null;
        GlStateManager.pushMatrix();
        if (!hasSettingsScreen) {
            this.openAnimation.setReversed(false).setEasing(200).update();
            GlStateManager.translate((float)this.width / centerDivisor, (float)this.height / centerDivisor, 0.0f);
            GlStateManager.scale(this.openAnimation.getValue() / animationDivisor, this.openAnimation.getValue() / animationDivisor, 1.0);
        } else {
            this.openAnimation.setReversed(!this.settingsScreen.isClosing()).setEasing(100).update();
            GlStateManager.translate((float)this.width / centerDivisor, (float)this.height / centerDivisor, 0.0f);
            GlStateManager.scale(0.9 + this.openAnimation.getValue() / 1000.0 * 1.0, 0.9 + this.openAnimation.getValue() / 1000.0 * 1.0, 1.0);
        }
        GlStateManager.translate(-((float)this.width / centerDivisor), -((float)this.height / centerDivisor), 0.0f);
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new SettingsUpdateEvent());
        for (CategoryFrame categoryFrame : this.frames) {
            categoryFrame.render(mouseX, mouseY);
        }
        if (!hasSettingsScreen) {
            GlStateManager.scale(-(this.openAnimation.getValue() / animationDivisor), -(this.openAnimation.getValue() / animationDivisor), 1.0);
            GlStateManager.translate(-((float)this.width / centerDivisor), -((float)this.height / centerDivisor), 0.0f);
        } else {
            GlStateManager.translate((float)this.width / centerDivisor, (float)this.height / centerDivisor, 0.0f);
            GlStateManager.scale(this.openAnimation.getValue() / animationDivisor, this.openAnimation.getValue() / animationDivisor, 1.0);
        }
        GlStateManager.popMatrix();
        if (this.settingsScreen != null) {
            Gui.drawRect(0.0, 0.0, this.width, this.height, 0x60000000);
            this.settingsScreen.drawScreen(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.settingsScreen != null) {
            Gui.drawRect(0.0, 0.0, this.width, this.height, 0x60000000);
            this.settingsScreen.handleMouseClicked(mouseX, mouseY, mouseButton);
            return;
        }
        for (CategoryFrame categoryFrame : this.frames) {
            categoryFrame.handleMouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public void closeSettings() {
        this.settingsScreen = null;
    }
}

