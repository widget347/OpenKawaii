/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import net.minecraft.client.Minecraft;

public class Minecraft$11
extends Thread {
    public Minecraft field_74536_a;

    @Override
    public void run() {
        while (this.field_74536_a.running) {
            try {
                Thread.sleep(2147483647L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    public Minecraft$11(Minecraft minecraft, String threadName) {
        super(threadName);
        this.field_74536_a = minecraft;
    }
}

