/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.astolfo;

import kawaii.ui.astolfo.ModuleButton;

public class SettingComponent<T> {
    public T setting;
    public double offsetX;
    public double offsetY;
    public double animationProgress;
    public ModuleButton modulePanel;
    public double dragOffset;
    public ModuleButton parentButton;

    public SettingComponent(Object setting, ModuleButton modulePanel) {
        this.modulePanel = modulePanel;
        this.setting = (T)setting;
    }

    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public double drawComponent(int mouseX, int mouseY, double d) {
        return 0.0;
    }

    public void onMouseReleased(int mouseX, int mouseY, int mouseButton) {
    }
}

