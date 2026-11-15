/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui.kawaii;

import kawaii.setting.Setting;
import kawaii.ui.kawaii.ModuleButton;

public class SettingComponent {
    public Setting setting;
    public ModuleButton parent;

    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
    }

    public SettingComponent(Setting setting, ModuleButton moduleButton) {
        this.parent = moduleButton;
        this.setting = setting;
    }

    public int getBackgroundColor() {
        return -14606039;
    }

    public double render(int mouseX, int mouseY, double x, double y) {
        return 0.0;
    }
}

