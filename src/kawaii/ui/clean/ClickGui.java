/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.clean.CategoryPanel;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends GuiScreen {
    public static ClickGui instance;
    public boolean blockDefaultKeys = false;
    public ArrayList<CategoryPanel> panels;
    public Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!this.blockDefaultKeys) {
            super.keyTyped(typedChar, keyCode);
        }
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.keyTyped(typedChar, keyCode);
        }
    }

    public ClickGui() {
        this.panels = new ObjectList();
        int panelIndex = 0;
        Category[] categoryArray = Category.values();
        int categoryCount = categoryArray.length;
        for (int i = 0; i < categoryCount; ++i) {
            Category category = categoryArray[i];
            this.panels.add(new CategoryPanel(panelIndex * 130 + 10, 10.0, this, category));
            ++panelIndex;
        }
    }

    public static void open() {
        if (instance == null) {
            instance = new ClickGui();
        }
        ClickGui.instance.mc.displayGuiScreen(instance);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }
        Gui.drawRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0x50000000);
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new SettingsUpdateEvent());
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.renderPanel(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.panels) {
            categoryPanel.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.panels) {
            if (!categoryPanel.handleMouseClicked(mouseX, mouseY, mouseButton)) continue;
            return;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

