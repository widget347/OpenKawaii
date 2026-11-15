/*
 * Decompiled with CFR 0.152.
 */
package kawaii.network;

import java.net.Proxy;
import java.net.SocketAddress;

public class CustomProxy
extends Proxy {
    public CustomProxy(Proxy.Type type, SocketAddress socketAddress) {
        super(type, socketAddress);
    }
}

