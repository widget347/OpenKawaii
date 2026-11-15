/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.event.KeyPressEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.render.Blur;
import kawaii.module.render.Hud;
import kawaii.render.BlurRenderer;
import kawaii.render.ColorThemes;
import kawaii.render.RenderUtils;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.Animation;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class TabGui
extends Module {
    public int moduleIndex = 0;
    public ModeSetting modeSetting = new ModeSetting("Mode", "Kawaii", "Clean", "Modern");
    public NumberSetting widthSetting = new NumberSetting("Width", 100.0, 5.0, 100.0, 150.0);
    public Animation highlightAnimation = new Animation();
    public double moduleHighlightOffset = 0.0;
    public Animation expandAnimation = new Animation();
    public double categoryHighlightOffset = 0.0;
    public boolean inModuleList = false;
    public int categoryIndex = 0;

    public String getCategoryName(int index) {
        if (index <= 0) {
            index = Category.values().length + index;
        }
        if (index >= Category.values().length) {
            index -= Category.values().length;
        }
        return Category.values()[index].getDisplayName();
    }

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.widthSetting.setVisible(!this.modeSetting.getMode().equalsIgnoreCase("Kawaii"));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        double rowHeight = 18.0;
        double ten = 10.0;
        double margin = 5.0;
        float halfDivisor = 2.0f;
        double two = 2.0;
        double startY = 20.0;
        if (Hud.watermarkMode.getMode().equalsIgnoreCase("Basic")) {
            startY += margin;
        }
        if (!Kawaii.INSTANCE.getModuleManager().getModule(Hud.class).isEnabled()) {
            startY = margin;
        }
        block5 : switch (this.modeSetting.getMode()) {
            case "Kawaii": {
                ArrayList<Module> kawaiiModules = Kawaii.INSTANCE.getModuleManager().getModulesInCategory(Category.values()[this.categoryIndex], FontLoader.val20);
                if (cancellableEvent instanceof EventRender2D) {
                    double tabWidth = this.widthSetting.getValue();
                    double startX = margin;
                    GlStateManager.pushMatrix();
                    GL11.glEnable(3089);
                    RenderUtils.applyScissor(startX, startY, startX + tabWidth, startY + rowHeight + 1.0);
                    if (cancellableEvent.getEventType().equals((Object)EventType.pre)) {
                        Gui.drawRect(startX, startY, startX + tabWidth, startY + rowHeight, Hud.getHudColor().getRGB());
                    }
                    FontLoader.popMed20.drawString(Category.values()[this.categoryIndex].getDisplayName(), startX + tabWidth / two - (double)((float)FontLoader.popMed20.getStringWidth(Category.values()[this.categoryIndex].getDisplayName()) / halfDivisor), startY + ten - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1);
                    FontLoader.popMed20.drawString(this.getCategoryName(this.categoryIndex - 1), startX + tabWidth / two - (double)((float)FontLoader.popMed20.getStringWidth(Category.values()[this.categoryIndex].getDisplayName()) / halfDivisor) - (double)FontLoader.popMed20.getStringWidth(this.getCategoryName(this.categoryIndex - 1)) - margin, startY + ten - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1879048193);
                    FontLoader.popMed20.drawString(this.getCategoryName(this.categoryIndex - 2), startX + tabWidth / two - (double)((float)FontLoader.popMed20.getStringWidth(Category.values()[this.categoryIndex].getDisplayName()) / halfDivisor) - (double)FontLoader.popMed20.getStringWidth(this.getCategoryName(this.categoryIndex - 1)) - margin - (double)FontLoader.popMed20.getStringWidth(this.getCategoryName(this.categoryIndex - 2)) - margin, startY + ten - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1879048193);
                    FontLoader.popMed20.drawString(this.getCategoryName(this.categoryIndex + 1), startX + tabWidth / two + (double)((float)FontLoader.popMed20.getStringWidth(Category.values()[this.categoryIndex].getDisplayName()) / halfDivisor) + margin, startY + ten - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1879048193);
                    FontLoader.popMed20.drawString(this.getCategoryName(this.categoryIndex + 2), startX + tabWidth / two + (double)((float)FontLoader.popMed20.getStringWidth(Category.values()[this.categoryIndex].getDisplayName()) / halfDivisor) + margin + (double)FontLoader.popMed20.getStringWidth(this.getCategoryName(this.categoryIndex + 1)) + margin, startY + ten - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1879048193);
                    GL11.glDisable(3089);
                    GlStateManager.popMatrix();
                    int rowIndex = 0;
                    for (Module module : kawaiiModules) {
                        if (Blur.blurActive.booleanValue() && Blur.blurTabGui.getValue().booleanValue()) {
                            BlurRenderer.queueRect(startX, startY, startX + tabWidth, startY + rowHeight + (double)(rowIndex * 18) + rowHeight);
                        }
                        if (this.moduleIndex == rowIndex) {
                            RenderUtils.drawGradientRect(startX, startY + rowHeight + (double)(rowIndex * 18), startX + tabWidth, startY + rowHeight + (double)(rowIndex * 18) + rowHeight, ColorThemes.getThemeColor((int)(startY + rowHeight + (double)(rowIndex * 18))), ColorThemes.getThemeColor((int)(startY + rowHeight + (double)(rowIndex * 18)) + 100));
                        } else if (cancellableEvent.getEventType() == EventType.pre) {
                            Gui.drawRect(startX, startY + rowHeight + (double)(rowIndex * 18), startX + tabWidth, startY + rowHeight + (double)(rowIndex * 18) + rowHeight, Hud.getHudColor().getRGB());
                        }
                        FontLoader.popMed20.drawString(module.getName(), startX + margin, startY + rowHeight + (double)(rowIndex * 18) + 9.0 - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), module.isEnabled() ? -1 : -1879048193);
                        ++rowIndex;
                    }
                }
                if (!(cancellableEvent instanceof KeyPressEvent)) return;
                int keyCode = ((KeyPressEvent)cancellableEvent).getKeyCode();
                switch (keyCode) {
                    case 208: {
                        if (this.moduleIndex >= kawaiiModules.size() - 1) {
                            this.moduleIndex = 0;
                            return;
                        }
                        ++this.moduleIndex;
                        return;
                    }
                    case 28: {
                        kawaiiModules.get(this.moduleIndex).toggle();
                        return;
                    }
                    case 200: {
                        if (this.moduleIndex < 1) {
                            this.moduleIndex = kawaiiModules.size() - 1;
                            return;
                        }
                        --this.moduleIndex;
                        return;
                    }
                    case 205: {
                        this.categoryIndex = this.categoryIndex > Category.values().length - 2 ? 0 : ++this.categoryIndex;
                        this.moduleIndex = 0;
                        return;
                    }
                    case 203: {
                        this.categoryIndex = this.categoryIndex < 1 ? Category.values().length - 1 : --this.categoryIndex;
                        this.moduleIndex = 0;
                    }
                }
                return;
            }
            case "Modern": {
                ArrayList<Module> modernModules = Kawaii.INSTANCE.getModuleManager().getModulesInCategory(Category.values()[this.categoryIndex], FontLoader.val20);
                if (cancellableEvent instanceof EventRender2D) {
                    double columnX = margin;
                    double panelWidth = 95.0;
                    double entryHeight = 20.0;
                    int modernEntryIndex = 0;
                    Category[] modernCategories = Category.values();
                    int modernCategoryCount = modernCategories.length;
                    for (int i = 0; i < modernCategoryCount; ++i) {
                        Category category = modernCategories[i];
                        if (cancellableEvent.getEventType() == EventType.post) {
                            if (Blur.blurActive.booleanValue() && Blur.blurTabGui.getValue().booleanValue()) {
                                BlurRenderer.queueRect(columnX, startY + (double)modernEntryIndex * entryHeight, columnX + panelWidth, startY + (double)modernEntryIndex * entryHeight + entryHeight);
                            }
                            Gui.drawRect(columnX, startY + (double)modernEntryIndex * entryHeight, columnX + panelWidth, startY + (double)modernEntryIndex * entryHeight + entryHeight, this.categoryIndex == modernEntryIndex ? 0x70000000 : 0x50000000);
                        }
                        FontLoader.popMed20.drawString(category.getDisplayName(), columnX + margin + (double)(this.categoryIndex == modernEntryIndex ? 5 : 0), startY + (double)modernEntryIndex * entryHeight + entryHeight / two - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), -1);
                        ++modernEntryIndex;
                    }
                    this.expandAnimation.setReversed(!this.inModuleList).setEasing(200).update();
                    GL11.glEnable(3089);
                    RenderUtils.applyScissor((columnX += panelWidth + margin) - 1.0, startY - 1.0, columnX + this.expandAnimation.getValue() / 100.0 * panelWidth, startY + 500.0);
                    modernEntryIndex = 0;
                    for (Module module : modernModules) {
                        if (cancellableEvent.getEventType() == EventType.post) {
                            if (Blur.blurActive.booleanValue() && Blur.blurTabGui.getValue().booleanValue()) {
                                BlurRenderer.queueRect(columnX, startY + (double)modernEntryIndex * entryHeight, columnX + this.expandAnimation.getValue() / 100.0 * panelWidth, startY + (double)modernEntryIndex * entryHeight + entryHeight);
                            }
                            Gui.drawRect(columnX, startY + (double)modernEntryIndex * entryHeight, columnX + panelWidth, startY + (double)modernEntryIndex * entryHeight + entryHeight, this.moduleIndex == modernEntryIndex ? 0x70000000 : 0x50000000);
                        }
                        FontLoader.popMed20.drawString(module.getName(), columnX + margin + (double)(this.moduleIndex == modernEntryIndex ? 5 : 0), startY + (double)modernEntryIndex * entryHeight + entryHeight / two - (double)((float)FontLoader.popMed20.getHeight() / halfDivisor), module.isEnabled() ? -1 : -2130706433);
                        ++modernEntryIndex;
                    }
                    GL11.glDisable(3089);
                }
                if (!(cancellableEvent instanceof KeyPressEvent)) return;
                int modernKeyCode = ((KeyPressEvent)cancellableEvent).getKeyCode();
                switch (modernKeyCode) {
                    case 208: {
                        if (this.inModuleList) {
                            if (this.moduleIndex > modernModules.size() - 2) {
                                this.moduleIndex = 0;
                                return;
                            }
                            ++this.moduleIndex;
                            return;
                        }
                        if (this.categoryIndex > Category.values().length - 2) {
                            this.categoryIndex = 0;
                            return;
                        }
                        ++this.categoryIndex;
                        return;
                    }
                    case 200: {
                        if (this.inModuleList) {
                            if (this.moduleIndex < 1) {
                                this.moduleIndex = modernModules.size() - 1;
                                return;
                            }
                            --this.moduleIndex;
                            return;
                        }
                        if (this.categoryIndex < 1) {
                            this.categoryIndex = Category.values().length - 1;
                            return;
                        }
                        --this.categoryIndex;
                        return;
                    }
                    case 205: {
                        if (!this.inModuleList) {
                            this.inModuleList = true;
                            this.moduleIndex = 0;
                            return;
                        }
                        Kawaii.INSTANCE.getModuleManager().getModulesInCategory(Category.values()[this.categoryIndex], FontLoader.val20).get(this.moduleIndex).toggle();
                        return;
                    }
                    case 203: {
                        if (!this.inModuleList) break;
                        this.inModuleList = false;
                    }
                }
                return;
            }
            case "Clean": {
                ArrayList<Module> cleanModules = Kawaii.INSTANCE.getModuleManager().getModulesInCategory(Category.values()[this.categoryIndex], FontLoader.val20);
                if (cancellableEvent instanceof EventRender2D) {
                    double cleanPanelWidth = 65.0;
                    double cleanX = margin;
                    this.highlightAnimation.setEnd(0.3).update();
                    RenderUtils.drawRoundedRect(cleanX, startY, cleanPanelWidth, Category.values().length * 15, margin, 0x50000000);
                    if (Blur.blurActive.booleanValue() && Blur.blurTabGui.getValue().booleanValue()) {
                        BlurRenderer.queueBlur(cleanX, startY, cleanX + cleanPanelWidth, startY + (double)(Category.values().length * 15), margin);
                    }
                    RenderUtils.drawRoundedRect(cleanX + two, startY + two + (!this.inModuleList ? this.categoryHighlightOffset + this.highlightAnimation.getStart() : (double)(this.categoryIndex * 15)), two, ten, two, -1);
                    int cleanEntryIndex = 0;
                    Category[] cleanCategories = Category.values();
                    int cleanCategoryCount = cleanCategories.length;
                    for (int i = 0; i < cleanCategoryCount; ++i) {
                        Category category = cleanCategories[i];
                        GlStateManager.color(0.0f, 0.0f, 0.0f);
                        FontLoader.val20.drawString(category.getDisplayName(), cleanX + two + (double)(this.categoryIndex == cleanEntryIndex ? 3 : 0), startY + (double)(cleanEntryIndex * 15) + (double)((float)FontLoader.val20.getHeight() / halfDivisor), this.categoryIndex == cleanEntryIndex ? -1 : -2130706433);
                        ++cleanEntryIndex;
                    }
                    cleanEntryIndex = 0;
                    if (this.inModuleList) {
                        RenderUtils.drawRoundedRect(cleanX + cleanPanelWidth + margin, startY, cleanPanelWidth + ten, cleanModules.size() * 15, margin, 0x50000000);
                        if (Blur.blurActive.booleanValue() && Blur.blurTabGui.getValue().booleanValue()) {
                            BlurRenderer.queueBlur(cleanX + cleanPanelWidth + margin, startY, cleanX + cleanPanelWidth + cleanPanelWidth + 15.0, (double)(cleanModules.size() * 15) + startY, margin);
                        }
                        RenderUtils.drawRoundedRect(cleanX + cleanPanelWidth + margin + two, startY + two + (this.inModuleList ? this.moduleHighlightOffset + this.highlightAnimation.getStart() : (double)(this.moduleIndex * 15)), two, ten, two, cleanModules.get(this.moduleIndex).isEnabled() ? -10879046 : -1);
                        for (Module module : cleanModules) {
                            GlStateManager.color(0.0f, 0.0f, 0.0f);
                            FontLoader.val20.drawString(module.getName(), cleanX + cleanPanelWidth + margin + two + (double)(this.moduleIndex == cleanEntryIndex ? 3 : 0), startY + (double)(cleanEntryIndex * 15) + (double)((float)FontLoader.val20.getHeight() / halfDivisor), module.isEnabled() ? -10879046 : (this.moduleIndex == cleanEntryIndex ? -1 : -2130706433));
                            ++cleanEntryIndex;
                        }
                    }
                }
                if (!(cancellableEvent instanceof KeyPressEvent)) return;
                int cleanKeyCode = ((KeyPressEvent)cancellableEvent).getKeyCode();
                switch (cleanKeyCode) {
                    case 208: {
                        if (this.inModuleList) {
                            if (this.moduleIndex > cleanModules.size() - 2) {
                                this.highlightAnimation.setEasing(this.moduleIndex * 15);
                                this.highlightAnimation.setReversed(true);
                                this.highlightAnimation.restart();
                                this.moduleIndex = 0;
                                this.moduleHighlightOffset = 0.0;
                                break block5;
                            }
                            this.highlightAnimation.setEasing(15);
                            this.highlightAnimation.setReversed(false);
                            this.highlightAnimation.restart();
                            ++this.moduleIndex;
                            this.moduleHighlightOffset = this.moduleIndex * 15 - 15;
                            break block5;
                        }
                        if (this.categoryIndex > Category.values().length - 2) {
                            this.highlightAnimation.setEasing(this.categoryIndex * 15);
                            this.highlightAnimation.setReversed(true);
                            this.highlightAnimation.restart();
                            this.categoryIndex = 0;
                            this.categoryHighlightOffset = 0.0;
                            break block5;
                        }
                        this.highlightAnimation.setEasing(15);
                        this.highlightAnimation.setReversed(false);
                        this.highlightAnimation.restart();
                        ++this.categoryIndex;
                        this.categoryHighlightOffset = this.categoryIndex * 15 - 15;
                        break block5;
                    }
                    case 200: {
                        if (this.inModuleList) {
                            if (this.moduleIndex < 1) {
                                this.highlightAnimation.setEasing((cleanModules.size() - 1) * 15);
                                this.highlightAnimation.setReversed(false);
                                this.highlightAnimation.restart();
                                this.moduleIndex = cleanModules.size() - 1;
                                this.moduleHighlightOffset = 0.0;
                                break block5;
                            }
                            this.highlightAnimation.setEasing(15);
                            this.highlightAnimation.setReversed(true);
                            this.highlightAnimation.restart();
                            --this.moduleIndex;
                            this.moduleHighlightOffset = this.moduleIndex * 15;
                            break block5;
                        }
                        if (this.categoryIndex < 1) {
                            this.highlightAnimation.setEasing((Category.values().length - 1) * 15);
                            this.highlightAnimation.setReversed(false);
                            this.highlightAnimation.restart();
                            this.categoryIndex = Category.values().length - 1;
                            this.categoryHighlightOffset = 0.0;
                            break block5;
                        }
                        this.highlightAnimation.setEasing(15);
                        this.highlightAnimation.setReversed(true);
                        this.highlightAnimation.restart();
                        --this.categoryIndex;
                        this.categoryHighlightOffset = this.categoryIndex * 15;
                        break block5;
                    }
                    case 205: {
                        if (!this.inModuleList) {
                            this.inModuleList = true;
                            this.categoryHighlightOffset = this.categoryIndex * 15;
                            this.moduleHighlightOffset = 0.0;
                            this.moduleIndex = 0;
                            this.highlightAnimation.setEasing(0);
                            this.highlightAnimation.setReversed(true);
                            this.highlightAnimation.restart();
                            break block5;
                        }
                        Kawaii.INSTANCE.getModuleManager().getModulesInCategory(Category.values()[this.categoryIndex], FontLoader.val20).get(this.moduleIndex).toggle();
                        break block5;
                    }
                    case 203: {
                        if (!this.inModuleList) return;
                        this.inModuleList = false;
                        this.highlightAnimation.setEasing(0);
                        this.highlightAnimation.setReversed(true);
                        this.highlightAnimation.restart();
                    }
                }
            }
        }
    }

    public TabGui() {
        super("TabGUI", "Quick GUI for modifying modules", Category.visuals);
        this.addSettings(this.modeSetting, this.widthSetting);
    }
}

