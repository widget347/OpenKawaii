/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.NumberSetting;

public class Reach
extends Module {
    public static NumberSetting reachSetting = new NumberSetting("Reach", 4.2, 0.1, 3.0, 6.0);

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        this.setSuffix("" + reachSetting.getValue());
        super.onEvent(settingsUpdateEvent);
    }

    public Reach() {
        super("Reach", "Reach further", Category.combat);
        this.addSettings(reachSetting);
    }
}

