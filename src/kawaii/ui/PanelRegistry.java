/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.ui.ModulePanel;
import kawaii.util.ConcurrentArrayList;

public class PanelRegistry {
    public static CopyOnWriteArrayList<ModulePanel> elements = new ConcurrentArrayList();

    public static CopyOnWriteArrayList<ModulePanel> getElements() {
        return elements;
    }

    public static void register(ModulePanel modulePanel) {
        elements.add(0, modulePanel);
    }
}

