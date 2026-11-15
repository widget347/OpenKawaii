/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import kawaii.Kawaii;
import kawaii.event.AbstractEventDispatcher;
import kawaii.event.CancellableEvent;
import kawaii.module.Category;
import kawaii.module.SuffixStyle;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.setting.Setting;
import kawaii.ui.Notification;
import kawaii.ui.UiElement;
import kawaii.util.ObjectList;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class Module
extends AbstractEventDispatcher
implements UiElement {
    public String suffix;
    public String name;
    public ArrayList<Setting> settings = new ObjectList();
    public boolean enabled;
    public String description;
    public Category category;
    public int keyBind;

    public String getSuffix() {
        return this.suffix;
    }

    public void toggle() {
        this.setEnabled(!this.isEnabled());
    }

    public String getDisplayName(SuffixStyle suffixStyle) {
        return suffixStyle == SuffixStyle.bracketedSuffix ? this.name + (this.suffix != null ? " \u00a77[\u00a7f" + this.suffix + "\u00a77]" : "") : this.name + (this.suffix != null ? " " + this.suffix : "");
    }

    public ArrayList<Setting> getSettings() {
        return this.settings;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", this.name);
        jsonObject.addProperty("enabled", this.enabled);
        jsonObject.addProperty("bind", this.keyBind);
        this.getSettings().forEach(setting -> Module.writeSetting(jsonObject, setting));
        return jsonObject;
    }

    public void setKeyBind(int keyCode) {
        this.keyBind = keyCode;
    }

    public void fromJson(JsonObject jsonObject, boolean applyBinds) {
        jsonObject.entrySet().forEach(entry -> this.applyJsonEntry(applyBinds, entry));
    }

    public static void writeSetting(JsonObject jsonObject, Setting setting) {
        try {
            jsonObject.addProperty(setting.getName(), String.valueOf(setting.getTypeName()));
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getDescription() {
        return this.description;
    }

    public void addSettings(Setting ... settingArray) {
        this.settings.addAll(Arrays.asList(settingArray));
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (enabled) {
            Kawaii.INSTANCE.getModuleKeyHandler().addModule(this);
        } else {
            Kawaii.INSTANCE.getModuleKeyHandler().removeModule(this);
        }
        if (Module.mc.thePlayer != null) {
            if (enabled) {
                this.onEnable();
            } else {
                this.onDisable();
            }
        }
    }

    public void applyJsonEntry(boolean applyBinds, Map.Entry entry) {
        switch ((String)entry.getKey()) {
            case "name": {
                break;
            }
            case "enabled": {
                if (this.isEnabled() == ((JsonElement)entry.getValue()).getAsBoolean()) break;
                this.setEnabled(((JsonElement)entry.getValue()).getAsBoolean());
                break;
            }
            case "bind": {
                if (!applyBinds) break;
                this.setKeyBind(((JsonElement)entry.getValue()).getAsInt());
            }
        }
        Object setting = this.getSetting(String.valueOf(entry.getKey()));
        if (setting == null) {
            return;
        }
        if (setting instanceof ModeSetting) {
            ((ModeSetting)setting).setModeIndex(Integer.parseInt(((JsonElement)entry.getValue()).getAsString()));
        }
        if (setting instanceof NumberSetting) {
            ((NumberSetting)setting).setValue(Double.parseDouble(((JsonElement)entry.getValue()).getAsString()));
        }
        if (setting instanceof BooleanSetting) {
            ((BooleanSetting)setting).setValue(Boolean.parseBoolean(((JsonElement)entry.getValue()).getAsString()));
        }
        if (setting instanceof MultiBooleanSetting) {
            int valueIndex = 0;
            if (((MultiBooleanSetting)setting).getValues().length != new JsonParser().parse(((JsonElement)entry.getValue()).getAsString()).getAsJsonArray().size()) {
                Kawaii.INSTANCE.getInfoElementManager().addElement(new Notification("Old Config", "Expect some bugs.", 2000.0, C19PacketResourcePackStatus.Action.WARNING));
                return;
            }
            BooleanSetting[] booleanSettingArray = ((MultiBooleanSetting)setting).getValues();
            int valueCount = booleanSettingArray.length;
            for (int i = 0; i < valueCount; ++i) {
                BooleanSetting booleanSetting = booleanSettingArray[i];
                booleanSetting.setValue(Boolean.parseBoolean(new JsonParser().parse(((JsonElement)entry.getValue()).getAsString()).getAsJsonArray().get(valueIndex).getAsString()));
                ++valueIndex;
            }
        }
    }

    public Setting getSetting(String name) {
        for (Setting setting : this.settings) {
            if (!setting.getName().equalsIgnoreCase(name)) continue;
            return setting;
        }
        return null;
    }

    public Category getCategory() {
        return this.category;
    }

    public int getKeyBind() {
        return this.keyBind;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
    }

    public String getName() {
        return this.name;
    }

    public Module(String name, String description, Category category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}

