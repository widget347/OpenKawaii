/*
 * Decompiled with CFR 0.152.
 */
package kawaii.setting;

import kawaii.setting.Setting;

public class ModeSetting
extends Setting {
    public String[] modes;
    public int modeIndex;

    @Override
    public String getTypeName() {
        return String.valueOf(this.modeIndex);
    }

    public String getMode() {
        return this.modes[this.modeIndex];
    }

    public int getModeIndex() {
        return this.modeIndex;
    }

    public ModeSetting(String settingName, int defaultModeIndex, String ... modeNames) {
        this.name = settingName;
        this.modes = modeNames;
        this.modeIndex = defaultModeIndex;
    }

    public ModeSetting(String settingName, String ... modeNames) {
        this.name = settingName;
        this.modes = modeNames;
    }

    public String[] getModes() {
        return this.modes;
    }

    public void setModeIndex(int index) {
        this.modeIndex = index;
    }
}

