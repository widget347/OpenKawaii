/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.player;

import kawaii.event.CancellableEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.NumberSetting;

public class FastPlace
extends Module {
    public NumberSetting speedSetting = new NumberSetting("Speed", 2.0, 1.0, 1.0, 7.0);

    public FastPlace() {
        super("FastPlace", "Mine Faster", Category.player);
        this.addSettings(this.speedSetting);
    }

    @Override
    public void onDisable() {
        FastPlace.mc.rightClickDelayTimer = 6;
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        FastPlace.mc.rightClickDelayTimer = (int)(7.0 - this.speedSetting.getValue());
    }
}

