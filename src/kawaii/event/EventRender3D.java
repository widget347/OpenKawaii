/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.CancellableEvent;

public class EventRender3D
extends CancellableEvent {
    public float value;

    public float getValue() {
        return this.value;
    }

    public EventRender3D(float partialTicks) {
        this.value = partialTicks;
    }
}

