/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import com.google.common.util.concurrent.FutureCallback;
import kawaii.network.DownloadStatus;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class NetHandlerPlayClient$2
implements FutureCallback<Object> {
    public NetHandlerPlayClient field_178896_b;
    public String field_178897_a;

    @Override
    public void onFailure(Throwable throwable) {
        NetHandlerPlayClient.access$000(this.field_178896_b).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_178897_a, DownloadStatus.UMa));
    }

    public NetHandlerPlayClient$2(NetHandlerPlayClient netHandlerPlayClient, String resourcePackHash) {
        this.field_178896_b = netHandlerPlayClient;
        this.field_178897_a = resourcePackHash;
    }

    @Override
    public void onSuccess(Object result) {
        NetHandlerPlayClient.access$000(this.field_178896_b).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_178897_a, DownloadStatus.Kna));
    }
}

