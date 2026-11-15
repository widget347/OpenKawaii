/*
 * Decompiled with CFR 0.152.
 */
package kawaii.ui;

import java.util.concurrent.CopyOnWriteArrayList;
import kawaii.ui.Notification;
import kawaii.util.ConcurrentArrayList;
import net.minecraft.client.gui.ScaledResolution;

public class InfoElementManager {
    public CopyOnWriteArrayList<Notification> elements = new ConcurrentArrayList();

    public void addElement(Notification notification) {
        this.elements.add(notification);
    }

    public void renderElements(ScaledResolution scaledResolution) {
        int renderIndex = 0;
        for (Notification notification : this.elements) {
            if (notification.getRemainingTime() >= 0) {
                notification.render(scaledResolution, renderIndex);
                ++renderIndex;
                continue;
            }
            this.elements.remove(notification);
        }
    }
}

