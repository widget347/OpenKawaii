/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.Kawaii;
import kawaii.event.AbstractEventDispatcher;
import kawaii.event.CancellableEvent;
import kawaii.event.KeyPressEvent;
import kawaii.module.Module;
import kawaii.util.ConcurrentArrayList;

public class ModuleKeyHandler {
    public CopyOnWriteArrayList<AbstractEventDispatcher> modules = new ConcurrentArrayList();

    public void removeModule(Module module) {
        this.modules.remove(module);
    }

    public void onKeyInput(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof KeyPressEvent) {
            for (Module module : Kawaii.INSTANCE.getModuleManager().getModules()) {
                if (module.getKeyBind() != ((KeyPressEvent)cancellableEvent).getKeyCode()) continue;
                module.toggle();
            }
        }
        for (AbstractEventDispatcher eventDispatcher : this.modules) {
            eventDispatcher.dispatch(cancellableEvent);
        }
    }

    public void addModule(Module module) {
        this.modules.add(module);
    }
}

