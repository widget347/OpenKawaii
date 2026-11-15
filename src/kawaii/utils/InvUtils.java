/*
 * Decompiled with CFR 0.152.
 */
package kawaii.utils;

import kawaii.ui.UiElement;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;

public class InvUtils
implements UiElement {
    public static void dropStackFromSlot(int slotIndex) {
        InvUtils.mc.playerController.windowClick(InvUtils.mc.thePlayer.inventoryContainer.windowId, slotIndex, 1, 4, InvUtils.mc.thePlayer);
    }

    public static void quickMoveSlot(int slotIndex) {
        InvUtils.mc.playerController.windowClick(InvUtils.mc.thePlayer.inventoryContainer.windowId, slotIndex, 0, 1, InvUtils.mc.thePlayer);
    }

    public static int getToolType(Item item) {
        switch (item.getClass().getSimpleName()) {
            case "ItemPickaxe": {
                return 1;
            }
            case "ItemAxe": {
                return 2;
            }
            case "ItemSpade": {
                return 3;
            }
        }
        return -1;
    }

    public static void swapToHotbarSlot(int slotIndex, int button) {
        InvUtils.mc.playerController.windowClick(InvUtils.mc.thePlayer.inventoryContainer.windowId, slotIndex, button, 2, InvUtils.mc.thePlayer);
    }

    public static int getArmorSlot(Item item) {
        return 5 + ((ItemArmor)item).gac;
    }
}

