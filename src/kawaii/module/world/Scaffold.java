/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.world;

import java.util.Arrays;
import java.util.List;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.event.ImageInitEvent;
import kawaii.event.MotionEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.render.Blur;
import kawaii.module.render.Hud;
import kawaii.render.BlurRenderer;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.font.FontLoader;
import kawaii.util.BlockFace;
import kawaii.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Scaffold
extends Module {
    public NumberSetting timerSetting;
    public Timer teleportTimer;
    public ModeSetting modeSetting;
    public ModeSetting placeEventSetting;
    public static ModeSetting towerSetting = new ModeSetting("Tower", "Disabled", "LowJump", "Motion", "Teleport");
    public BlockFace placeFace;
    public ModeSetting itemModeSetting;
    public List<Block> blockList = Arrays.asList(Blocks.air, Blocks.water, Blocks.flowing_water, Blocks.lava, Blocks.flowing_lava, Blocks.enchanting_table, Blocks.carpet, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.snow_layer, Blocks.ice, Blocks.packed_ice, Blocks.coal_ore, Blocks.diamond_ore, Blocks.emerald_ore, Blocks.chest, Blocks.trapped_chest, Blocks.torch, Blocks.anvil, Blocks.trapped_chest, Blocks.noteblock, Blocks.jukebox, Blocks.tnt, Blocks.gold_ore, Blocks.iron_ore, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.quartz_ore, Blocks.redstone_ore, Blocks.wooden_pressure_plate, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.tallgrass, Blocks.tripwire, Blocks.tripwire_hook, Blocks.rail, Blocks.waterlily, Blocks.red_flower, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.vine, Blocks.trapdoor, Blocks.yellow_flower, Blocks.ladder, Blocks.furnace, Blocks.sand, Blocks.cactus, Blocks.dispenser, Blocks.noteblock, Blocks.dropper, Blocks.crafting_table, Blocks.web, Blocks.pumpkin, Blocks.sapling, Blocks.cobblestone_wall, Blocks.fence);
    public BooleanSetting sprintSetting;

    public int getBlockCountInInventory() {
        int blockCount = 0;
        for (int i = 0; i < Scaffold.mc.thePlayer.inventory.mainInventory.length; ++i) {
            ItemStack itemStack = Scaffold.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack != null && this.isInBlockList(itemStack) && itemStack.stackSize >= 1) {
                blockCount += itemStack.stackSize;
            }
        }
        return blockCount;
    }

    public BlockPos findAirBlockPos(double distance) {
        if (this.getBlock(new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ)) instanceof BlockAir) {
            return new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ);
        }
        double directionX = Scaffold.mc.thePlayer.posX - Scaffold.mc.thePlayer.lastTickPosX > 0.1 ? 1 : 0;
        double directionZ = Scaffold.mc.thePlayer.posZ - Scaffold.mc.thePlayer.lastTickPosZ > 0.1 ? 1 : 0;
        if (Scaffold.mc.thePlayer.lastTickPosX - Scaffold.mc.thePlayer.posX > 0.1) {
            directionX = -1.0;
        }
        if (Scaffold.mc.thePlayer.lastTickPosZ - Scaffold.mc.thePlayer.posZ > 0.1) {
            directionZ = -1.0;
        }
        int step = 0;
        while ((double)step < distance) {
            if (this.getBlock(new BlockPos(Scaffold.mc.thePlayer.posX + (double)step * directionX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ + (double)step * directionZ)) instanceof BlockAir) {
                return new BlockPos(Scaffold.mc.thePlayer.posX + (double)step * directionX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ + (double)step * directionZ);
            }
            ++step;
        }
        return null;
    }

    public float[] getRotations(BlockPos blockPos) {
        double deltaX = (double)blockPos.getX() - Scaffold.mc.thePlayer.posX;
        double deltaY = (double)blockPos.getY() - (Scaffold.mc.thePlayer.posY + (double)Scaffold.mc.thePlayer.getEyeHeight() * 1.8);
        if ((double)(blockPos.getY() - 1) > Scaffold.mc.thePlayer.posY) {
            deltaY = (double)blockPos.getY() - (Scaffold.mc.thePlayer.posY + (double)Scaffold.mc.thePlayer.getEyeHeight() * 1.1);
        }
        double deltaZ = (double)blockPos.getZ() - Scaffold.mc.thePlayer.posZ;
        double horizontalDistance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / horizontalDistance)));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        if (yaw > 180.0f) {
            yaw = 180.0f;
        }
        if (yaw < -180.0f) {
            yaw = -180.0f;
        }
        return new float[]{yaw, pitch};
    }

    public int findBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            ItemStack itemStack = Scaffold.mc.thePlayer.inventory.mainInventory[i];
            if (itemStack == null || !this.isInBlockList(itemStack) || itemStack.stackSize < 1) continue;
            return i;
        }
        return -1;
    }

    @Override
    public void onDisable() {
        Scaffold.mc.thePlayer.jumpMovementFactor = 0.02f;
        Scaffold.mc.timer.timerSpeed = 1.0f;
        Scaffold.mc.gameSettings.keyBindSneak.pressed = Keyboard.isKeyDown(Scaffold.mc.gameSettings.keyBindSneak.getKeyCode());
        Scaffold.mc.gameSettings.keyBindSprint.pressed = Keyboard.isKeyDown(Scaffold.mc.gameSettings.keyBindSprint.getKeyCode());
    }

    public BlockFace findPlaceData() {
        EnumFacing[] enumFacingArray = new EnumFacing[]{EnumFacing.UP, EnumFacing.DOWN, EnumFacing.SOUTH, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
        double yOffset = 0.0;
        BlockPos blockPos = new BlockPos(Scaffold.mc.thePlayer.getPositionVector()).offset(EnumFacing.DOWN).add(0.0, yOffset, 0.0);
        List<EnumFacing> facingList = Arrays.asList(EnumFacing.values());
        for (int i = 0; i < facingList.size(); ++i) {
            if (Scaffold.mc.theWorld.getBlockState(blockPos.offset(facingList.get(i))).getBlock().getMaterial() == Material.air) continue;
            return new BlockFace(blockPos.offset(facingList.get(i)), enumFacingArray[facingList.get(i).ordinal()], null);
        }
        BlockPos[] blockPosArray = new BlockPos[]{new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1)};
        int candidateCount = blockPosArray.length;
        for (int i = 0; i < candidateCount; ++i) {
            BlockPos neighborPos = blockPos.add(blockPosArray[i].getX(), 0, blockPosArray[i].getZ());
            if (Scaffold.mc.theWorld.getBlockState(neighborPos).getBlock() instanceof BlockAir) {
                for (int j = 0; j < EnumFacing.values().length; ++j) {
                    if (Scaffold.mc.theWorld.getBlockState(neighborPos.offset(EnumFacing.values()[j])).getBlock().getMaterial() == Material.air) continue;
                    return new BlockFace(neighborPos.offset(EnumFacing.values()[j]), enumFacingArray[EnumFacing.values()[j].ordinal()], null);
                }
            }
        }
        return null;
    }

    public EnumFacing getRelativeFacing(BlockPos blockPos) {
        if ((double)blockPos.getX() > Scaffold.mc.thePlayer.posX) {
            return EnumFacing.WEST;
        }
        if ((double)blockPos.getY() > Scaffold.mc.thePlayer.posY - 1.0) {
            return EnumFacing.UP;
        }
        if ((double)blockPos.getZ() > Scaffold.mc.thePlayer.posZ) {
            return EnumFacing.NORTH;
        }
        if ((double)blockPos.getX() < Scaffold.mc.thePlayer.posX) {
            return EnumFacing.EAST;
        }
        if ((double)blockPos.getY() < Scaffold.mc.thePlayer.posY - 1.0) {
            return EnumFacing.DOWN;
        }
        if ((double)blockPos.getZ() < Scaffold.mc.thePlayer.posZ) {
            return EnumFacing.SOUTH;
        }
        return null;
    }

    public Scaffold() {
        super("Scaffold", "You already know XD", Category.player);
        this.modeSetting = new ModeSetting("Mode", "Legit", "Auto");
        this.placeEventSetting = new ModeSetting("Place Event", "Pre", "Post");
        this.timerSetting = new NumberSetting("Timer", 1.0, 0.1, 0.5, 1.5);
        this.sprintSetting = new BooleanSetting("Sprint", false);
        this.itemModeSetting = new ModeSetting("Item Mode", "Spoof", "Switch");
        this.teleportTimer = new Timer();
        this.addSettings(this.modeSetting, this.itemModeSetting, this.placeEventSetting, towerSetting, this.timerSetting, this.sprintSetting);
    }

    public boolean placeBlock(BlockPos blockPos, EnumFacing enumFacing) {
        Vec3i facingVector = this.placeFace.facing.getDirectionVec();
        if (Scaffold.mc.playerController.onPlayerRightClick(Scaffold.mc.thePlayer, Scaffold.mc.theWorld, Scaffold.mc.thePlayer.getHeldItem(), blockPos, enumFacing, new Vec3(this.placeFace.pos).addVector(0.5, 0.5, 0.5).subtractReverse(new Vec3((double)facingVector.getX() * 0.5, (double)facingVector.getY() * 0.5, (double)facingVector.getZ() * 0.5)))) {
            Scaffold.mc.thePlayer.swingItem();
            return true;
        }
        return false;
    }

    public boolean isInBlockList(ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBlock) {
            boolean blockInList = false;
            ItemBlock itemBlock = (ItemBlock)itemStack.getItem();
            for (int i = 0; i < this.blockList.size(); ++i) {
                if (!itemBlock.getBlock().equals(this.blockList.get(i))) continue;
                blockInList = true;
            }
            return !blockInList;
        }
        return false;
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        int value;
        Object current;
        float guiScale = 2.0f;
        if (cancellableEvent instanceof ImageInitEvent) {
            Scaffold.mc.timer.timerSpeed = (float)this.timerSetting.getValue();
            if (this.modeSetting.getMode().equalsIgnoreCase("Legit")) {
                if (this.getBlock(new BlockPos(Scaffold.mc.thePlayer.posX, Scaffold.mc.thePlayer.posY - 1.0, Scaffold.mc.thePlayer.posZ)) instanceof BlockAir) {
                    Scaffold.mc.thePlayer.setSneaking(true);
                    Scaffold.mc.gameSettings.keyBindSneak.pressed = true;
                } else {
                    Scaffold.mc.gameSettings.keyBindSneak.pressed = false;
                }
            }
            Scaffold.mc.gameSettings.keyBindSprint.pressed = this.sprintSetting.getValue() != false;
        }
        if (cancellableEvent instanceof MotionEvent) {
            current = (MotionEvent)cancellableEvent;
            if (Scaffold.mc.gameSettings.keyBindJump.pressed) {
                switch (towerSetting.getMode()) {
                    case "LowJump": {
                        Scaffold.mc.thePlayer.jumpMovementFactor = 0.0f;
                        if (!Scaffold.mc.thePlayer.onGround) break;
                        Scaffold.mc.thePlayer.motionY = 0.38;
                        break;
                    }
                    case "Motion": {
                        Scaffold.mc.thePlayer.jumpMovementFactor = 0.0f;
                        Scaffold.mc.thePlayer.motionY = 0.36;
                        break;
                    }
                    case "Teleport": {
                        if (!Scaffold.mc.thePlayer.onGround || !this.teleportTimer.hasTimePassed(300L)) break;
                        Scaffold.mc.thePlayer.setPosition(Scaffold.mc.thePlayer.posX, (double)((int)Scaffold.mc.thePlayer.posY) + 1.1, Scaffold.mc.thePlayer.posZ);
                        this.teleportTimer.reset();
                        Scaffold.mc.thePlayer.motionY = 0.0;
                        break;
                    }
                    default: {
                        Scaffold.mc.thePlayer.jumpMovementFactor = 0.02f;
                    }
                }
            }
            if ((value = (int)(this.placeEventSetting.getMode().equalsIgnoreCase("PRE") ? 1 : 0)) != 0 && ((CancellableEvent)current).getEventType() == EventType.post) {
                return;
            }
            if (value == 0 && ((CancellableEvent)current).getEventType() == EventType.pre) {
                return;
            }
            if (this.findPlaceData() != null) {
                this.placeFace = this.findPlaceData();
            }
            if (this.placeFace != null) {
                ((MotionEvent)current).setPitch(this.getRotations(this.placeFace.pos)[1]);
                ((MotionEvent)current).setYaw(this.getRotations(this.placeFace.pos)[0]);
            }
            Scaffold.mc.thePlayer.setSprinting(false);
            int spoofEnabled = this.itemModeSetting.getMode().equalsIgnoreCase("Spoof") ? 1 : 0;
            int blockSlot = this.findBlockSlot();
            if (blockSlot != -1 && this.placeFace != null) {
                int previousSlot = Scaffold.mc.thePlayer.inventory.currentItem;
                Scaffold.mc.thePlayer.inventory.currentItem = blockSlot;
                if (this.placeBlock(this.placeFace.pos, this.placeFace.facing) && previousSlot != Scaffold.mc.thePlayer.inventory.currentItem) {
                    Scaffold.mc.thePlayer.sendQueue.sendPacket(new C09PacketHeldItemChange(previousSlot));
                }
                if (spoofEnabled != 0) {
                    Scaffold.mc.thePlayer.inventory.currentItem = previousSlot;
                }
            }
        }
        if (cancellableEvent instanceof EventRender2D) {
            current = new ScaledResolution(Minecraft.getMinecraft());
            value = this.getBlockCountInInventory();
            FontRenderer fontRenderer = Scaffold.mc.fontRendererObj;
            String blocksLeftText = value + " blocks left";
            GL11.glPushMatrix();
            GL11.glTranslated(-60.0, 0.0, 0.0);
            if (cancellableEvent.getEventType().equals((Object)EventType.pre)) {
                RenderUtils.drawRoundedRect((float)((ScaledResolution)current).getScaledWidth() / guiScale - 30.0f, (float)((ScaledResolution)current).getScaledHeight() / guiScale - 10.0f, (this.findBlockSlot() != -1 ? Math.max(FontLoader.popMed15.getStringWidth(Scaffold.mc.thePlayer.inventory.mainInventory[this.findBlockSlot()].getDisplayName()), FontLoader.popMed15.getStringWidth(blocksLeftText)) + 36 : FontLoader.popMed15.getStringWidth(blocksLeftText)) + 5, 40.0, 2.0, Hud.getHudColor().getRGB());
                if (Blur.blurActive.booleanValue() && Blur.blurInfoElements.getValue().booleanValue()) {
                    BlurRenderer.queueBlur((float)((ScaledResolution)current).getScaledWidth() / guiScale - 30.0f - 60.0f, (float)((ScaledResolution)current).getScaledHeight() / guiScale - 10.0f, (float)((ScaledResolution)current).getScaledWidth() / guiScale - 30.0f + (float)(this.findBlockSlot() != -1 ? Math.max(FontLoader.popMed15.getStringWidth(Scaffold.mc.thePlayer.inventory.mainInventory[this.findBlockSlot()].getDisplayName()), FontLoader.popMed15.getStringWidth(blocksLeftText)) + 36 : FontLoader.popMed15.getStringWidth(blocksLeftText)) + 5.0f - 60.0f, (float)((ScaledResolution)current).getScaledHeight() / guiScale - 10.0f + 40.0f, 2.0);
                }
            }
            if (this.findBlockSlot() != -1) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)((ScaledResolution)current).getScaledWidth() / guiScale - 28.0f, (int)((float)(((ScaledResolution)current).getScaledHeight() / 2 + fontRenderer.FONT_HEIGHT) - 17.1f), 0.0f);
                GlStateManager.scale(guiScale, guiScale, guiScale);
                mc.getRenderItem().renderItemAndEffectIntoGUI(Scaffold.mc.thePlayer.inventory.mainInventory[this.findBlockSlot()], 0, 2);
                GlStateManager.popMatrix();
                FontLoader.popMed15.drawString(Scaffold.mc.thePlayer.inventory.mainInventory[this.findBlockSlot()].getDisplayName(), (float)((ScaledResolution)current).getScaledWidth() / guiScale - 28.0f + 32.0f, (float)((ScaledResolution)current).getScaledHeight() / guiScale + (float)fontRenderer.FONT_HEIGHT - 17.1f + 13.0f, -1);
                FontLoader.popMed15.drawString(blocksLeftText + "", (float)((ScaledResolution)current).getScaledWidth() / guiScale - 28.0f + 32.0f, (int)((float)(((ScaledResolution)current).getScaledHeight() / 2 + fontRenderer.FONT_HEIGHT) + 0.1f) + 5, -1862270977);
            } else {
                FontLoader.popMed15.drawString(blocksLeftText + "", (float)((ScaledResolution)current).getScaledWidth() / guiScale - 28.0f, (int)((float)(((ScaledResolution)current).getScaledHeight() / 2 + fontRenderer.FONT_HEIGHT) + 0.1f) + 7, -1862270977);
            }
            GL11.glPopMatrix();
        }
    }

    public Vec3 getHitVec(BlockPos blockPos, EnumFacing enumFacing) {
        double hitX = (float)blockPos.getX() + 0.5f;
        double hitY = (float)blockPos.getY() + 0.5f;
        double hitZ = (float)blockPos.getZ() + 0.5f;
        return new Vec3(hitX += (double)enumFacing.getFrontOffsetX() / 2.0, hitY += (double)enumFacing.vro() / 2.0, hitZ += (double)enumFacing.getFrontOffsetZ() / 2.0);
    }

    public double randomInRange(double firstBound, double secondBound) {
        return Math.random() * (firstBound - secondBound) + secondBound;
    }

    public Block getBlock(BlockPos blockPos) {
        return Scaffold.mc.theWorld.getBlockState(blockPos).getBlock();
    }
}

