/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.CancellableEvent;
import kawaii.network.PacketDirection;
import net.minecraft.network.Packet;

public class PacketEvent
extends CancellableEvent {
    public Packet packet;
    public PacketDirection type;

    public PacketDirection getType() {
        return this.type;
    }

    public PacketEvent(PacketDirection packetDirection, Packet packet) {
        this.type = packetDirection;
        this.packet = packet;
    }
}

