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
import kawaii.util.Timer;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;

public class ChestStealer
extends Module {
    public int stolenCount = 0;
    public NumberSetting delaySetting;
    public Timer delayTimer = new Timer();
    public BooleanSetting titleCheckSetting;

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        if (cancellableEvent instanceof ImageInitEvent) {
            this.setSuffix("" + this.delaySetting.getValue());
            if (ChestStealer.mc.thePlayer.openContainer != null && ChestStealer.mc.thePlayer.openContainer instanceof ContainerChest) {
                int i;
                boolean isValidChest;
                ContainerChest containerChest = (ContainerChest)ChestStealer.mc.thePlayer.openContainer;
                boolean isChestTitle = isValidChest = this.titleCheckSetting.getValue() == false || containerChest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Chest") || containerChest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Contai") || containerChest.getLowerChestInventory().getDisplayName().getUnformattedText().contains("Crate") || containerChest.getLowerChestInventory().getDisplayName().getUnformattedText().equalsIgnoreCase("LOW");
                if (!isValidChest) {
                    return;
                }
                int remainingItems = 0;
                for (i = 0; i < containerChest.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (containerChest.getLowerChestInventory().getStackInSlot(i) != null && !this.isStealableItem(containerChest.getLowerChestInventory().getStackInSlot(i).getItem())) {
                        ++remainingItems;
                    }
                }
                for (i = 0; i < containerChest.getLowerChestInventory().getSizeInventory(); ++i) {
                    if (containerChest.getLowerChestInventory().getStackInSlot(i) != null && !this.isStealableItem(containerChest.getLowerChestInventory().getStackInSlot(i).getItem()) && this.delayTimer.hasTimePassed(this.delaySetting.getValue())) {
                        ChestStealer.mc.playerController.windowClick(containerChest.windowId, i, 0, 1, ChestStealer.mc.thePlayer);
                        ++this.stolenCount;
                        this.delayTimer.reset();
                    }
                }
                if (remainingItems == 0) {
                    ChestStealer.mc.thePlayer.closeScreen();
                    this.stolenCount = 0;
                }
            }
        }
    }

    public boolean isStealableItem(Item item) {
        return item.getUnlocalizedName().contains("stick") || item.getUnlocalizedName().contains("string") || item.getUnlocalizedName().contains("flint") || item.getUnlocalizedName().contains("bucket") || item.getUnlocalizedName().contains("feather") || item.getUnlocalizedName().contains("snow") || item.getUnlocalizedName().contains("piston") || item instanceof ItemGlassBottle || item.getUnlocalizedName().contains("web") || item.getUnlocalizedName().contains("slime") || item.getUnlocalizedName().contains("trip") || item.getUnlocalizedName().contains("wire") || item.getUnlocalizedName().contains("sugar") || item.getUnlocalizedName().contains("note") || item.getUnlocalizedName().contains("record") || item.getUnlocalizedName().contains("flower") || item.getUnlocalizedName().contains("wheat") || item.getUnlocalizedName().contains("fishing") || item.getUnlocalizedName().contains("boat") || item.getUnlocalizedName().contains("leather") || item.getUnlocalizedName().contains("seeds") || item.getUnlocalizedName().contains("skull") || item.getUnlocalizedName().contains("torch") || item.getUnlocalizedName().contains("anvil") || item.getUnlocalizedName().contains("enchant") || item.getUnlocalizedName().contains("exp") || item.getUnlocalizedName().contains("shears");
    }

    public ChestStealer() {
        super("ChestStealer", "Steals from chests automatically", Category.player);
        this.delaySetting = new NumberSetting("Delay", 50.0, 1.0, 5.0, 150.0);
        this.titleCheckSetting = new BooleanSetting("Title Check", true);
        this.addSettings(this.delaySetting, this.titleCheckSetting);
    }
}

