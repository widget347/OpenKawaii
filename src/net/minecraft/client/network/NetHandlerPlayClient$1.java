/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import com.google.common.util.concurrent.FutureCallback;
import kawaii.network.DownloadStatus;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class NetHandlerPlayClient$1
implements FutureCallback<Object> {
    public String field_146300_a;
    public NetHandlerPlayClient field_146299_f;

    @Override
    public void onSuccess(Object result) {
        NetHandlerPlayClient.access$000(this.field_146299_f).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_146300_a, DownloadStatus.Kna));
    }

    public NetHandlerPlayClient$1(NetHandlerPlayClient netHandlerPlayClient, String resourcePackHash) {
        this.field_146299_f = netHandlerPlayClient;
        this.field_146300_a = resourcePackHash;
    }

    @Override
    public void onFailure(Throwable throwable) {
        NetHandlerPlayClient.access$000(this.field_146299_f).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_146300_a, DownloadStatus.UMa));
    }
}

