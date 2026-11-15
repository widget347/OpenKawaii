/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.MessageDeserializer2;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;

/*
 * Signature claims super is net.minecraft.util.LazyLoadBase<io.netty.channel.nio.NioEventLoopGroup>, not io.netty.channel.ChannelInitializer - discarding signature.
 */
public class NetworkManager$1
extends ChannelInitializer {
    public NetworkManager this$0;

    public NetworkManager$1(NetworkManager networkManager) {
        this.this$0 = networkManager;
    }

    public void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        }
        catch (ChannelException channelException) {
            // empty catch block
        }
        channel.pipeline().addLast("timeout", (ChannelHandler)new ReadTimeoutHandler(30)).addLast("splitter", (ChannelHandler)new MessageDeserializer2()).addLast("decoder", (ChannelHandler)new MessageDeserializer(EnumPacketDirection.CLIENTBOUND)).addLast("prepender", (ChannelHandler)new MessageSerializer2()).addLast("encoder", (ChannelHandler)new MessageSerializer(EnumPacketDirection.SERVERBOUND)).addLast("packet_handler", (ChannelHandler)this.this$0);
    }
}

