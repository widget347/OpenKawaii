/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import java.util.ArrayList;
import kawaii.ui.Button;
import kawaii.ui.Widget;
import kawaii.util.ObjectList;

public interface RunnableQueue {
    public static final ArrayList<Widget> tasks = new ObjectList();

    default public void runButtonActionAt(int buttonIndex) {
        for (int i = 0; i < tasks.size(); ++i) {
            Widget widget;
            if (i == buttonIndex && (widget = tasks.get(i)) instanceof Button) {
                ((Button)widget).getAction().run();
            }
        }
    }

    public void onCleared();

    private static void updateMouseOverTask(int mouseX, int mouseY, Widget widget) {
        widget.isMouseOver(mouseX, mouseY);
    }

    default public void updateMouseOver(int mouseX, int mouseY) {
        tasks.stream().forEach(task -> RunnableQueue.updateMouseOverTask(mouseX, mouseY, task));
    }

    default public void clearTasks() {
        tasks.clear();
        this.onCleared();
    }
}

