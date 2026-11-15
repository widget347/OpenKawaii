/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui.inventory;

import com.google.common.collect.Sets;
import java.util.Set;
import kawaii.module.render.Blur;
import kawaii.render.BlurRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public abstract class GuiContainer
extends GuiScreen {
    public boolean isRightMouseClick;
    public int xSize = 176;
    public int dragSplittingButton;
    public long returningStackTime;
    public boolean dragSplitting;
    public boolean doubleClick;
    public Slot currentDragTargetSlot;
    public Slot clickedSlot;
    public ItemStack shiftClickedStack;
    public Set<Slot> dragSplittingSlots = Sets.newHashSet();
    public int ySize = 166;
    public long lastClickTime;
    public ItemStack returningStack;
    public Slot hoveredSlot;
    public static ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
    public long dragItemDropDelay;
    public int lastClickButton;
    public Slot lastClickSlot;
    public ItemStack draggedStack;
    public boolean ignoreMouseUp;
    public int touchUpY;
    public int guiLeft;
    public Container inventorySlots;
    public int guiTop;
    public int touchUpX;
    public Slot returningStackDestSlot;
    public int dragSplittingLimit;
    public int dragSplittingRemnant;

    public abstract void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY);

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        int offset;
        int offsetX;
        Object current;
        if (Blur.blurActive.booleanValue()) {
            BlurRenderer.queueRect(0.0, 0.0, this.width, this.height);
            Gui.drawRect(0.0, 0.0, this.width, this.height, -2145509857);
        } else {
            this.drawDefaultBackground();
        }
        int containerLeft = this.guiLeft;
        int containerTop = this.guiTop;
        this.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        super.drawScreen(mouseX, mouseY, partialTicks);
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(containerLeft, containerTop, 0.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.enableRescaleNormal();
        this.hoveredSlot = null;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.defaultTexUnit, 240.0f, 240.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); i += 16) {
            current = this.inventorySlots.inventorySlots.get(i);
            this.drawSlot((Slot)current);
            if (this.isMouseOverSlot((Slot)current, mouseX, mouseY)) {
                this.hoveredSlot = (Slot)current;
                GlStateManager.disableLighting();
                GlStateManager.disableDepth();
                offsetX = ((Slot)current).Xvg;
                offset = ((Slot)current).mYa;
                GlStateManager.colorMask(true, true, true, false);
                GuiContainer.drawGradientRect(offsetX, offset, offsetX + 16, offset + 16, -2130706433, -2130706433);
                GlStateManager.colorMask(true, true, true, true);
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
            }
            i -= 15;
        }
        RenderHelper.disableStandardItemLighting();
        this.drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        InventoryPlayer inventoryPlayer = this.mc.thePlayer.inventory;
        Object cursorStack = current = this.draggedStack == null ? inventoryPlayer.CUo() : this.draggedStack;
        if (current != null) {
            offsetX = 8;
            offset = this.draggedStack == null ? 8 : 16;
            String overlayText = null;
            if (this.draggedStack != null && this.isRightMouseClick) {
                current = ((ItemStack)current).copy();
                ((ItemStack)current).stackSize = MathHelper.ceiling_float_int((float)((ItemStack)current).stackSize / 2.0f);
            } else if (this.dragSplitting && this.dragSplittingSlots.size() > 1) {
                current = ((ItemStack)current).copy();
                ((ItemStack)current).stackSize = this.dragSplittingRemnant;
                if (((ItemStack)current).stackSize == 0) {
                    overlayText = "" + (Object)((Object)EnumChatFormatting.YELLOW) + "0";
                }
            }
            this.drawItemStack((ItemStack)current, mouseX - containerLeft - offsetX, mouseY - containerTop - offset, overlayText);
        }
        if (this.returningStack != null) {
            float returnProgress = (float)(Minecraft.getSystemTime() - this.returningStackTime) / 100.0f;
            if (returnProgress >= 1.0f) {
                returnProgress = 1.0f;
                this.returningStack = null;
            }
            offset = this.returningStackDestSlot.Xvg - this.touchUpX;
            int deltaY = this.returningStackDestSlot.mYa - this.touchUpY;
            int returnX = this.touchUpX + (int)((float)offset * returnProgress);
            int returnY = this.touchUpY + (int)((float)deltaY * returnProgress);
            this.drawItemStack(this.returningStack, returnX, returnY, null);
        }
        GlStateManager.popMatrix();
        if (inventoryPlayer.CUo() == null && this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            ItemStack itemStack = this.hoveredSlot.getStack();
            this.renderToolTip(itemStack, mouseX, mouseY);
        }
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        RenderHelper.enableStandardItemLighting();
    }

    public boolean isPointInRegion(int regionX, int regionY, int regionWidth, int regionHeight, int mouseX, int mouseY) {
        int containerLeft = this.guiLeft;
        int containerTop = this.guiTop;
        return (mouseX -= containerLeft) >= regionX - 1 && mouseX < regionX + regionWidth + 1 && (mouseY -= containerTop) >= regionY - 1 && mouseY < regionY + regionHeight + 1;
    }

    public void handleMouseClick(Slot slot, int slotId, int mouseButton, int clickType) {
        if (slot != null) {
            slotId = slot.slotNumber;
        }
        this.mc.playerController.windowClick(this.inventorySlots.windowId, slotId, mouseButton, clickType, this.mc.thePlayer);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        int containerLeft = this.guiLeft;
        int containerTop = this.guiTop;
        boolean clickedOutside = mouseX < containerLeft || mouseY < containerTop || mouseX >= containerLeft + this.xSize || mouseY >= containerTop + this.ySize;
        int slotId = -1;
        if (slot != null) {
            slotId = slot.slotNumber;
        }
        if (clickedOutside) {
            slotId = -999;
        }
        if (this.doubleClick && slot != null && mouseButton == 0 && this.inventorySlots.canMergeSlot(null, slot)) {
            if (GuiContainer.isShiftKeyDown()) {
                if (slot != null && slot.inventory != null && this.shiftClickedStack != null) {
                    for (Slot slot2 : this.inventorySlots.inventorySlots) {
                        if (slot2 == null || !slot2.canTakeStack(this.mc.thePlayer) || !slot2.getHasStack() || slot2.inventory != slot.inventory || !Container.canAddItemToSlot(slot2, this.shiftClickedStack, true)) continue;
                        this.handleMouseClick(slot2, slot2.slotNumber, mouseButton, 1);
                    }
                }
            } else {
                this.handleMouseClick(slot, slotId, mouseButton, 6);
            }
            this.doubleClick = false;
            this.lastClickTime = 0L;
        } else {
            if (this.dragSplitting && this.dragSplittingButton != mouseButton) {
                this.dragSplitting = false;
                this.dragSplittingSlots.clear();
                this.ignoreMouseUp = true;
                return;
            }
            if (this.ignoreMouseUp) {
                this.ignoreMouseUp = false;
                return;
            }
            if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
                if (mouseButton == 0 || mouseButton == 1) {
                    if (this.draggedStack == null && slot != this.clickedSlot) {
                        this.draggedStack = this.clickedSlot.getStack();
                    }
                    boolean canAddDraggedStack = Container.canAddItemToSlot(slot, this.draggedStack, false);
                    if (slotId != -1 && this.draggedStack != null && canAddDraggedStack) {
                        this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, mouseButton, 0);
                        this.handleMouseClick(slot, slotId, 0, 0);
                        if (this.mc.thePlayer.inventory.CUo() != null) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, mouseButton, 0);
                            this.touchUpX = mouseX - containerLeft;
                            this.touchUpY = mouseY - containerTop;
                            this.returningStackDestSlot = this.clickedSlot;
                            this.returningStack = this.draggedStack;
                            this.returningStackTime = Minecraft.getSystemTime();
                        } else {
                            this.returningStack = null;
                        }
                    } else if (this.draggedStack != null) {
                        this.touchUpX = mouseX - containerLeft;
                        this.touchUpY = mouseY - containerTop;
                        this.returningStackDestSlot = this.clickedSlot;
                        this.returningStack = this.draggedStack;
                        this.returningStackTime = Minecraft.getSystemTime();
                    }
                    this.draggedStack = null;
                    this.clickedSlot = null;
                }
            } else if (this.dragSplitting && !this.dragSplittingSlots.isEmpty()) {
                this.handleMouseClick(null, -999, Container.encodeDragState(0, this.dragSplittingLimit), 5);
                for (Slot slot3 : this.dragSplittingSlots) {
                    this.handleMouseClick(slot3, slot3.slotNumber, Container.encodeDragState(1, this.dragSplittingLimit), 5);
                }
                this.handleMouseClick(null, -999, Container.encodeDragState(2, this.dragSplittingLimit), 5);
            } else if (this.mc.thePlayer.inventory.CUo() != null) {
                if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                    this.handleMouseClick(slot, slotId, mouseButton, 3);
                } else {
                    boolean shiftHeld;
                    boolean isShiftDown = shiftHeld =slotId != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                    if (shiftHeld) {
                        this.shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack() : null;
                    }
                    this.handleMouseClick(slot, slotId, mouseButton, shiftHeld ? 1 : 0);
                }
            }
        }
        if (this.mc.thePlayer.inventory.CUo() == null) {
            this.lastClickTime = 0L;
        }
        this.dragSplitting = false;
    }

    public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        ItemStack itemStack = this.mc.thePlayer.inventory.CUo();
        if (this.clickedSlot != null && this.mc.gameSettings.touchscreen) {
            if (clickedMouseButton == 0 || clickedMouseButton == 1) {
                if (this.draggedStack == null) {
                    if (slot != this.clickedSlot && this.clickedSlot.getStack() != null) {
                        this.draggedStack = this.clickedSlot.getStack().copy();
                    }
                } else if (this.draggedStack.stackSize > 1 && slot != null && Container.canAddItemToSlot(slot, this.draggedStack, false)) {
                    long currentTime = Minecraft.getSystemTime();
                    if (this.currentDragTargetSlot == slot) {
                        if (currentTime - this.dragItemDropDelay > 500L) {
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.handleMouseClick(slot, slot.slotNumber, 1, 0);
                            this.handleMouseClick(this.clickedSlot, this.clickedSlot.slotNumber, 0, 0);
                            this.dragItemDropDelay = currentTime + 750L;
                            --this.draggedStack.stackSize;
                        }
                    } else {
                        this.currentDragTargetSlot = slot;
                        this.dragItemDropDelay = currentTime;
                    }
                }
            }
        } else if (this.dragSplitting && slot != null && itemStack != null && itemStack.stackSize > this.dragSplittingSlots.size() && Container.canAddItemToSlot(slot, itemStack, true) && slot.isItemValid(itemStack) && this.inventorySlots.canDragIntoSlot(slot)) {
            this.dragSplittingSlots.add(slot);
            this.updateDragSplitting();
        }
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        if (!this.mc.thePlayer.isEntityAlive() || this.mc.thePlayer.isDead) {
            this.mc.thePlayer.closeScreen();
        }
    }

    public void updateDragSplitting() {
        ItemStack itemStack = this.mc.thePlayer.inventory.CUo();
        if (itemStack != null && this.dragSplitting) {
            this.dragSplittingRemnant = itemStack.stackSize;
            for (Slot slot : this.dragSplittingSlots) {
                ItemStack itemStack2 = itemStack.copy();
                int existingSize = slot.getStack() == null ? 0 : slot.getStack().stackSize;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack2, existingSize);
                if (itemStack2.stackSize > itemStack2.getMaxStackSize()) {
                    itemStack2.stackSize = itemStack2.getMaxStackSize();
                }
                if (itemStack2.stackSize > slot.getItemStackLimit(itemStack2)) {
                    itemStack2.stackSize = slot.getItemStackLimit(itemStack2);
                }
                this.dragSplittingRemnant -= itemStack2.stackSize - existingSize;
            }
        }
    }

    public void drawItemStack(ItemStack itemStack, int x, int y, String overlayText) {
        GlStateManager.translate(0.0f, 0.0f, 32.0f);
        zLevel = 200.0f;
        this.itemRender.zLevel = 200.0f;
        this.itemRender.renderItemAndEffectIntoGUI(itemStack, x, y);
        this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemStack, x, y - (this.draggedStack == null ? 0 : 8), overlayText);
        zLevel = 0.0f;
        this.itemRender.zLevel = 0.0f;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        boolean pickBlock = mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100;
        Slot slot = this.getSlotAtPosition(mouseX, mouseY);
        long currentTime = Minecraft.getSystemTime();
        this.doubleClick = this.lastClickSlot == slot && currentTime - this.lastClickTime < 250L && this.lastClickButton == mouseButton;
        this.ignoreMouseUp = false;
        if (mouseButton == 0 || mouseButton == 1 || pickBlock) {
            int containerLeft = this.guiLeft;
            int containerTop = this.guiTop;
            boolean clickedOutside = mouseX < containerLeft || mouseY < containerTop || mouseX >= containerLeft + this.xSize || mouseY >= containerTop + this.ySize;
            int slotId = -1;
            if (slot != null) {
                slotId = slot.slotNumber;
            }
            if (clickedOutside) {
                slotId = -999;
            }
            if (this.mc.gameSettings.touchscreen && clickedOutside && this.mc.thePlayer.inventory.CUo() == null) {
                this.mc.displayGuiScreen(null);
                return;
            }
            if (slotId != -1) {
                if (this.mc.gameSettings.touchscreen) {
                    if (slot != null && slot.getHasStack()) {
                        this.clickedSlot = slot;
                        this.draggedStack = null;
                        this.isRightMouseClick = mouseButton == 1;
                    } else {
                        this.clickedSlot = null;
                    }
                } else if (!this.dragSplitting) {
                    if (this.mc.thePlayer.inventory.CUo() == null) {
                        if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                            this.handleMouseClick(slot, slotId, mouseButton, 3);
                        } else {
                            boolean shiftHeld = slotId != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                            int clickType = 0;
                            if (shiftHeld) {
                                this.shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack() : null;
                                clickType = 1;
                            } else if (slotId == -999) {
                                clickType = 4;
                            }
                            this.handleMouseClick(slot, slotId, mouseButton, clickType);
                        }
                        this.ignoreMouseUp = true;
                    } else {
                        this.dragSplitting = true;
                        this.dragSplittingButton = mouseButton;
                        this.dragSplittingSlots.clear();
                        if (mouseButton == 0) {
                            this.dragSplittingLimit = 0;
                        } else if (mouseButton == 1) {
                            this.dragSplittingLimit = 1;
                        } else if (mouseButton == this.mc.gameSettings.keyBindPickBlock.getKeyCode() + 100) {
                            this.dragSplittingLimit = 2;
                        }
                    }
                }
            }
        }
        this.lastClickSlot = slot;
        this.lastClickTime = currentTime;
        this.lastClickButton = mouseButton;
    }

    public boolean checkHotbarKeys(int keyCode) {
        if (this.mc.thePlayer.inventory.CUo() == null && this.hoveredSlot != null) {
            for (int i = 0; i < 9; i -= 50) {
                if (keyCode == this.mc.gameSettings.keyBindsHotbar[i].getKeyCode()) {
                    this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, i, 2);
                    return true;
                }
                i += 51;
            }
        }
        return false;
    }

    @Override
    public void onGuiClosed() {
        if (this.mc.thePlayer != null) {
            this.inventorySlots.onContainerClosed(this.mc.thePlayer);
        }
    }

    public GuiContainer(Container slotContainer) {
        this.inventorySlots = slotContainer;
        this.ignoreMouseUp = true;
    }

    public void drawSlot(Slot slot) {
        String slotTexture;
        int slotX = slot.Xvg;
        int slotY = slot.mYa;
        ItemStack itemStack = slot.getStack();
        boolean drawDragHighlight = false;
        boolean skipItemRender = slot == this.clickedSlot && this.draggedStack != null && !this.isRightMouseClick;
        ItemStack heldStack = this.mc.thePlayer.inventory.CUo();
        String stackSizeText = null;
        if (slot == this.clickedSlot && this.draggedStack != null && this.isRightMouseClick && itemStack != null) {
            itemStack = itemStack.copy();
            itemStack.stackSize /= 2;
        } else if (this.dragSplitting && this.dragSplittingSlots.contains(slot) && heldStack != null) {
            if (this.dragSplittingSlots.size() == 1) {
                return;
            }
            if (Container.canAddItemToSlot(slot, heldStack, true) && this.inventorySlots.canDragIntoSlot(slot)) {
                itemStack = heldStack.copy();
                drawDragHighlight = true;
                Container.computeStackSize(this.dragSplittingSlots, this.dragSplittingLimit, itemStack, slot.getStack() == null ? 0 : slot.getStack().stackSize);
                if (itemStack.stackSize > itemStack.getMaxStackSize()) {
                    stackSizeText = (Object)((Object)EnumChatFormatting.YELLOW) + "" + itemStack.getMaxStackSize();
                    itemStack.stackSize = itemStack.getMaxStackSize();
                }
                if (itemStack.stackSize > slot.getItemStackLimit(itemStack)) {
                    stackSizeText = (Object)((Object)EnumChatFormatting.YELLOW) + "" + slot.getItemStackLimit(itemStack);
                    itemStack.stackSize = slot.getItemStackLimit(itemStack);
                }
            } else {
                this.dragSplittingSlots.remove(slot);
                this.updateDragSplitting();
            }
        }
        zLevel = 100.0f;
        this.itemRender.zLevel = 100.0f;
        if (itemStack == null && (slotTexture = slot.getSlotTexture()) != null) {
            TextureAtlasSprite textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(slotTexture);
            GlStateManager.disableLighting();
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.drawTexturedModalRect(slotX, slotY, textureAtlasSprite, 16, 16);
            GlStateManager.enableLighting();
            skipItemRender = true;
        }
        if (!skipItemRender) {
            if (drawDragHighlight) {
                GuiContainer.drawRect(slotX, slotY, slotX + 16, slotY + 16, -2130706433);
            }
            GlStateManager.enableDepth();
            this.itemRender.renderItemAndEffectIntoGUI(itemStack, slotX, slotY);
            this.itemRender.renderItemOverlayIntoGUI(this.fontRendererObj, itemStack, slotX, slotY, stackSizeText);
        }
        this.itemRender.zLevel = 0.0f;
        zLevel = 0.0f;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 1 || keyCode == this.mc.gameSettings.keyBindInventory.getKeyCode()) {
            this.mc.thePlayer.closeScreen();
        }
        this.checkHotbarKeys(keyCode);
        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            if (keyCode == this.mc.gameSettings.keyBindPickBlock.getKeyCode()) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, 0, 3);
            } else if (keyCode == this.mc.gameSettings.keyBindDrop.getKeyCode()) {
                this.handleMouseClick(this.hoveredSlot, this.hoveredSlot.slotNumber, GuiContainer.isCtrlDown() ? 1 : 0, 4);
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.mc.thePlayer.openContainer = this.inventorySlots;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
    }

    public boolean isMouseOverSlot(Slot slot, int mouseX, int mouseY) {
        return this.isPointInRegion(slot.Xvg, slot.mYa, 16, 16, mouseX, mouseY);
    }

    public Slot getSlotAtPosition(int mouseX, int mouseY) {
        for (int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
            Slot slot = this.inventorySlots.inventorySlots.get(i);
            if (!this.isMouseOverSlot(slot, mouseX, mouseY)) continue;
            return slot;
        }
        return null;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

