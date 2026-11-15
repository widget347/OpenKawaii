package net.minecraft.client.network;

import com.google.common.collect.Maps;
import com.google.common.util.concurrent.Futures;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import kawaii.network.DownloadStatus;
import kawaii.ui.MainMenu;
import kawaii.util.SeededRandom;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDisconnected_2;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.stream.MetadataAchievement;
import net.minecraft.client.stream.MetadataCombat;
import net.minecraft.client.stream.MetadataPlayerDeath;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.S38PacketPlayerListItem.Action_2;
import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
import net.minecraft.network.play.server.S3CPacketUpdateScore.Action;
import net.minecraft.network.play.server.S42PacketCombatEvent.Event;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   public Minecraft gameController;
   public GameProfile profile;
   public int currentServerMaxPlayers;
   public WorldClient clientWorldController;
   public NetworkManager netManager;
   public static Logger logger = LogManager.getLogger();
   public GuiScreen guiScreenServer;
   public Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   public boolean doneLoadingTerrain;
   public Random avRandomizer;
   public boolean field_147308_k;

   @Override
   public void handleUpdateSign(S33PacketUpdateSign packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean signFound = false;
      if (this.gameController.theWorld.gro(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         if (tileEntity instanceof TileEntitySign) {
            TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
            if (tileEntitySign.Hgo()) {
               System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
               tileEntitySign.markDirty();
            }

            signFound = true;
         }
      }

      if (!signFound && this.gameController.thePlayer != null) {
         this.gameController
            .thePlayer
            .addChatMessage(
               new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ())
            );
      }
   }

   @Override
   public void handleBlockChange(S23PacketBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
   }

   @Override
   public void handleEffect(S28PacketEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.isSoundServerwide()) {
         this.gameController.theWorld.Nt(packetIn.wn(), packetIn.getSoundPos(), packetIn.fNo());
      } else {
         this.gameController.theWorld.playAuxSFX(packetIn.wn(), packetIn.getSoundPos(), packetIn.fNo());
      }
   }

   @Override
   public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.yT());
      if (entity instanceof EntityLivingBase) {
         PotionEffect potionEffect = new PotionEffect(packetIn.sL(), packetIn.rgr(), packetIn.KEr(), false, packetIn.HK());
         potionEffect.m_o(packetIn.My());
         ((EntityLivingBase)entity).addPotionEffect(potionEffect);
      }
   }

   @Override
   public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      TileEntity tileEntity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());
      if (!(tileEntity instanceof TileEntitySign)) {
         tileEntity = new TileEntitySign();
         tileEntity.setWorldObj(this.clientWorldController);
         tileEntity.setPos(packetIn.getSignPosition());
      }

      this.gameController.thePlayer.openEditSign((TileEntitySign)tileEntity);
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.gameController.loadWorld((WorldClient)null);
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
            this.gameController.displayGuiScreen(new GuiDisconnected(((GuiScreenRealmsProxy)this.guiScreenServer).CYr(), "disconnect.lost", reason).getProxy());
         } else {
            this.gameController.displayGuiScreen(new GuiDisconnected_2(this.guiScreenServer, "disconnect.lost", reason));
         }
      } else {
         this.gameController.displayGuiScreen(new GuiDisconnected_2(new GuiMultiplayer(new MainMenu()), "disconnect.lost", reason));
      }
   }

   @Override
   public void handleDisconnect(S40PacketDisconnect packetIn) {
      this.netManager.closeChannel(packetIn.getReason());
   }

   @Override
   public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      double x = packetIn.Wa();
      double y = packetIn.UH();
      double z = packetIn.kSo();
      float yaw = packetIn.$dr();
      float pitch = packetIn.NWo();
      if (packetIn.getRelativeFlags().contains(EnumFlags.X)) {
         x += player.posX;
      } else {
         player.motionX = 0.0;
      }

      if (packetIn.getRelativeFlags().contains(EnumFlags.Y)) {
         y += player.posY;
      } else {
         player.motionY = 0.0;
      }

      if (packetIn.getRelativeFlags().contains(EnumFlags.Z)) {
         z += player.posZ;
      } else {
         player.motionZ = 0.0;
      }

      if (packetIn.getRelativeFlags().contains(EnumFlags.X_ROT)) {
         pitch += player.rotationPitch;
      }

      if (packetIn.getRelativeFlags().contains(EnumFlags.Y_ROT)) {
         yaw += player.rotationYaw;
      }

      player.setPositionAndRotation(x, y, z, yaw, pitch);
      this.netManager
         .sendPacketWithEvent(new C06PacketPlayerPosLook(player.posX, player.getEntityBoundingBox().Bxp, player.posZ, player.rotationYaw, player.rotationPitch, false));
      if (!this.doneLoadingTerrain) {
         this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
         this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
         this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
         this.doneLoadingTerrain = true;
         this.gameController.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.Pso());
      if (entity instanceof EntityLivingBase) {
         ((EntityLivingBase)entity).Wn(packetIn.sRo());
      }
   }

   @Override
   public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.mBo(this.clientWorldController);
      if (entity != null) {
         entity.clientUpdateEntityNBT(packetIn.getTagCompound());
      }
   }

   @Override
   public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.closeScreenAndDropStack();
   }

   @Override
   public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.getEntityId());
      if (entity != null && packetIn.getMetadataList() != null) {
         entity.getDataWatcher().updateWatchedObjectsFromList(packetIn.getMetadataList());
      }
   }

   @Override
   public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (this.gameController.theWorld.gro(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         int tileEntityType = packetIn.getTileEntityType();
         if (tileEntityType == 1 && tileEntity instanceof TileEntityMobSpawner
            || tileEntityType == 2 && tileEntity instanceof TileEntityCommandBlock
            || tileEntityType == 3 && tileEntity instanceof TileEntityBeacon
            || tileEntityType == 4 && tileEntity instanceof TileEntitySkull
            || tileEntityType == 5 && tileEntity instanceof TileEntityFlowerPot
            || tileEntityType == 6 && tileEntity instanceof TileEntityBanner) {
            tileEntity.readFromNBT(packetIn.getNbtCompound());
         }
      }
   }

   @Override
   public void handleJoinGame(S01PacketJoinGame packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
      this.clientWorldController = new WorldClient(
         this,
         new WorldSettings(0L, packetIn.getGameType(), false, packetIn.Pa(), packetIn.getWorldType()),
         packetIn.USr(),
         packetIn.getDifficulty(),
         this.gameController.mcProfiler
      );
      this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
      this.gameController.loadWorld(this.clientWorldController);
      this.gameController.thePlayer.dimension = packetIn.USr();
      this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      this.gameController.thePlayer.setEntityId(packetIn.yD());
      this.currentServerMaxPlayers = packetIn.HJr();
      this.gameController.thePlayer.setReducedDebug(packetIn.kag());
      this.gameController.playerController.setGameType(packetIn.getGameType());
      this.gameController.gameSettings.sendSettingsToServer();
      this.netManager
         .sendPacketWithEvent(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
   }

   @Override
   public void handleEntityMovement(S14PacketEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.boo(this.clientWorldController);
      if (entity != null) {
         entity.serverPosX = entity.serverPosX + packetIn.Ejo();
         entity.serverPosY = entity.serverPosY + packetIn.lZo();
         entity.serverPosZ = entity.serverPosZ + packetIn.Ye();
         double x = (double)entity.serverPosX / 32.0;
         double y = (double)entity.serverPosY / 32.0;
         double z = (double)entity.serverPosZ / 32.0;
         float yaw = packetIn.YU() ? (float)(packetIn.Eqr() * 360) / 256.0F : entity.rotationYaw;
         float pitch = packetIn.YU() ? (float)(packetIn.Lag() * 360) / 256.0F : entity.rotationPitch;
         entity.setPositionAndRotation2(x, y, z, yaw, pitch, 3, false);
         entity.onGround = packetIn.Lmo();
      }
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   @Override
   public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
      double velocityScale = 8000.0;
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double x = (double)packetIn.qlo() / 32.0;
      double y = (double)packetIn.jlg() / 32.0;
      double z = (double)packetIn.DQ() / 32.0;
      Object entity = null;
      if (packetIn.oFo() == 10) {
         entity = EntityMinecart.createMinecart(this.clientWorldController, x, y, z, EnumMinecartType.byNetworkID(packetIn.PTo()));
      } else if (packetIn.oFo() == 90) {
         Entity shooter = this.clientWorldController.gw(packetIn.PTo());
         if (shooter instanceof EntityPlayer) {
            entity = new EntityFishHook(this.clientWorldController, x, y, z, (EntityPlayer)shooter);
         }

         packetIn.Owo(0);
      } else if (packetIn.oFo() == 60) {
         entity = new EntityArrow(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 61) {
         entity = new EntityEgg(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 71) {
         entity = new EntityItemFrame(
            this.clientWorldController, new BlockPos(MathHelper.Vlg(x), MathHelper.Vlg(y), MathHelper.Vlg(z)), EnumFacing.tnr(packetIn.PTo())
         );
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 77) {
         entity = new EntityLeashKnot(this.clientWorldController, new BlockPos(MathHelper.Vlg(x), MathHelper.Vlg(y), MathHelper.Vlg(z)));
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 65) {
         entity = new EntityEnderPearl(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 72) {
         entity = new EntityEnderEye(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 76) {
         entity = new EntityFireworkRocket(this.clientWorldController, x, y, z, (ItemStack)null);
      } else if (packetIn.oFo() == 63) {
         entity = new EntityLargeFireball(
            this.clientWorldController, x, y, z, (double)packetIn.iU() / velocityScale, (double)packetIn.QL() / velocityScale, (double)packetIn.xmo() / velocityScale
         );
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 64) {
         entity = new EntitySmallFireball(
            this.clientWorldController, x, y, z, (double)packetIn.iU() / velocityScale, (double)packetIn.QL() / velocityScale, (double)packetIn.xmo() / velocityScale
         );
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 66) {
         entity = new EntityWitherSkull(
            this.clientWorldController, x, y, z, (double)packetIn.iU() / velocityScale, (double)packetIn.QL() / velocityScale, (double)packetIn.xmo() / velocityScale
         );
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 62) {
         entity = new EntitySnowball(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 73) {
         entity = new EntityPotion(this.clientWorldController, x, y, z, packetIn.PTo());
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 75) {
         entity = new EntityExpBottle(this.clientWorldController, x, y, z);
         packetIn.Owo(0);
      } else if (packetIn.oFo() == 1) {
         entity = new EntityBoat(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 50) {
         entity = new EntityTNTPrimed(this.clientWorldController, x, y, z, (EntityLivingBase)null);
      } else if (packetIn.oFo() == 78) {
         entity = new EntityArmorStand(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 51) {
         entity = new EntityEnderCrystal(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 2) {
         entity = new EntityItem(this.clientWorldController, x, y, z);
      } else if (packetIn.oFo() == 70) {
         entity = new EntityFallingBlock(this.clientWorldController, x, y, z, Block.getStateById(packetIn.PTo() & 65535));
         packetIn.Owo(0);
      }

      if (entity != null) {
         ((Entity)entity).serverPosX = packetIn.qlo();
         ((Entity)entity).serverPosY = packetIn.jlg();
         ((Entity)entity).serverPosZ = packetIn.DQ();
         ((Entity)entity).rotationPitch = (float)(packetIn.SYo() * 360) / 256.0F;
         ((Entity)entity).rotationYaw = (float)(packetIn.hzo() * 360) / 256.0F;
         Entity[] parts = ((Entity)entity).getParts();
         if (parts != null) {
            int entityIdOffset = packetIn.Npo() - ((Entity)entity).getEntityId();

            for (int i = 0; i < parts.length; i++) {
               parts[i].setEntityId(parts[i].getEntityId() + entityIdOffset);
            }
         }

         ((Entity)entity).setEntityId(packetIn.Npo());
         this.clientWorldController.jrr(packetIn.Npo(), (Entity)entity);
         if (packetIn.PTo() > 0) {
            if (packetIn.oFo() == 60) {
               Entity arrowShooter = this.clientWorldController.gw(packetIn.PTo());
               if (arrowShooter instanceof EntityLivingBase && entity instanceof EntityArrow) {
                  ((EntityArrow)entity).XNa = arrowShooter;
               }
            }

            ((Entity)entity).setVelocity((double)packetIn.iU() / velocityScale, (double)packetIn.QL() / velocityScale, (double)packetIn.xmo() / velocityScale);
         }
      }
   }

   @Override
   public void handleCamera(S43PacketCamera packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.CNo(this.clientWorldController);
      if (entity != null) {
         this.gameController.setRenderViewEntity(entity);
      }
   }

   @Override
   public void handleSoundEffect(S29PacketSoundEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.playSound(packetIn.iNo(), packetIn.Qeg(), packetIn.DYr(), packetIn.getSoundName(), packetIn.WCr(), packetIn.th(), false);
   }

   public static NetworkManager access$000(NetHandlerPlayClient handler) {
      return handler.netManager;
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   @Override
   public void handleUseBed(S0APacketUseBed packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
   }

   @Override
   public void handleEntityProperties(S20PacketEntityProperties packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.getEntityId());
      if (entity != null) {
         if (!(entity instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entity + ")");
         }

         BaseAttributeMap attributeMap = ((EntityLivingBase)entity).getAttributeMap();

         for (Snapshot snapshot : packetIn.getSnapshots()) {
            IAttributeInstance attributeInstance = attributeMap.getAttributeInstanceByName(snapshot.getAttributeName());
            if (attributeInstance == null) {
               attributeInstance = attributeMap.registerAttribute(
                  new RangedAttribute(
                     (IAttribute)null,
                     snapshot.getAttributeName(),
                     0.0,
                     Double.MIN_NORMAL,                      1.7976931348623157e+308)
               );
            }

            attributeInstance.setBaseValue(snapshot.getBaseValue());
            attributeInstance.removeAllModifiers();

            for (AttributeModifier modifier : snapshot.getAttributeModifiers()) {
               attributeInstance.applyModifier(modifier);
            }
         }
      }
   }

   @Override
   public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      int gameState = packetIn.getGameState();
      float value = packetIn.getGameStateValue();
      int roundedValue = MathHelper.zor(value + 0.5F);
      if (gameState >= 0 && gameState < S2BPacketChangeGameState.Yxg.length && S2BPacketChangeGameState.Yxg[gameState] != null) {
         player.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.Yxg[gameState]));
      }

      if (gameState == 1) {
         this.clientWorldController.getWorldInfo().veg(true);
         this.clientWorldController.hho(0.0F);
      } else if (gameState == 2) {
         this.clientWorldController.getWorldInfo().veg(false);
         this.clientWorldController.hho(1.0F);
      } else if (gameState == 3) {
         this.gameController.playerController.setGameType(GameType.getByID(roundedValue));
      } else if (gameState == 4) {
         this.gameController.displayGuiScreen(new GuiWinGame());
      } else if (gameState == 5) {
         GameSettings settings = this.gameController.gameSettings;
         if (value == 0.0F) {
            this.gameController.displayGuiScreen(new GuiScreenDemo());
         } else if (value == 101.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.getKeyDisplayString(settings.keyBindForward.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindLeft.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindBack.getKeyCode()),
                     GameSettings.getKeyDisplayString(settings.keyBindRight.getKeyCode())
                  )
               );
         } else if (value == 102.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(settings.keyBindJump.getKeyCode())));
         } else if (value == 103.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(settings.keyBindInventory.getKeyCode())));
         }
      } else if (gameState == 6) {
         this.clientWorldController.playSound(player.posX, player.posY + (double)player.getEyeHeight(), player.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (gameState == 7) {
         this.clientWorldController.hho(value);
      } else if (gameState == 8) {
         this.clientWorldController.oer(value);
      } else if (gameState == 10) {
         this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, player.posX, player.posY, player.posZ, 0.0, 0.0, 0.0, new int[0]);
         this.clientWorldController.playSound(player.posX, player.posY, player.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleEntityStatus(S19PacketEntityStatus packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.gvo(this.clientWorldController);
      if (entity != null) {
         float rotationAngle = (float)(packetIn.getOpCode() * 360) / 256.0F;
         entity.updatePartialPosition(rotationAngle);
      }
   }

   public NetworkPlayerInfo getPlayerInfo(String playerName) {
      for (NetworkPlayerInfo playerInfo : this.playerInfoMap.values()) {
         if (playerInfo.getGameProfile().getName().equals(playerName)) {
            return playerInfo;
         }
      }

      return null;
   }

   @Override
   public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
      this.gameController.theWorld.getWorldInfo().Aor(packetIn.isDifficultyLocked());
   }

   @Override
   public void handleAnimation(S0BPacketAnimation packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.oEr());
      if (entity != null) {
         if (packetIn.iw() == 0) {
            EntityLivingBase livingEntity = (EntityLivingBase)entity;
            livingEntity.swingItem();
         } else if (packetIn.iw() == 1) {
            entity.performHurtAnimation();
         } else if (packetIn.iw() == 2) {
            EntityPlayer player = (EntityPlayer)entity;
            player.wakeUpPlayer(false, false, false);
         } else if (packetIn.iw() == 4) {
            this.gameController.effectRenderer.pA(entity, EnumParticleTypes.CRIT);
         } else if (packetIn.iw() == 5) {
            this.gameController.effectRenderer.pA(entity, EnumParticleTypes.CRIT_MAGIC);
         }
      }
   }

   public NetworkPlayerInfo getPlayerInfo(UUID playerUuid) {
      return this.playerInfoMap.get(playerUuid);
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   @Override
   public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
      this.gameController.ingameGUI.getTabList().TWr(packetIn.dGo().getFormattedText().length() == 0 ? null : packetIn.dGo());
      this.gameController.ingameGUI.getTabList().TMo(packetIn.Hrr().getFormattedText().length() == 0 ? null : packetIn.Hrr());
   }

   @Override
   public void handleSetExperience(S1FPacketSetExperience packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setXPStats(packetIn.getExperienceBar(), packetIn.kNr(), packetIn.jHo());
   }

   @Override
   public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Object attachedEntity = this.clientWorldController.gw(packetIn.zIo());
      Entity targetEntity = this.clientWorldController.gw(packetIn.xG());
      if (packetIn.eJr() == 0) {
         boolean showMountHint = false;
         if (packetIn.zIo() == this.gameController.thePlayer.getEntityId()) {
            attachedEntity = this.gameController.thePlayer;
            if (targetEntity instanceof EntityBoat) {
               ((EntityBoat)targetEntity).setIsBoatEmpty(false);
            }

            showMountHint = ((Entity)attachedEntity).ridingEntity == null && targetEntity != null;
         } else if (targetEntity instanceof EntityBoat) {
            ((EntityBoat)targetEntity).setIsBoatEmpty(true);
         }

         if (attachedEntity == null) {
            return;
         }

         ((Entity)attachedEntity).mountEntity(targetEntity);
         if (showMountHint) {
            GameSettings settings = this.gameController.gameSettings;
            this.gameController
               .ingameGUI
               .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(settings.keyBindSneak.getKeyCode())), false);
         }
      } else if (packetIn.eJr() == 1 && attachedEntity instanceof EntityLiving) {
         if (targetEntity != null) {
            ((EntityLiving)attachedEntity).vGr(targetEntity, false);
         } else {
            ((EntityLiving)attachedEntity).clearLeashed(false, false);
         }
      }
   }

   @Override
   public void handleCollectItem(S0DPacketCollectItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity collectedEntity = this.clientWorldController.gw(packetIn.AJo());
      Object collector = (EntityLivingBase)this.clientWorldController.gw(packetIn.Gj());
      if (collector == null) {
         collector = this.gameController.thePlayer;
      }

      if (collectedEntity != null) {
         if (collectedEntity instanceof EntityXPOrb) {
            this.clientWorldController.cOo(collectedEntity, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.clientWorldController.cOo(collectedEntity, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.gameController.effectRenderer.ZYr(new EntityPickupFX(this.clientWorldController, collectedEntity, (Entity)collector, 0.5F));
         this.clientWorldController.HCo(packetIn.AJo());
      }
   }

   @Override
   public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.AVo());
      if (entity != null) {
         entity.setVelocity((double)packetIn.qB() / 8000.0, (double)packetIn.Ino() / 8000.0, (double)packetIn.rho() / 8000.0);
      }
   }

   @Override
   public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.uu(this.clientWorldController);
      if (entity != null) {
         if (packetIn.getYaw() == 21) {
            this.gameController.getSoundHandler().t_(new GuardianSound((EntityGuardian)entity));
         } else {
            entity.handleStatusUpdate(packetIn.getYaw());
         }
      }
   }

   @Override
   public void handleWindowProperty(S31PacketWindowProperty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      if (player.openContainer != null && player.openContainer.windowId == packetIn.Zkr()) {
         player.openContainer.updateProgressBar(packetIn.RH(), packetIn.oho());
      }
   }

   public NetHandlerPlayClient(Minecraft minecraft, GuiScreen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
      this.currentServerMaxPlayers = 20;
      this.field_147308_k = false;
      this.avRandomizer = new SeededRandom();
      this.gameController = minecraft;
      this.guiScreenServer = guiScreen;
      this.netManager = networkManager;
      this.profile = gameProfile;
   }

   @Override
   public void handleExplosion(S27PacketExplosion packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Explosion explosion = new Explosion(
         this.gameController.theWorld, (Entity)null, packetIn.dZo(), packetIn.LVo(), packetIn.wRr(), packetIn.E_o(), packetIn.getAffectedBlockPositions()
      );
      explosion.doExplosionB(true);
      this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + (double)packetIn.aZr();
      this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + (double)packetIn.TPo();
      this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + (double)packetIn.dx();
   }

   @Override
   public void handleChat(S02PacketChat packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getType() == 2) {
         this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
      } else {
         this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
      }
   }

   @Override
   public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      int chunkIndex = 0;

      while (chunkIndex < packetIn.getChunkCount()) {
         int chunkX = packetIn.Yko(chunkIndex);
         int chunkZ = packetIn.DCr(chunkIndex);
         this.clientWorldController.doPreChunk(chunkX, chunkZ, true);
         this.clientWorldController.invalidateBlockReceiveRegion(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
         Chunk chunk = this.clientWorldController.GXr(chunkX, chunkZ);
         chunk.fillChunk(packetIn.getChunkBytes(chunkIndex), packetIn.W$r(chunkIndex), true);
         this.clientWorldController.markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
         if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunk.resetRelightChecks();
         }

         chunkIndex -= 31;
         chunkIndex += 32;
      }
   }

   @Override
   public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double x = (double)packetIn.yb() / 32.0;
      double y = (double)packetIn._wr() / 32.0;
      double z = (double)packetIn.Ymo() / 32.0;
      EntityLightningBolt lightningBolt = null;
      if (packetIn.RIo() == 1) {
         lightningBolt = new EntityLightningBolt(this.clientWorldController, x, y, z);
      }

      if (lightningBolt != null) {
         lightningBolt.serverPosX = packetIn.yb();
         lightningBolt.serverPosY = packetIn._wr();
         lightningBolt.serverPosZ = packetIn.Ymo();
         lightningBolt.rotationYaw = 0.0F;
         lightningBolt.rotationPitch = 0.0F;
         lightningBolt.setEntityId(packetIn.lH());
         this.clientWorldController.sH(lightningBolt);
      }
   }

   public void addToSendQueue(Packet packet) {
      this.netManager.sendPacket(packet);
   }

   @Override
   public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScoreObjective objective = scoreboard.getObjective(packetIn.XCr());
      if (packetIn.cjr() == Action.REMOVE) {
         Score score = scoreboard.getValueFromObjective(packetIn.Shr(), objective);
         score.setScorePoints(packetIn.getScoreValue());
      } else if (packetIn.cjr() == Action.CHANGE) {
         if (StringUtils.isNullOrEmpty(packetIn.XCr())) {
            scoreboard.removeObjectiveFromEntity(packetIn.Shr(), (ScoreObjective)null);
         } else if (objective != null) {
            scoreboard.removeObjectiveFromEntity(packetIn.Shr(), objective);
         }
      }
   }

   @Override
   public void handleKeepAlive(S00PacketKeepAlive packetIn) {
      this.sendPacket(new C00PacketKeepAlive(packetIn.getKeepAliveId()));
   }

   @Override
   public void handleRespawn(S07PacketRespawn packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.clientWorldController.getScoreboard();
         this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().fYo(), packetIn.getWorldType()),
            packetIn.getDimensionID(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
         );
         this.clientWorldController.setWorldScoreboard(scoreboard);
         this.gameController.loadWorld(this.clientWorldController);
         this.gameController.thePlayer.dimension = packetIn.getDimensionID();
         this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
      this.gameController.playerController.setGameType(packetIn.getGameType());
   }

   @Override
   public void handleMaps(S34PacketMaps packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      MapData mapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
      packetIn.setMapdataTo(mapData);
      this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(mapData);
   }

   public void sendPacket(Packet packet) {
      this.netManager.sendPacketWithEvent(packet);
   }

   @Override
   public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (BlockUpdateData blockUpdate : packetIn.getChangedBlocks()) {
         this.clientWorldController.invalidateRegionAndSetBlock(blockUpdate.getPos(), blockUpdate.getBlockState());
      }
   }

   public static Minecraft access$102(NetHandlerPlayClient handler, Minecraft minecraft) {
      return handler.gameController = minecraft;
   }

   @Override
   public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setPlayerSPHealth(packetIn.Uhr());
      this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
      this.gameController.thePlayer.getFoodStats().Im(packetIn.Pqo());
   }

   @Override
   public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
         this.gameController.thePlayer.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
      }
   }

   @Override
   public void handleResourcePack(S48PacketResourcePackSend packetIn) {
      String url = packetIn.RFr();
      String hash = packetIn.qVr();
      if (url.startsWith("level://")) {
         String levelName = url.substring(8);
         File savesDir = new File(this.gameController.mcDataDir, "saves");
         File levelDir = new File(savesDir, levelName);
         if (levelDir.isFile()) {
            this.netManager.sendPacketWithEvent(new C19PacketResourcePackStatus(hash, DownloadStatus.Gsa));
            Futures.addCallback(this.gameController.getResourcePackRepository().setResourcePackInstance(levelDir), new NetHandlerPlayClient$1(this, hash));
         } else {
            this.netManager.sendPacketWithEvent(new C19PacketResourcePackStatus(hash, DownloadStatus.UMa));
         }
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() == ServerResourceMode.ENABLED) {
         this.netManager.sendPacketWithEvent(new C19PacketResourcePackStatus(hash, DownloadStatus.Gsa));
         Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(url, hash), new NetHandlerPlayClient$2(this, hash));
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() != ServerResourceMode.PROMPT) {
         this.netManager.sendPacketWithEvent(new C19PacketResourcePackStatus(hash, DownloadStatus.kBp));
      } else {
         this.gameController.addScheduledTask(new NetHandlerPlayClient$3(this, hash, url));
      }
   }

   @Override
   public void handleWorldBorder(S44PacketWorldBorder packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.applyToWorldBorder(this.clientWorldController.getWorldBorder());
   }

   @Override
   public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPosition(), true);
      this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPosition());
   }

   @Override
   public void handleStatistics(S37PacketStatistics packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean achievementGained = false;

      for (Entry entry : packetIn.getStatisticsMap().entrySet()) {
         StatBase stat = (StatBase)entry.getKey();
         int value = (Integer)entry.getValue();
         if (stat.isAchievement() && value > 0) {
            if (this.field_147308_k && this.gameController.thePlayer.getStatFileWriter().readStat(stat) == 0) {
               Achievement achievement = (Achievement)stat;
               this.gameController.loadingScreen.queueTakenAchievement(achievement);
               this.gameController.getTwitchStream().sendMetadata(new MetadataAchievement(achievement), 0L);
               if (stat == AchievementList.Hrp) {
                  this.gameController.gameSettings.showInventoryAchievementHint = false;
                  this.gameController.gameSettings.saveOptions();
               }
            }

            achievementGained = true;
         }

         this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, stat, value);
      }

      if (!this.field_147308_k && !achievementGained && this.gameController.gameSettings.showInventoryAchievementHint) {
         this.gameController.loadingScreen.queueAchievementInformation(AchievementList.Hrp);
      }

      this.field_147308_k = true;
      if (this.gameController.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.gameController.currentScreen).doneLoading();
      }
   }

   @Override
   public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
      try {
         PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
         double x = (double)packetIn.ws() / 32.0;
         double y = (double)packetIn.LWo() / 32.0;
         double z = (double)packetIn.gho() / 32.0;
         float yaw = (float)(packetIn.qS() * 360) / 256.0F;
         float pitch = (float)(packetIn.iMo() * 360) / 256.0F;
         EntityOtherPlayerMP player = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
         player.prevPosX = player.lastTickPosX = (double)(player.serverPosX = packetIn.ws());
         player.prevPosY = player.lastTickPosY = (double)(player.serverPosY = packetIn.LWo());
         player.prevPosZ = player.lastTickPosZ = (double)(player.serverPosZ = packetIn.gho());
         int heldItemId = packetIn.jko();
         if (heldItemId == 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
         } else {
            player.inventory.mainInventory[player.inventory.currentItem] = new ItemStack(Item.getItemById(heldItemId), 1, 0);
         }

         player.setPositionAndRotation(x, y, z, yaw, pitch);
         this.clientWorldController.jrr(packetIn.Tgr(), player);
         List metadataList = packetIn.getMetadataList();
         if (metadataList != null) {
            player.getDataWatcher().updateWatchedObjectsFromList(metadataList);
         }
      } catch (Exception exception) {
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleTitle(S45PacketTitle packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Type type = packetIn.getType();
      String title = null;
      String subtitle = null;
      String message = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
      switch (NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S45PacketTitle$Type[type.ordinal()]) {
         case 1:
            title = message;
            break;
         case 2:
            subtitle = message;
            break;
         case 3:
            this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
            this.gameController.ingameGUI.setDefaultTitlesTimes();
            return;
      }

      this.gameController.ingameGUI.displayTitle(title, subtitle, packetIn.RFo(), packetIn.Si(), packetIn.kDo());
   }

   @Override
   public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      if ("minecraft:container".equals(packetIn.getGuiId())) {
         player.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.fMo()));
         player.openContainer.windowId = packetIn.Cmr();
      } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
         player.displayVillagerTradeGui(new NpcMerchant(player, packetIn.getWindowTitle()));
         player.openContainer.windowId = packetIn.Cmr();
      } else if ("EntityHorse".equals(packetIn.getGuiId())) {
         Entity entity = this.clientWorldController.gw(packetIn.vAr());
         if (entity instanceof EntityHorse) {
            player.displayGUIHorse((EntityHorse)entity, new AnimalChest(packetIn.getWindowTitle(), packetIn.fMo()));
            player.openContainer.windowId = packetIn.Cmr();
         }
      } else if (!packetIn.hasSlots()) {
         player.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
         player.openContainer.windowId = packetIn.Cmr();
      } else {
         ContainerLocalMenu localMenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.fMo());
         player.displayGUIChest(localMenu);
         player.openContainer.windowId = packetIn.Cmr();
      }
   }

   @Override
   public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPainting painting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
      this.clientWorldController.jrr(packetIn.getEntityID(), painting);
   }

   @Override
   public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double x = (double)packetIn.SJo() / 32.0;
      double y = (double)packetIn.Gp() / 32.0;
      double z = (double)packetIn.Iio() / 32.0;
      float yaw = (float)(packetIn.vtr() * 360) / 256.0F;
      float pitch = (float)(packetIn.teg() * 360) / 256.0F;
      EntityLivingBase entity = (EntityLivingBase)EntityList.Wl(packetIn.bRo(), this.gameController.theWorld);
      entity.serverPosX = packetIn.SJo();
      entity.serverPosY = packetIn.Gp();
      entity.serverPosZ = packetIn.Iio();
      entity.eXp = entity.renderYawOffset = (float)(packetIn.oHr() * 360) / 256.0F;
      Entity[] parts = entity.getParts();
      if (parts != null) {
         int entityIdOffset = packetIn.Bl() - entity.getEntityId();
         int i = 0;

         while (i < parts.length) {
            parts[i].setEntityId(parts[i].getEntityId() + entityIdOffset);
            i += 35;
            i -= 34;
         }
      }

      entity.setEntityId(packetIn.Bl());
      entity.setPositionAndRotation(x, y, z, yaw, pitch);
      entity.motionX = (double)((float)packetIn.Mro() / 8000.0F);
      entity.motionY = (double)((float)packetIn.bpo() / 8000.0F);
      entity.motionZ = (double)((float)packetIn.Bro() / 8000.0F);
      this.clientWorldController.jrr(packetIn.Bl(), entity);
      List metadataList = packetIn.getMetadataList();
      if (metadataList != null) {
         entity.getDataWatcher().updateWatchedObjectsFromList(metadataList);
      }
   }

   @Override
   public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      player.capabilities.allowFlying = packetIn.ysr();
      player.capabilities.$$c = packetIn.Bz();
      player.capabilities.LYg = packetIn.Sso();
      player.capabilities.MAa = packetIn.Ehg();
      player.capabilities.gWr(packetIn.err());
      player.capabilities.hOr(packetIn.Dd());
   }

   @Override
   public void handleBlockAction(S24PacketBlockAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.qqr(), packetIn.bEo());
   }

   @Override
   public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Container container = null;
      EntityPlayerSP player = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         container = player.inventoryContainer;
      } else if (packetIn.getWindowId() == player.openContainer.windowId) {
         container = player.openContainer;
      }

      if (container != null && !packetIn.isTransactionAccepted()) {
         this.sendPacket(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
      }
   }

   @Override
   public void handleTeams(S3EPacketTeams packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScorePlayerTeam team;
      if (packetIn.QYr() == 0) {
         team = scoreboard.createTeam(packetIn.La());
      } else {
         team = scoreboard.Sn(packetIn.La());
      }

      if (packetIn.QYr() == 0 || packetIn.QYr() == 2) {
         team.RA(packetIn.rdr());
         team.ZD(packetIn.Bbo());
         team.setNameSuffix(packetIn._fo());
         team.setChatFormat(EnumChatFormatting.getFormattingByIndex(packetIn.prg()));
         team.setFriendlyFlags(packetIn.dSr());
         EnumVisible visibility = EnumVisible.getVisibilityByName(packetIn.FAr());
         if (visibility != null) {
            team.Oxr(visibility);
         }
      }

      if (packetIn.QYr() == 0 || packetIn.QYr() == 3) {
         for (String playerName : packetIn.getPlayerNames()) {
            scoreboard.addPlayerToTeam(playerName, packetIn.La());
         }
      }

      if (packetIn.QYr() == 4) {
         for (String removedPlayerName : packetIn.getPlayerNames()) {
            scoreboard.removePlayerFromTeam(removedPlayerName, team);
         }
      }

      if (packetIn.QYr() == 1) {
         scoreboard.removeTeam(team);
      }
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (AddPlayerData addPlayerData : packetIn.getPlayerEntries()) {
         if (packetIn.HLr() == Action_2.REMOVE_PLAYER) {
            this.playerInfoMap.remove(addPlayerData.getProfile().getId());
         } else {
            NetworkPlayerInfo playerInfo = this.playerInfoMap.get(addPlayerData.getProfile().getId());
            if (packetIn.HLr() == Action_2.ADD_PLAYER) {
               playerInfo = new NetworkPlayerInfo(addPlayerData);
               this.playerInfoMap.put(playerInfo.getGameProfile().getId(), playerInfo);
            }

            if (playerInfo != null) {
               switch (NetHandlerPlayClient$4.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action_2[packetIn.HLr().ordinal()]) {
                  case 1:
                     playerInfo.setGameType(addPlayerData.getGameMode());
                     playerInfo.rx(addPlayerData.getPing());
                     break;
                  case 2:
                     playerInfo.setGameType(addPlayerData.getGameMode());
                     break;
                  case 3:
                     playerInfo.rx(addPlayerData.getPing());
                     break;
                  case 4:
                     playerInfo.setDisplayName(addPlayerData.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (int i = 0; i < packetIn.getEntityIDs().length; i++) {
         this.clientWorldController.HCo(packetIn.getEntityIDs()[i]);
      }
   }

   @Override
   public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.Zeg());
      if (entity != null) {
         entity.setCurrentItemOrArmor(packetIn.jSr(), packetIn.getItemStack());
      }
   }

   @Override
   public void handleCombatEvent(S42PacketCombatEvent packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.$sp);
      EntityLivingBase livingEntity = entity instanceof EntityLivingBase ? (EntityLivingBase)entity : null;
      if (packetIn.eventType == Event.ENTITY_DIED) {
         long durationMs = (long)(1000 * packetIn.Gka / 20);
         MetadataCombat combatMetadata = new MetadataCombat(this.gameController.thePlayer, livingEntity);
         this.gameController.getTwitchStream().sendMetadataForRange(combatMetadata, 0L - durationMs, 0L);
      } else if (packetIn.eventType == Event.END_COMBAT) {
         Entity playerEntity = this.clientWorldController.gw(packetIn.ZYg);
         if (playerEntity instanceof EntityPlayer) {
            MetadataPlayerDeath deathMetadata = new MetadataPlayerDeath((EntityPlayer)playerEntity, livingEntity);
            deathMetadata.setDeathMessage(packetIn.deathMessage);
            this.gameController.getTwitchStream().sendMetadata(deathMetadata, 0L);
         }
      }
   }

   @Override
   public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
      if (!this.netManager.isLocalChannel()) {
         this.netManager.setCompressionTreshold(packetIn.getCompressionThreshold());
      }
   }

   @Override
   public void handleWindowItems(S30PacketWindowItems packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         player.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
      } else if (packetIn.getWindowId() == player.openContainer.windowId) {
         player.openContainer.putStacksInSlots(packetIn.getItemStacks());
      }
   }

   @Override
   public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = this.clientWorldController.gw(packetIn.oyo());
      if (entity != null) {
         entity.serverPosX = packetIn.qHo();
         entity.serverPosY = packetIn.uno();
         entity.serverPosZ = packetIn.KMo();
         double x = (double)entity.serverPosX / 32.0;
         double y = (double)entity.serverPosY / 32.0;
         double z = (double)entity.serverPosZ / 32.0;
         float yaw = (float)(packetIn.Jlo() * 360) / 256.0F;
         float pitch = (float)(packetIn.KD() * 360) / 256.0F;
         if (Math.abs(entity.posX - x) < 0.03125 && Math.abs(entity.posY - y) < 0.015625 && Math.abs(entity.posZ - z) < 0.03125) {
            entity.setPositionAndRotation2(entity.posX, entity.posY, entity.posZ, yaw, pitch, 3, true);
         } else {
            entity.setPositionAndRotation2(x, y, z, yaw, pitch, 3, true);
         }

         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleSetSlot(S2FPacketSetSlot packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP player = this.gameController.thePlayer;
      if (packetIn._bo() == -1) {
         player.inventory.setItemStack(packetIn.getItemStack());
      } else {
         boolean notInventoryTab = false;
         if (this.gameController.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative creativeGui = (GuiContainerCreative)this.gameController.currentScreen;
            notInventoryTab = creativeGui.getSelectedTabIndex() != CreativeTabs.xmc.sLr();
         }

         if (packetIn._bo() == 0 && packetIn.Vzo() >= 36 && packetIn.Vzo() < 45) {
            ItemStack currentStack = player.inventoryContainer.getSlot(packetIn.Vzo()).getStack();
            if (packetIn.getItemStack() != null && (currentStack == null || currentStack.stackSize < packetIn.getItemStack().stackSize)) {
               packetIn.getItemStack().Auc = 5;
            }

            player.inventoryContainer.putStackInSlot(packetIn.Vzo(), packetIn.getItemStack());
         } else if (packetIn._bo() == player.openContainer.windowId && (packetIn._bo() != 0 || !notInventoryTab)) {
            player.openContainer.putStackInSlot(packetIn.Vzo(), packetIn.getItemStack());
         }
      }
   }

   @Override
   public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.KNr(packetIn.wpr());
      this.gameController.theWorld.setWorldTime(packetIn.qyo());
   }

   public void cleanup() {
      this.clientWorldController = null;
   }

   @Override
   public void handleTabComplete(S3APacketTabComplete packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      String[] completions = packetIn.getTabCompletions();
      if (this.gameController.currentScreen instanceof GuiChat) {
         GuiChat guiChat = (GuiChat)this.gameController.currentScreen;
         guiChat.onAutocompleteResponse(completions);
      }
   }

   @Override
   public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityXPOrb orb = new EntityXPOrb(
         this.clientWorldController, (double)packetIn.SRr() / 32.0, (double)packetIn.JMr() / 32.0, (double)packetIn.TLr() / 32.0, packetIn.hdr()
      );
      orb.serverPosX = packetIn.SRr();
      orb.serverPosY = packetIn.JMr();
      orb.serverPosZ = packetIn.TLr();
      orb.rotationYaw = 0.0F;
      orb.rotationPitch = 0.0F;
      orb.setEntityId(packetIn.lJr());
      this.clientWorldController.jrr(packetIn.lJr(), orb);
   }

   @Override
   public void handleChunkData(S21PacketChunkData packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.isFullChunk()) {
         if (packetIn.gko() == 0) {
            this.clientWorldController.doPreChunk(packetIn.kN(), packetIn.Dq(), false);
            return;
         }

         this.clientWorldController.doPreChunk(packetIn.kN(), packetIn.Dq(), true);
      }

      this.clientWorldController.invalidateBlockReceiveRegion(packetIn.kN() << 4, 0, packetIn.Dq() << 4, (packetIn.kN() << 4) + 15, 256, (packetIn.Dq() << 4) + 15);
      Chunk chunk = this.clientWorldController.GXr(packetIn.kN(), packetIn.Dq());
      chunk.fillChunk(packetIn.getChunkData(), packetIn.gko(), packetIn.isFullChunk());
      this.clientWorldController.markBlockRangeForRenderUpdate(packetIn.kN() << 4, 0, packetIn.Dq() << 4, (packetIn.kN() << 4) + 15, 256, (packetIn.Dq() << 4) + 15);
      if (!packetIn.isFullChunk() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
         chunk.resetRelightChecks();
      }
   }

   @Override
   public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getActionType() == 0) {
         ScoreObjective newObjective = scoreboard.addScoreObjective(packetIn.kmr(), IScoreObjectiveCriteria.dummy);
         newObjective.setDisplayName(packetIn.cp());
         newObjective.setRenderType(packetIn.getRenderType());
      } else {
         ScoreObjective objective = scoreboard.getObjective(packetIn.kmr());
         if (packetIn.getActionType() == 1) {
            scoreboard.removeObjective(objective);
         } else if (packetIn.getActionType() == 2) {
            objective.setDisplayName(packetIn.cp());
            objective.setRenderType(packetIn.getRenderType());
         }
      }
   }

   @Override
   public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getObjectiveName().length() == 0) {
         scoreboard.setObjectiveInDisplaySlot(packetIn.getDisplaySlot(), (ScoreObjective)null);
      } else {
         ScoreObjective objective = scoreboard.getObjective(packetIn.getObjectiveName());
         scoreboard.setObjectiveInDisplaySlot(packetIn.getDisplaySlot(), objective);
      }
   }

   @Override
   public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if ("MC|TrList".equals(packetIn.getChannelName())) {
         PacketBuffer buffer = packetIn.getBufferData();

         try {
            int tradeWindowId = buffer.readInt();
            GuiScreen screen = this.gameController.currentScreen;
            if (screen != null && screen instanceof GuiMerchant && tradeWindowId == this.gameController.thePlayer.openContainer.windowId) {
               IMerchant merchant = ((GuiMerchant)screen).getMerchant();
               MerchantRecipeList recipeList = MerchantRecipeList.readFromBuf(buffer);
               merchant.setRecipes(recipeList);
            }
         } catch (Exception exception) {
            logger.error("Couldn't load trade info", exception);
         } finally {
            buffer.release();
         }
      } else if ("MC|Brand".equals(packetIn.getChannelName())) {
         this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
      } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
         ItemStack heldItem = this.gameController.thePlayer.getCurrentInventoryItem();
         if (heldItem != null && heldItem.getItem() == Items.written_book) {
            this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, heldItem, false));
         }
      }
   }

   @Override
   public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.szr(packetIn.qO(), packetIn.getPosition(), packetIn.Mkr());
   }

   // $VF: Could not verify finally blocks. A semaphore variable has been added to preserve control flow.
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public void handleParticles(S2APacketParticles packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getParticleCount() == 0) {
         double speedX = (double)(packetIn.D$() * packetIn.LOr());
         double speedY = (double)(packetIn.D$() * packetIn.wjo());
         double speedZ = (double)(packetIn.D$() * packetIn.wgo());
         boolean spawnFailed = false /* VF: Semaphore variable */;

         try {
            spawnFailed = true;
            this.clientWorldController
               .spawnParticle(packetIn.getParticleType(), packetIn.isLongDistance(), packetIn.iRo(), packetIn.jqr(), packetIn.jxo(), speedX, speedY, speedZ, packetIn.getParticleArgs());
            spawnFailed = false;
         } finally {
            if (spawnFailed) {
               logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
               return;
            }
         }
      } else {
         int i = 0;

         while (i < packetIn.getParticleCount()) {
            double offsetX = this.avRandomizer.nextGaussian() * (double)packetIn.LOr();
            double offsetY = this.avRandomizer.nextGaussian() * (double)packetIn.wjo();
            double offsetZ = this.avRandomizer.nextGaussian() * (double)packetIn.wgo();
            double velocityX = this.avRandomizer.nextGaussian() * (double)packetIn.D$();
            double velocityY = this.avRandomizer.nextGaussian() * (double)packetIn.D$();
            double velocityZ = this.avRandomizer.nextGaussian() * (double)packetIn.D$();
            boolean spawnFailed = false /* VF: Semaphore variable */;

            try {
               spawnFailed = true;
               this.clientWorldController
                  .spawnParticle(
                     packetIn.getParticleType(),
                     packetIn.isLongDistance(),
                     packetIn.iRo() + offsetX,
                     packetIn.jqr() + offsetY,
                     packetIn.jxo() + offsetZ,
                     velocityX,
                     velocityY,
                     velocityZ,
                     packetIn.getParticleArgs()
                  );
               spawnFailed = false;
            } finally {
               if (spawnFailed) {
                  logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
                  return;
               }
            }

            i -= 11;
            i += 12;
         }
      }
   }

   public static Minecraft access$100(NetHandlerPlayClient handler) {
      return handler.gameController;
   }
}
