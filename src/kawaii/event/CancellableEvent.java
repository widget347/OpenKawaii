/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.EventType;

public class CancellableEvent {
    public EventType eventType;
    public boolean cancelled;

    public void setEventType(EventType type) {
        this.eventType = type;
    }

    public EventType getEventType() {
        return this.eventType;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void setCancelled(boolean value) {
        this.cancelled = value;
    }
}

