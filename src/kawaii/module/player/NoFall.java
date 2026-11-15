/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.player;

import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.event.PacketEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.ModeSetting;
import net.minecraft.network.play.client.C03PacketPlayer;

public class NoFall
extends Module {
    public ModeSetting mode = new ModeSetting("Mode", "Spoof", "Packet");

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (NoFall.mc.thePlayer == null) {
            return;
        }
        switch (this.mode.getMode()) {
            case "Spoof": {
                if (!(cancellableEvent instanceof PacketEvent)) break;
                PacketEvent packetEvent = (PacketEvent)cancellableEvent;
                if (!((double)NoFall.mc.thePlayer.fallDistance >= 2.5) || !(packetEvent.packet instanceof C03PacketPlayer)) break;
                C03PacketPlayer c03PacketPlayer = (C03PacketPlayer)packetEvent.packet;
                c03PacketPlayer.onGround = true;
                NoFall.mc.thePlayer.fallDistance = 0.0f;
                break;
            }
            case "Packet": {
                if (!(cancellableEvent instanceof ImageInitEvent) || !((double)NoFall.mc.thePlayer.fallDistance >= 2.5)) break;
                NoFall.mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
                NoFall.mc.thePlayer.fallDistance = 0.0f;
            }
        }
    }

    public NoFall() {
        super("NoFall", "Prevents Fall Damage", Category.player);
        this.addSettings(this.mode);
    }
}

