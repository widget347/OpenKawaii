/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FullBright
extends Module {
    public FullBright() {
        super("FullBright", "Horror Game Type Cheat", Category.visuals);
    }

    @Override
    public void onDisable() {
        if (FullBright.mc.thePlayer != null) {
            FullBright.mc.thePlayer.removePotionEffect(Potion.nightVision.id);
        }
    }

    @Override
    public void onEnable() {
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent && FullBright.mc.thePlayer != null) {
            FullBright.mc.thePlayer.addPotionEffect(new PotionEffect(Potion.nightVision.id, 5200, 2));
        }
    }
}

