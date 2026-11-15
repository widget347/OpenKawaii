/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network;

import com.google.common.collect.Queues;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import kawaii.Kawaii;
import kawaii.event.PacketEvent;
import kawaii.network.PacketDirection;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NettyCompressionDecoder;
import net.minecraft.network.NettyCompressionEncoder;
import net.minecraft.network.NettyEncryptingDecoder;
import net.minecraft.network.NettyEncryptingEncoder;
import net.minecraft.network.NetworkManager$1;
import net.minecraft.network.NetworkManager$2;
import net.minecraft.network.NetworkManager$3;
import net.minecraft.network.NetworkManager$4;
import net.minecraft.network.NetworkManager$5;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager
extends SimpleChannelInboundHandler<Packet> {
    public IChatComponent terminationReason;
    public static Marker logMarkerNetwork;
    public EnumPacketDirection direction;
    public static Marker logMarkerPackets;
    public boolean disconnected;
    public ReentrantReadWriteLock field_181680_j;
    public static LazyLoadBase<NioEventLoopGroup> clientNioEventLoop;
    public static Logger logger;
    public static AttributeKey<EnumConnectionState> attrKeyConnectionState;
    public Queue<InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
    public boolean isEncrypted;
    public SocketAddress socketAddress;
    public INetHandler packetListener;
    public Channel channel;
    public static LazyLoadBase<LocalEventLoopGroup> clientLocalEventLoop;
    public static LazyLoadBase<EpollEventLoopGroup> clientEpollEventLoop;

    public boolean hasNoChannel() {
        return this.channel == null;
    }

    public void setConnectionState(EnumConnectionState connectionState) {
        this.channel.attr(attrKeyConnectionState).set(connectionState);
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    public void dispatchPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>>[] genericFutureListenerArray) {
        EnumConnectionState packetState = EnumConnectionState.getFromPacket(packet);
        EnumConnectionState currentState = this.channel.attr(attrKeyConnectionState).get();
        if (currentState != packetState) {
            logger.debug("Disabled auto read");
            this.channel.config().setAutoRead(false);
        }
        if (this.channel.eventLoop().inEventLoop()) {
            if (packetState != currentState) {
                this.setConnectionState(packetState);
            }
            ChannelFuture channelFuture = this.channel.writeAndFlush(packet);
            if (genericFutureListenerArray != null) {
                channelFuture.addListeners(genericFutureListenerArray);
            }
            channelFuture.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new NetworkManager$4(this, packetState, currentState, packet, genericFutureListenerArray));
        }
    }

    public boolean getIsencrypted() {
        return this.isEncrypted;
    }

    public void sendPacket(Packet packet) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packet, null);
        } else {
            this.field_181680_j.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, null));
            }
            finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void closeChannel(IChatComponent reason) {
        if (this.channel.isOpen()) {
            this.channel.close().awaitUninterruptibly();
            this.terminationReason = reason;
        }
    }

    public void sendPacketWithEvent(Packet packet) {
        PacketEvent packetEvent = new PacketEvent(PacketDirection.Oxc, packet);
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(packetEvent);
        if (packetEvent.isCancelled()) {
            return;
        }
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packetEvent.packet, null);
        } else {
            this.field_181680_j.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packetEvent.packet, null));
            }
            finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public void checkDisconnected() {
        if (this.channel != null && !this.channel.isOpen()) {
            if (!this.disconnected) {
                this.disconnected = true;
                if (this.getExitMessage() != null) {
                    this.getNetHandler().onDisconnect(this.getExitMessage());
                } else if (this.getNetHandler() != null) {
                    this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                }
            } else {
                logger.warn("handleDisconnection() called twice");
            }
        }
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
    }

    public INetHandler getNetHandler() {
        return this.packetListener;
    }

    @Override
    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.socketAddress = this.channel.remoteAddress();
        try {
            this.setConnectionState(EnumConnectionState.HANDSHAKING);
        }
        catch (Throwable throwable) {
            logger.fatal(throwable);
        }
    }

    public static NetworkManager createNetworkManager(InetAddress inetAddress, int port, boolean useEpoll) {
        LazyLoadBase<? extends MultithreadEventLoopGroup> eventLoopGroup;
        Class channelClass;
        NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        if (Epoll.isAvailable() && useEpoll) {
            channelClass = EpollSocketChannel.class;
            eventLoopGroup = clientEpollEventLoop;
        } else {
            channelClass = NioSocketChannel.class;
            eventLoopGroup = clientNioEventLoop;
        }
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(eventLoopGroup.getValue())).handler(new NetworkManager$1(networkManager))).channel(channelClass)).connect(inetAddress, port).syncUninterruptibly();
        return networkManager;
    }

    static {
        logger = LogManager.getLogger();
        logMarkerNetwork = MarkerManager.getMarker("NETWORK");
        logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
        attrKeyConnectionState = AttributeKey.valueOf("protocol");
        clientNioEventLoop = new NetworkManager$5();
        clientEpollEventLoop = new NetworkManager$2();
        clientLocalEventLoop = new NetworkManager$3();
    }

    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public void enableEncryption(SecretKey secretKey) {
        this.isEncrypted = true;
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, secretKey)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, secretKey)));
    }

    @Override
    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream", new Object[0]));
    }

    public IChatComponent getExitMessage() {
        return this.terminationReason;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendPacket(Packet packet, GenericFutureListener<? extends Future<? super Void>> genericFutureListener, GenericFutureListener<? extends Future<? super Void>> ... genericFutureListenerArray) {
        if (this.isChannelOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(packet, ArrayUtils.add(genericFutureListenerArray, 0, genericFutureListener));
        } else {
            this.field_181680_j.writeLock().lock();
            try {
                this.outboundPacketsQueue.add(new InboundHandlerTuplePacketListener(packet, ArrayUtils.add(genericFutureListenerArray, 0, genericFutureListener)));
            }
            finally {
                this.field_181680_j.writeLock().unlock();
            }
        }
    }

    public NetworkManager(EnumPacketDirection packetDirection) {
        this.field_181680_j = new ReentrantReadWriteLock();
        this.direction = packetDirection;
    }

    public static Channel access$000(NetworkManager networkManager) {
        return networkManager.channel;
    }

    public static NetworkManager provideLocalClient(SocketAddress socketAddress) {
        NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(clientLocalEventLoop.getValue())).handler(new ChannelInitializer(networkManager))).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return networkManager;
    }

    @Override
    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) {
        if (this.channel.isOpen()) {
            try {
                PacketEvent packetEvent = new PacketEvent(PacketDirection.qJa, packet);
                Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(packetEvent);
                if (packetEvent.isCancelled()) {
                    return;
                }
                packetEvent.packet.processPacket(this.packetListener);
            }
            catch (ThreadQuickExitException threadQuickExitException) {
                // empty catch block
            }
        }
    }

    public void disableAutoRead() {
        this.channel.config().setAutoRead(false);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        ChatComponentTranslation chatComponentTranslation = throwable instanceof TimeoutException ? new ChatComponentTranslation("disconnect.timeout", new Object[0]) : new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable);
        this.closeChannel(chatComponentTranslation);
    }

    public void setCompressionTreshold(int threshold) {
        if (threshold >= 0) {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(threshold));
            }
            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(threshold);
            } else {
                this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(threshold));
            }
        } else {
            if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
                this.channel.pipeline().remove("decompress");
            }
            if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
                this.channel.pipeline().remove("compress");
            }
        }
    }

    public void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            this.field_181680_j.readLock().lock();
            try {
                while (!this.outboundPacketsQueue.isEmpty()) {
                    InboundHandlerTuplePacketListener queuedPacket = this.outboundPacketsQueue.poll();
                    this.dispatchPacket(InboundHandlerTuplePacketListener.access$100(queuedPacket), InboundHandlerTuplePacketListener.access$200(queuedPacket));
                }
            }
            finally {
                this.field_181680_j.readLock().unlock();
            }
        }
    }

    public void setNetHandler(INetHandler netHandler) {
        Validate.notNull(netHandler, "packetListener", new Object[0]);
        logger.debug("Set listener of {} to {}", this, netHandler);
        this.packetListener = netHandler;
    }

    public void processReceivedPackets() {
        this.flushOutboundQueue();
        if (this.packetListener instanceof ITickable) {
            ((ITickable)((Object)this.packetListener)).update();
        }
        this.channel.flush();
    }

    public static class InboundHandlerTuplePacketListener {
        public Packet packet;
        public GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

        public static GenericFutureListener[] access$200(InboundHandlerTuplePacketListener tuple) {
            return tuple.futureListeners;
        }

        public static Packet access$100(InboundHandlerTuplePacketListener tuple) {
            return tuple.packet;
        }

        public InboundHandlerTuplePacketListener(Packet packet, GenericFutureListener<? extends Future<? super Void>> ... genericFutureListenerArray) {
            this.packet = packet;
            this.futureListeners = genericFutureListenerArray;
        }
    }

    public static class ChannelInitializer
    extends io.netty.channel.ChannelInitializer {
        public NetworkManager networkManager;

        public void initChannel(Channel channel) {
            channel.pipeline().addLast("packet_handler", (ChannelHandler)this.networkManager);
        }

        public ChannelInitializer(NetworkManager networkManager) {
            this.networkManager = networkManager;
        }
    }
}

