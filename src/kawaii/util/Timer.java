/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

public class Timer {
    public long lastTime = System.currentTimeMillis();

    public boolean hasTimePassed(long delayMillis) {
        return System.currentTimeMillis() - this.lastTime >= delayMillis;
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastTime;
    }

    public boolean hasTimePassed(double delayMillis) {
        return (double)(System.currentTimeMillis() - this.lastTime) >= delayMillis;
    }
}

