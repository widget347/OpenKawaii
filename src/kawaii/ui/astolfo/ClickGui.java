/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import kawaii.ui.astolfo.CategoryPanel;
import kawaii.util.ObjectList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

public class ClickGui
extends GuiScreen {
    public ArrayList<CategoryPanel> categoryPanels;
    public Minecraft mc = Minecraft.getMinecraft();
    public static ClickGui instance;
    public boolean blockDefaultKeys = false;

    public ClickGui() {
        this.categoryPanels = new ObjectList();
        int columnIndex = 0;
        Category[] categoryArray = Category.values();
        int categoryCount = categoryArray.length;
        for (int i = 0; i < categoryCount; ++i) {
            Category category = categoryArray[i];
            this.categoryPanels.add(new CategoryPanel(columnIndex * 135 + 10, 10.0, this, category));
            ++columnIndex;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
        }
        Gui.drawRect(0.0, 0.0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, 0x50000000);
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new SettingsUpdateEvent());
        for (CategoryPanel categoryPanel : this.categoryPanels) {
            categoryPanel.drawPanel(mouseX, mouseY);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.categoryPanels) {
            categoryPanel.onMouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!this.blockDefaultKeys) {
            super.keyTyped(typedChar, keyCode);
        }
        for (CategoryPanel categoryPanel : this.categoryPanels) {
            categoryPanel.onKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        for (CategoryPanel categoryPanel : this.categoryPanels) {
            if (!categoryPanel.onMouseClicked(mouseX, mouseY, mouseButton)) continue;
            return;
        }
    }

    public static void open() {
        if (instance == null) {
            instance = new ClickGui();
        }
        ClickGui.instance.mc.displayGuiScreen(instance);
    }
}

