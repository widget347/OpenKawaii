/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.entity.player;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import kawaii.Kawaii;
import kawaii.module.world.Scaffold;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.player.EntityPlayer$1;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.event.ClickEvent;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;

public abstract class EntityPlayer
extends EntityLivingBase {
    public BlockPos spawnChunk;
    public double prevChasingPosZ;
    public float speedOnGround = 0.1f;
    public float speedInAir = 0.02f;
    public int xpCooldown;
    public PlayerCapabilities capabilities;
    public float experience;
    public float prevExperience;
    public float cameraYaw;
    public int lastXPSound;
    public boolean reducedDebugInfo = false;
    public BlockPos startMinecartRidingCoordinate;
    public float renderOffsetZ;
    public int itemInUseCount;
    public double chasingPosZ;
    public int sleepTimer;
    public ItemStack itemInUse;
    public int experienceLevel;
    public int experienceTotal;
    public BlockPos playerLocation;
    public Container openContainer;
    public boolean spawnForced;
    public int flyToggleTimer;
    public EntityFishHook fishEntity;
    public boolean sleeping;
    public double prevChasingPosY;
    public double chasingPosX;
    public InventoryEnderChest theInventoryEnderChest;
    public GameProfile gameProfile;
    public float prevCameraYaw;
    public FoodStats foodStats;
    public float renderOffsetX;
    public InventoryPlayer inventory = new InventoryPlayer(this);
    public Container inventoryContainer;
    public int xpSeed;
    public double prevChasingPosX;
    public double chasingPosY;

    @Override
    public void preparePlayerToSpawn() {
        this.setSize(0.6f, 1.8f);
        super.preparePlayerToSpawn();
        this.LLr(this.getMaxHealth());
        this.esa = 0;
    }

    public int xpBarCap() {
        return this.experienceLevel >= 30 ? 112 + (this.experienceLevel - 30) * 9 : (this.experienceLevel >= 15 ? 37 + (this.experienceLevel - 15) * 5 : 7 + this.experienceLevel * 2);
    }

    @Override
    public void updateRidden() {
        if (!this.worldObj.crc && this.isEating()) {
            this.mountEntity(null);
            this.setSneaking(false);
        } else {
            double oldPosX = this.posX;
            double oldPosY = this.posY;
            double oldPosZ = this.posZ;
            float oldYaw = this.rotationYaw;
            float oldPitch = this.rotationPitch;
            super.updateRidden();
            this.prevCameraYaw = this.cameraYaw;
            this.cameraYaw = 0.0f;
            this.addMountedMovementStat(this.posX - oldPosX, this.posY - oldPosY, this.posZ - oldPosZ);
            if (this.ridingEntity instanceof EntityPig) {
                this.rotationPitch = oldPitch;
                this.rotationYaw = oldYaw;
                this.eXp = ((EntityPig)this.ridingEntity).eXp;
            }
        }
    }

    public boolean interactWith(Entity entity) {
        ItemStack itemCopy;
        if (this.isSpectator()) {
            if (entity instanceof IInventory) {
                this.displayGUIChest((IInventory)((Object)entity));
            }
            return false;
        }
        ItemStack currentItem = this.getCurrentInventoryItem();
        ItemStack copiedItemStack = itemCopy = currentItem != null ? currentItem.copy() : null;
        if (!entity.interactFirst(this)) {
            if (currentItem != null && entity instanceof EntityLivingBase) {
                if (this.capabilities.$$c) {
                    currentItem = itemCopy;
                }
                if (currentItem.interactWithEntity(this, (EntityLivingBase)entity)) {
                    if (currentItem.stackSize <= 0 && !this.capabilities.$$c) {
                        this.destroyCurrentEquippedItem();
                    }
                    return true;
                }
            }
            return false;
        }
        if (currentItem != null && currentItem == this.getCurrentInventoryItem()) {
            if (currentItem.stackSize <= 0 && !this.capabilities.$$c) {
                this.destroyCurrentEquippedItem();
            } else if (currentItem.stackSize < itemCopy.stackSize && this.capabilities.$$c) {
                currentItem.stackSize = itemCopy.stackSize;
            }
        }
        return true;
    }

    public EntityItem dropItem(ItemStack itemStack, boolean dropAround, boolean traceItem) {
        float halfTurnDegrees = 180.0f;
        float piValue = (float)Math.PI;
        if (itemStack == null) {
            return null;
        }
        if (itemStack.stackSize == 0) {
            return null;
        }
        double dropY = this.posY - 0.30000001192092896 + (double)this.getEyeHeight();
        EntityItem entityItem = new EntityItem(this.worldObj, this.posX, dropY, this.posZ, itemStack);
        entityItem.uKr(40);
        if (traceItem) {
            entityItem.tfo(this.getName());
        }
        if (dropAround) {
            float randomThrowSpeed = this.rand.nextFloat() * 0.5f;
            float randomThrowAngle = this.rand.nextFloat() * piValue * 2.0f;
            entityItem.motionX = -MathHelper.STo(randomThrowAngle) * randomThrowSpeed;
            entityItem.motionZ = MathHelper.imr(randomThrowAngle) * randomThrowSpeed;
            entityItem.motionY = 0.20000000298023224;
        } else {
            float motionScale = 0.3f;
            entityItem.motionX = -MathHelper.STo(this.rotationYaw / halfTurnDegrees * piValue) * MathHelper.imr(this.rotationPitch / halfTurnDegrees * piValue) * motionScale;
            entityItem.motionZ = MathHelper.imr(this.rotationYaw / halfTurnDegrees * piValue) * MathHelper.imr(this.rotationPitch / halfTurnDegrees * piValue) * motionScale;
            entityItem.motionY = -MathHelper.STo(this.rotationPitch / halfTurnDegrees * piValue) * motionScale + 0.1f;
            float jitterAngle = this.rand.nextFloat() * piValue * 2.0f;
            motionScale = 0.02f * this.rand.nextFloat();
            entityItem.motionX += Math.cos(jitterAngle) * (double)motionScale;
            entityItem.motionY += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1f);
            entityItem.motionZ += Math.sin(jitterAngle) * (double)motionScale;
        }
        this.joinEntityItemWithWorld(entityItem);
        if (traceItem) {
            this.triggerAchievement(StatList.spa);
        }
        return entityItem;
    }

    @Override
    public void onUpdate() {
        this.noClip = this.isSpectator();
        if (this.isSpectator()) {
            this.onGround = false;
        }
        if (this.itemInUse != null) {
            ItemStack currentItem = this.inventory.Tdr();
            if (currentItem == this.itemInUse) {
                if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0) {
                    this.updateItemUse(currentItem, 5);
                }
                if (--this.itemInUseCount == 0 && !this.worldObj.crc) {
                    this.onItemUseFinish();
                }
            } else {
                this.clearItemInUse();
            }
        }
        if (this.xpCooldown > 0) {
            --this.xpCooldown;
        }
        if (this.isSpawnForced()) {
            ++this.sleepTimer;
            if (this.sleepTimer > 100) {
                this.sleepTimer = 100;
            }
            if (!this.worldObj.crc) {
                if (!this.isInBed()) {
                    this.wakeUpPlayer(true, true, false);
                } else if (this.worldObj.rhg()) {
                    this.wakeUpPlayer(false, true, true);
                }
            }
        } else if (this.sleepTimer > 0) {
            ++this.sleepTimer;
            if (this.sleepTimer >= 110) {
                this.sleepTimer = 0;
            }
        }
        super.onUpdate();
        if (!this.worldObj.crc && this.openContainer != null && !this.openContainer.canInteractWith(this)) {
            this.closeScreen();
            this.openContainer = this.inventoryContainer;
        }
        if (this.isBurning() && this.capabilities.LYg) {
            this.extinguish();
        }
        this.prevChasingPosX = this.chasingPosX;
        this.prevChasingPosY = this.chasingPosY;
        this.prevChasingPosZ = this.chasingPosZ;
        double chasingDeltaX = this.posX - this.chasingPosX;
        double chasingDeltaY = this.posY - this.chasingPosY;
        double chasingDeltaZ = this.posZ - this.chasingPosZ;
        double maxChasingDistance = 10.0;
        if (chasingDeltaX > maxChasingDistance) {
            this.prevChasingPosX = this.chasingPosX = this.posX;
        }
        if (chasingDeltaZ > maxChasingDistance) {
            this.prevChasingPosZ = this.chasingPosZ = this.posZ;
        }
        if (chasingDeltaY > maxChasingDistance) {
            this.prevChasingPosY = this.chasingPosY = this.posY;
        }
        if (chasingDeltaX < -maxChasingDistance) {
            this.prevChasingPosX = this.chasingPosX = this.posX;
        }
        if (chasingDeltaZ < -maxChasingDistance) {
            this.prevChasingPosZ = this.chasingPosZ = this.posZ;
        }
        if (chasingDeltaY < -maxChasingDistance) {
            this.prevChasingPosY = this.chasingPosY = this.posY;
        }
        this.chasingPosX += chasingDeltaX * 0.25;
        this.chasingPosZ += chasingDeltaZ * 0.25;
        this.chasingPosY += chasingDeltaY * 0.25;
        if (this.ridingEntity == null) {
            this.startMinecartRidingCoordinate = null;
        }
        if (!this.worldObj.crc) {
            this.foodStats.onUpdate(this);
            this.triggerAchievement(StatList.oMp);
            if (this.isEntityAlive()) {
                this.triggerAchievement(StatList.dfg);
            }
        }
        double clampedPosX = MathHelper.QH(this.posX, -2.9999999E7, 2.9999999E7);
        double clampedPosZ = MathHelper.QH(this.posZ, -2.9999999E7, 2.9999999E7);
        if (clampedPosX != this.posX || clampedPosZ != this.posZ) {
            this.setPosition(clampedPosX, this.posY, clampedPosZ);
        }
    }

    @Override
    public boolean doesEntityNotTriggerPressurePlate() {
        return !this.capabilities.allowFlying;
    }

    @Override
    public void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.HNa).setBaseValue(1.0);
        this.getEntityAttribute(SharedMonsterAttributes.qja).setBaseValue(0.1f);
    }

    @Override
    public ItemStack getEquipmentInSlot(int slot) {
        return slot == 0 ? this.inventory.Tdr() : this.inventory.csp[slot - 1];
    }

    @Override
    public ItemStack getHeldItem() {
        return this.inventory.Tdr();
    }

    public void displayGUIChest(IInventory chestInventory) {
    }

    public void setSpawnPoint(BlockPos blockPos, boolean isForced) {
        if (blockPos != null) {
            this.spawnChunk = blockPos;
            this.spawnForced = isForced;
        } else {
            this.spawnChunk = null;
            this.spawnForced = false;
        }
    }

    public void openEditCommandBlock(CommandBlockLogic commandBlockLogic) {
    }

    public void joinEntityItemWithWorld(EntityItem entityItem) {
        this.worldObj.Dao(entityItem);
    }

    public abstract boolean isSpectator();

    public EntityPlayer(World world, GameProfile gameProfile) {
        super(world);
        this.theInventoryEnderChest = new InventoryEnderChest();
        this.foodStats = new FoodStats();
        this.capabilities = new PlayerCapabilities();
        this.entityUniqueID = EntityPlayer.getUUID(gameProfile);
        this.gameProfile = gameProfile;
        this.openContainer = this.inventoryContainer = new ContainerPlayer(this.inventory, !world.crc, this);
        BlockPos blockPos = world.getSpawnPoint();
        this.setLocationAndAngles((double)blockPos.getX() + 0.5, blockPos.getY() + 1, (double)blockPos.getZ() + 0.5, 0.0f, 0.0f);
        this.fQp = 180.0f;
        this.fireResistance = 20;
    }

    public void addMovementStat(double deltaX, double deltaY, double deltaZ) {
        if (this.ridingEntity == null) {
            if (this.isInsideOfMaterial(Material.PGa)) {
                int distanceCm = Math.round(MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f);
                if (distanceCm > 0) {
                    this.addStat(StatList.Wta, distanceCm);
                    this.addExhaustion(0.015f * (float)distanceCm * 0.01f);
                }
            } else if (this.isInWaterState()) {
                int distanceCm = Math.round(MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
                if (distanceCm > 0) {
                    this.addStat(StatList.ydp, distanceCm);
                    this.addExhaustion(0.015f * (float)distanceCm * 0.01f);
                }
            } else if (this.isOnLadder()) {
                if (deltaY > 0.0) {
                    this.addStat(StatList.pSg, (int)Math.round(deltaY * 100.0));
                }
            } else if (this.onGround) {
                int distanceCm = Math.round(MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
                if (distanceCm > 0) {
                    this.addStat(StatList.dac, distanceCm);
                    if (this.isSneaking()) {
                        this.addStat(StatList.occ, distanceCm);
                        this.addExhaustion(0.099999994f * (float)distanceCm * 0.01f);
                    } else {
                        if (this.isEating()) {
                            this.addStat(StatList.DFg, distanceCm);
                        }
                        this.addExhaustion(0.01f * (float)distanceCm * 0.01f);
                    }
                }
            } else {
                int distanceCm = Math.round(MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 100.0f);
                if (distanceCm > 25) {
                    this.addStat(StatList.lLa, distanceCm);
                }
            }
        }
    }

    public void wakeUpPlayer(boolean immediately, boolean updateWorldFlag, boolean setSpawn) {
        this.setSize(0.6f, 1.8f);
        IBlockState bedState = this.worldObj.getBlockState(this.playerLocation);
        if (this.playerLocation != null && bedState.getBlock() == Blocks.bed) {
            this.worldObj.setBlockState(this.playerLocation, bedState.withProperty(BlockBed.OCCUPIED, false), 4);
            BlockPos blockPos = BlockBed.getSafeExitLocation(this.worldObj, this.playerLocation, 0);
            if (blockPos == null) {
                blockPos = this.playerLocation.fC();
            }
            this.setPosition((float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.1f, (float)blockPos.getZ() + 0.5f);
        }
        this.sleeping = false;
        if (!this.worldObj.crc && updateWorldFlag) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }
        int sleepTimerValue = this.sleepTimer = immediately ? 0 : 100;
        if (setSpawn) {
            this.setSpawnPoint(this.playerLocation, false);
        }
    }

    public void displayGUIHorse(EntityHorse entityHorse, IInventory horseInventory) {
    }

    public void addExperience(int amount) {
        this.addScore(amount);
        int maxExperienceAddable = Integer.MAX_VALUE - this.experienceTotal;
        if (amount > maxExperienceAddable) {
            amount = maxExperienceAddable;
        }
        this.experience += (float)amount / (float)this.xpBarCap();
        this.experienceTotal += amount;
        while (this.experience >= 1.0f) {
            this.experience = (this.experience - 1.0f) * (float)this.xpBarCap();
            this.addExperienceLevel(1);
            this.experience /= (float)this.xpBarCap();
        }
    }

    public EntityItem dropPlayerItemWithRandomChoice(ItemStack itemStack, boolean randomChoice) {
        return this.dropItem(itemStack, false, false);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nBTTagCompound) {
        super.readEntityFromNBT(nBTTagCompound);
        this.entityUniqueID = EntityPlayer.getUUID(this.gameProfile);
        NBTTagList inventoryList = nBTTagCompound.getTagList("Inventory", 10);
        this.inventory.readFromNBT(inventoryList);
        this.inventory.currentItem = nBTTagCompound.getInteger("SelectedItemSlot");
        this.sleeping = nBTTagCompound.getBoolean("Sleeping");
        this.sleepTimer = nBTTagCompound.getShort("SleepTimer");
        this.experience = nBTTagCompound.getFloat("XpP");
        this.experienceLevel = nBTTagCompound.getInteger("XpLevel");
        this.experienceTotal = nBTTagCompound.getInteger("XpTotal");
        this.xpSeed = nBTTagCompound.getInteger("XpSeed");
        if (this.xpSeed == 0) {
            this.xpSeed = this.rand.nextInt();
        }
        this.setScore(nBTTagCompound.getInteger("Score"));
        if (this.sleeping) {
            this.playerLocation = new BlockPos(this);
            this.wakeUpPlayer(true, true, false);
        }
        if (nBTTagCompound.hasKey("SpawnX", 99) && nBTTagCompound.hasKey("SpawnY", 99) && nBTTagCompound.hasKey("SpawnZ", 99)) {
            this.spawnChunk = new BlockPos(nBTTagCompound.getInteger("SpawnX"), nBTTagCompound.getInteger("SpawnY"), nBTTagCompound.getInteger("SpawnZ"));
            this.spawnForced = nBTTagCompound.getBoolean("SpawnForced");
        }
        this.foodStats.readNBT(nBTTagCompound);
        this.capabilities.readCapabilitiesFromNBT(nBTTagCompound);
        if (nBTTagCompound.hasKey("EnderItems", 9)) {
            NBTTagList enderItemList = nBTTagCompound.getTagList("EnderItems", 10);
            this.theInventoryEnderChest.loadInventoryFromNBT(enderItemList);
        }
    }

    public Scoreboard getWorldScoreboard() {
        return this.worldObj.getScoreboard();
    }

    @Override
    public void damageArmor(float damage) {
        this.inventory.damageArmor(damage);
    }

    public float getBedOrientationInDegrees() {
        if (this.playerLocation != null) {
            EnumFacing enumFacing = this.worldObj.getBlockState(this.playerLocation).getValue(BlockDirectional.FACING);
            switch (EntityPlayer$1.$SwitchMap$net$minecraft$util$EnumFacing[enumFacing.ordinal()]) {
                case 1: {
                    return 90.0f;
                }
                case 2: {
                    return 270.0f;
                }
                case 3: {
                    return 0.0f;
                }
                case 4: {
                    return 180.0f;
                }
            }
        }
        return 0.0f;
    }

    @Override
    public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
        if (this.isEntityInvulnerable(damageSource)) {
            return false;
        }
        if (this.capabilities.LYg && !damageSource.Noo()) {
            return false;
        }
        this.w$c = 0;
        if (this.getHealth() <= 0.0f) {
            return false;
        }
        if (this.isSpawnForced() && !this.worldObj.crc) {
            this.wakeUpPlayer(true, true, false);
        }
        if (damageSource.Imr()) {
            if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL) {
                damageAmount = 0.0f;
            }
            if (this.worldObj.getDifficulty() == EnumDifficulty.EASY) {
                damageAmount = damageAmount / 2.0f + 1.0f;
            }
            if (this.worldObj.getDifficulty() == EnumDifficulty.HARD) {
                damageAmount = damageAmount * 3.0f / 2.0f;
            }
        }
        if (damageAmount == 0.0f) {
            return false;
        }
        Entity entity = damageSource.getEntity();
        if (entity instanceof EntityArrow && ((EntityArrow)entity).XNa != null) {
            entity = ((EntityArrow)entity).XNa;
        }
        return super.attackEntityFrom(damageSource, damageAmount);
    }

    public void setGameType(WorldSettings.GameType gameType) {
    }

    @Override
    public void playSound(String soundName, float volume, float pitch) {
        this.worldObj.playSoundToNearExcept(this, soundName, volume, pitch);
    }

    public void setReducedDebug(boolean reducedDebugEnabled) {
        this.reducedDebugInfo = reducedDebugEnabled;
    }

    public boolean isPlayerSleeping() {
        return this.spawnForced;
    }

    @Override
    public void resetHeight() {
        if (!this.isSpectator()) {
            super.resetHeight();
        }
    }

    public void addMountedMovementStat(double deltaX, double deltaY, double deltaZ) {
        int distanceCm;
        if (this.ridingEntity != null && (distanceCm = Math.round(MathHelper.sqrt_double(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ) * 100.0f)) > 0) {
            if (this.ridingEntity instanceof EntityMinecart) {
                this.addStat(StatList._ep, distanceCm);
                if (this.startMinecartRidingCoordinate == null) {
                    this.startMinecartRidingCoordinate = new BlockPos(this);
                } else if (this.startMinecartRidingCoordinate.distanceSq(MathHelper.Vlg(this.posX), MathHelper.Vlg(this.posY), MathHelper.Vlg(this.posZ)) >= 1000000.0) {
                    this.triggerAchievement(AchievementList.a_a);
                }
            } else if (this.ridingEntity instanceof EntityBoat) {
                this.addStat(StatList.Ria, distanceCm);
            } else if (this.ridingEntity instanceof EntityPig) {
                this.addStat(StatList.OEg, distanceCm);
            } else if (this.ridingEntity instanceof EntityHorse) {
                this.addStat(StatList.Uyg, distanceCm);
            }
        }
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
        this.dataWatcher.addObject(17, Float.valueOf(0.0f));
        this.dataWatcher.addObject(18, 0);
        this.dataWatcher.addObject(10, (byte)0);
    }

    public void respawnPlayer() {
    }

    @Override
    public boolean isInvisibleToPlayer(EntityPlayer entityPlayer) {
        if (!this.isInvisible()) {
            return false;
        }
        if (entityPlayer.isSpectator()) {
            return false;
        }
        Team team = this.getTeam();
        return team == null || entityPlayer == null || entityPlayer.getTeam() != team || !team.hwo();
    }

    public void sendPlayerAbilities() {
    }

    public int getItemInUseDuration() {
        return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
    }

    public float getArmorVisibility() {
        int equippedCount = 0;
        for (ItemStack itemStack : this.inventory.csp) {
            if (itemStack == null) continue;
            ++equippedCount;
        }
        return (float)equippedCount / (float)this.inventory.csp.length;
    }

    @Override
    public String getJumpSound() {
        return "game.player.die";
    }

    @Override
    public void setAbsorptionAmount(float absorptionAmount) {
        if (absorptionAmount < 0.0f) {
            absorptionAmount = 0.0f;
        }
        this.getDataWatcher().updateObject(17, Float.valueOf(absorptionAmount));
    }

    public void clonePlayer(EntityPlayer entityPlayer, boolean respawnFromEnd) {
        if (respawnFromEnd) {
            this.inventory.copyInventory(entityPlayer.inventory);
            this.LLr(entityPlayer.getHealth());
            this.foodStats = entityPlayer.foodStats;
            this.experienceLevel = entityPlayer.experienceLevel;
            this.experienceTotal = entityPlayer.experienceTotal;
            this.experience = entityPlayer.experience;
            this.setScore(entityPlayer.getScore());
            this.field_181016_an = entityPlayer.field_181016_an;
            this.field_181017_ao = entityPlayer.field_181017_ao;
            this.field_181018_ap = entityPlayer.field_181018_ap;
        } else if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
            this.inventory.copyInventory(entityPlayer.inventory);
            this.experienceLevel = entityPlayer.experienceLevel;
            this.experienceTotal = entityPlayer.experienceTotal;
            this.experience = entityPlayer.experience;
            this.setScore(entityPlayer.getScore());
        }
        this.xpSeed = entityPlayer.xpSeed;
        this.theInventoryEnderChest = entityPlayer.theInventoryEnderChest;
        this.getDataWatcher().updateObject(10, entityPlayer.getDataWatcher().getWatchableObjectByte(10));
    }

    public boolean isBlocking() {
        return this.isUsingItem() && this.itemInUse.getItem().getItemUseAction(this.itemInUse) == S38PacketPlayerListItem.Action.BLOCK;
    }

    public boolean canEat(boolean alwaysEat) {
        return (alwaysEat || this.foodStats.needFood()) && !this.capabilities.LYg;
    }

    @Override
    public String getName() {
        return this.gameProfile.getName();
    }

    public void addExhaustion(float exhaustionAmount) {
        if (!this.capabilities.LYg && !this.worldObj.crc) {
            this.foodStats.nrg(exhaustionAmount);
        }
    }

    @Override
    public float getAbsorptionAmount() {
        return this.getDataWatcher().getWatchableObjectFloat(17);
    }

    @Override
    public String getHurtSound() {
        return "game.player.hurt";
    }

    public boolean canPlayerEdit(BlockPos blockPos, EnumFacing enumFacing, ItemStack itemStack) {
        if (this.capabilities.pBg) {
            return true;
        }
        if (itemStack == null) {
            return false;
        }
        BlockPos adjacentPos = blockPos.offset(enumFacing.getOpposite());
        Block block = this.worldObj.getBlockState(adjacentPos).getBlock();
        return itemStack.canDestroy(block) || itemStack.canEditBlocks();
    }

    public void updateItemUse(ItemStack itemStack, int particleCount) {
        if (itemStack.XC() == S38PacketPlayerListItem.Action.DRINK) {
            this.playSound("random.drink", 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if (itemStack.XC() == S38PacketPlayerListItem.Action.EAT) {
            for (int i = 0; i < particleCount; ++i) {
                Vec3 particleVelocity = new Vec3(((double)this.rand.nextFloat() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0);
                particleVelocity = particleVelocity.ZUo(-this.rotationPitch * (float)Math.PI / 180.0f);
                particleVelocity = particleVelocity.uZ(-this.rotationYaw * (float)Math.PI / 180.0f);
                double particleOffsetY = (double)(-this.rand.nextFloat()) * 0.6 - 0.3;
                Vec3 particlePosition = new Vec3(((double)this.rand.nextFloat() - 0.5) * 0.3, particleOffsetY, 0.6);
                particlePosition = particlePosition.ZUo(-this.rotationPitch * (float)Math.PI / 180.0f);
                particlePosition = particlePosition.uZ(-this.rotationYaw * (float)Math.PI / 180.0f);
                particlePosition = particlePosition.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
                if (itemStack.getHasSubtypes()) {
                    this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, particlePosition.xCoord, particlePosition.rea, particlePosition.zCoord, particleVelocity.xCoord, particleVelocity.rea + 0.05, particleVelocity.zCoord, Item.getIdFromItem(itemStack.getItem()), itemStack.Xs());
                    continue;
                }
                this.worldObj.spawnParticle(EnumParticleTypes.ITEM_CRACK, particlePosition.xCoord, particlePosition.rea, particlePosition.zCoord, particleVelocity.xCoord, particleVelocity.rea + 0.05, particleVelocity.zCoord, Item.getIdFromItem(itemStack.getItem()));
            }
            this.playSound("random.eat", 0.5f + 0.5f * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2f + 1.0f);
        }
    }

    @Override
    public void setCurrentItemOrArmor(int slot, ItemStack itemStack) {
        this.inventory.csp[slot] = itemStack;
    }

    public int getScore() {
        return this.dataWatcher.getWatchableObjectInt(18);
    }

    public int getItemInUseCount() {
        return this.xpSeed;
    }

    @Override
    public void moveEntityWithHeading(float strafe, float forward) {
        double prevPosX = this.posX;
        double prevPosY = this.posY;
        double prevPosZ = this.posZ;
        if (this.capabilities.allowFlying && this.ridingEntity == null) {
            double savedMotionY = this.motionY;
            float savedJumpFactor = this.jumpMovementFactor;
            this.jumpMovementFactor = this.capabilities.D_o() * (float)(this.isSneaking() ? 2 : 1);
            super.moveEntityWithHeading(strafe, forward);
            this.motionY = savedMotionY * 0.6;
            this.jumpMovementFactor = savedJumpFactor;
        } else {
            super.moveEntityWithHeading(strafe, forward);
        }
        this.addMovementStat(this.posX - prevPosX, this.posY - prevPosY, this.posZ - prevPosZ);
    }

    public boolean canHarvestBlock(Block block) {
        return this.inventory.canHeldItemHarvest(block);
    }

    @Override
    public boolean isImmuneToExplosions() {
        return !this.capabilities.allowFlying;
    }

    public void removeExperienceLevel(int levelsToRemove) {
        this.experienceLevel -= levelsToRemove;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0f;
            this.experienceTotal = 0;
        }
        this.xpSeed = this.rand.nextInt();
    }

    public BlockPos getBedLocation() {
        return this.spawnChunk;
    }

    public void displayGUIBook(ItemStack itemStack) {
    }

    public void setScore(int score) {
        this.dataWatcher.updateObject(18, score);
    }

    public ItemStack getCurrentInventoryItem() {
        return this.inventory.Tdr();
    }

    public void collideWithPlayer(Entity entity) {
        entity.onCollideWithPlayer(this);
    }

    public void openEditSign(TileEntitySign tileEntitySign) {
    }

    @Override
    public int getTotalArmorValue() {
        return this.inventory.getTotalArmorValue();
    }

    @Override
    public void fall(float fallHeight, float fallDamageMultiplier) {
        if (!this.capabilities.MAa) {
            if (fallHeight >= 2.0f) {
                this.addStat(StatList.NCp, (int)Math.round((double)fallHeight * 100.0));
            }
            super.fall(fallHeight, fallDamageMultiplier);
        }
    }

    @Override
    public void handleStatusUpdate(byte statusId) {
        if (statusId == 9) {
            this.onItemUseFinish();
        } else if (statusId == 23) {
            this.reducedDebugInfo = false;
        } else if (statusId == 22) {
            this.reducedDebugInfo = true;
        } else {
            super.handleStatusUpdate(statusId);
        }
    }

    public boolean isUser() {
        return false;
    }

    public ItemStack getItemInUse() {
        return this.itemInUse;
    }

    public boolean isPlayerFullyAsleep() {
        return this.sleeping && this.sleepTimer >= 100;
    }

    public boolean canOpen(LockCode lockCode) {
        if (lockCode.isEmpty()) {
            return true;
        }
        ItemStack itemStack = this.getCurrentInventoryItem();
        return itemStack != null && itemStack.hasDisplayName() ? itemStack.getDisplayName().equals(lockCode.getLock()) : false;
    }

    @Override
    public double getYOffset() {
        return -0.35;
    }

    @Override
    public ItemStack[] getInventory() {
        return this.inventory.csp;
    }

    public FoodStats getFoodStats() {
        return this.foodStats;
    }

    @Override
    public int getMaxInPortalTime() {
        return this.capabilities.LYg ? 0 : 80;
    }

    @Override
    public void onLivingUpdate() {
        if (this.flyToggleTimer > 0) {
            --this.flyToggleTimer;
        }
        if (this.worldObj.getDifficulty() == EnumDifficulty.PEACEFUL && this.worldObj.getGameRules().getBoolean("naturalRegeneration")) {
            if (this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 == 0) {
                this.heal(1.0f);
            }
            if (this.foodStats.needFood() && this.ticksExisted % 10 == 0) {
                this.foodStats.setFoodLevel(this.foodStats.KYr() + 1);
            }
        }
        this.inventory.Yto();
        this.prevCameraYaw = this.cameraYaw;
        super.onLivingUpdate();
        IAttributeInstance movementSpeedAttribute = this.getEntityAttribute(SharedMonsterAttributes.qja);
        if (!this.worldObj.crc) {
            movementSpeedAttribute.setBaseValue(this.capabilities.iD());
        }
        this.jumpMovementFactor = this.speedInAir;
        if (this.isSneaking()) {
            this.jumpMovementFactor = (float)((double)this.jumpMovementFactor + (double)this.speedInAir * 0.3);
        }
        this.setAIMoveSpeed((float)movementSpeedAttribute.getAttributeValue());
        float targetCameraYaw = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        float targetCameraPitch = (float)(Math.atan(-this.motionY * (double)0.2f) * 15.0);
        if (targetCameraYaw > 0.1f) {
            targetCameraYaw = 0.1f;
        }
        if (!this.onGround || this.getHealth() <= 0.0f) {
            targetCameraYaw = 0.0f;
        }
        if (this.onGround || this.getHealth() <= 0.0f) {
            targetCameraPitch = 0.0f;
        }
        this.cameraYaw += (targetCameraYaw - this.cameraYaw) * 0.4f;
        this.Gfc += (targetCameraPitch - this.Gfc) * 0.8f;
        if (this.getHealth() > 0.0f && !this.isSpectator()) {
            AxisAlignedBB axisAlignedBB = null;
            axisAlignedBB = this.ridingEntity != null && !this.ridingEntity.isDead ? this.getEntityBoundingBox().union(this.ridingEntity.getEntityBoundingBox()).nJ(1.0, 0.0, 1.0) : this.getEntityBoundingBox().nJ(1.0, 0.5, 1.0);
            List nearbyEntities = this.worldObj.Bcr(this, axisAlignedBB);
            for (int i = 0; i < nearbyEntities.size(); i -= 52) {
                Entity entity = (Entity)nearbyEntities.get(i);
                if (!entity.isDead) {
                    this.collideWithPlayer(entity);
                }
                i += 53;
            }
        }
    }

    @Override
    public void onDeath(DamageSource damageSource) {
        super.onDeath(damageSource);
        this.setSize(0.2f, 0.2f);
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionY = 0.10000000149011612;
        if (this.getName().equals("Notch")) {
            this.dropItem(new ItemStack(Items.apple, 1), true, false);
        }
        if (!this.worldObj.getGameRules().getBoolean("keepInventory")) {
            this.inventory.dropAllItems();
        }
        if (damageSource != null) {
            this.motionX = -MathHelper.imr((this.ODp + this.rotationYaw) * (float)Math.PI / 180.0f) * 0.1f;
            this.motionZ = -MathHelper.STo((this.ODp + this.rotationYaw) * (float)Math.PI / 180.0f) * 0.1f;
        } else {
            this.motionZ = 0.0;
            this.motionX = 0.0;
        }
        this.triggerAchievement(StatList.iZp);
        this.addStatistic(StatList.dfg);
    }

    public void attackTargetEntityWithCurrentItem(Entity entity) {
        if (entity.canAttackWithItem() && !entity.hitByEntity(this)) {
            float attackDamage = (float)this.getEntityAttribute(SharedMonsterAttributes.HNa).getAttributeValue();
            int knockbackLevel = 0;
            float enchantmentDamage = 0.0f;
            enchantmentDamage = entity instanceof EntityLivingBase ? EnchantmentHelper.xV(this.getHeldItem(), ((EntityLivingBase)entity).fL()) : EnchantmentHelper.xV(this.getHeldItem(), EnumCreatureAttribute.UNDEFINED);
            knockbackLevel += EnchantmentHelper.jPo(this);
            if (this.isSneaking()) {
                knockbackLevel -= 33;
                knockbackLevel += 34;
            }
            if (attackDamage > 0.0f || enchantmentDamage > 0.0f) {
                boolean isCriticalHit;
                boolean criticalHitAssigned = isCriticalHit = this.fallDistance > 0.0f && !this.onGround && !this.isOnLadder() && !this.isInWaterState() && !this.isPotionActive(Potion.ara) && this.ridingEntity == null && entity instanceof EntityLivingBase;
                if (isCriticalHit && attackDamage > 0.0f) {
                    attackDamage *= 1.5f;
                }
                attackDamage += enchantmentDamage;
                boolean appliedFire = false;
                int fireAspectLevel = EnchantmentHelper.Uh(this);
                if (entity instanceof EntityLivingBase && fireAspectLevel > 0 && !entity.isBurning()) {
                    appliedFire = true;
                    entity.setFire(1);
                }
                double savedMotionX = entity.motionX;
                double savedMotionY = entity.motionY;
                double savedMotionZ = entity.motionZ;
                boolean attackSucceeded = entity.attackEntityFrom(DamageSource.causePlayerDamage(this), attackDamage);
                if (attackSucceeded) {
                    IEntityMultiPart multiPart;
                    if (knockbackLevel > 0) {
                        entity.addVelocity(-MathHelper.STo(this.rotationYaw * (float)Math.PI / 180.0f) * (float)knockbackLevel * 0.5f, 0.1, MathHelper.imr(this.rotationYaw * (float)Math.PI / 180.0f) * (float)knockbackLevel * 0.5f);
                        this.motionX *= 0.6;
                        this.motionZ *= 0.6;
                        this.setSprinting(false);
                    }
                    if (entity instanceof EntityPlayerMP && entity.velocityChanged) {
                        ((EntityPlayerMP)entity).playerNetServerHandler.sendPacket(new S12PacketEntityVelocity(entity));
                        entity.velocityChanged = false;
                        entity.motionX = savedMotionX;
                        entity.motionY = savedMotionY;
                        entity.motionZ = savedMotionZ;
                    }
                    if (isCriticalHit) {
                        this.onCriticalHit(entity);
                    }
                    if (enchantmentDamage > 0.0f) {
                        this.onEnchantmentCritical(entity);
                    }
                    if (attackDamage >= 18.0f) {
                        this.triggerAchievement(AchievementList.overkill);
                    }
                    this.IO(entity);
                    if (entity instanceof EntityLivingBase) {
                        EnchantmentHelper.ddr((EntityLivingBase)entity, this);
                    }
                    EnchantmentHelper.vZr(this, entity);
                    ItemStack currentItem = this.getCurrentInventoryItem();
                    Entity hitTarget = entity;
                    if (entity instanceof EntityDragonPart && (multiPart = ((EntityDragonPart)entity).entityDragonObj) instanceof EntityLivingBase) {
                        hitTarget = (EntityLivingBase)((Object)multiPart);
                    }
                    if (currentItem != null && hitTarget instanceof EntityLivingBase) {
                        currentItem.hitEntity((EntityLivingBase)hitTarget, this);
                        if (currentItem.stackSize <= 0) {
                            this.destroyCurrentEquippedItem();
                        }
                    }
                    if (entity instanceof EntityLivingBase) {
                        this.addStat(StatList.rbc, Math.round(attackDamage * 10.0f));
                        if (fireAspectLevel > 0) {
                            entity.setFire(fireAspectLevel * 4);
                        }
                    }
                    this.addExhaustion(0.3f);
                } else if (appliedFire) {
                    entity.extinguish();
                }
            }
        }
    }

    public int getSleepTimer() {
        return this.sleepTimer;
    }

    public void displayGui(IInteractionObject iInteractionObject) {
    }

    public int getXPSeed() {
        return this.itemInUseCount;
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public ItemStack getCurrentArmor(int armorSlot) {
        return this.inventory.Dn(armorSlot);
    }

    public boolean isAllowEdit() {
        return this.capabilities.pBg;
    }

    @Override
    public Team getTeam() {
        return this.getWorldScoreboard().Ys(this.getName());
    }

    public void setRenderOffsetForSleep(EnumFacing enumFacing) {
        this.renderOffsetX = 0.0f;
        this.renderOffsetZ = 0.0f;
        switch (EntityPlayer$1.$SwitchMap$net$minecraft$util$EnumFacing[enumFacing.ordinal()]) {
            case 1: {
                this.renderOffsetZ = -1.8f;
                break;
            }
            case 2: {
                this.renderOffsetZ = 1.8f;
                break;
            }
            case 3: {
                this.renderOffsetX = 1.8f;
                break;
            }
            case 4: {
                this.renderOffsetX = -1.8f;
            }
        }
    }

    public void addScore(int scoreToAdd) {
        int oldScore = this.getScore();
        this.dataWatcher.updateObject(18, oldScore + scoreToAdd);
    }

    @Override
    public String getFallSoundString(int fallDamage) {
        return fallDamage > 4 ? "game.player.hurt.fall.big" : "game.player.hurt.fall.small";
    }

    public boolean isWearing(EnumPlayerModelParts enumPlayerModelParts) {
        return (this.getDataWatcher().getWatchableObjectByte(10) & enumPlayerModelParts.JPo()) == enumPlayerModelParts.JPo();
    }

    public void addStat(StatBase statBase, int statAmount) {
    }

    @Override
    public String getSwimSound() {
        return "game.player.swim";
    }

    public void onItemUseFinish() {
        if (this.itemInUse != null) {
            this.updateItemUse(this.itemInUse, 16);
            int stackSizeBefore = this.itemInUse.stackSize;
            ItemStack resultStack = this.itemInUse.mt(this.worldObj, this);
            if (resultStack != this.itemInUse || resultStack != null && resultStack.stackSize != stackSizeBefore) {
                this.inventory.mainInventory[this.inventory.currentItem] = resultStack;
                if (resultStack.stackSize == 0) {
                    this.inventory.mainInventory[this.inventory.currentItem] = null;
                }
            }
            this.clearItemInUse();
        }
    }

    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    public static UUID getOfflineUUID(String playerName) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + playerName).getBytes(Charsets.UTF_8));
    }

    @Override
    public boolean isMovementBlocked() {
        return this.getHealth() <= 0.0f || this.isSpawnForced();
    }

    @Override
    public void jumpWithHeight(double jumpHeight) {
        if (Kawaii.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && !Scaffold.towerSetting.getMode().equalsIgnoreCase("Disabled")) {
            return;
        }
        super.jumpWithHeight(jumpHeight);
        this.triggerAchievement(StatList.wmp);
        if (this.isSneaking()) {
            this.addExhaustion(0.8f);
        } else {
            this.addExhaustion(0.2f);
        }
    }

    public void addExperienceLevel(int levelsToAdd) {
        this.experienceLevel += levelsToAdd;
        if (this.experienceLevel < 0) {
            this.experienceLevel = 0;
            this.experience = 0.0f;
            this.experienceTotal = 0;
        }
        if (levelsToAdd > 0 && this.experienceLevel % 5 == 0 && (float)this.lastXPSound < (float)this.ticksExisted - 100.0f) {
            float levelUpVolume = this.experienceLevel > 30 ? 1.0f : (float)this.experienceLevel / 30.0f;
            this.worldObj.cOo(this, "random.levelup", levelUpVolume * 0.75f, 1.0f);
            this.lastXPSound = this.ticksExisted;
        }
    }

    public void destroyCurrentEquippedItem() {
        this.inventory.setInventorySlotContents(this.inventory.currentItem, null);
    }

    public void onCriticalHit(Entity entity) {
    }

    @Override
    public boolean isUsingItem() {
        return this.itemInUse != null;
    }

    public EntityItem dropOneItem(boolean dropWholeStack) {
        return this.dropItem(this.inventory.decrStackSize(this.inventory.currentItem, dropWholeStack && this.inventory.Tdr() != null ? this.inventory.Tdr().stackSize : 1), false, true);
    }

    public InventoryEnderChest getInventoryEnderChest() {
        return this.theInventoryEnderChest;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nBTTagCompound) {
        super.writeEntityToNBT(nBTTagCompound);
        nBTTagCompound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
        nBTTagCompound.setInteger("SelectedItemSlot", this.inventory.currentItem);
        nBTTagCompound.setBoolean("Sleeping", this.sleeping);
        nBTTagCompound.setShort("SleepTimer", (short)this.sleepTimer);
        nBTTagCompound.setFloat("XpP", this.experience);
        nBTTagCompound.setInteger("XpLevel", this.experienceLevel);
        nBTTagCompound.setInteger("XpTotal", this.experienceTotal);
        nBTTagCompound.setInteger("XpSeed", this.xpSeed);
        nBTTagCompound.setInteger("Score", this.getScore());
        if (this.spawnChunk != null) {
            nBTTagCompound.setInteger("SpawnX", this.spawnChunk.getX());
            nBTTagCompound.setInteger("SpawnY", this.spawnChunk.getY());
            nBTTagCompound.setInteger("SpawnZ", this.spawnChunk.getZ());
            nBTTagCompound.setBoolean("SpawnForced", this.spawnForced);
        }
        this.foodStats.writeNBT(nBTTagCompound);
        this.capabilities.writeCapabilitiesToNBT(nBTTagCompound);
        nBTTagCompound.setTag("EnderItems", this.theInventoryEnderChest.saveInventoryToNBT());
        ItemStack selectedItem = this.inventory.Tdr();
        if (selectedItem != null && selectedItem.getItem() != null) {
            nBTTagCompound.setTag("SelectedItem", selectedItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public boolean isSpawnForced() {
        return this.sleeping;
    }

    public Collection getScoreboardObjectivesFor(Entity entity) {
        int entityColorIndex;
        ScorePlayerTeam entityTeam;
        int ownColorIndex;
        ScorePlayerTeam ownTeam = this.getWorldScoreboard().Ys(this.getName());
        if (ownTeam != null && (ownColorIndex = ownTeam.getChatFormat().getColorIndex()) >= 0 && ownColorIndex < IScoreObjectiveCriteria.kwa.length) {
            for (ScoreObjective scoreObjective : this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.kwa[ownColorIndex])) {
                Score score = this.getWorldScoreboard().getValueFromObjective(entity.getName(), scoreObjective);
                score.incrementScore();
            }
        }
        if ((entityTeam = this.getWorldScoreboard().Ys(entity.getName())) != null && (entityColorIndex = entityTeam.getChatFormat().getColorIndex()) >= 0 && entityColorIndex < IScoreObjectiveCriteria.Cgc.length) {
            return this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.Cgc[entityColorIndex]);
        }
        return Lists.newArrayList();
    }

    public void addStatistic(StatBase statBase) {
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        this.updateArmSwingProgress();
        this.renderYawOffset = this.rotationYaw;
    }

    public void onEnchantmentCritical(Entity entity) {
    }

    public static BlockPos getBedSpawnLocation(World world, BlockPos blockPos, boolean checkSafeSpace) {
        Block block = world.getBlockState(blockPos).getBlock();
        if (block != Blocks.bed) {
            if (!checkSafeSpace) {
                return null;
            }
            boolean blockSafe = block.isSafeToStandIn();
            boolean blockAboveSafe = world.getBlockState(blockPos.fC()).getBlock().isSafeToStandIn();
            return blockSafe && blockAboveSafe ? blockPos : null;
        }
        return BlockBed.getSafeExitLocation(world, blockPos, 0);
    }

    public float getToolDigEfficiency(Block block) {
        float digEfficiency = this.inventory.getStrVsBlock(block);
        if (digEfficiency > 1.0f) {
            int efficiencyLevel = EnchantmentHelper.UHr(this);
            ItemStack currentItem = this.inventory.Tdr();
            if (efficiencyLevel > 0 && currentItem != null) {
                digEfficiency += (float)(efficiencyLevel * efficiencyLevel + 1);
            }
        }
        if (this.isPotionActive(Potion.digSpeed)) {
            digEfficiency *= 1.0f + (float)(this.getActivePotionEffect(Potion.digSpeed).DZo() + 1) * 0.2f;
        }
        if (this.isPotionActive(Potion.Tnc)) {
            float miningFatigueMultiplier = 1.0f;
            switch (this.getActivePotionEffect(Potion.Tnc).DZo()) {
                case 0: {
                    miningFatigueMultiplier = 0.3f;
                    break;
                }
                case 1: {
                    miningFatigueMultiplier = 0.09f;
                    break;
                }
                case 2: {
                    miningFatigueMultiplier = 0.0027f;
                    break;
                }
                default: {
                    miningFatigueMultiplier = 8.1E-4f;
                }
            }
            digEfficiency *= miningFatigueMultiplier;
        }
        if (this.isInsideOfMaterial(Material.PGa) && !EnchantmentHelper.getAquaAffinityModifier(this)) {
            digEfficiency /= 5.0f;
        }
        if (!this.onGround) {
            digEfficiency /= 5.0f;
        }
        return digEfficiency;
    }

    @Override
    public void setDead() {
        super.setDead();
        this.inventoryContainer.onContainerClosed(this);
        if (this.openContainer != null) {
            this.openContainer.onContainerClosed(this);
        }
    }

    public void closeScreen() {
        this.openContainer = this.inventoryContainer;
    }

    @Override
    public int getExperiencePoints(EntityPlayer entityPlayer) {
        if (this.worldObj.getGameRules().getBoolean("keepInventory")) {
            return 0;
        }
        int rawExperience = this.experienceLevel * 7;
        return rawExperience > 100 ? 100 : rawExperience;
    }

    @Override
    public String getSplashSound() {
        return "game.player.swim.splash";
    }

    public void clearItemInUse() {
        this.itemInUse = null;
        this.itemInUseCount = 0;
        if (!this.worldObj.crc) {
            this.setEating(false);
        }
    }

    @Override
    public void addToPlayerScore(Entity entity, int scoreToAdd) {
        this.addScore(scoreToAdd);
        Collection<ScoreObjective> collection = this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.obc);
        if (entity instanceof EntityPlayer) {
            this.triggerAchievement(StatList.APc);
            collection.addAll(this.getWorldScoreboard().getObjectivesFromCriteria(IScoreObjectiveCriteria.Aba));
            collection.addAll(this.getScoreboardObjectivesFor(entity));
        } else {
            this.triggerAchievement(StatList.voc);
        }
        for (ScoreObjective scoreObjective : collection) {
            Score score = this.getWorldScoreboard().getValueFromObjective(this.getName(), scoreObjective);
            score.incrementScore();
        }
    }

    @Override
    public IChatComponent getDisplayName() {
        ChatComponentText chatComponentText = new ChatComponentText(ScorePlayerTeam.formatPlayerName(this.getTeam(), this.getName()));
        chatComponentText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/msg " + this.getName() + " "));
        chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
        chatComponentText.getChatStyle().setInsertion(this.getName());
        return chatComponentText;
    }

    @Override
    public void damageEntity(DamageSource damageSource, float damageAmount) {
        if (!this.isEntityInvulnerable(damageSource)) {
            if (!damageSource.gtg() && this.isBlocking() && damageAmount > 0.0f) {
                damageAmount = (1.0f + damageAmount) * 0.5f;
            }
            damageAmount = this.applyArmorCalculations(damageSource, damageAmount);
            float damageAfterPotion = damageAmount = this.applyPotionDamageCalculations(damageSource, damageAmount);
            damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0f);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - (damageAfterPotion - damageAmount));
            if (damageAmount != 0.0f) {
                this.addExhaustion(damageSource.getHungerDamage());
                float healthBefore = this.getHealth();
                this.LLr(this.getHealth() - damageAmount);
                this.getCombatTracker().trackDamage(damageSource, healthBefore, damageAmount);
                if (damageAmount < 3.4028235E37f) {
                    this.addStat(StatList.Njg, Math.round(damageAmount * 10.0f));
                }
            }
        }
    }

    public boolean hasReducedDebug() {
        return this.reducedDebugInfo;
    }

    @Override
    public boolean sendCommandFeedback() {
        return MinecraftServer.getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
    }

    public void triggerAchievement(StatBase statBase) {
        this.addStat(statBase, 1);
    }

    @Override
    public float getEyeHeight() {
        float eyeHeightValue = 1.62f;
        if (this.isSpawnForced()) {
            eyeHeightValue = 0.2f;
        }
        if (this.isEating()) {
            eyeHeightValue -= 0.08f;
        }
        return eyeHeightValue;
    }

    @Override
    public boolean replaceItemInInventory(int slotIndex, ItemStack itemStack) {
        if (slotIndex >= 0 && slotIndex < this.inventory.mainInventory.length) {
            this.inventory.setInventorySlotContents(slotIndex, itemStack);
            return true;
        }
        int armorIndex = slotIndex - 100;
        if (armorIndex >= 0 && armorIndex < this.inventory.csp.length) {
            int armorPosition = armorIndex + 1;
            if (itemStack != null && itemStack.getItem() != null && (itemStack.getItem() instanceof ItemArmor ? EntityLiving.getArmorPosition(itemStack) != armorPosition : armorPosition != 4 || itemStack.getItem() != Items.skull && !(itemStack.getItem() instanceof ItemBlock))) {
                return false;
            }
            this.inventory.setInventorySlotContents(armorIndex + this.inventory.mainInventory.length, itemStack);
            return true;
        }
        int enderSlotIndex = slotIndex - 200;
        if (enderSlotIndex >= 0 && enderSlotIndex < this.theInventoryEnderChest.getSizeInventory()) {
            this.theInventoryEnderChest.setInventorySlotContents(enderSlotIndex, itemStack);
            return true;
        }
        return false;
    }

    @Override
    public boolean isEntityInsideOpaqueBlock() {
        return !this.sleeping && super.isEntityInsideOpaqueBlock();
    }

    public boolean isInBed() {
        return this.worldObj.getBlockState(this.playerLocation).getBlock() == Blocks.bed;
    }

    public void stopUsingItem() {
        if (this.itemInUse != null) {
            this.itemInUse.onPlayerStoppedUsing(this.worldObj, this, this.itemInUseCount);
        }
        this.clearItemInUse();
    }

    @Override
    public float getAIMoveSpeed() {
        return (float)this.getEntityAttribute(SharedMonsterAttributes.qja).getAttributeValue();
    }

    @Override
    public void setInWeb() {
        if (!this.capabilities.allowFlying) {
            super.setInWeb();
        }
    }

    public void displayVillagerTradeGui(IMerchant iMerchant) {
    }

    public EnumStatus trySleep(BlockPos blockPos) {
        if (!this.worldObj.crc) {
            if (this.isSpawnForced() || !this.isEntityAlive()) {
                return EnumStatus.OTHER_PROBLEM;
            }
            if (!this.worldObj.provider.gi()) {
                return EnumStatus.NOT_POSSIBLE_HERE;
            }
            if (this.worldObj.rhg()) {
                return EnumStatus.NOT_POSSIBLE_NOW;
            }
            if (Math.abs(this.posX - (double)blockPos.getX()) > 3.0 || Math.abs(this.posY - (double)blockPos.getY()) > 2.0 || Math.abs(this.posZ - (double)blockPos.getZ()) > 3.0) {
                return EnumStatus.TOO_FAR_AWAY;
            }
            double horizontalRange = 8.0;
            double verticalRange = 5.0;
            List<EntityMob> nearbyMobs = this.worldObj.getEntitiesWithinAABB(EntityMob.class, new AxisAlignedBB((double)blockPos.getX() - horizontalRange, (double)blockPos.getY() - verticalRange, (double)blockPos.getZ() - horizontalRange, (double)blockPos.getX() + horizontalRange, (double)blockPos.getY() + verticalRange, (double)blockPos.getZ() + horizontalRange));
            if (!nearbyMobs.isEmpty()) {
                return EnumStatus.NOT_SAFE;
            }
        }
        if (this.isRiding()) {
            this.mountEntity(null);
        }
        this.setSize(0.2f, 0.2f);
        if (this.worldObj.gro(blockPos)) {
            EnumFacing enumFacing = this.worldObj.getBlockState(blockPos).getValue(BlockDirectional.FACING);
            float offsetX = 0.5f;
            float offsetZ = 0.5f;
            switch (EntityPlayer$1.$SwitchMap$net$minecraft$util$EnumFacing[enumFacing.ordinal()]) {
                case 1: {
                    offsetZ = 0.9f;
                    break;
                }
                case 2: {
                    offsetZ = 0.1f;
                    break;
                }
                case 3: {
                    offsetX = 0.1f;
                    break;
                }
                case 4: {
                    offsetX = 0.9f;
                }
            }
            this.setRenderOffsetForSleep(enumFacing);
            this.setPosition((float)blockPos.getX() + offsetX, (float)blockPos.getY() + 0.6875f, (float)blockPos.getZ() + offsetZ);
        } else {
            this.setPosition((float)blockPos.getX() + 0.5f, (float)blockPos.getY() + 0.6875f, (float)blockPos.getZ() + 0.5f);
        }
        this.sleeping = true;
        this.sleepTimer = 0;
        this.playerLocation = blockPos;
        this.motionY = 0.0;
        this.motionZ = 0.0;
        this.motionX = 0.0;
        if (!this.worldObj.crc) {
            this.worldObj.updateAllPlayersSleepingFlag();
        }
        return EnumStatus.OK;
    }

    @Override
    public void jump() {
        if (Kawaii.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled() && !Scaffold.towerSetting.getMode().equalsIgnoreCase("Disabled")) {
            return;
        }
        super.jump();
        this.triggerAchievement(StatList.wmp);
        if (this.isSneaking()) {
            this.addExhaustion(0.8f);
        } else {
            this.addExhaustion(0.2f);
        }
    }

    public boolean shouldHeal() {
        return this.getHealth() > 0.0f && this.getHealth() < this.getMaxHealth();
    }

    public void addChatComponentMessage(IChatComponent iChatComponent) {
    }

    @Override
    public void onKillEntity(EntityLivingBase entityLivingBase) {
        EntityList.EntityEggInfo entityEggInfo;
        if (entityLivingBase instanceof IMob) {
            this.triggerAchievement(AchievementList.ezp);
        }
        if ((entityEggInfo = EntityList.vqa.get(EntityList.Rso(entityLivingBase))) != null) {
            this.triggerAchievement(entityEggInfo.uRa);
        }
    }

    public static UUID getUUID(GameProfile gameProfile) {
        UUID uUID = gameProfile.getId();
        if (uUID == null) {
            uUID = EntityPlayer.getOfflineUUID(gameProfile.getName());
        }
        return uUID;
    }

    public void setItemInUse(ItemStack itemStack, int useCount) {
        if (itemStack != this.itemInUse) {
            this.itemInUse = itemStack;
            this.itemInUseCount = useCount;
            if (!this.worldObj.crc) {
                this.setEating(true);
            }
        }
    }

    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return true;
    }

    @Override
    public int getPortalCooldown() {
        return 10;
    }

    public boolean canAttackPlayer(EntityPlayer entityPlayer) {
        Team team = this.getTeam();
        Team team2 = entityPlayer.getTeam();
        return team == null ? true : (!team.isSameTeam(team2) ? true : team.Gcr());
    }

    public static enum EnumChatVisibility {
        FULL(0, "options.chat.visibility.full"),
        SYSTEM(1, "options.chat.visibility.system"),
        HIDDEN(2, "options.chat.visibility.hidden");

        public int chatVisibility;
        public static EnumChatVisibility[] ID_LOOKUP;
        public String resourceKey;

        public static EnumChatVisibility getEnumChatVisibility(int visibilityId) {
            return ID_LOOKUP[visibilityId % ID_LOOKUP.length];
        }

        public int getChatVisibility() {
            return this.chatVisibility;
        }

        EnumChatVisibility(int chatVisibilityId, String resourceKeyName) {
            this.chatVisibility = chatVisibilityId;
            this.resourceKey = resourceKeyName;
        }

        static {
            ID_LOOKUP = new EnumChatVisibility[EnumChatVisibility.values().length];
            EnumChatVisibility[] enumChatVisibilityArray = EnumChatVisibility.values();
            int visibilityCount = enumChatVisibilityArray.length;
            for (int i = 0; i < visibilityCount; ++i) {
                EnumChatVisibility enumChatVisibility = enumChatVisibilityArray[i];
                EnumChatVisibility.ID_LOOKUP[enumChatVisibility.chatVisibility] = enumChatVisibility;
            }
        }

        public String getResourceKey() {
            return this.resourceKey;
        }
    }

    public static enum EnumStatus {
        OK,
        NOT_POSSIBLE_HERE,
        NOT_POSSIBLE_NOW,
        TOO_FAR_AWAY,
        OTHER_PROBLEM,
        NOT_SAFE;

    }
}

