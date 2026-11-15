/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.CancellableEvent;

public class KeyPressEvent
extends CancellableEvent {
    public int keyCode;

    public int getKeyCode() {
        return this.keyCode;
    }

    public KeyPressEvent(int pressedKeyCode) {
        this.keyCode = pressedKeyCode;
    }
}

