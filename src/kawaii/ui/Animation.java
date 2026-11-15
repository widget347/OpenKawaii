/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

public class Animation {
    public double start = -1.0;
    public double end = 1.0;
    public long startTime = 0L;
    public boolean reversed = false;
    public int easing = 0;
    public double value;

    public int getEasing() {
        return this.easing;
    }

    public double getValue() {
        return this.value;
    }

    public double getStart() {
        return this.start;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    public static double calculateProgress(double duration, long startTimeMs) {
        double elapsed = System.currentTimeMillis() - startTimeMs;
        double progress = elapsed / duration * 100.0;
        return progress <= 100.0 ? progress : 100.0;
    }

    public void restart() {
        this.startTime = System.currentTimeMillis();
    }

    public boolean isFinished() {
        return this.reversed ? this.getValue() == 0.0 : this.getValue() == 100.0 || this.startTime == 0L;
    }

    public Animation setEasing(int easingMs) {
        this.easing = easingMs;
        return this;
    }

    public double getFraction() {
        return this.value / 100.0;
    }

    public void adjustStartTime(boolean reverse) {
        this.startTime = (long)((double)System.currentTimeMillis() - (reverse ? (double)this.easing / this.end - this.start : this.start));
    }

    public Animation setReversed(boolean reverse) {
        if (this.reversed != reverse) {
            this.adjustStartTime(reverse);
            this.reversed = reverse;
            return this;
        }
        return this;
    }

    public double getEnd() {
        return this.end;
    }

    public static double scaleByPercent(double base, double percent) {
        return percent / 100.0 * base;
    }

    public Animation setEnd(double endValue) {
        this.end = endValue;
        return this;
    }

    public void update() {
        double progress = Animation.calculateProgress((double)this.easing / this.end, this.startTime);
        if (this.reversed) {
            progress = 100.0 - progress;
        }
        this.start = Animation.scaleByPercent(progress, this.easing);
        this.value = progress;
    }
}

