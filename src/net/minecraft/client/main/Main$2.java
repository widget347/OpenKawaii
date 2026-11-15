/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.main;

import net.minecraft.client.Minecraft;

public class Main$2
extends Thread {
    public Main$2(String threadName) {
        super(threadName);
    }

    @Override
    public void run() {
        Minecraft.stopIntegratedServer();
    }
}

