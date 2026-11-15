/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

public class NetworkManager$4
implements Runnable {
    public GenericFutureListener[] field_181133_d;
    public Packet field_181132_c;
    public NetworkManager field_181134_e;
    public EnumConnectionState NEa;
    public EnumConnectionState QLa;

    public NetworkManager$4(NetworkManager networkManager, EnumConnectionState packetState, EnumConnectionState currentState, Packet packet, GenericFutureListener[] futureListeners) {
        this.field_181134_e = networkManager;
        this.QLa = packetState;
        this.NEa = currentState;
        this.field_181132_c = packet;
        this.field_181133_d = futureListeners;
    }

    @Override
    public void run() {
        if (this.QLa != this.NEa) {
            this.field_181134_e.setConnectionState(this.QLa);
        }
        ChannelFuture channelFuture = NetworkManager.access$000(this.field_181134_e).writeAndFlush(this.field_181132_c);
        if (this.field_181133_d != null) {
            channelFuture.addListeners(this.field_181133_d);
        }
        channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }
}

