package net.minecraft.client.multiplayer;

import kawaii.Kawaii;
import kawaii.module.combat.Reach;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C07PacketPlayerDigging.Action;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class PlayerControllerMP {
   public float stepSoundTickCounter;
   public NetHandlerPlayClient netClientHandler;
   public BlockPos currentBlock = new BlockPos(-1, -1, -1);
   public GameType currentGameType = GameType.SURVIVAL;
   public int blockHitDelay;
   public float curBlockDamageMP;
   public Minecraft mc;
   public ItemStack currentItemHittingBlock;
   public boolean isHittingBlock;
   public int currentPlayerItem;

   public boolean isInCreativeMode() {
      return this.currentGameType.nfr();
   }

   public boolean isSurvivalOrAdventure() {
      return this.currentGameType.iZ();
   }

   public void setGameType(GameType gameType) {
      this.currentGameType = gameType;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public static void clickBlockCreative(Minecraft minecraft, PlayerControllerMP playerController, BlockPos pos, EnumFacing side) {
      if (!minecraft.theWorld.extinguishFire(minecraft.thePlayer, pos, side)) {
         playerController.onPlayerDestroyBlock(pos, side);
      }
   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.theWorld.szr(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
      }
   }

   public GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean sendUseItem(EntityPlayer player, World world, ItemStack stack) {
      if (this.currentGameType == GameType.SPECTATOR) {
         return false;
      } else {
         this.syncCurrentPlayItem();
         this.netClientHandler.sendPacket(new C08PacketPlayerBlockPlacement(player.inventory.Tdr()));
         int initialStackSize = stack.stackSize;
         ItemStack resultStack = stack.bDr(world, player);
         if (resultStack != stack || resultStack != null && resultStack.stackSize != initialStackSize) {
            player.inventory.mainInventory[player.inventory.currentItem] = resultStack;
            if (resultStack.stackSize == 0) {
               player.inventory.mainInventory[player.inventory.currentItem] = null;
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.iZ();
   }

   public void sendSlotPacket(ItemStack stack, int slotId) {
      if (this.currentGameType.nfr()) {
         this.netClientHandler.sendPacket(new C10PacketCreativeInventoryAction(slotId, stack));
      }
   }

   public EntityPlayerSP createClientPlayer(World world, StatFileWriter statFileWriter) {
      return new EntityPlayerSP(this.mc, world, this.netClientHandler, statFileWriter);
   }

   public boolean isCreativeMode() {
      return this.currentGameType.nfr();
   }

   public boolean interactWithEntitySendPacket(EntityPlayer player, Entity entity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.sendPacket(new C02PacketUseEntity(entity, net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT_AT));
      return this.currentGameType != GameType.SPECTATOR && player.interactWith(entity);
   }

   public boolean isRidingHorse() {
      return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public void setPlayerCapabilities(EntityPlayer player) {
      this.currentGameType.configurePlayerCapabilities(player.capabilities);
   }

   public boolean clickBlock(BlockPos pos, EnumFacing side) {
      if (this.currentGameType.vQr()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            ItemStack heldItemStack = this.mc.thePlayer.getCurrentInventoryItem();
            if (heldItemStack == null) {
               return false;
            }

            if (!heldItemStack.pj(block)) {
               return false;
            }
         }
      }

      if (!this.mc.theWorld.getWorldBorder().contains(pos)) {
         return false;
      } else {
         if (this.currentGameType.nfr()) {
            this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, side));
            clickBlockCreative(this.mc, this, pos, side);
            this.blockHitDelay = 5;
         } else if (!this.isHittingBlock || !this.isHittingPosition(pos)) {
            if (this.isHittingBlock) {
               this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.ABORT_DESTROY_BLOCK, this.currentBlock, side));
            }

            this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, side));
            Block clickedBlock = this.mc.theWorld.getBlockState(pos).getBlock();
            boolean isNotAir = clickedBlock.getMaterial() != Material.air;
            if (isNotAir && this.curBlockDamageMP == 0.0F) {
               clickedBlock.onBlockClicked(this.mc.theWorld, pos, this.mc.thePlayer);
            }

            if (isNotAir && clickedBlock.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, pos) >= 1.0F) {
               this.onPlayerDestroyBlock(pos, side);
            } else {
               this.isHittingBlock = true;
               this.currentBlock = pos;
               this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.mc.theWorld.szr(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            }
         }

         return true;
      }
   }

   public void sendEnchantPacket(int windowId, int enchantment) {
      this.netClientHandler.sendPacket(new C11PacketEnchantItem(windowId, enchantment));
   }

   public boolean interactWithEntityAt(EntityPlayer player, Entity entity, MovingObjectPosition hitResult) {
      this.syncCurrentPlayItem();
      Vec3 relativeHitVec = new Vec3(hitResult.hitVec.xCoord - entity.posX, hitResult.hitVec.rea - entity.posY, hitResult.hitVec.zCoord - entity.posZ);
      this.netClientHandler.sendPacket(new C02PacketUseEntity(entity, relativeHitVec));
      return this.currentGameType != GameType.SPECTATOR && entity.interactAt(player, relativeHitVec);
   }

   public boolean isSpectator() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public boolean isHittingPosition(BlockPos pos) {
      ItemStack heldItem = this.mc.thePlayer.getHeldItem();
      boolean sameItem = this.currentItemHittingBlock == null && heldItem == null;
      if (this.currentItemHittingBlock != null && heldItem != null) {
         sameItem = heldItem.getItem() == this.currentItemHittingBlock.getItem()
            && ItemStack.areItemStackTagsEqual(heldItem, this.currentItemHittingBlock)
            && (heldItem.isItemStackDamageable() || heldItem.Xs() == this.currentItemHittingBlock.Xs());
      }

      return pos.equals(this.currentBlock) && sameItem;
   }

   public ItemStack windowClick(int windowId, int slotId, int mouseButton, int mode, EntityPlayer player) {
      short transactionId = player.openContainer.getNextTransactionID(player.inventory);
      ItemStack clickedStack = player.openContainer.slotClick(slotId, mouseButton, mode, player);
      this.netClientHandler.sendPacket(new C0EPacketClickWindow(windowId, slotId, mouseButton, mode, clickedStack, transactionId));
      return clickedStack;
   }

   public void flipPlayer(EntityPlayer player) {
      player.rotationYaw = -180.0F;
   }

   public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient world, ItemStack heldStack, BlockPos pos, EnumFacing side, Vec3 hitVec) {
      this.syncCurrentPlayItem();
      float hitX = (float)(hitVec.xCoord - (double)pos.getX());
      float hitY = (float)(hitVec.rea - (double)pos.getY());
      float hitZ = (float)(hitVec.zCoord - (double)pos.getZ());
      boolean blockActivated = false;
      if (!this.mc.theWorld.getWorldBorder().contains(pos)) {
         return false;
      } else {
         if (this.currentGameType != GameType.SPECTATOR) {
            IBlockState blockState = world.getBlockState(pos);
            if ((!player.isEating() || player.getHeldItem() == null) && blockState.getBlock().onBlockActivated(world, pos, blockState, player, side, hitX, hitY, hitZ)) {
               blockActivated = true;
            }

            if (!blockActivated && heldStack != null && heldStack.getItem() instanceof ItemBlock) {
               ItemBlock itemBlock = (ItemBlock)heldStack.getItem();
               if (!itemBlock.canPlaceBlockOnSide(world, pos, side, player, heldStack)) {
                  return false;
               }
            }
         }

         this.netClientHandler.sendPacket(new C08PacketPlayerBlockPlacement(pos, side.hx(), player.inventory.Tdr(), hitX, hitY, hitZ));
         if (blockActivated || this.currentGameType == GameType.SPECTATOR) {
            return true;
         } else if (heldStack == null) {
            return false;
         } else if (this.currentGameType.nfr()) {
            int savedDamage = heldStack.Xs();
            int savedStackSize = heldStack.stackSize;
            boolean useResult = heldStack.onItemUse(player, world, pos, side, hitX, hitY, hitZ);
            heldStack.SR(savedDamage);
            heldStack.stackSize = savedStackSize;
            return useResult;
         } else {
            return heldStack.onItemUse(player, world, pos, side, hitX, hitY, hitZ);
         }
      }
   }

   public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side) {
      if (this.currentGameType.vQr()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            ItemStack inventoryStack = this.mc.thePlayer.getCurrentInventoryItem();
            if (inventoryStack == null) {
               return false;
            }

            if (!inventoryStack.pj(block)) {
               return false;
            }
         }
      }

      if (this.currentGameType.nfr() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      } else {
         WorldClient world = this.mc.theWorld;
         IBlockState blockState = world.getBlockState(pos);
         Block destroyedBlock = blockState.getBlock();
         if (destroyedBlock.getMaterial() == Material.air) {
            return false;
         } else {
            world.playAuxSFX(2001, pos, Block.getStateId(blockState));
            boolean wasDestroyed = world.setBlockToAir(pos);
            if (wasDestroyed) {
               destroyedBlock.onBlockDestroyedByPlayer(world, pos, blockState);
            }

            this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
            if (!this.currentGameType.nfr()) {
               ItemStack currentStack = this.mc.thePlayer.getCurrentInventoryItem();
               if (currentStack != null) {
                  currentStack.onBlockDestroyed(world, destroyedBlock, pos, this.mc.thePlayer);
                  if (currentStack.stackSize == 0) {
                     this.mc.thePlayer.destroyCurrentEquippedItem();
                  }
               }
            }

            return wasDestroyed;
         }
      }
   }

   public void updateController() {
      this.syncCurrentPlayItem();
      if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
         this.netClientHandler.getNetworkManager().processReceivedPackets();
      } else {
         this.netClientHandler.getNetworkManager().checkDisconnected();
      }
   }

   public void onStoppedUsingItem(EntityPlayer player) {
      this.syncCurrentPlayItem();
      this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      player.stopUsingItem();
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public void syncCurrentPlayItem() {
      int heldSlot = this.mc.thePlayer.inventory.currentItem;
      if (heldSlot != this.currentPlayerItem) {
         this.currentPlayerItem = heldSlot;
         this.netClientHandler.sendPacket(new C09PacketHeldItemChange(this.currentPlayerItem));
      }
   }

   public boolean isNotCreative() {
      return !this.currentGameType.nfr();
   }

   public void sendPacketDropItem(ItemStack stack) {
      if (this.currentGameType.nfr() && stack != null) {
         this.netClientHandler.sendPacket(new C10PacketCreativeInventoryAction(-1, stack));
      }
   }

   public void attackEntity(EntityPlayer player, Entity entity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.sendPacket(new C02PacketUseEntity(entity, net.minecraft.network.play.client.C02PacketUseEntity.Action.INTERACT));
      if (this.currentGameType != GameType.SPECTATOR) {
         player.attackTargetEntityWithCurrentItem(entity);
      }
   }

   public PlayerControllerMP(Minecraft minecraft, NetHandlerPlayClient netHandler) {
      this.mc = minecraft;
      this.netClientHandler = netHandler;
   }

   public float getBlockReachDistance() {
      return Kawaii.INSTANCE.getModuleManager().getModule(Reach.class).isEnabled()
         ? (float)Reach.reachSetting.getValue()
         : (this.currentGameType.nfr() ? 5.0F : 4.5F);
   }

   public boolean onPlayerDamageBlock(BlockPos pos, EnumFacing side) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         this.blockHitDelay--;
         return true;
      } else if (this.currentGameType.nfr() && this.mc.theWorld.getWorldBorder().contains(pos)) {
         this.blockHitDelay = 5;
         this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.START_DESTROY_BLOCK, pos, side));
         clickBlockCreative(this.mc, this, pos, side);
         return true;
      } else if (this.isHittingPosition(pos)) {
         Block block = this.mc.theWorld.getBlockState(pos).getBlock();
         if (block.getMaterial() == Material.air) {
            this.isHittingBlock = false;
            return false;
         } else {
            this.curBlockDamageMP = this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, pos);
            if (this.stepSoundTickCounter % 4.0F == 0.0F) {
               this.mc
                  .getSoundHandler()
                  .t_(
                     new PositionedSoundRecord(
                        new ResourceLocation(block.stepSound.Ob()),
                        (block.stepSound.XXo() + 1.0F) / 8.0F,
                        block.stepSound.qbr() * 0.5F,
                        (float)pos.getX() + 0.5F,
                        (float)pos.getY() + 0.5F,
                        (float)pos.getZ() + 0.5F
                     )
                  );
            }

            this.stepSoundTickCounter++;
            if (this.curBlockDamageMP >= 1.0F) {
               this.isHittingBlock = false;
               this.netClientHandler.sendPacket(new C07PacketPlayerDigging(Action.STOP_DESTROY_BLOCK, pos, side));
               this.onPlayerDestroyBlock(pos, side);
               this.curBlockDamageMP = 0.0F;
               this.stepSoundTickCounter = 0.0F;
               this.blockHitDelay = 5;
            }

            this.mc.theWorld.szr(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
            return true;
         }
      } else {
         return this.clickBlock(pos, side);
      }
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }
}
