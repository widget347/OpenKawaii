/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import java.util.Random;

public class SeededRandom
extends Random {
    @Override
    public int nextInt() {
        return super.nextInt();
    }

    @Override
    public float nextFloat() {
        return super.nextFloat();
    }

    @Override
    public int nextInt(int bound) {
        return super.nextInt(bound);
    }

    @Override
    public long nextLong() {
        return super.nextLong();
    }

    public SeededRandom(long seed) {
        super(seed);
    }

    @Override
    public void setSeed(long seed) {
        super.setSeed(seed);
    }

    @Override
    public boolean nextBoolean() {
        return super.nextBoolean();
    }

    @Override
    public double nextDouble() {
        return super.nextDouble();
    }

    public SeededRandom() {
    }
}

