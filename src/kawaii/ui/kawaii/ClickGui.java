/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.kawaii.CategoryPanel;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends GuiScreen {
    public boolean isBinding = false;
    public Minecraft mc = Minecraft.getMinecraft();
    public static ClickGui instance;
    public ArrayList<CategoryPanel> panels = new ObjectList();

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.onMouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public ClickGui() {
        int panelIndex = 0;
        Category[] categoryArray = Category.values();
        int categoryCount = categoryArray.length;
        for (int i = 0; i < categoryCount; ++i) {
            Category category = categoryArray[i];
            this.panels.add(new CategoryPanel(panelIndex * 130 + 10, 10, category, this));
            ++panelIndex;
        }
    }

    public static int getEnabledColor() {
        return -7984641;
    }

    public static void openClickGui() {
        if (instance == null) {
            instance = new ClickGui();
        }
        ClickGui.instance.mc.displayGuiScreen(instance);
    }

    public static int getDisabledColor() {
        return -13487555;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.onMouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void onGuiClosed() {
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        } else {
            Gui.drawRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0x30000000);
        }
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new SettingsUpdateEvent());
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.render(mouseX, mouseY);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!this.isBinding) {
            super.keyTyped(typedChar, keyCode);
        }
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.onKeyTyped(typedChar, keyCode);
        }
    }
}

