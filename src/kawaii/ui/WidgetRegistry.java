/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.ArrayList;
import kawaii.ui.Widget;
import kawaii.util.ObjectList;

public interface WidgetRegistry {
    public static final ArrayList<Widget> entries = new ObjectList();

    default public void clearAll() {
        entries.clear();
        this.onClear();
    }

    public void onClear();

    default public boolean anyEntryMatches(int mouseX, int mouseY) {
        for (Widget widget : entries) {
            if (!widget.isMouseOver(mouseX, mouseY)) continue;
            return true;
        }
        return false;
    }
}

