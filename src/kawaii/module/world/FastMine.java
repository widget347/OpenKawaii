/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.world;

import kawaii.event.CancellableEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.NumberSetting;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FastMine
extends Module {
    public NumberSetting speed = new NumberSetting("Speed", 2.0, 1.0, 2.0, 5.0);

    public FastMine() {
        super("FastMine", "Mine Faster", Category.player);
        this.addSettings(this.speed);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (FastMine.mc.thePlayer != null) {
            FastMine.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 5200, (int)this.speed.getValue() - 1));
        }
    }

    @Override
    public void onDisable() {
        if (FastMine.mc.thePlayer != null) {
            FastMine.mc.thePlayer.removePotionEffect(Potion.digSpeed.id);
        }
    }
}

