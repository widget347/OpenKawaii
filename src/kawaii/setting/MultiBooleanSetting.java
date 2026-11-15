/*
 * Decompiled with CFR 0.152.
 */
package kawaii.setting;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import kawaii.setting.BooleanSetting;
import kawaii.setting.Setting;

public class MultiBooleanSetting
extends Setting {
    public BooleanSetting[] values;

    public BooleanSetting findByName(String valueName) {
        return Arrays.stream(this.values).filter(booleanSetting -> MultiBooleanSetting.matchesName(valueName, booleanSetting)).findFirst().orElse(null);
    }

    public static boolean matchesName(String expectedName, BooleanSetting booleanSetting) {
        return booleanSetting.getName().equalsIgnoreCase(expectedName);
    }

    public BooleanSetting[] getValues() {
        return this.values;
    }

    public MultiBooleanSetting(String name, BooleanSetting ... booleanSettingArray) {
        this.name = name;
        this.values = booleanSettingArray;
    }

    @Override
    public String getTypeName() {
        ArrayList typeNames = Arrays.stream(this.values).map(BooleanSetting::getTypeName).collect(Collectors.toCollection(ArrayList::new));
        return new Gson().toJsonTree(typeNames).getAsJsonArray().toString();
    }
}

