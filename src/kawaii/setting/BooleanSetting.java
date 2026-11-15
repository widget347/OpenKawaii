/*
 * Decompiled with CFR 0.152.
 */
package kawaii.setting;

import kawaii.setting.Setting;

public class BooleanSetting
extends Setting {
    public Boolean value;

    @Override
    public String getTypeName() {
        return this.value.toString();
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = this.value == false;
    }

    public Boolean getValue() {
        return this.value;
    }

    public BooleanSetting(String name, Boolean value) {
        this.name = name;
        this.value = value;
    }
}

