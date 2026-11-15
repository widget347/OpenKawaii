/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.entity;

import kawaii.Kawaii;
import kawaii.event.EventType;
import kawaii.event.ImageInitEvent;
import kawaii.event.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecartRiding;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiCommandBlock;
import net.minecraft.client.gui.GuiEnchantment;
import net.minecraft.client.gui.GuiHopper;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiRepair;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.inventory.GuiBeacon;
import net.minecraft.client.gui.inventory.GuiBrewingStand;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiFurnace;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;

public class EntityPlayerSP
extends AbstractClientPlayer {
    public int sprintingTicksLeft;
    public float horseJumpPower;
    public NetHandlerPlayClient sendQueue;
    public boolean serverSprintState;
    public MovementInput movementInput;
    public boolean hasValidHealth;
    public int positionUpdateTicks;
    public float timeInPortal;
    public float prevTimeInPortal;
    public String clientBrand;
    public Minecraft mc;
    public boolean serverSneakState;
    public float renderArmPitch;
    public int horseJumpPowerCounter;
    public float lastReportedPitch;
    public float renderArmYaw;
    public float lastReportedYaw;
    public int sprintToggleTimer;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    public double lastReportedPosX;
    public double lastReportedPosZ;
    public StatFileWriter statWriter;
    public double lastReportedPosY;

    @Override
    public void addChatComponentMessage(IChatComponent iChatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(iChatComponent);
    }

    public float getMovementDirection() {
        float direction = this.rotationYaw;
        if (this.moveForward < 0.0f) {
            direction += 180.0f;
        }
        float forwardScale = 1.0f;
        if (this.moveForward < 0.0f) {
            forwardScale = -0.5f;
        } else if (this.moveForward > 0.0f) {
            forwardScale = 0.5f;
        }
        if (this.moveStrafing > 0.0f) {
            direction -= 90.0f * forwardScale;
        }
        if (this.moveStrafing < 0.0f) {
            direction += 90.0f * forwardScale;
        }
        return direction *= (float)Math.PI / 180;
    }

    @Override
    public BlockPos getPosition() {
        return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(null);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }

    @Override
    public void displayGUIBook(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (item == Items.writable_book) {
            this.mc.displayGuiScreen(new GuiScreenBook(this, itemStack, true));
        }
    }

    @Override
    public void addChatMessage(IChatComponent iChatComponent) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(iChatComponent);
    }

    @Override
    public void openEditCommandBlock(CommandBlockLogic commandBlockLogic) {
        this.mc.displayGuiScreen(new GuiCommandBlock(commandBlockLogic));
    }

    @Override
    public void displayGUIHorse(EntityHorse entityHorse, IInventory iInventory) {
        this.mc.displayGuiScreen(new GuiScreenHorseInventory(this.inventory, iInventory, entityHorse));
    }

    public void sendChatMessage(String message) {
        if (!Kawaii.INSTANCE.getCommandManager().dispatch(message)) {
            this.sendQueue.sendPacket(new C01PacketChatMessage(message));
        }
    }

    public boolean isOpenBlockSpace(BlockPos blockPos) {
        return !this.worldObj.getBlockState(blockPos).getBlock().isNormalCube() && !this.worldObj.getBlockState(blockPos.fC()).getBlock().isNormalCube();
    }

    @Override
    public void onLivingUpdate() {
        boolean canSprint;
        if (this.sprintingTicksLeft > 0) {
            --this.sprintingTicksLeft;
            if (this.sprintingTicksLeft == 0) {
                this.setSprinting(false);
            }
        }
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.doesGuiPauseGame()) {
                this.mc.displayGuiScreen(null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().t_(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"), this.rand.nextFloat() * 0.4f + 0.8f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        } else if (this.isPotionActive(Potion.bjp) && this.getActivePotionEffect(Potion.bjp).aNo() > 60) {
            this.timeInPortal += 0.006666667f;
            if (this.timeInPortal > 1.0f) {
                this.timeInPortal = 1.0f;
            }
        } else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        if (this.timeUntilPortal > 0) {
            --this.timeUntilPortal;
        }
        boolean wasJumping = this.movementInput.jump;
        boolean sneaking = this.movementInput.sneak;
        float sprintThreshold = 0.8f;
        boolean wasMovingForward = MovementInput.moveForward >= sprintThreshold;
        this.movementInput.updatePlayerMoveState();
        if (this.isUsingItem() && !this.isRiding()) {
            this.movementInput.yfc *= 0.2f;
            MovementInput.moveForward *= 0.2f;
            this.sprintToggleTimer = 0;
        }
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().Bxp + 0.5, this.posZ + (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX - (double)this.width * 0.35, this.getEntityBoundingBox().Bxp + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().Bxp + 0.5, this.posZ - (double)this.width * 0.35);
        this.pushOutOfBlocks(this.posX + (double)this.width * 0.35, this.getEntityBoundingBox().Bxp + 0.5, this.posZ + (double)this.width * 0.35);
        boolean sprintResult = canSprint = (float)this.getFoodStats().KYr() > 6.0f || this.capabilities.MAa;
        if (this.onGround && !sneaking && !wasMovingForward) {
            if (MovementInput.moveForward >= sprintThreshold && !this.isSneaking() && canSprint && !this.isUsingItem() && !this.isPotionActive(Potion.ara)) {
                if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.f$()) {
                    this.sprintToggleTimer = 7;
                } else {
                    this.setSprinting(true);
                }
            }
        }
        if (!this.isSneaking()) {
            if (MovementInput.moveForward >= sprintThreshold && canSprint && !this.isUsingItem() && !this.isPotionActive(Potion.ara) && this.mc.gameSettings.keyBindSprint.f$()) {
                this.setSprinting(true);
            }
        }
        if (this.isSneaking()) {
            if (MovementInput.moveForward < sprintThreshold || this.isCollidedHorizontally || !canSprint) {
                this.setSprinting(false);
            }
        }
        if (this.capabilities.MAa) {
            if (this.mc.playerController.isSpectator()) {
                if (!this.capabilities.allowFlying) {
                    this.capabilities.allowFlying = true;
                    this.sendPlayerAbilities();
                }
            } else if (!wasJumping && this.movementInput.jump) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else {
                    this.capabilities.allowFlying = !this.capabilities.allowFlying;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.capabilities.allowFlying && this.isCurrentViewEntity()) {
            if (this.movementInput.sneak) {
                this.motionY -= (double)(this.capabilities.D_o() * 3.0f);
            }
            if (this.movementInput.jump) {
                this.motionY += (double)(this.capabilities.D_o() * 3.0f);
            }
        }
        if (this.isRidingHorse()) {
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (wasJumping && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                this.sendHorseJump();
            } else if (!wasJumping && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            } else if (wasJumping) {
                ++this.horseJumpPowerCounter;
                this.horseJumpPower = this.horseJumpPowerCounter < 10 ? (float)this.horseJumpPowerCounter * 0.1f : 0.8f + 2.0f / (float)(this.horseJumpPowerCounter - 9) * 0.1f;
            }
        } else {
            this.horseJumpPower = 0.0f;
        }
        super.onLivingUpdate();
        if (this.onGround && this.capabilities.allowFlying && !this.mc.playerController.isSpectator()) {
            this.capabilities.allowFlying = false;
            this.sendPlayerAbilities();
        }
    }

    @Override
    public void respawnPlayer() {
        this.sendQueue.sendPacket(new C16PacketClientStatus(C16PacketClientStatus.EnumState.PERFORM_RESPAWN));
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String commandName) {
        return permissionLevel <= 0;
    }

    public void setXPStats(float xp, int totalXp, int xpLevel) {
        this.experience = xp;
        this.experienceTotal = totalXp;
        this.experienceLevel = xpLevel;
    }

    public EntityPlayerSP(Minecraft minecraft, World world, NetHandlerPlayClient netHandlerPlayClient, StatFileWriter statFileWriter) {
        super(world, netHandlerPlayClient.getGameProfile());
        this.sendQueue = netHandlerPlayClient;
        this.statWriter = statFileWriter;
        this.mc = minecraft;
        this.dimension = 0;
    }

    @Override
    public boolean pushOutOfBlocks(double x, double y, double z) {
        if (this.noClip) {
            return false;
        }
        BlockPos blockPos = new BlockPos(x, y, z);
        double fracX = x - (double)blockPos.getX();
        double fracZ = z - (double)blockPos.getZ();
        if (!this.isOpenBlockSpace(blockPos)) {
            int pushDirection = -1;
            double minDistance = 9999.0;
            if (this.isOpenBlockSpace(blockPos.Tu()) && fracX < minDistance) {
                minDistance = fracX;
                pushDirection = 0;
            }
            if (this.isOpenBlockSpace(blockPos.ZDr()) && 1.0 - fracX < minDistance) {
                minDistance = 1.0 - fracX;
                pushDirection = 1;
            }
            if (this.isOpenBlockSpace(blockPos.KGr()) && fracZ < minDistance) {
                minDistance = fracZ;
                pushDirection = 4;
            }
            if (this.isOpenBlockSpace(blockPos.el()) && 1.0 - fracZ < minDistance) {
                minDistance = 1.0 - fracZ;
                pushDirection = 5;
            }
            float pushSpeed = 0.1f;
            if (pushDirection == 0) {
                this.motionX = -pushSpeed;
            }
            if (pushDirection == 1) {
                this.motionX = pushSpeed;
            }
            if (pushDirection == 4) {
                this.motionZ = -pushSpeed;
            }
            if (pushDirection == 5) {
                this.motionZ = pushSpeed;
            }
        }
        return false;
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    @Override
    public EntityItem dropOneItem(boolean dropStack) {
        C07PacketPlayerDigging.Action action = dropStack ?C07PacketPlayerDigging.Action.DROP_ALL_ITEMS : C07PacketPlayerDigging.Action.DROP_ITEM;
        this.sendQueue.sendPacket(new C07PacketPlayerDigging(action, BlockPos.ORIGIN, EnumFacing.DOWN));
        return null;
    }

    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    @Override
    public void swingItem() {
        super.swingItem();
        this.sendQueue.sendPacket(new C0APacketAnimation());
    }

    @Override
    public void onCriticalHit(Entity entity) {
        this.mc.effectRenderer.pA(entity, EnumParticleTypes.CRIT);
    }

    @Override
    public void displayGui(IInteractionObject interactionObject) {
        String guiId = interactionObject.getGuiID();
        if ("minecraft:crafting_table".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiCrafting(this.inventory, this.worldObj));
        } else if ("minecraft:enchanting_table".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiEnchantment(this.inventory, this.worldObj, interactionObject));
        } else if ("minecraft:anvil".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiRepair(this.inventory, this.worldObj));
        }
    }

    @Override
    public void addStat(StatBase statBase, int amount) {
        if (statBase != null && statBase.isIndependent) {
            super.addStat(statBase, amount);
        }
    }

    public void sendHorseInventory() {
        this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.OPEN_INVENTORY));
    }

    @Override
    public void mountEntity(Entity entity) {
        super.mountEntity(entity);
        if (entity instanceof EntityMinecart) {
            this.mc.getSoundHandler().t_(new MovingSoundMinecartRiding(this, (EntityMinecart)entity));
        }
    }

    @Override
    public void playSound(String soundName, float volume, float pitch) {
        this.worldObj.playSound(this.posX, this.posY, this.posZ, soundName, volume, pitch, false);
    }

    @Override
    public void displayGUIChest(IInventory iInventory) {
        String guiId;
        String guiIdCopy = guiId = iInventory instanceof IInteractionObject ? ((IInteractionObject)((Object)iInventory)).getGuiID() : "minecraft:container";
        if ("minecraft:chest".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, iInventory));
        } else if ("minecraft:hopper".equals(guiId)) {
            this.mc.displayGuiScreen(new net.minecraft.client.gui.inventory.GuiHopper(this.inventory, iInventory));
        } else if ("minecraft:furnace".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiFurnace(this.inventory, iInventory));
        } else if ("minecraft:brewing_stand".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiHopper(this.inventory, iInventory));
        } else if ("minecraft:beacon".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiBeacon(this.inventory, iInventory));
        } else if (!"minecraft:dispenser".equals(guiId) && !"minecraft:dropper".equals(guiId)) {
            this.mc.displayGuiScreen(new GuiChest(this.inventory, iInventory));
        } else {
            this.mc.displayGuiScreen(new GuiBrewingStand(this.inventory, iInventory));
        }
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void sendPlayerAbilities() {
        this.sendQueue.sendPacket(new C13PacketPlayerAbilities(this.capabilities));
    }

    public float getHorizontalSpeed() {
        float speed = (float)Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
        return speed;
    }

    public void setPlayerSPHealth(float newHealth) {
        if (this.hasValidHealth) {
            float healthLost = this.getHealth() - newHealth;
            if (healthLost <= 0.0f) {
                this.LLr(newHealth);
                if (healthLost < 0.0f) {
                    this.hurtResistantTime = this.nEa / 2;
                }
            } else {
                this.fwg = healthLost;
                this.LLr(this.getHealth());
                this.hurtResistantTime = this.nEa;
                this.damageEntity(DamageSource.generic, healthLost);
                this.xng = 10;
                this.hurtTime = 10;
            }
        } else {
            this.LLr(newHealth);
            this.hasValidHealth = true;
        }
    }

    public boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    @Override
    public void joinEntityItemWithWorld(EntityItem entityItem) {
    }

    public void setForwardSpeedDouble(double speed) {
        this.setForwardSpeed((float)speed);
    }

    public boolean isRidingHorse() {
        return this.ridingEntity != null && this.ridingEntity instanceof EntityHorse && ((EntityHorse)this.ridingEntity).hOo();
    }

    public void onUpdateWalkingPlayer() {
        boolean eating;
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new ImageInitEvent(EventType.pre));
        boolean sneaking = this.isSneaking();
        if (sneaking != this.serverSneakState) {
            if (sneaking) {
                this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SPRINTING));
            } else {
                this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.serverSneakState = sneaking;
        }
        if ((eating = this.isEating()) != this.serverSprintState) {
            if (eating) {
                this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.START_SNEAKING));
            } else {
                this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.serverSprintState = eating;
        }
        if (this.isCurrentViewEntity()) {
            MotionEvent motionEvent = new MotionEvent(this.posX, this.getEntityBoundingBox().Bxp, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
            motionEvent.setEventType(EventType.pre);
            Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(motionEvent);
            if (!motionEvent.isCancelled()) {
                boolean rotated;
                double dx = motionEvent.getX() - this.lastReportedPosX;
                double dy = motionEvent.getY() - this.lastReportedPosY;
                double dz = motionEvent.getZ() - this.lastReportedPosZ;
                double dYaw = motionEvent.getYaw() - this.lastReportedYaw;
                double dPitch = motionEvent.getPitch() - this.lastReportedPitch;
                boolean moved = dx * dx + dy * dy + dz * dz > 0.0009 || this.positionUpdateTicks >= 20;
                boolean rotationResult = rotated = dYaw != 0.0 || dPitch != 0.0;
                if (this.ridingEntity == null) {
                    if (moved && rotated) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                    } else if (moved) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C04PacketPlayerPosition(motionEvent.getX(), motionEvent.getY(), motionEvent.getZ(), motionEvent.isOnGround()));
                    } else if (rotated) {
                        this.sendQueue.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                    } else {
                        this.sendQueue.sendPacket(new C03PacketPlayer(motionEvent.isOnGround()));
                    }
                } else {
                    this.sendQueue.sendPacket(new C03PacketPlayer.C06PacketPlayerPosLook(this.motionX, -999.0, this.motionZ, motionEvent.getYaw(), motionEvent.getPitch(), motionEvent.isOnGround()));
                    moved = false;
                }
                ++this.positionUpdateTicks;
                if (moved) {
                    this.lastReportedPosX = motionEvent.getX();
                    this.lastReportedPosY = motionEvent.getY();
                    this.lastReportedPosZ = motionEvent.getZ();
                    this.positionUpdateTicks = 0;
                }
                if (rotated) {
                    this.lastReportedYaw = motionEvent.getYaw();
                    this.lastReportedPitch = motionEvent.getPitch();
                }
            }
            motionEvent.setEventType(EventType.post);
            Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(motionEvent);
        }
    }

    @Override
    public void onUpdate() {
        if (this.worldObj.gro(new BlockPos(this.posX, 0.0, this.posZ))) {
            super.onUpdate();
            if (this.isRiding()) {
                this.sendQueue.sendPacket(new C03PacketPlayer.C05PacketPlayerLook(this.rotationYaw, this.rotationPitch, this.onGround));
                this.sendQueue.sendPacket(new C0CPacketInput(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneak));
            } else {
                this.onUpdateWalkingPlayer();
            }
        }
    }

    @Override
    public void heal(float amount) {
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = sprinting ? 600 : 0;
    }

    @Override
    public boolean isEating() {
        boolean sneaking = this.movementInput != null ? this.movementInput.sneak : false;
        return sneaking && !this.sleeping;
    }

    public String getClientBrand() {
        return this.clientBrand;
    }

    @Override
    public void damageEntity(DamageSource damageSource, float amount) {
        if (!this.isEntityInvulnerable(damageSource)) {
            this.LLr(this.getHealth() - amount);
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float amount) {
        return false;
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity()) {
            this.moveStrafing = this.movementInput.yfc;
            this.moveForward = MovementInput.moveForward;
            this.DIg = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5);
            this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5);
        }
    }

    @Override
    public void displayVillagerTradeGui(IMerchant merchant) {
        this.mc.displayGuiScreen(new GuiMerchant(this.inventory, merchant, this.worldObj));
    }

    public boolean isMoving() {
        return this.mc.thePlayer.moveStrafing != 0.0f || this.mc.thePlayer.moveForward != 0.0f;
    }

    public StatFileWriter getStatFileWriter() {
        return this.statWriter;
    }

    @Override
    public void closeScreen() {
        this.sendQueue.sendPacket(new C0DPacketCloseWindow(this.openContainer.windowId));
        this.closeScreenAndDropStack();
    }

    @Override
    public void openEditSign(TileEntitySign tileEntitySign) {
        this.mc.displayGuiScreen(new GuiEditSign(tileEntitySign));
    }

    public void sendHorseJump() {
        this.sendQueue.sendPacket(new C0BPacketEntityAction(this, C0BPacketEntityAction.Action.RIDING_JUMP, (int)(this.getHorseJumpPower() * 100.0f)));
    }

    public void setMotionInts(int x, int y, int z) {
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    @Override
    public boolean isUser() {
        return true;
    }

    public void setForwardSpeed(float speed) {
        this.motionX = -(Math.sin(this.getMovementDirection()) * (double)speed);
        this.motionZ = Math.cos(this.getMovementDirection()) * (double)speed;
    }

    @Override
    public void onEnchantmentCritical(Entity entity) {
        this.mc.effectRenderer.pA(entity, EnumParticleTypes.CRIT_MAGIC);
    }
}

