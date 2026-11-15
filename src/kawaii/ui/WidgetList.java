/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.ArrayList;
import kawaii.ui.Button;
import kawaii.ui.Widget;
import kawaii.util.ObjectList;

public interface WidgetList {
    public static final ArrayList<Widget> entries = new ObjectList();

    public void onClear();

    private static void checkMouseOver(int mouseX, int mouseY, Widget widget) {
        widget.isMouseOver(mouseX, mouseY);
    }

    default public void clearAll() {
        entries.clear();
        this.onClear();
    }

    default public void checkMouseOverAll(int mouseX, int mouseY) {
        entries.stream().forEach(widget -> WidgetList.checkMouseOver(mouseX, mouseY, widget));
    }

    default public void runButtonAt(int targetIndex) {
        for (int i = 0; i < entries.size(); ++i) {
            Widget widget;
            if (i == targetIndex && (widget = entries.get(i)) instanceof Button) {
                ((Button)widget).getAction().run();
            }
        }
    }
}

