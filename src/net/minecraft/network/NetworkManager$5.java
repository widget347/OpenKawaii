/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.channel.nio.NioEventLoopGroup;
import net.minecraft.util.LazyLoadBase;

/*
 * Signature claims super is io.netty.channel.ChannelInitializer<io.netty.channel.Channel>, not net.minecraft.util.LazyLoadBase - discarding signature.
 */
public class NetworkManager$5
extends LazyLoadBase {
    public NioEventLoopGroup load() {
        return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
    }
}

