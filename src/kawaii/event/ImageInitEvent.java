/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import javax.imageio.ImageIO;
import kawaii.event.CancellableEvent;
import kawaii.event.EventType;

public class ImageInitEvent
extends CancellableEvent {
    public ImageInitEvent(EventType eventType) {
        this.setEventType(eventType);
    }

    public static void setUseCache(boolean useCache, char unusedChar) {
        ImageIO.setUseCache(useCache);
    }
}

