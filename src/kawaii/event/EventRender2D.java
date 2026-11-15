/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.CancellableEvent;
import kawaii.event.EventType;
import net.minecraft.client.gui.ScaledResolution;

public class EventRender2D
extends CancellableEvent {
    public ScaledResolution scaledResolution;

    public EventRender2D(EventType eventType, ScaledResolution scaledResolution) {
        this.eventType = eventType;
        this.scaledResolution = scaledResolution;
    }
}

