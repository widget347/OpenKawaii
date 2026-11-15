/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.play.client;

import kawaii.network.DownloadStatus;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C19PacketResourcePackStatus
implements Packet<INetHandlerPlayServer> {
    public DownloadStatus downloadStatus;
    public String hash;

    public C19PacketResourcePackStatus() {
    }

    @Override
    public void writePacketData(PacketBuffer packetBuffer) {
        this.hash = packetBuffer.readStringFromBuffer(40);
        this.downloadStatus = packetBuffer.readEnumValue(DownloadStatus.class);
    }

    @Override
    public void readPacketData(PacketBuffer packetBuffer) {
        packetBuffer.writeString(this.hash);
        packetBuffer.writeEnumValue(this.downloadStatus);
    }

    public C19PacketResourcePackStatus(String resourceHash, DownloadStatus downloadStatus) {
        if (resourceHash.length() > 40) {
            resourceHash = resourceHash.substring(0, 40);
        }
        this.hash = resourceHash;
        this.downloadStatus = downloadStatus;
    }

    @Override
    public void processPacket(INetHandlerPlayServer netHandler) {
        netHandler.$Ro(this);
    }

    public static enum Action {
        ERROR,
        WARNING,
        INFORMATION,
        SUCCESS;

    }
}

