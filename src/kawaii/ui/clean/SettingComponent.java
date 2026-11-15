/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.clean;

import kawaii.ui.clean.ModuleComponent;

public class SettingComponent<T> {
    public double componentX;
    public T setting;
    public ModuleComponent parentComponent;
    public double componentY;
    public ModuleComponent moduleComponent;
    public double componentHeight;
    public double animationProgress;

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public double render(int mouseX, int mouseY, double offsetY) {
        return 0.0;
    }

    public SettingComponent(T setting, ModuleComponent moduleComponent) {
        this.moduleComponent = moduleComponent;
        this.setting = setting;
    }
}

