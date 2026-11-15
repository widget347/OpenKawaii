package net.minecraft.entity;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import kawaii.Kawaii;
import kawaii.module.movement.SafeWalk;
import kawaii.module.world.Scaffold;
import kawaii.util.SeededRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockWall;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern.PatternHelper;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentProtection;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.HoverEvent;
import net.minecraft.event.HoverEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class Entity implements ICommandSender {
   public CommandResultStats cmdResultStats;
   public int fireResistance;
   public boolean preventEntitySpawning;
   public int chunkCoordX;
   public boolean noClip;
   public double posX;
   public int nextStepDistance;
   public int entityId;
   public boolean isCollidedVertically;
   public double lastTickPosY;
   public float distanceWalkedModified;
   public boolean ignoreFrustumCheck;
   public boolean inPortal;
   public double prevPosX;
   public boolean outsideBorder;
   public boolean addedToChunk;
   public UUID entityUniqueID;
   public float fallDistance;
   public float prevRotationYaw;
   public boolean firstUpdate;
   public float rotationPitch;
   public double motionZ;
   public BlockPos field_181016_an;
   public double prevPosZ;
   public World worldObj;
   public double prevPosY;
   public boolean isCollidedHorizontally;
   public boolean isInWeb;
   public int hurtResistantTime;
   public double renderDistanceWeight;
   public Random rand;
   public float width;
   public boolean isCollided;
   public float stepHeight;
   public double entityRiderPitchDelta;
   public int timeUntilPortal;
   public double posZ;
   public float distanceWalkedOnStepModified;
   public float entityCollisionReduction;
   public int dimension;
   public Entity ridingEntity;
   public boolean isAirBorne;
   public int chunkCoordY;
   public int serverPosZ;
   public double entityRiderYawDelta;
   public boolean isDead;
   public static AxisAlignedBB ZERO_AABB = new AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   public int serverPosX;
   public boolean isImmuneToFire;
   public float prevDistanceWalkedModified;
   public double motionY;
   public double lastTickPosZ;
   public boolean velocityChanged;
   public float rotationYaw;
   public boolean forceSpawn;
   public boolean onGround;
   public float prevRotationPitch;
   public int portalCounter;
   public boolean inWater;
   public int fire;
   public int chunkCoordZ;
   public AxisAlignedBB boundingBox;
   public DataWatcher dataWatcher;
   public Entity riddenByEntity;
   public float height;
   public int ticksExisted;
   public double lastTickPosX;
   public Vec3 field_181017_ao;
   public boolean invulnerable;
   public double posY;
   public static int nextEntityID;
   public double motionX;
   public EnumFacing field_181018_ap;
   public int serverPosY;

   public boolean replaceItemInInventory(int slotIndex, ItemStack stack) {
      return false;
   }

   public boolean isRiding() {
      return this.ridingEntity != null;
   }

   public double getMountedYOffset() {
      return (double)this.height * 0.75;
   }

   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   public double getDistanceSqToBlockPos(BlockPos pos) {
      return pos.distanceSq(this.posX, this.posY, this.posZ);
   }

   public boolean canAttackWithItem() {
      return true;
   }

   public void onKillEntity(EntityLivingBase victim) {
   }

   public void setSneaking(boolean sneaking) {
      this.setFlag(1, sneaking);
   }

   public void setOnFireFromLava() {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.lava, 4.0F);
         this.setFire(15);
      }
   }

   @Override
   public boolean sendCommandFeedback() {
      return false;
   }

   public void setBeenAttacked() {
      this.velocityChanged = true;
   }

   public void addToPlayerScore(Entity entityIn, int amount) {
   }

   public void setFlag(int flagIndex, boolean enabled) {
      byte flags = this.dataWatcher.getWatchableObjectByte(0);
      if (enabled) {
         this.dataWatcher.updateObject(0, (byte)(flags | 1 << flagIndex));
      } else {
         this.dataWatcher.updateObject(0, (byte)(flags & ~(1 << flagIndex)));
      }
   }

   public void extinguish() {
      this.fire = 0;
   }

   public boolean isSneaking() {
      return this.getFlag(3);
   }

   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.setPosition(x, y, z);
      this.setRotation(yaw, pitch);
      List collidingBoxes = this.worldObj.Alo(this, this.getEntityBoundingBox()._cg(0.03125, 0.0, 0.03125));
      if (!collidingBoxes.isEmpty()) {
         double highestBoxY = 0.0;

         for (AxisAlignedBB collisionBox : (Iterable<AxisAlignedBB>)(Iterable)collidingBoxes) {
            if (collisionBox.aCa > highestBoxY) {
               highestBoxY = collisionBox.aCa;
            }
         }

         y += highestBoxY - this.getEntityBoundingBox().Bxp;
         this.setPosition(x, y, z);
      }
   }

   public boolean getAlwaysRenderNameTagForRender() {
      return this.getAlwaysRenderNameTag();
   }

   public boolean isInsideOfMaterial(Material material) {
      double eyeY = this.posY + (double)this.getEyeHeight();
      BlockPos eyePos = new BlockPos(this.posX, eyeY, this.posZ);
      IBlockState blockState = this.worldObj.getBlockState(eyePos);
      Block block = blockState.getBlock();
      if (block.getMaterial() == material) {
         float liquidHeight = BlockLiquid.getLiquidHeightPercent(blockState.getBlock().getMetaFromState(blockState)) - 0.11111111F;
         float surfaceY = (float)(eyePos.getY() + 1) - liquidHeight;
         boolean isSubmerged = eyeY < (double)surfaceY;
         return !isSubmerged && this instanceof EntityPlayer ? false : isSubmerged;
      } else {
         return false;
      }
   }

   public float getCollisionBorderSize() {
      return 0.1F;
   }

   public void addVelocity(double x, double y, double z) {
      this.motionX += x;
      this.motionY += y;
      this.motionZ += z;
      this.isAirBorne = true;
   }

   public boolean interactFirst(EntityPlayer player) {
      return false;
   }

   public void applyEntityCollision(Entity other) {
      if (other.riddenByEntity != this && other.ridingEntity != this && !other.noClip && !this.noClip) {
         double dx = other.posX - this.posX;
         double dz = other.posZ - this.posZ;
         double dist = MathHelper.Akr(dx, dz);
         if (dist >= 0.009999999776482582) {
            dist = (double)MathHelper.sqrt_double(dist);
            dx /= dist;
            dz /= dist;
            double scale = 1.0 / dist;
            if (scale > 1.0) {
               scale = 1.0;
            }

            dx *= scale;
            dz *= scale;
            dx *= 0.05000000074505806;
            dz *= 0.05F;
            dx *= (double)(1.0F - this.entityCollisionReduction);
            dz *= (double)(1.0F - this.entityCollisionReduction);
            if (this.riddenByEntity == null) {
               this.addVelocity(-dx, 0.0, -dz);
            }

            if (other.riddenByEntity == null) {
               other.addVelocity(dx, 0.0, dz);
            }
         }
      }
   }

   public boolean isWet() {
      return this.inWater
         || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY, this.posZ))
         || this.worldObj.canLightningStrike(new BlockPos(this.posX, this.posY + (double)this.height, this.posZ));
   }

   public void performHurtAnimation() {
   }

   public void resetHeight() {
      float volume = MathHelper.sqrt_double(
            this.motionX
                  * this.motionX
                  * 0.20000000298023224
               + this.motionY * this.motionY
               + this.motionZ * this.motionZ * 0.20000000298023224
         )
         * 0.2F;
      if (volume > 1.0F) {
         volume = 1.0F;
      }

      this.playSound(this.getSplashSound(), volume, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
      float surfaceY = (float)MathHelper.Vlg(this.getEntityBoundingBox().Bxp);

      for (int i = 0; (float)i < 1.0F + this.width * 20.0F; i++) {
         float bubbleOffsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float bubbleOffsetZ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_BUBBLE,
               this.posX + (double)bubbleOffsetX,
               (double)(surfaceY + 1.0F),
               this.posZ + (double)bubbleOffsetZ,
               this.motionX,
               this.motionY - (double)(this.rand.nextFloat() * 0.2F),
               this.motionZ
            );
      }

      for (int j = 0; (float)j < 1.0F + this.width * 20.0F; j++) {
         float splashOffsetX = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         float splashOffsetZ = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width;
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.WATER_SPLASH,
               this.posX + (double)splashOffsetX,
               (double)(surfaceY + 1.0F),
               this.posZ + (double)splashOffsetZ,
               this.motionX,
               this.motionY,
               this.motionZ
            );
      }
   }

   public NBTTagCompound getNBTTagCompound() {
      return null;
   }

   public HoverEvent getHoverEvent() {
      NBTTagCompound tag = new NBTTagCompound();
      String entityTypeId = EntityList.HZ(this);
      tag.setString("id", this.getUniqueID().toString());
      if (entityTypeId != null) {
         tag.setString("type", entityTypeId);
      }

      tag.setString("name", this.getName());
      return new HoverEvent(Action.SHOW_ENTITY, new ChatComponentText(tag.toString()));
   }

   public String getCustomNameTag() {
      return this.dataWatcher.getWatchableObjectString(2);
   }

   @Override
   public int hashCode() {
      return this.entityId;
   }

   public void playSound(String soundName, float volume, float pitch) {
      if (!this.isSilent()) {
         this.worldObj.cOo(this, soundName, volume, pitch);
      }
   }

   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float halfWidth = this.width / 2.0F;
      float entityHeight = this.height;
      this.setEntityBoundingBox(
         new AxisAlignedBB(x - (double)halfWidth, y, z - (double)halfWidth, x + (double)halfWidth, y + (double)entityHeight, z + (double)halfWidth)
      );
   }

   public Vec3 getLastPortalVec() {
      return this.field_181017_ao;
   }

   public abstract void writeEntityToNBT(NBTTagCompound tag);

   public NBTTagList newFloatNBTList(float... floats) {
      NBTTagList list = new NBTTagList();
      float[] array = floats;
      int length = floats.length;
      int index = 0;

      while (index < length) {
         float value = array[index];
         list.appendTag(new NBTTagFloat(value));
         index += 22;
         index -= 21;
      }

      return list;
   }

   public boolean isInvisible() {
      return this.getFlag(5);
   }

   public boolean shouldSetPosAfterLoading() {
      return true;
   }

   public double getDistanceSqToEntity(Entity other) {
      double dx = this.posX - other.posX;
      double dy = this.posY - other.posY;
      double dz = this.posZ - other.posZ;
      return dx * dx + dy * dy + dz * dz;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void readFromNBT(NBTTagCompound tag) {
      try {
         NBTTagList posList = tag.getTagList("Pos", 6);
         NBTTagList motionList = tag.getTagList("Motion", 6);
         NBTTagList rotationList = tag.getTagList("Rotation", 5);
         this.motionX = motionList.getDoubleAt(0);
         this.motionY = motionList.getDoubleAt(1);
         this.motionZ = motionList.getDoubleAt(2);
         if (Math.abs(this.motionX) > 10.0) {
            this.motionX = 0.0;
         }

         if (Math.abs(this.motionY) > 10.0) {
            this.motionY = 0.0;
         }

         if (Math.abs(this.motionZ) > 10.0) {
            this.motionZ = 0.0;
         }

         this.prevPosX = this.lastTickPosX = this.posX = posList.getDoubleAt(0);
         this.prevPosY = this.lastTickPosY = this.posY = posList.getDoubleAt(1);
         this.prevPosZ = this.lastTickPosZ = this.posZ = posList.getDoubleAt(2);
         this.prevRotationYaw = this.rotationYaw = rotationList.getFloatAt(0);
         this.prevRotationPitch = this.rotationPitch = rotationList.getFloatAt(1);
         this.updatePartialPosition(this.rotationYaw);
         this.setRotationYawHead(this.rotationYaw);
         this.fallDistance = tag.getFloat("FallDistance");
         this.fire = tag.getShort("Fire");
         this.setAir(tag.getShort("Air"));
         this.onGround = tag.getBoolean("OnGround");
         this.dimension = tag.getInteger("Dimension");
         this.invulnerable = tag.getBoolean("Invulnerable");
         this.timeUntilPortal = tag.getInteger("PortalCooldown");
         if (tag.hasKey("UUIDMost", 4) && tag.hasKey("UUIDLeast", 4)) {
            this.entityUniqueID = new UUID(tag.getLong("UUIDMost"), tag.getLong("UUIDLeast"));
         } else if (tag.hasKey("UUID", 8)) {
            this.entityUniqueID = UUID.fromString(tag.getString("UUID"));
         }

         this.setPosition(this.posX, this.posY, this.posZ);
         this.setRotation(this.rotationYaw, this.rotationPitch);
         if (tag.hasKey("CustomName", 8) && tag.getString("CustomName").length() > 0) {
            this.setCustomNameTag(tag.getString("CustomName"));
         }

         this.setAlwaysRenderNameTag(tag.getBoolean("CustomNameVisible"));
         this.cmdResultStats.readStatsFromNBT(tag);
         this.setSilent(tag.getBoolean("Silent"));
         this.readEntityFromNBT(tag);
         if (this.shouldSetPosAfterLoading()) {
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Loading entity NBT");
         CrashReportCategory crashCategory = crashReport.makeCategory("Entity being loaded");
         this.addEntityCrashInfo(crashCategory);
         throw new ReportedException(crashReport);
      }
   }

   public void setSprinting(boolean sprinting) {
      this.setFlag(3, sprinting);
   }

   public boolean isEntityAlive() {
      return !this.isDead;
   }

   public Vec3 getPositionEyes(float partialTicks) {
      if (partialTicks == 1.0F) {
         return new Vec3(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      } else {
         double eyeX = this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks;
         double eyeY = this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks + (double)this.getEyeHeight();
         double eyeZ = this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks;
         return new Vec3(eyeX, eyeY, eyeZ);
      }
   }

   public float getExplosionResistance(Explosion explosion, World world, BlockPos pos, IBlockState state) {
      return state.getBlock().Hho(this);
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP player) {
      return true;
   }

   public Vec3 getLookVec() {
      return null;
   }

   public void onKillCommand() {
      this.setDead();
   }

   public float getDistanceToEntity(Entity other) {
      float dx = (float)(this.posX - other.posX);
      float dy = (float)(this.posY - other.posY);
      float dz = (float)(this.posZ - other.posZ);
      return MathHelper.Msr(dx * dx + dy * dy + dz * dz);
   }

   public boolean isSilent() {
      return this.dataWatcher.getWatchableObjectByte(4) == 1;
   }

   public boolean isOffsetPositionInLiquid(double x, double y, double z) {
      AxisAlignedBB offsetBox = this.getEntityBoundingBox().usr(x, y, z);
      return this.isLiquidPresentInAABB(offsetBox);
   }

   public int getAir() {
      return this.dataWatcher.getWatchableObjectShort(1);
   }

   public void setPositionAndUpdate(double x, double y, double z) {
      this.setLocationAndAngles(x, y, z, this.rotationYaw, this.rotationPitch);
   }

   public boolean canBePushed() {
      return false;
   }

   @Override
   public String getName() {
      if (this.hasCustomName()) {
         return this.getCustomNameTag();
      } else {
         String entityName = EntityList.HZ(this);
         if (entityName == null) {
            entityName = "generic";
         }

         return StatCollector.cWo("entity." + entityName + ".name");
      }
   }

   public float getRotationYawHead() {
      return 0.0F;
   }

   public Vec3 getVectorForRotation(float pitch, float yaw) {
      float yawCos = MathHelper.imr(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float yawSin = MathHelper.STo(-yaw * (float) (Math.PI / 180.0) - (float) Math.PI);
      float negatedPitchCos = -MathHelper.imr(-pitch * (float) (Math.PI / 180.0));
      float pitchSin = MathHelper.STo(-pitch * (float) (Math.PI / 180.0));
      return new Vec3((double)(yawSin * negatedPitchCos), (double)pitchSin, (double)(yawCos * negatedPitchCos));
   }

   public int getEntityId() {
      return this.entityId;
   }

   public void applyEnchantments(EntityLivingBase attacker, Entity target) {
      if (target instanceof EntityLivingBase) {
         EnchantmentHelper.ddr((EntityLivingBase)target, attacker);
      }

      EnchantmentHelper.vZr(attacker, target);
   }

   public double getYOffset() {
      return 0.0;
   }

   public boolean isInWaterState() {
      return this.inWater;
   }

   public void updateRidden() {
      if (this.ridingEntity.isDead) {
         this.ridingEntity = null;
      } else {
         this.motionX = 0.0;
         this.motionY = 0.0;
         this.motionZ = 0.0;
         this.onUpdate();
         if (this.ridingEntity != null) {
            this.ridingEntity.updateRiderPosition();
            this.entityRiderYawDelta = this.entityRiderYawDelta + (double)(this.ridingEntity.rotationYaw - this.ridingEntity.prevRotationYaw);
            this.entityRiderPitchDelta = this.entityRiderPitchDelta + (double)(this.ridingEntity.rotationPitch - this.ridingEntity.prevRotationPitch);

            while (this.entityRiderYawDelta >= 180.0) {
               this.entityRiderYawDelta -= 360.0;
            }

            while (this.entityRiderYawDelta < -180.0) {
               this.entityRiderYawDelta += 360.0;
            }

            while (this.entityRiderPitchDelta >= 180.0) {
               this.entityRiderPitchDelta -= 360.0;
            }

            while (this.entityRiderPitchDelta < -180.0) {
               this.entityRiderPitchDelta += 360.0;
            }

            double halfYawDelta = this.entityRiderYawDelta * 0.5;
            double halfPitchDelta = this.entityRiderPitchDelta * 0.5;
            float maxDelta = 10.0F;
            if (halfYawDelta > (double)maxDelta) {
               halfYawDelta = (double)maxDelta;
            }

            if (halfYawDelta < (double)(-maxDelta)) {
               halfYawDelta = (double)(-maxDelta);
            }

            if (halfPitchDelta > (double)maxDelta) {
               halfPitchDelta = (double)maxDelta;
            }

            if (halfPitchDelta < (double)(-maxDelta)) {
               halfPitchDelta = (double)(-maxDelta);
            }

            this.entityRiderYawDelta -= halfYawDelta;
            this.entityRiderPitchDelta -= halfPitchDelta;
         }
      }
   }

   public String getEntityString() {
      return EntityList.HZ(this);
   }

   public boolean isEntityInsideOpaqueBlock() {
      if (this.noClip) {
         return false;
      } else {
         MutableBlockPos checkPos = new MutableBlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
         int i = 0;

         while (i < 8) {
            int sampleY = MathHelper.Vlg(this.posY + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.getEyeHeight());
            int sampleX = MathHelper.Vlg(this.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
            int sampleZ = MathHelper.Vlg(this.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * this.width * 0.8F));
            if (checkPos.getX() != sampleX || checkPos.getY() != sampleY || checkPos.getZ() != sampleZ) {
               checkPos.set(sampleX, sampleY, sampleZ);
               if (this.worldObj.getBlockState(checkPos).getBlock().Ij()) {
                  return true;
               }
            }

            i -= 62;
            i += 63;
         }

         return false;
      }
   }

   public boolean writeMountToNBT(NBTTagCompound tag) {
      String entityString = this.getEntityString();
      if (!this.isDead && entityString != null) {
         tag.setString("id", entityString);
         this.writeToNBT(tag);
         return true;
      } else {
         return false;
      }
   }

   public void setSilent(boolean silent) {
      this.dataWatcher.updateObject(4, (byte)(silent ? 1 : 0));
   }

   public AxisAlignedBB getEntityBoundingBox() {
      return this.boundingBox;
   }

   @Override
   public IChatComponent getDisplayName() {
      ChatComponentText component = new ChatComponentText(this.getName());
      component.getChatStyle().setChatHoverEvent(this.getHoverEvent());
      component.getChatStyle().setInsertion(this.getUniqueID().toString());
      return component;
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      this.attackEntityFrom(DamageSource.egp, 5.0F);
      this.fire++;
      if (this.fire == 0) {
         this.setFire(8);
      }
   }

   public boolean isInLava() {
      return this.worldObj
         .isMaterialInBB(
            this.getEntityBoundingBox()
               .nJ(
                  -0.1F,                   -0.4000000059604645,                   -0.10000000149011612),
            Material.Gta
         );
   }

   @Override
   public void setCommandStat(Type type, int amount) {
      this.cmdResultStats.applyCommandResult(this, type, amount);
   }

   public void setDead() {
      this.isDead = true;
   }

   public CommandResultStats getCommandStats() {
      return this.cmdResultStats;
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void moveEntity(double x, double y, double z) {
      if (this.noClip) {
         this.setEntityBoundingBox(this.getEntityBoundingBox().usr(x, y, z));
         this.resetPositionToBB();
      } else {
         this.worldObj.theProfiler.kqr("move");
         double startPosX = this.posX;
         double startPosY = this.posY;
         double startPosZ = this.posZ;
         if (this.isInWeb) {
            this.isInWeb = false;
            x *= 0.25;
            y *= 0.05000000074505806;
            z *= 0.25;
            this.motionX = 0.0;
            this.motionY = 0.0;
            this.motionZ = 0.0;
         }

         double origX = x;
         double origY = y;
         double origZ = z;
         boolean safeWalking = this.onGround
            && (
               this.isEating()
                  || Kawaii.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled()
                  || Kawaii.INSTANCE.getModuleManager().getModule(SafeWalk.class).isEnabled()
            )
            && this instanceof EntityPlayer;
         if (safeWalking) {
            double stepSize;
            for (stepSize = 0.05;
               x != 0.0 && this.worldObj.Alo(this, this.getEntityBoundingBox().usr(x, -1.0, 0.0)).isEmpty();
               origX = x
            ) {
               if (x < stepSize && x >= -stepSize) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= stepSize;
               } else {
                  x += stepSize;
               }
            }

            for (; z != 0.0 && this.worldObj.Alo(this, this.getEntityBoundingBox().usr(0.0, -1.0, z)).isEmpty(); origZ = z) {
               if (z < stepSize && z >= -stepSize) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= stepSize;
               } else {
                  z += stepSize;
               }
            }

            for (; x != 0.0 && z != 0.0 && this.worldObj.Alo(this, this.getEntityBoundingBox().usr(x, -1.0, z)).isEmpty(); origZ = z) {
               if (x < stepSize && x >= -stepSize) {
                  x = 0.0;
               } else if (x > 0.0) {
                  x -= stepSize;
               } else {
                  x += stepSize;
               }

               origX = x;
               if (z < stepSize && z >= -stepSize) {
                  z = 0.0;
               } else if (z > 0.0) {
                  z -= stepSize;
               } else {
                  z += stepSize;
               }
            }
         }

         List collidingBoxes = this.worldObj.Alo(this, this.getEntityBoundingBox().eqr(x, y, z));
         AxisAlignedBB originalBox = this.getEntityBoundingBox();

         for (AxisAlignedBB yCollisionBox : (Iterable<AxisAlignedBB>)(Iterable)collidingBoxes) {
            y = yCollisionBox.vZo(this.getEntityBoundingBox(), y);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().usr(0.0, y, 0.0));
         boolean canStep = this.onGround || origY != y && origY < 0.0;

         for (AxisAlignedBB xCollisionBox : (Iterable<AxisAlignedBB>)(Iterable)collidingBoxes) {
            x = xCollisionBox.P_(this.getEntityBoundingBox(), x);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().usr(x, 0.0, 0.0));

         for (AxisAlignedBB zCollisionBox : (Iterable<AxisAlignedBB>)(Iterable)collidingBoxes) {
            z = zCollisionBox.eeo(this.getEntityBoundingBox(), z);
         }

         this.setEntityBoundingBox(this.getEntityBoundingBox().usr(0.0, 0.0, z));
         if (this.stepHeight > 0.0F && canStep && (origX != x || origZ != z)) {
            double firstAttemptX = x;
            double firstAttemptY = y;
            double firstAttemptZ = z;
            AxisAlignedBB firstAttemptBox = this.getEntityBoundingBox();
            this.setEntityBoundingBox(originalBox);
            y = (double)this.stepHeight;
            List stepCollidingBoxes = this.worldObj.Alo(this, this.getEntityBoundingBox().eqr(origX, y, origZ));
            AxisAlignedBB stepBox = this.getEntityBoundingBox();
            AxisAlignedBB stepBoxHoriz = stepBox.eqr(origX, 0.0, origZ);
            double stepUpAmount = y;

            for (AxisAlignedBB stepYBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               stepUpAmount = stepYBox.vZo(stepBoxHoriz, stepUpAmount);
            }

            stepBox = stepBox.usr(0.0, stepUpAmount, 0.0);
            double stepMoveX = origX;

            for (AxisAlignedBB stepXBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               stepMoveX = stepXBox.P_(stepBox, stepMoveX);
            }

            stepBox = stepBox.usr(stepMoveX, 0.0, 0.0);
            double stepMoveZ = origZ;

            for (AxisAlignedBB stepZBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               stepMoveZ = stepZBox.eeo(stepBox, stepMoveZ);
            }

            stepBox = stepBox.usr(0.0, 0.0, stepMoveZ);
            AxisAlignedBB altStepBox = this.getEntityBoundingBox();
            double altStepUpAmount = y;

            for (AxisAlignedBB altStepYBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               altStepUpAmount = altStepYBox.vZo(altStepBox, altStepUpAmount);
            }

            altStepBox = altStepBox.usr(0.0, altStepUpAmount, 0.0);
            double altStepMoveX = origX;

            for (AxisAlignedBB altStepXBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               altStepMoveX = altStepXBox.P_(altStepBox, altStepMoveX);
            }

            altStepBox = altStepBox.usr(altStepMoveX, 0.0, 0.0);
            double altStepMoveZ = origZ;

            for (AxisAlignedBB altStepZBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               altStepMoveZ = altStepZBox.eeo(altStepBox, altStepMoveZ);
            }

            altStepBox = altStepBox.usr(0.0, 0.0, altStepMoveZ);
            double stepDistSq = stepMoveX * stepMoveX + stepMoveZ * stepMoveZ;
            double altStepDistSq = altStepMoveX * altStepMoveX + altStepMoveZ * altStepMoveZ;
            if (stepDistSq > altStepDistSq) {
               x = stepMoveX;
               z = stepMoveZ;
               y = -stepUpAmount;
               this.setEntityBoundingBox(stepBox);
            } else {
               x = altStepMoveX;
               z = altStepMoveZ;
               y = -altStepUpAmount;
               this.setEntityBoundingBox(altStepBox);
            }

            for (AxisAlignedBB finalStepYBox : (Iterable<AxisAlignedBB>)(Iterable)stepCollidingBoxes) {
               y = finalStepYBox.vZo(this.getEntityBoundingBox(), y);
            }

            this.setEntityBoundingBox(this.getEntityBoundingBox().usr(0.0, y, 0.0));
            if (firstAttemptX * firstAttemptX + firstAttemptZ * firstAttemptZ >= x * x + z * z) {
               x = firstAttemptX;
               y = firstAttemptY;
               z = firstAttemptZ;
               this.setEntityBoundingBox(firstAttemptBox);
            }
         }

         this.worldObj.theProfiler.endSection();
         this.worldObj.theProfiler.kqr("rest");
         this.resetPositionToBB();
         this.isCollidedHorizontally = origX != x || origZ != z;
         this.isCollidedVertically = origY != y;
         this.onGround = this.isCollidedVertically && origY < 0.0;
         this.isCollided = this.isCollidedHorizontally || this.isCollidedVertically;
         int blockX = MathHelper.Vlg(this.posX);
         int blockY = MathHelper.Vlg(this.posY - 0.2F);
         int blockZ = MathHelper.Vlg(this.posZ);
         BlockPos groundPos = new BlockPos(blockX, blockY, blockZ);
         Block groundBlock = this.worldObj.getBlockState(groundPos).getBlock();
         if (groundBlock.getMaterial() == Material.air) {
            Block blockBelow = this.worldObj.getBlockState(groundPos.down()).getBlock();
            if (blockBelow instanceof BlockFence || blockBelow instanceof BlockWall || blockBelow instanceof BlockFenceGate) {
               groundBlock = blockBelow;
               groundPos = groundPos.down();
            }
         }

         this.updateFallState(y, this.onGround, groundBlock, groundPos);
         if (origX != x) {
            this.motionX = 0.0;
         }

         if (origZ != z) {
            this.motionZ = 0.0;
         }

         if (origY != y) {
            groundBlock.kXr(this.worldObj, this);
         }

         if (this.isImmuneToExplosions() && !safeWalking && this.ridingEntity == null) {
            double dxMoved = this.posX - startPosX;
            double dyMoved = this.posY - startPosY;
            double dzMoved = this.posZ - startPosZ;
            if (groundBlock != Blocks.ladder) {
               dyMoved = 0.0;
            }

            if (groundBlock != null && this.onGround) {
               groundBlock.cur(this.worldObj, groundPos, this);
            }

            this.distanceWalkedModified = (float)(
               (double)this.distanceWalkedModified
                  + (double)MathHelper.sqrt_double(dxMoved * dxMoved + dzMoved * dzMoved)
                     * 0.6
            );
            this.distanceWalkedOnStepModified = (float)(
               (double)this.distanceWalkedOnStepModified
                  + (double)MathHelper.sqrt_double(dxMoved * dxMoved + dyMoved * dyMoved + dzMoved * dzMoved)
                     * 0.6
            );
            if (this.distanceWalkedOnStepModified > (float)this.nextStepDistance && groundBlock.getMaterial() != Material.air) {
               this.nextStepDistance = (int)this.distanceWalkedOnStepModified + 1;
               if (this.isInWaterState()) {
                  float swimVolume = MathHelper.sqrt_double(
                        this.motionX * this.motionX * 0.2F
                           + this.motionY * this.motionY
                           + this.motionZ
                              * this.motionZ
                              * 0.20000000298023224
                     )
                     * 0.35F;
                  if (swimVolume > 1.0F) {
                     swimVolume = 1.0F;
                  }

                  this.playSound(this.getSwimSound(), swimVolume, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
               }

               this.playStepSound(groundPos, groundBlock);
            }
         }

         try {
            this.doBlockCollisions();
         } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Checking entity block collision");
            CrashReportCategory crashCategory = crashReport.makeCategory("Entity being checked for collision");
            this.addEntityCrashInfo(crashCategory);
            throw new ReportedException(crashReport);
         }

         boolean wasWet = this.isWet();
         if (this.worldObj
            .isFlammableWithin(
               this.getEntityBoundingBox()
                  ._cg(                      0.001,                      0.001,
                     0.001
                  )
            )) {
            this.dealFireDamage(1);
            if (!wasWet) {
               this.fire++;
               if (this.fire == 0) {
                  this.setFire(8);
               }
            }
         } else if (this.fire <= 0) {
            this.fire = -this.fireResistance;
         }

         if (wasWet && this.fire > 0) {
            this.playSound("random.fizz", 0.7F, 1.6F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
            this.fire = -this.fireResistance;
         }

         this.worldObj.theProfiler.endSection();
      }
   }

   public void setLocationAndAngles(double x, double y, double z, float yaw, float pitch) {
      this.lastTickPosX = this.prevPosX = this.posX = x;
      this.lastTickPosY = this.prevPosY = this.posY = y;
      this.lastTickPosZ = this.prevPosZ = this.posZ = z;
      this.rotationYaw = yaw;
      this.rotationPitch = pitch;
      this.setPosition(this.posX, this.posY, this.posZ);
   }

   public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch) {
      this.prevPosX = this.posX = x;
      this.prevPosY = this.posY = y;
      this.prevPosZ = this.posZ = z;
      this.prevRotationYaw = this.rotationYaw = yaw;
      this.prevRotationPitch = this.rotationPitch = pitch;
      double yawDelta = (double)(this.prevRotationYaw - yaw);
      if (yawDelta < -180.0) {
         this.prevRotationYaw += 360.0F;
      }

      if (yawDelta >= 180.0) {
         this.prevRotationYaw -= 360.0F;
      }

      this.setPosition(this.posX, this.posY, this.posZ);
      this.setRotation(yaw, pitch);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   @Override
   public boolean equals(Object other) {
      return other instanceof Entity ? ((Entity)other).entityId == this.entityId : false;
   }

   public boolean isBurning() {
      boolean isRemote = this.worldObj != null && this.worldObj.crc;
      return !this.isImmuneToFire && (this.fire > 0 || isRemote && this.getFlag(0));
   }

   public void setAir(int air) {
      this.dataWatcher.updateObject(1, (short)air);
   }

   public boolean isEntityInvulnerable(DamageSource source) {
      return this.invulnerable && source != DamageSource.lop && !source.Vzr();
   }

   public void onDataWatcherUpdate(int dataId) {
   }

   public boolean hasCustomName() {
      return this.dataWatcher.getWatchableObjectString(2).length() > 0;
   }

   public Entity(World world) {
      this.entityId = nextEntityID++;
      this.renderDistanceWeight = 1.0;
      this.boundingBox = ZERO_AABB;
      this.width = 0.6F;
      this.height = 1.8F;
      this.nextStepDistance = 1;
      this.rand = new SeededRandom();
      this.fireResistance = 1;
      this.firstUpdate = true;
      this.entityUniqueID = MathHelper.getRandomUuid(this.rand);
      this.cmdResultStats = new CommandResultStats();
      this.worldObj = world;
      this.setPosition(0.0, 0.0, 0.0);
      if (world != null) {
         this.dimension = world.provider.Iy();
      }

      this.dataWatcher = new DataWatcher(this);
      this.dataWatcher.addObject(0, (byte)0);
      this.dataWatcher.addObject(1, (short)300);
      this.dataWatcher.addObject(3, (byte)0);
      this.dataWatcher.addObject(2, "");
      this.dataWatcher.addObject(4, (byte)0);
      this.entityInit();
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void doBlockCollisions() {
      BlockPos minPos = new BlockPos(
         this.getEntityBoundingBox().Z$g + 0.001,
         this.getEntityBoundingBox().Bxp + 0.001,
         this.getEntityBoundingBox().$$g + 0.001
      );
      BlockPos maxPos = new BlockPos(this.getEntityBoundingBox().$Ha - 0.001, this.getEntityBoundingBox().aCa - 0.001, this.getEntityBoundingBox().H_p - 0.001);
      if (this.worldObj.isAreaLoaded(minPos, maxPos)) {
         for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            int y = minPos.getY();

            while (y <= maxPos.getY()) {
               int z = minPos.getZ();

               while (z <= maxPos.getZ()) {
                  BlockPos pos = new BlockPos(x, y, z);
                  IBlockState state = this.worldObj.getBlockState(pos);

                  try {
                     state.getBlock().onEntityCollidedWithBlock(this.worldObj, pos, state, this);
                  } catch (Throwable throwable) {
                     CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Colliding entity with block");
                     CrashReportCategory crashCategory = crashReport.makeCategory("Block being collided with");
                     CrashReportCategory.addBlockInfo(crashCategory, pos, state);
                     throw new ReportedException(crashReport);
                  }

                  z -= 33;
                  z += 34;
               }

               y += 41;
               y -= 40;
            }
         }
      }
   }

   public void onAddedToWorld() {
   }

   public boolean doesEntityNotTriggerPressurePlate() {
      return true;
   }

   public void copyDataFromOld(Entity oldEntity) {
      NBTTagCompound tag = new NBTTagCompound();
      oldEntity.writeToNBT(tag);
      this.readFromNBT(tag);
      this.timeUntilPortal = oldEntity.timeUntilPortal;
      this.field_181016_an = oldEntity.field_181016_an;
      this.field_181017_ao = oldEntity.field_181017_ao;
      this.field_181018_ap = oldEntity.field_181018_ap;
   }

   public EnumFacing getHorizontalFacing() {
      return EnumFacing.tnr(MathHelper.Vlg((double)(this.rotationYaw * 4.0F / 360.0F) + 0.5) & 3);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void writeToNBT(NBTTagCompound tag) {
      try {
         tag.setTag("Pos", this.newDoubleNBTList(this.posX, this.posY, this.posZ));
         tag.setTag("Motion", this.newDoubleNBTList(this.motionX, this.motionY, this.motionZ));
         tag.setTag("Rotation", this.newFloatNBTList(this.rotationYaw, this.rotationPitch));
         tag.setFloat("FallDistance", this.fallDistance);
         tag.setShort("Fire", (short)this.fire);
         tag.setShort("Air", (short)this.getAir());
         tag.setBoolean("OnGround", this.onGround);
         tag.setInteger("Dimension", this.dimension);
         tag.setBoolean("Invulnerable", this.invulnerable);
         tag.setInteger("PortalCooldown", this.timeUntilPortal);
         tag.setLong("UUIDMost", this.getUniqueID().getMostSignificantBits());
         tag.setLong("UUIDLeast", this.getUniqueID().getLeastSignificantBits());
         if (this.getCustomNameTag() != null && this.getCustomNameTag().length() > 0) {
            tag.setString("CustomName", this.getCustomNameTag());
            tag.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
         }

         this.cmdResultStats.writeStatsToNBT(tag);
         if (this.isSilent()) {
            tag.setBoolean("Silent", this.isSilent());
         }

         this.writeEntityToNBT(tag);
         if (this.ridingEntity != null) {
            NBTTagCompound ridingTag = new NBTTagCompound();
            if (this.ridingEntity.writeMountToNBT(ridingTag)) {
               tag.setTag("Riding", ridingTag);
            }
         }
      } catch (Throwable throwable) {
         CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Saving entity NBT");
         CrashReportCategory crashCategory = crashReport.makeCategory("Entity being saved");
         this.addEntityCrashInfo(crashCategory);
         throw new ReportedException(crashReport);
      }
   }

   public int getPortalCooldown() {
      return 300;
   }

   public void fall(float distance, float damageMultiplier) {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.fall(distance, damageMultiplier);
      }
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX, this.posY + 0.5, this.posZ);
   }

   public void setAlwaysRenderNameTag(boolean alwaysRender) {
      this.dataWatcher.updateObject(3, (byte)(alwaysRender ? 1 : 0));
   }

   public void moveToBlockPosAndAngles(BlockPos pos, float yaw, float pitch) {
      this.setLocationAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
   }

   public void kill() {
      this.setDead();
   }

   public ItemStack[] getInventory() {
      return null;
   }

   public EnumFacing getEntityFacing() {
      return this.field_181018_ap;
   }

   public String getSplashSound() {
      return "game.neutral.swim.splash";
   }

   public boolean isUsingItem() {
      return this.getFlag(4);
   }

   public void dealFireDamage(int damage) {
      if (!this.isImmuneToFire) {
         this.attackEntityFrom(DamageSource.jQp, (float)damage);
      }
   }

   public boolean isEntityEqual(Entity other) {
      return this == other;
   }

   public EntityItem dropItem(Item item, int amount) {
      return this.dropItemWithOffset(item, amount, 0.0F);
   }

   @Override
   public String toString() {
      return String.format(
         "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
         this.getClass().getSimpleName(),
         this.getName(),
         this.entityId,
         this.worldObj == null ? "~NULL~" : this.worldObj.getWorldInfo().getWorldName(),
         this.posX,
         this.posY,
         this.posZ
      );
   }

   public boolean isInWater() {
      return this.isImmuneToFire;
   }

   public boolean isEating() {
      return this.getFlag(1);
   }

   public void travelToDimension(int targetDimension) {
      if (!this.worldObj.crc && !this.isDead) {
         this.worldObj.theProfiler.kqr("changeDimension");
         MinecraftServer server = MinecraftServer.getServer();
         int oldDimension = this.dimension;
         WorldServer oldWorld = server.worldServerForDimension(oldDimension);
         WorldServer newWorld = server.worldServerForDimension(targetDimension);
         this.dimension = targetDimension;
         if (oldDimension == 1 && targetDimension == 1) {
            newWorld = server.worldServerForDimension(0);
            this.dimension = 0;
         }

         this.worldObj.gBo(this);
         this.isDead = false;
         this.worldObj.theProfiler.kqr("reposition");
         server.getConfigurationManager().IVr(this, oldDimension, oldWorld, newWorld);
         this.worldObj.theProfiler.endStartSection("reloading");
         Entity newEntity = EntityList.Jho(EntityList.HZ(this), newWorld);
         if (newEntity != null) {
            newEntity.copyDataFromOld(this);
            if (oldDimension == 1 && targetDimension == 1) {
               BlockPos spawnPos = this.worldObj.getTopSolidOrLiquidBlock(newWorld.getSpawnPoint());
               newEntity.moveToBlockPosAndAngles(spawnPos, newEntity.rotationYaw, newEntity.rotationPitch);
            }

            newWorld.Dao(newEntity);
         }

         this.isDead = true;
         this.worldObj.theProfiler.endSection();
         oldWorld.YG();
         newWorld.YG();
         this.worldObj.theProfiler.endSection();
      }
   }

   public void setCurrentItemOrArmor(int slot, ItemStack stack) {
   }

   public void playStepSound(BlockPos pos, Block block) {
      SoundType soundType = block.stepSound;
      if (this.worldObj.getBlockState(pos.fC()).getBlock() == Blocks.snow_layer) {
         soundType = Blocks.snow_layer.stepSound;
         this.playSound(soundType.Ob(), soundType.XXo() * 0.15F, soundType.qbr());
      } else if (!block.getMaterial().Wkr()) {
         this.playSound(soundType.Ob(), soundType.XXo() * 0.15F, soundType.qbr());
      }
   }

   public boolean writeToNBTOptional(NBTTagCompound tag) {
      String entityString = this.getEntityString();
      if (!this.isDead && entityString != null && this.riddenByEntity == null) {
         tag.setString("id", entityString);
         this.writeToNBT(tag);
         return true;
      } else {
         return false;
      }
   }

   public void mountEntity(Entity vehicle) {
      this.entityRiderPitchDelta = 0.0;
      this.entityRiderYawDelta = 0.0;
      if (vehicle == null) {
         if (this.ridingEntity != null) {
            this.setLocationAndAngles(
               this.ridingEntity.posX,
               this.ridingEntity.getEntityBoundingBox().Bxp + (double)this.ridingEntity.height,
               this.ridingEntity.posZ,
               this.rotationYaw,
               this.rotationPitch
            );
            this.ridingEntity.riddenByEntity = null;
         }

         this.ridingEntity = null;
      } else {
         if (this.ridingEntity != null) {
            this.ridingEntity.riddenByEntity = null;
         }

         if (vehicle != null) {
            for (Entity ancestor = vehicle.ridingEntity; ancestor != null; ancestor = ancestor.ridingEntity) {
               if (ancestor == this) {
                  return;
               }
            }
         }

         this.ridingEntity = vehicle;
         vehicle.riddenByEntity = this;
      }
   }

   public boolean isOutsideBorder() {
      return this.outsideBorder;
   }

   public void onUpdate() {
      this.onEntityUpdate();
   }

   @Override
   public Entity getCommandSenderEntity() {
      return this;
   }

   public void copyLocationAndAnglesFrom(Entity source) {
      this.setLocationAndAngles(source.posX, source.posY, source.posZ, source.rotationYaw, source.rotationPitch);
   }

   public void setAngles(float yaw, float pitch) {
      float oldPitch = this.rotationPitch;
      float oldYaw = this.rotationYaw;
      this.rotationYaw = (float)(
         (double)this.rotationYaw + (double)yaw * 0.15
      );
      this.rotationPitch = (float)((double)this.rotationPitch - (double)pitch * 0.15);
      this.rotationPitch = MathHelper.clamp_float(this.rotationPitch, -90.0F, 90.0F);
      this.prevRotationPitch = this.prevRotationPitch + (this.rotationPitch - oldPitch);
      this.prevRotationYaw = this.prevRotationYaw + (this.rotationYaw - oldYaw);
   }

   public EntityItem entityDropItem(ItemStack stack, float offsetY) {
      if (stack.stackSize != 0 && stack.getItem() != null) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY + (double)offsetY, this.posZ, stack);
         entityItem.Yur();
         this.worldObj.Dao(entityItem);
         return entityItem;
      } else {
         return null;
      }
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean interactAt(EntityPlayer player, Vec3 hitVec) {
      return false;
   }

   public void setInWeb() {
      this.isInWeb = true;
      this.fallDistance = 0.0F;
   }

   public double getDistanceSq(double x, double y, double z) {
      double dx = this.posX - x;
      double dy = this.posY - y;
      double dz = this.posZ - z;
      return dx * dx + dy * dy + dz * dz;
   }

   public void copyCommandStatsFrom(Entity source) {
      this.cmdResultStats.copyStatsFrom(source.getCommandStats());
   }

   public int getMaxInPortalTime() {
      return 0;
   }

   public void setRotationYawHead(float yawHead) {
   }

   public boolean isInRangeToRenderDist(double distance) {
      double renderDist = this.getEntityBoundingBox().getAverageEdgeLength();
      if (Double.isNaN(renderDist)) {
         renderDist = 1.0;
      }

      renderDist = renderDist * 64.0 * this.renderDistanceWeight;
      return distance < renderDist * renderDist;
   }

   public void updateFallState(double deltaY, boolean onGroundIn, Block blockIn, BlockPos pos) {
      if (onGroundIn) {
         if (this.fallDistance > 0.0F) {
            if (blockIn != null) {
               blockIn.Vw(this.worldObj, pos, this, this.fallDistance);
            } else {
               this.fall(this.fallDistance, 1.0F);
            }

            this.fallDistance = 0.0F;
         }
      } else if (deltaY < 0.0) {
         this.fallDistance = (float)((double)this.fallDistance - deltaY);
      }
   }

   public EntityItem dropItemWithOffset(Item item, int amount, float offsetY) {
      return this.entityDropItem(new ItemStack(item, amount, 0), offsetY);
   }

   public void resetPositionToBB() {
      this.posX = (this.getEntityBoundingBox().Z$g + this.getEntityBoundingBox().$Ha) / 2.0;
      this.posY = this.getEntityBoundingBox().Bxp;
      this.posZ = (this.getEntityBoundingBox().$$g + this.getEntityBoundingBox().H_p) / 2.0;
   }

   public void setFire(int seconds) {
      int ticks = seconds * 20;
      ticks = EnchantmentProtection.BS(this, ticks);
      if (this.fire < ticks) {
         this.fire = ticks;
      }
   }

   public boolean canRenderOnFire() {
      return this.isBurning();
   }

   public abstract void readEntityFromNBT(NBTTagCompound tag);

   public void setWorld(World world) {
      this.worldObj = world;
   }

   public void onCollideWithPlayer(EntityPlayer player) {
   }

   public UUID getUniqueID() {
      return this.entityUniqueID;
   }

   public void updateRiderPosition() {
      if (this.riddenByEntity != null) {
         this.riddenByEntity.setPosition(this.posX, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ);
      }
   }

   public boolean isInvisibleToPlayer(EntityPlayer player) {
      return player.isSpectator() ? false : this.isInvisible();
   }

   public MovingObjectPosition rayTrace(double distance, float partialTicks) {
      Vec3 eyePos = this.getPositionEyes(partialTicks);
      Vec3 lookVec = this.getLook(partialTicks);
      Vec3 endPos = eyePos.addVector(lookVec.xCoord * distance, lookVec.rea * distance, lookVec.zCoord * distance);
      return this.worldObj.rayTraceBlocks(eyePos, endPos, false, false, true);
   }

   public void setEntityId(int id) {
      this.entityId = id;
   }

   public void setOutsideBorder(boolean outside) {
      this.outsideBorder = outside;
   }

   public void clientUpdateEntityNBT(NBTTagCompound tag) {
   }

   public boolean getAlwaysRenderNameTag() {
      return this.dataWatcher.getWatchableObjectByte(3) == 1;
   }

   public int getBrightnessForRender(float partialTicks) {
      BlockPos pos = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      return this.worldObj.gro(pos) ? this.worldObj.getCombinedLight(pos, 0) : 0;
   }

   public double getDistance(double x, double y, double z) {
      double dx = this.posX - x;
      double dy = this.posY - y;
      double dz = this.posZ - z;
      return (double)MathHelper.sqrt_double(dx * dx + dy * dy + dz * dz);
   }

   public double getDistanceSqToCenter(BlockPos pos) {
      return pos.ocr(this.posX, this.posY, this.posZ);
   }

   public boolean hitByEntity(Entity other) {
      return false;
   }

   public boolean verifyExplosion(Explosion explosion, World world, BlockPos pos, IBlockState blockState, float chance) {
      return true;
   }

   public void setSize(float newWidth, float newHeight) {
      if (newWidth != this.width || newHeight != this.height) {
         float oldWidth = this.width;
         this.width = newWidth;
         this.height = newHeight;
         this.setEntityBoundingBox(
            new AxisAlignedBB(
               this.getEntityBoundingBox().Z$g,
               this.getEntityBoundingBox().Bxp,
               this.getEntityBoundingBox().$$g,
               this.getEntityBoundingBox().Z$g + (double)this.width,
               this.getEntityBoundingBox().Bxp + (double)this.height,
               this.getEntityBoundingBox().$$g + (double)this.width
            )
         );
         if (this.width > oldWidth && !this.firstUpdate && !this.worldObj.crc) {
            this.moveEntity((double)(oldWidth - this.width), 0.0, (double)(oldWidth - this.width));
         }
      }
   }

   public boolean handleWaterMovement() {
      if (this.worldObj
         .$mr(
            this.getEntityBoundingBox()
               .nJ(0.0, -0.4000000059604645, 0.0)
               ._cg(0.001, 0.001, 0.001),
            Material.PGa,
            this
         )) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.fallDistance = 0.0F;
         this.inWater = true;
         this.fire = 0;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   @Override
   public boolean canCommandSenderUseCommand(int permissionLevel, String commandName) {
      return true;
   }

   public AxisAlignedBB getCollisionBox(Entity other) {
      return null;
   }

   public void setInvisible(boolean invisible) {
      this.setFlag(5, invisible);
   }

   public void setEating(boolean eating) {
      this.setFlag(4, eating);
   }

   public void moveFlying(float strafe, float forward, float friction) {
      float magnitude = strafe * strafe + forward * forward;
      if (magnitude >= 1.0E-4F) {
         magnitude = MathHelper.Msr(magnitude);
         if (magnitude < 1.0F) {
            magnitude = 1.0F;
         }

         magnitude = friction / magnitude;
         strafe *= magnitude;
         forward *= magnitude;
         float sinYaw = MathHelper.STo(this.rotationYaw * (float) Math.PI / 180.0F);
         float cosYaw = MathHelper.imr(this.rotationYaw * (float) Math.PI / 180.0F);
         this.motionX += (double)(strafe * cosYaw - forward * sinYaw);
         this.motionZ += (double)(forward * cosYaw + strafe * sinYaw);
      }
   }

   public void onEntityUpdate() {
      this.worldObj.theProfiler.kqr("entityBaseTick");
      if (this.ridingEntity != null && this.ridingEntity.isDead) {
         this.ridingEntity = null;
      }

      this.prevDistanceWalkedModified = this.distanceWalkedModified;
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.prevRotationPitch = this.rotationPitch;
      this.prevRotationYaw = this.rotationYaw;
      if (!this.worldObj.crc && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.kqr("portal");
         MinecraftServer server = ((WorldServer)this.worldObj).ZO();
         int maxPortalTime = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (server.zso()) {
               if (this.ridingEntity == null && this.portalCounter++ >= maxPortalTime) {
                  this.portalCounter = maxPortalTime;
                  this.timeUntilPortal = this.getPortalCooldown();
                  byte targetDimension;
                  if (this.worldObj.provider.Iy() == -1) {
                     targetDimension = 0;
                  } else {
                     targetDimension = -1;
                  }

                  this.travelToDimension(targetDimension);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         this.worldObj.theProfiler.endSection();
      }

      this.spawnRunningParticles();
      this.handleWaterMovement();
      if (this.worldObj.crc) {
         this.fire = 0;
      } else if (this.fire > 0) {
         if (this.isImmuneToFire) {
            this.fire -= 4;
            if (this.fire < 0) {
               this.fire = 0;
            }
         } else {
            if (this.fire % 20 == 0) {
               this.attackEntityFrom(DamageSource.CTp, 1.0F);
            }

            this.fire--;
         }
      }

      if (this.isInLava()) {
         this.setOnFireFromLava();
         this.fallDistance *= 0.5F;
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.crc) {
         this.setFlag(0, this.fire > 0);
      }

      this.firstUpdate = false;
      this.worldObj.theProfiler.endSection();
   }

   public void preparePlayerToSpawn() {
      if (this.worldObj != null) {
         while (true) {
            if (this.posY > 0.0 && this.posY < 256.0) {
               this.setPosition(this.posX, this.posY, this.posZ);
               if (!this.worldObj.Alo(this, this.getEntityBoundingBox()).isEmpty()) {
                  this.posY++;
                  continue;
               }
            }

            this.motionX = this.motionY = this.motionZ = 0.0;
            this.rotationPitch = 0.0F;
            break;
         }
      }
   }

   public int getMaxFallHeight() {
      return 3;
   }

   public void spawnRunningParticles() {
      if (this.isSneaking() && !this.isInWaterState()) {
         this.createRunningParticles();
      }
   }

   public boolean canRiderInteract() {
      return false;
   }

   public boolean isInRangeToRender3d(double x, double y, double z) {
      double dx = this.posX - x;
      double dy = this.posY - y;
      double dz = this.posZ - z;
      double distSq = dx * dx + dy * dy + dz * dz;
      return this.isInRangeToRenderDist(distSq);
   }

   public NBTTagList newDoubleNBTList(double... values) {
      NBTTagList list = new NBTTagList();

      for (double value : values) {
         list.appendTag(new NBTTagDouble(value));
      }

      return list;
   }

   public float getBrightness(float partialTicks) {
      BlockPos pos = new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
      return this.worldObj.gro(pos) ? this.worldObj.getLightBrightness(pos) : 0.0F;
   }

   public Entity[] getParts() {
      return null;
   }

   public void setCustomNameTag(String name) {
      this.dataWatcher.updateObject(2, name);
   }

   public boolean isImmuneToExplosions() {
      return true;
   }

   public void createRunningParticles() {
      int x = MathHelper.Vlg(this.posX);
      int y = MathHelper.Vlg(this.posY - 0.2F);
      int z = MathHelper.Vlg(this.posZ);
      BlockPos pos = new BlockPos(x, y, z);
      IBlockState state = this.worldObj.getBlockState(pos);
      Block block = state.getBlock();
      if (block.getRenderType() != -1) {
         this.worldObj
            .spawnParticle(
               EnumParticleTypes.BLOCK_CRACK,
               this.posX + ((double)this.rand.nextFloat() - 0.5) * (double)this.width,
               this.getEntityBoundingBox().Bxp + 0.1,
               this.posZ + ((double)this.rand.nextFloat() - 0.5) * (double)this.width,
               -this.motionX * 4.0,
               1.5,
               -this.motionZ * 4.0,
               Block.getStateId(state)
            );
      }
   }

   public void setEntityBoundingBox(AxisAlignedBB box) {
      this.boundingBox = box;
   }

   public boolean pushOutOfBlocks(double x, double y, double z) {
      BlockPos pos = new BlockPos(x, y, z);
      double fracX = x - (double)pos.getX();
      double fracY = y - (double)pos.getY();
      double fracZ = z - (double)pos.getZ();
      List collidingBoxes = this.worldObj.findCollidingBoxes(this.getEntityBoundingBox());
      if (collidingBoxes.isEmpty() && !this.worldObj.isBlockFullCube(pos)) {
         return false;
      } else {
         byte pushDirection = 3;
         double minPush = 9999.0;
         if (!this.worldObj.isBlockFullCube(pos.Tu()) && fracX < minPush) {
            minPush = fracX;
            pushDirection = 0;
         }

         if (!this.worldObj.isBlockFullCube(pos.ZDr()) && 1.0 - fracX < minPush) {
            minPush = 1.0 - fracX;
            pushDirection = 1;
         }

         if (!this.worldObj.isBlockFullCube(pos.fC()) && 1.0 - fracY < minPush) {
            minPush = 1.0 - fracY;
            pushDirection = 3;
         }

         if (!this.worldObj.isBlockFullCube(pos.KGr()) && fracZ < minPush) {
            minPush = fracZ;
            pushDirection = 4;
         }

         if (!this.worldObj.isBlockFullCube(pos.el()) && 1.0 - fracZ < minPush) {
            minPush = 1.0 - fracZ;
            pushDirection = 5;
         }

         float pushSpeed = this.rand.nextFloat() * 0.2F + 0.1F;
         if (pushDirection == 0) {
            this.motionX = (double)(-pushSpeed);
         }

         if (pushDirection == 1) {
            this.motionX = (double)pushSpeed;
         }

         if (pushDirection == 3) {
            this.motionY = (double)pushSpeed;
         }

         if (pushDirection == 4) {
            this.motionZ = (double)(-pushSpeed);
         }

         if (pushDirection == 5) {
            this.motionZ = (double)pushSpeed;
         }

         return true;
      }
   }

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(this.posX, this.posY, this.posZ);
   }

   public void updatePartialPosition(float partialTicks) {
   }

   public boolean isLiquidPresentInAABB(AxisAlignedBB box) {
      return this.worldObj.Alo(this, box).isEmpty() && !this.worldObj.isAnyLiquid(box);
   }

   public void setVelocity(double x, double y, double z) {
      this.motionX = x;
      this.motionY = y;
      this.motionZ = z;
   }

   public boolean getFlag(int flag) {
      return (this.dataWatcher.getWatchableObjectByte(0) & 1 << flag) != 0;
   }

   public void addEntityCrashInfo(CrashReportCategory category) {
      category.addCrashSectionCallable("Entity Type", new Entity$1(this));
      category.addCrashSection("Entity ID", this.entityId);
      category.addCrashSectionCallable("Entity Name", new Entity$2(this));
      category.addCrashSection("Entity's Exact location", String.format("%.2f, %.2f, %.2f", this.posX, this.posY, this.posZ));
      category.addCrashSection(
         "Entity's Block location",
         CrashReportCategory.getCoordinateInfo((double)MathHelper.Vlg(this.posX), (double)MathHelper.Vlg(this.posY), (double)MathHelper.Vlg(this.posZ))
      );
      category.addCrashSection("Entity's Momentum", String.format("%.2f, %.2f, %.2f", this.motionX, this.motionY, this.motionZ));
      category.addCrashSectionCallable("Entity's Rider", new Entity.EntityInfoCallable(this));
      category.addCrashSectionCallable("Entity's Vehicle", new Entity$3(this));
   }

   @Override
   public World getEntityWorld() {
      return this.worldObj;
   }

   public Vec3 getLook(float partialTicks) {
      if (partialTicks == 1.0F) {
         return this.getVectorForRotation(this.rotationPitch, this.rotationYaw);
      } else {
         float interpPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * partialTicks;
         float interpYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * partialTicks;
         return this.getVectorForRotation(interpPitch, interpYaw);
      }
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      } else {
         this.setBeenAttacked();
         return false;
      }
   }

   public float getEyeHeight() {
      return this.height * 0.85F;
   }

   public void handleStatusUpdate(byte status) {
   }

   public abstract void entityInit();

   public DataWatcher getDataWatcher() {
      return this.dataWatcher;
   }

   public void setRotation(float yaw, float pitch) {
      this.rotationYaw = yaw % 360.0F;
      this.rotationPitch = pitch % 360.0F;
   }

   @Override
   public void addChatMessage(IChatComponent component) {
   }

   public String getSwimSound() {
      return "game.neutral.swim";
   }

   public void setPortal(BlockPos pos) {
      if (this.timeUntilPortal > 0) {
         this.timeUntilPortal = this.getPortalCooldown();
      } else {
         if (!this.worldObj.crc && !pos.equals(this.field_181016_an)) {
            this.field_181016_an = pos;
            PatternHelper portalHelper = Blocks.portal.findPortalPattern(this.worldObj, pos);
            double portalBaseCoord = portalHelper.PUr()._Zo() == Axis.X ? (double)portalHelper.getFrontTopLeft().getZ() : (double)portalHelper.getFrontTopLeft().getX();
            double portalOffset = portalHelper.PUr()._Zo() == Axis.X ? this.posZ : this.posX;
            portalOffset = Math.abs(MathHelper.Svr(portalOffset - (double)(portalHelper.PUr().rFo().kk() == AxisDirection.NEGATIVE ? 1 : 0), portalBaseCoord, portalBaseCoord - (double)portalHelper.SB()));
            double portalY = MathHelper.Svr(this.posY - 1.0, (double)portalHelper.getFrontTopLeft().getY(), (double)(portalHelper.getFrontTopLeft().getY() - portalHelper.GW()));
            this.field_181017_ao = new Vec3(portalOffset, portalY, 0.0);
            this.field_181018_ap = portalHelper.PUr();
         }

         this.inPortal = true;
      }
   }

   public static class EntityInfoCallable implements Callable {
      public Entity entity;

      public EntityInfoCallable(Entity targetEntity) {
         super();
         this.entity = targetEntity;
      }

      public String call() {
         return this.entity.riddenByEntity.toString();
      }
   }
}
