/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.network;

import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetHandlerPlayClient$3$1;
import net.minecraft.client.resources.I18n;

public class NetHandlerPlayClient$3
implements Runnable {
    public String MMg;
    public NetHandlerPlayClient field_178899_c;
    public String SMa;

    public NetHandlerPlayClient$3(NetHandlerPlayClient netHandlerPlayClient, String url, String hash) {
        this.field_178899_c = netHandlerPlayClient;
        this.MMg = url;
        this.SMa = hash;
    }

    @Override
    public void run() {
        NetHandlerPlayClient.access$100(this.field_178899_c).displayGuiScreen(new GuiYesNo(new NetHandlerPlayClient$3$1(this), I18n.format("multiplayer.texturePrompt.line1", new Object[0]), I18n.format("multiplayer.texturePrompt.line2", new Object[0]), 0));
    }
}

