/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import java.util.List;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.ResourcePackListEntry;

public class Minecraft$9
implements GuiYesNoCallback {
    public ResourcePackListEntry qzp;

    public Minecraft$9(ResourcePackListEntry resourcePackListEntry) {
        this.qzp = resourcePackListEntry;
    }

    @Override
    public void confirmClicked(boolean confirmed, int buttonId) {
        List<ResourcePackListEntry> list = this.qzp.resourcePacksGUI.getListContaining(this.qzp);
        this.qzp.mc.displayGuiScreen(this.qzp.resourcePacksGUI);
        if (confirmed) {
            list.remove(this.qzp);
            this.qzp.resourcePacksGUI.Zcg().add(0, this.qzp);
        }
    }
}

