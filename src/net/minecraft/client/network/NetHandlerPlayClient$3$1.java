/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import com.google.common.util.concurrent.Futures;
import kawaii.network.DownloadStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetHandlerPlayClient$3;
import net.minecraft.client.network.NetHandlerPlayClient$3$1$1;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

public class NetHandlerPlayClient$3$1
implements GuiYesNoCallback {
    public NetHandlerPlayClient$3 field_175395_a;

    @Override
    public void confirmClicked(boolean accepted, int id) {
        NetHandlerPlayClient.access$102(this.field_175395_a.field_178899_c, Minecraft.getMinecraft());
        if (accepted) {
            if (NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getCurrentServerData() != null) {
                NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.ENABLED);
            }
            NetHandlerPlayClient.access$000(this.field_175395_a.field_178899_c).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_175395_a.MMg, DownloadStatus.Gsa));
            Futures.addCallback(NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getResourcePackRepository().downloadResourcePack(this.field_175395_a.SMa, this.field_175395_a.MMg), new NetHandlerPlayClient$3$1$1(this));
        } else {
            if (NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getCurrentServerData() != null) {
                NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getCurrentServerData().setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
            NetHandlerPlayClient.access$000(this.field_175395_a.field_178899_c).sendPacketWithEvent(new C19PacketResourcePackStatus(this.field_175395_a.MMg, DownloadStatus.kBp));
        }
        ServerList.recordConnectedServer(NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).getCurrentServerData());
        NetHandlerPlayClient.access$100(this.field_175395_a.field_178899_c).displayGuiScreen(null);
    }

    public NetHandlerPlayClient$3$1(NetHandlerPlayClient$3 owner) {
        this.field_175395_a = owner;
    }
}

