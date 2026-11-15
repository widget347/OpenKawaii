/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import java.util.Map;
import java.util.function.Consumer;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.EventRender3D;
import kawaii.event.HandlerMap;
import kawaii.event.ImageInitEvent;
import kawaii.event.KeyPressEvent;
import kawaii.event.MotionEvent;
import kawaii.event.PacketEvent;
import kawaii.event.SettingsUpdateEvent;

public abstract class AbstractEventDispatcher {
    public Map<Class<? extends CancellableEvent>, Consumer<CancellableEvent>> handlers = new HandlerMap();

    public void onEvent(KeyPressEvent keyPressEvent) {
    }

    public void onEvent(MotionEvent motionEvent) {
    }

    public void onEvent(ImageInitEvent imageInitEvent) {
    }

    public void registerHandlers() {
        this.register(KeyPressEvent.class, this::onEvent);
        this.register(MotionEvent.class, this::onEvent);
        this.register(EventRender2D.class, this::onEvent);
        this.register(PacketEvent.class, this::onEvent);
        this.register(ImageInitEvent.class, this::onEvent);
        this.register(SettingsUpdateEvent.class, this::onEvent);
        this.register(EventRender3D.class, this::onEvent);
    }

    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
    }

    public void onEvent(EventRender2D eventRender2D) {
    }

    public <T extends CancellableEvent> void register(Class<T> eventClass, Consumer<T> handler) {
        this.handlers.put(eventClass, (Consumer<CancellableEvent>)(Consumer)handler);
    }

    public void onEvent(PacketEvent packetEvent) {
    }

    public void onEvent(CancellableEvent cancellableEvent) {
    }

    public void dispatch(CancellableEvent cancellableEvent) {
        if (this.handlers.isEmpty()) {
            this.registerHandlers();
        }
        this.onEvent(cancellableEvent);
        this.handlers.get(cancellableEvent.getClass()).accept(cancellableEvent);
    }

    public void onEvent(EventRender3D eventRender3D) {
    }
}

