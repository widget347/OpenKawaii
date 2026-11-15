/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import com.google.common.util.concurrent.FutureCallback;
import kawaii.network.DownloadStatus;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetHandlerPlayClient$3$1;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class NetHandlerPlayClient$3$1$1
implements FutureCallback<Object> {
    public NetHandlerPlayClient$3$1 field_178886_a;

    @Override
    public void onSuccess(Object result) {
        NetHandlerPlayClient.access$000(this.field_178886_a.field_175395_a.field_178899_c).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_178886_a.field_175395_a.MMg, DownloadStatus.Kna));
    }

    @Override
    public void onFailure(Throwable throwable) {
        NetHandlerPlayClient.access$000(this.field_178886_a.field_175395_a.field_178899_c).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_178886_a.field_175395_a.MMg, DownloadStatus.UMa));
    }

    public NetHandlerPlayClient$3$1$1(NetHandlerPlayClient$3$1 netHandlerPlayClient$3$1) {
        this.field_178886_a = netHandlerPlayClient$3$1;
    }
}

