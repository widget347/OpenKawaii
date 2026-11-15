/*
 * Decompiled with CFR 0.152.
 */
package kawaii.setting;

import kawaii.setting.Setting;

public class NumberSetting
extends Setting {
    public double min;
    public double value;
    public String name = "";
    public double increment;
    public double max;

    public String toDisplayString() {
        return this.value + this.name;
    }

    public void setMax(double newMax) {
        this.max = newMax;
    }

    public void setMin(double newMin) {
        this.min = newMin;
    }

    public NumberSetting(String settingName, double initialValue, double minimum, double step, double maximum) {
        this.name = settingName;
        this.value = initialValue;
        this.min = minimum;
        this.increment = step;
        this.max = maximum;
    }

    @Override
    public String getTypeName() {
        return String.valueOf(this.value);
    }

    public double getMax() {
        return this.max;
    }

    public double getValue() {
        return Math.floor(this.value * 1000.0) / 1000.0;
    }

    public double getIncrement() {
        return this.increment;
    }

    public NumberSetting(String settingName, double initialValue, double minimum, double step, double maximum, String displayName) {
        this.name = settingName;
        this.value = initialValue;
        this.min = minimum;
        this.increment = step;
        this.max = maximum;
        this.name = displayName;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setIncrement(double newIncrement) {
        this.increment = newIncrement;
    }

    public double getMin() {
        return this.min;
    }

    public void setValue(double newValue) {
        double clampedValue = Math.max(Math.min(this.max, newValue), this.increment);
        this.value = (double)Math.round(clampedValue / this.min) * this.min;
    }
}

