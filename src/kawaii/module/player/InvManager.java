/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.player;

import kawaii.event.CancellableEvent;
import kawaii.event.ImageInitEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.setting.BooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.util.MovementUtil;
import kawaii.util.Timer;
import kawaii.utils.InvUtils;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public class InvManager
extends Module {
    public BooleanSetting inventoryOnlySetting;
    public BooleanSetting notMovingSetting;
    public NumberSetting delaySetting;
    public BooleanSetting sortArmorSetting;
    public BooleanSetting keepToolsSetting;
    public Timer actionTimer = new Timer();

    public void swapToHotbar(int slotIndex, int hotbarIndex) {
        InvUtils.swapToHotbarSlot(slotIndex, hotbarIndex);
    }

    public boolean isBetterSword(ItemStack itemStack, ItemStack itemStack2) {
        return ((ItemSword)itemStack.getItem()).getDamageVsEntity() <= ((ItemSword)itemStack2.getItem()).getDamageVsEntity();
    }

    public boolean hasBetterArmor(ItemStack itemStack) {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack2 = slot.getStack();
            if (itemStack2 == null || itemStack2 == itemStack || !(itemStack2.getItem() instanceof ItemArmor) || ((ItemArmor)itemStack2.getItem()).gac != ((ItemArmor)itemStack.getItem()).gac || !this.isArmorWorseOrEqual(itemStack, itemStack2)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent) {
            this.setSuffix("" + this.delaySetting.getValue());
            if (this.inventoryOnlySetting.getValue().booleanValue() && (InvManager.mc.currentScreen == null || !(InvManager.mc.currentScreen instanceof GuiInventory))) {
                return;
            }
            if (this.notMovingSetting.getValue().booleanValue() && MovementUtil.isMoving()) {
                return;
            }
            if (this.actionTimer.hasTimePassed(this.delaySetting.getValue())) {
                if (this.organizeSwords()) {
                    this.actionTimer.reset();
                    return;
                }
                if (this.sortArmorSetting.getValue().booleanValue() && this.sortArmor()) {
                    this.actionTimer.reset();
                    return;
                }
                if (this.organizeTools()) {
                    this.actionTimer.reset();
                    return;
                }
                this.actionTimer.reset();
            }
        }
    }

    public boolean organizeSwords() {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack = slot.getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemSword)) continue;
            if (!this.isBestSword(itemStack)) {
                this.dropSlot(slot.slotNumber);
                return true;
            }
            if (0 == slot.slotNumber - 36) continue;
            this.swapToHotbar(slot.slotNumber, 0);
        }
        return false;
    }

    public boolean isSameToolType(ItemStack itemStack, ItemStack itemStack2) {
        return InvUtils.getToolType(itemStack2.getItem()) == InvUtils.getToolType(itemStack.getItem());
    }

    public boolean isToolWorseOrEqual(ItemStack itemStack, ItemStack itemStack2) {
        return ((ItemTool)itemStack.getItem()).getToolMaterial().getHarvestLevel() <= ((ItemTool)itemStack2.getItem()).getToolMaterial().getHarvestLevel();
    }

    public boolean isArmorWorseOrEqual(ItemStack itemStack, ItemStack itemStack2) {
        return this.getArmorValue(itemStack) <= this.getArmorValue(itemStack2);
    }

    public boolean organizeTools() {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack = slot.getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemTool)) continue;
            if (!this.keepToolsSetting.getValue().booleanValue() || !this.isBestTool(itemStack)) {
                this.dropSlot(slot.slotNumber);
                return true;
            }
            int targetHotbarSlot = InvUtils.getToolType(itemStack.getItem());
            if (targetHotbarSlot == -1) {
                this.dropSlot(slot.slotNumber);
                return true;
            }
            if (targetHotbarSlot == slot.slotNumber - 36) continue;
            this.swapToHotbar(slot.slotNumber, targetHotbarSlot);
            return true;
        }
        return false;
    }

    public boolean sortArmor() {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack = slot.getStack();
            if (itemStack == null || !(itemStack.getItem() instanceof ItemArmor)) continue;
            int armorSlot = InvUtils.getArmorSlot(itemStack.getItem());
            if (!this.hasBetterArmor(itemStack)) {
                this.dropSlot(slot.slotNumber);
                return true;
            }
            if (slot.slotNumber == armorSlot) continue;
            if (InvManager.mc.thePlayer.inventoryContainer.inventorySlots.get(armorSlot).getStack() != null && InvManager.mc.thePlayer.inventoryContainer.inventorySlots.get(armorSlot).getStack() != slot.getStack()) {
                this.dropSlot(5 + ((ItemArmor)itemStack.getItem()).gac);
                return true;
            }
            InvUtils.quickMoveSlot(slot.slotNumber);
            return true;
        }
        return false;
    }

    public void dropSlot(int slotIndex) {
        InvUtils.dropStackFromSlot(slotIndex);
    }

    public InvManager() {
        super("InvManager", "Organises your inventory automatically.", Category.player);
        this.delaySetting = new NumberSetting("Delay", 50.0, 5.0, 10.0, 500.0);
        this.keepToolsSetting = new BooleanSetting("Keep Tools", true);
        this.inventoryOnlySetting = new BooleanSetting("Inventory Only", true);
        this.sortArmorSetting = new BooleanSetting("Sort Armor", true);
        this.notMovingSetting = new BooleanSetting("Not Moving", false);
        this.addSettings(this.delaySetting, this.inventoryOnlySetting, this.notMovingSetting, this.keepToolsSetting, this.sortArmorSetting);
    }

    public boolean isBestSword(ItemStack itemStack) {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack2 = slot.getStack();
            if (itemStack2 == null || itemStack2 == itemStack || !(itemStack2.getItem() instanceof ItemSword) || !this.isBetterSword(itemStack, itemStack2)) continue;
            return false;
        }
        return true;
    }

    public boolean isBestTool(ItemStack itemStack) {
        for (Slot slot : InvManager.mc.thePlayer.inventoryContainer.inventorySlots) {
            ItemStack itemStack2 = slot.getStack();
            if (itemStack2 == null || itemStack2 == itemStack || !this.isSameToolType(itemStack, itemStack2) || !(itemStack2.getItem() instanceof ItemTool) || !this.isToolWorseOrEqual(itemStack, itemStack2)) continue;
            return false;
        }
        return true;
    }

    public int getArmorValue(ItemStack itemStack) {
        return ((ItemArmor)itemStack.getItem()).damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, itemStack);
    }
}

