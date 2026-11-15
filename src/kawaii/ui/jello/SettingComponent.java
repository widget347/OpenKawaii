/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.jello;

import kawaii.ui.jello.ModuleSettingsPanel;

public class SettingComponent<T> {
    public double posX;
    public double height;
    public ModuleSettingsPanel screen;
    public T value;
    public double posY;

    public boolean handleMouseReleased(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public boolean handleMouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public double getHeight() {
        return this.height;
    }

    public double getY() {
        return this.posY;
    }

    public ModuleSettingsPanel getScreen() {
        return this.screen;
    }

    public double render(int mouseX, int mouseY, double partialTicks) {
        return 0.0;
    }

    public double renderOptions(int mouseX, int mouseY, double partialTicks) {
        return 0.0;
    }

    public double getX() {
        return this.posX;
    }

    public T getValue() {
        return this.value;
    }
}

