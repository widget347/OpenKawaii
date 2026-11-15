package net.minecraft.client.renderer;

import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import kawaii.Kawaii;
import kawaii.event.EventRender3D;
import kawaii.util.CustomDate;
import kawaii.util.SeededRandom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import optfine.Config;
import optfine.CustomColorizer;
import optfine.Lagometer;
import optfine.RandomMobs;
import optfine.Reflector;
import optfine.RenderPlayerOF;
import optfine.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;

public class EntityRenderer implements ISpectatorMenuRecipient {
   public int rainSoundCounter;
   public float prevBossColorModifier;
   public float thirdPersonDistanceTemp;
   public float smoothWorldFactor;
   public int shaderIndex;
   public MouseFilter mouseFilterXAxis;
   public float farPlaneDistance;
   public boolean cloudFog;
   public static ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
   public IResourceManager resourceManager;
   public float fogColorBlue;
   public boolean optifineInitialized;
   public int serverWaitTimeCurrent;
   public int serverWaitTime;
   public Entity pointedEntity;
   public float fogColorRed;
   public DynamicTexture lightmapTexture;
   public float fogColorGreen;
   public float prevFogColor1;
   public static ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
   public float smoothCamPartialTicks;
   public float clipDistance;
   public long prevFrameTime;
   public float fovModifierHandPrev;
   public long renderEndNanoTime;
   public float smoothCamYaw;
   public boolean debugViewActive;
   public float bossColorModifier;
   public int lastServerTicks;
   public float smoothCamFilterX;
   public float fogColor1;
   public static Logger logger = LogManager.getLogger();
   public float[] rainYCoords;
   public boolean fogStandard;
   public int panoramaFace;
   public double cameraZoom;
   public int[] lightmapColors;
   public float smoothWorldThreshold;
   public ShaderGroup theShaderGroup;
   public FloatBuffer fogColorBuffer;
   public float smoothCamFilterY;
   public boolean renderHandEnabled;
   public static boolean anaglyphEnable;
   public long lastServerTime;
   public boolean useShader;
   public float smoothCamPitch;
   public Random random = new SeededRandom();
   public int frameCount;
   public Minecraft mc;
   public static int anaglyphField;
   public double cameraPitch;
   public static String classIdentifier = "CL_00000947";
   public float[] rainXCoords;
   public float torchFlickerDX;
   public boolean drawBlockOutline;
   public ResourceLocation locationLightMap;
   public boolean panoramaMode;
   public float thirdPersonDistance;
   public float fovModifierHand;
   public MouseFilter mouseFilterYAxis;
   public World lastWorld;
   public ItemRenderer itemRenderer;
   public static ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{
      new ResourceLocation("shaders/post/notch.json"),
      new ResourceLocation("shaders/post/fxaa.json"),
      new ResourceLocation("shaders/post/art.json"),
      new ResourceLocation("shaders/post/bumpy.json"),
      new ResourceLocation("shaders/post/blobs2.json"),
      new ResourceLocation("shaders/post/pencil.json"),
      new ResourceLocation("shaders/post/color_convolve.json"),
      new ResourceLocation("shaders/post/deconverge.json"),
      new ResourceLocation("shaders/post/flip.json"),
      new ResourceLocation("shaders/post/invert.json"),
      new ResourceLocation("shaders/post/ntsc.json"),
      new ResourceLocation("shaders/post/outline.json"),
      new ResourceLocation("shaders/post/phosphor.json"),
      new ResourceLocation("shaders/post/scan_pincushion.json"),
      new ResourceLocation("shaders/post/sobel.json"),
      new ResourceLocation("shaders/post/bits.json"),
      new ResourceLocation("shaders/post/desaturate.json"),
      new ResourceLocation("shaders/post/green.json"),
      new ResourceLocation("shaders/post/blur.json"),
      new ResourceLocation("shaders/post/wobble.json"),
      new ResourceLocation("shaders/post/blobs.json"),
      new ResourceLocation("shaders/post/antialias.json"),
      new ResourceLocation("shaders/post/creeper.json"),
      new ResourceLocation("shaders/post/spider.json")
   };
   public static int shaderCount = shaderResourceLocations.length;
   public float torchFlickerX;
   public boolean lightmapUpdateNeeded;
   public MapItemRenderer theMapItemRenderer;
   public int rendererUpdateCount;
   public long lastErrorCheckTime;
   public double cameraYaw;

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void updateCameraAndRender(float partialTicks, long nanoTime) {
      this.checkWorldChanged();
      boolean displayActive = Display.isActive();
      if (!displayActive && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
         if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
            this.mc.displayInGameMenu();
         }
      } else {
         this.prevFrameTime = Minecraft.getSystemTime();
      }

      this.mc.mcProfiler.kqr("mouse");
      if (displayActive && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      if (this.mc.inGameHasFocus && displayActive) {
         this.mc.mouseHelper.mouseXYChange();
         float sensitivity = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float mouseMultiplier = sensitivity * sensitivity * sensitivity * 8.0F;
         float mouseDeltaX = (float)this.mc.mouseHelper.lAp * mouseMultiplier;
         float mouseDeltaY = (float)this.mc.mouseHelper.QIa * mouseMultiplier;
         byte pitchSign = 1;
         if (this.mc.gameSettings.invertMouse) {
            pitchSign = -1;
         }

         if (this.mc.gameSettings.smoothCamera) {
            this.smoothCamYaw += mouseDeltaX;
            this.smoothCamPitch += mouseDeltaY;
            float partialTickDelta = partialTicks - this.smoothCamPartialTicks;
            this.smoothCamPartialTicks = partialTicks;
            mouseDeltaX = this.smoothCamFilterX * partialTickDelta;
            mouseDeltaY = this.smoothCamFilterY * partialTickDelta;
            this.mc.thePlayer.setAngles(mouseDeltaX, mouseDeltaY * (float)pitchSign);
         } else {
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
            this.mc.thePlayer.setAngles(mouseDeltaX, mouseDeltaY * (float)pitchSign);
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.skipRenderWorld) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int scaledWidth = scaledResolution.getScaledWidth();
         int scaledHeight = scaledResolution.getScaledHeight();
         int mouseX = Mouse.getX() * scaledWidth / this.mc.displayWidth;
         int mouseY = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
         int maxFps = this.mc.gameSettings.maxFps;
         if (this.mc.theWorld != null) {
            this.mc.mcProfiler.kqr("level");
            int targetFps = Math.min(Minecraft.getDebugFPS(), maxFps);
            targetFps = Math.max(targetFps, 60);
            long elapsedNanos = System.nanoTime() - nanoTime;
            long waitNanos = Math.max((long)(1000000000 / targetFps / 4) - elapsedNanos, 0L);
            this.renderWorld(partialTicks, System.nanoTime() + waitNanos);
            if (OpenGlHelper.bza) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.theShaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.ZI();
                  this.theShaderGroup.loadShaderGroup(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.renderEndNanoTime = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
               if (this.mc.gameSettings.showFps && !this.mc.gameSettings.showDebugInfo) {
                  Config.drawFps();
               }

               if (this.mc.gameSettings.showDebugInfo) {
                  Lagometer.showLagometer(scaledResolution);
               }
            }

            this.mc.mcProfiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.matrixMode(5889);
            GlStateManager.ZI();
            GlStateManager.matrixMode(5888);
            GlStateManager.ZI();
            this.setupOverlayRendering();
            this.renderEndNanoTime = System.nanoTime();
         }

         if (this.mc.currentScreen != null) {
            GlStateManager.Ier(256);

            try {
               if (Reflector.ForgeHooksClient_drawScreen.exists()) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, mouseX, mouseY, partialTicks);
               } else {
                  this.mc.currentScreen.drawScreen(mouseX, mouseY, partialTicks);
               }
            } catch (Throwable throwable) {
               CrashReport crashReport = CrashReport.makeCrashReport(throwable, "Rendering screen");
               CrashReportCategory crashCategory = crashReport.makeCategory("Screen render details");
               crashCategory.addCrashSectionCallable("Screen name", new EntityRenderer$2(this));
               crashCategory.addCrashSectionCallable("Mouse location", new EntityRenderer$3(this, mouseX, mouseY));
               crashCategory.addCrashSectionCallable("Screen size", new EntityRenderer$4(this, scaledResolution));
               throw new ReportedException(crashReport);
            }
         }
      }

      this.checkGLError();
      this.updateSmoothWorld();
      Lagometer.updateLagometer();
      if (this.mc.gameSettings.profiler) {
         this.mc.gameSettings.showDebugProfilerChart = true;
      }
   }

   public static Minecraft access$000(EntityRenderer renderer) {
      return renderer.mc;
   }

   public void hurtCameraEffect(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         EntityLivingBase entityLiving = (EntityLivingBase)this.mc.getRenderViewEntity();
         float hurtProgress = (float)entityLiving.hurtTime - partialTicks;
         if (entityLiving.getHealth() <= 0.0F) {
            float deathProgress = (float)entityLiving.esa + partialTicks;
            GlStateManager.rotate(40.0F - 8000.0F / (deathProgress + 200.0F), 0.0F, 0.0F, 1.0F);
         }

         if (hurtProgress < 0.0F) {
            return;
         }

         hurtProgress /= (float)entityLiving.xng;
         hurtProgress = MathHelper.STo(hurtProgress * hurtProgress * hurtProgress * hurtProgress * (float) Math.PI);
         float prevBodyYaw = entityLiving.ODp;
         GlStateManager.rotate(-prevBodyYaw, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-hurtProgress * 14.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(prevBodyYaw, 0.0F, 1.0F, 0.0F);
      }
   }

   public boolean isShaderActive() {
      return OpenGlHelper.bza && this.theShaderGroup != null;
   }

   public void setupFog(int startCoords, float partialTicks) {
      Entity entity = this.mc.getRenderViewEntity();
      boolean isCreative = false;
      this.fogStandard = false;
      if (entity instanceof EntityPlayer) {
         isCreative = ((EntityPlayer)entity).capabilities.$$c;
      }

      GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Block blockAtViewpoint = ActiveRenderInfo.zcr(this.mc.theWorld, entity, partialTicks);
      Object fogEvent = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogDensity_Constructor, this, entity, blockAtViewpoint, partialTicks, 0.1F);
      if (Reflector.postForgeBusEvent(fogEvent)) {
         float fogDensity = Reflector.getFieldValueFloat(fogEvent, Reflector.EntityViewRenderEvent_FogDensity_density, 0.0F);
         GL11.glFogf(2914, fogDensity);
      } else if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.ara)) {
         float potionFogDistance = 5.0F;
         int potionDuration = ((EntityLivingBase)entity).getActivePotionEffect(Potion.ara).aNo();
         if (potionDuration < 20) {
            potionFogDistance = 5.0F + (this.clipDistance - 5.0F) * (1.0F - (float)potionDuration / 20.0F);
         }

         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogDensity(0.0F);
            GlStateManager.ky(potionFogDistance * 0.8F);
         } else {
            GlStateManager.setFogDensity(potionFogDistance * 0.25F);
            GlStateManager.ky(potionFogDistance);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
            GL11.glFogi(34138, 34139);
         }
      } else if (this.cloudFog) {
         GlStateManager.setFog(2048);
         GlStateManager.ger(0.1F);
      } else if (blockAtViewpoint.getMaterial() == Material.PGa) {
         GlStateManager.setFog(2048);
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.wqa)) {
            GlStateManager.ger(0.01F);
         } else {
            GlStateManager.ger(0.1F - (float)EnchantmentHelper.bur(entity) * 0.03F);
         }

         if (Config.isClearWater()) {
            GL11.glFogf(2914, 0.02F);
         }
      } else if (blockAtViewpoint.getMaterial() == Material.Gta) {
         GlStateManager.setFog(2048);
         GlStateManager.ger(2.0F);
      } else {
         float fogDistance = this.clipDistance;
         this.fogStandard = true;
         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogDensity(0.0F);
            GlStateManager.ky(fogDistance);
         } else {
            GlStateManager.setFogDensity(fogDistance * Config.getFogStart());
            GlStateManager.ky(fogDistance);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
               GL11.glFogi(34138, 34139);
            }

            if (Config.isFogFast()) {
               GL11.glFogi(34138, 34140);
            }
         }

         if (this.mc.theWorld.provider.tTo((int)entity.posX, (int)entity.posZ)) {
            GlStateManager.setFogDensity(fogDistance * 0.05F);
            GlStateManager.ky(fogDistance);
         }

         if (Reflector.ForgeHooksClient_onFogRender.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, this, entity, blockAtViewpoint, partialTicks, startCoords, fogDistance);
         }
      }

      GlStateManager.tSo();
      GlStateManager.Gu();
      GlStateManager.mVr(1028, 4608);
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      this.getMouseOver(partialTicks);
      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.mc.mcProfiler.kqr("center");
      if (this.mc.gameSettings.anaglyph) {
         anaglyphField = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.renderWorldPass(0, partialTicks, finishTimeNano);
         anaglyphField = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.renderWorldPass(1, partialTicks, finishTimeNano);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.renderWorldPass(2, partialTicks, finishTimeNano);
      }

      this.mc.mcProfiler.endSection();
   }

   public void setupViewBobbing(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)this.mc.getRenderViewEntity();
         float walkDelta = player.distanceWalkedModified - player.prevDistanceWalkedModified;
         float walkDistance = -(player.distanceWalkedModified + walkDelta * partialTicks);
         float interpolatedYaw = player.prevCameraYaw + (player.cameraYaw - player.prevCameraYaw) * partialTicks;
         float interpolatedPitch = player.vug + (player.Gfc - player.vug) * partialTicks;
         GlStateManager.translate(MathHelper.STo(walkDistance * (float) Math.PI) * interpolatedYaw * 0.5F, -Math.abs(MathHelper.imr(walkDistance * (float) Math.PI) * interpolatedYaw), 0.0F);
         GlStateManager.rotate(MathHelper.STo(walkDistance * (float) Math.PI) * interpolatedYaw * 3.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.imr(walkDistance * (float) Math.PI - 0.2F) * interpolatedYaw) * 5.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(interpolatedPitch, 1.0F, 0.0F, 0.0F);
      }
   }

   public void renderCloudsCheck(RenderGlobal renderGlobal, float partialTicks, int pass) {
      if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff()) {
         this.mc.mcProfiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.ZI();
         Project.gluPerspective(
            this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F
         );
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.setupFog(0, partialTicks);
         renderGlobal.sf(partialTicks, pass);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.ZI();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
         GlStateManager.matrixMode(5888);
      }
   }

   public void updateTorchFlicker() {
      this.torchFlickerDX = (float)((double)this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.torchFlickerDX = (float)(
         (double)this.torchFlickerDX * 0.9
      );
      this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
      this.lightmapUpdateNeeded = true;
   }

   public void enableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.matrixMode(5890);
      GlStateManager.ZI();
      float lightmapScale = 0.00390625F;
      GlStateManager.scale(lightmapScale, lightmapScale, lightmapScale);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.mc.getTextureManager().bindTexture(this.locationLightMap);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 10496);
      GL11.glTexParameteri(3553, 10243, 10496);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.gxp);
   }

   public void updateMainMenu(GuiMainMenu mainMenu) {
      try {
         String birthdayMessage = null;
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new CustomDate());
         int dayOfMonth = calendar.get(5);
         int month = calendar.get(2) + 1;
         if (dayOfMonth == 8 && month == 4) {
            birthdayMessage = "Happy birthday, OptiFine!";
         }

         if (dayOfMonth == 14 && month == 8) {
            birthdayMessage = "Happy birthday, sp614x!";
         }

         if (birthdayMessage == null) {
            return;
         }

         Field[] fields = GuiMainMenu.class.getDeclaredFields();

         for (int i = 0; i < fields.length; i++) {
            if (fields[i].getType() == String.class) {
               fields[i].setAccessible(true);
               fields[i].set(mainMenu, birthdayMessage);
               break;
            }
         }
      } finally {
         return;
      }
   }

   public void updateFogColor(float partialTicks) {
      WorldClient world = this.mc.theWorld;
      Entity entity = this.mc.getRenderViewEntity();
      float fogFactor = 0.25F + 0.75F * (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
      fogFactor = 1.0F - (float)Math.pow((double)fogFactor, 0.25);
      Vec3 skyColor = world.Yso(this.mc.getRenderViewEntity(), partialTicks);
      skyColor = CustomColorizer.getWorldSkyColor(skyColor, world, this.mc.getRenderViewEntity(), partialTicks);
      float skyRed = (float)skyColor.xCoord;
      float skyGreen = (float)skyColor.rea;
      float skyBlue = (float)skyColor.zCoord;
      Vec3 fogColorVec = world.getFogColor(partialTicks);
      fogColorVec = CustomColorizer.getWorldFogColor(fogColorVec, world, this.mc.getRenderViewEntity(), partialTicks);
      this.fogColorRed = (float)fogColorVec.xCoord;
      this.fogColorGreen = (float)fogColorVec.rea;
      this.fogColorBlue = (float)fogColorVec.zCoord;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double directionX = -1.0;
         Vec3 sunDirection = MathHelper.STo(world.ajo(partialTicks)) > 0.0F ? new Vec3(directionX, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         float sunDot = (float)entity.getLook(partialTicks).Vt(sunDirection);
         if (sunDot < 0.0F) {
            sunDot = 0.0F;
         }

         if (sunDot > 0.0F) {
            float[] sunsetColors = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks), partialTicks);
            if (sunsetColors != null) {
               sunDot *= sunsetColors[3];
               this.fogColorRed = this.fogColorRed * (1.0F - sunDot) + sunsetColors[0] * sunDot;
               this.fogColorGreen = this.fogColorGreen * (1.0F - sunDot) + sunsetColors[1] * sunDot;
               this.fogColorBlue = this.fogColorBlue * (1.0F - sunDot) + sunsetColors[2] * sunDot;
            }
         }
      }

      this.fogColorRed = this.fogColorRed + (skyRed - this.fogColorRed) * fogFactor;
      this.fogColorGreen = this.fogColorGreen + (skyGreen - this.fogColorGreen) * fogFactor;
      this.fogColorBlue = this.fogColorBlue + (skyBlue - this.fogColorBlue) * fogFactor;
      float rainStrength = world.Oj(partialTicks);
      if (rainStrength > 0.0F) {
         float rainDimFactor = 1.0F - rainStrength * 0.5F;
         float rainBlueFactor = 1.0F - rainStrength * 0.4F;
         this.fogColorRed *= rainDimFactor;
         this.fogColorGreen *= rainDimFactor;
         this.fogColorBlue *= rainBlueFactor;
      }

      float celestialFactor = world.getCelestialAngleRadians(partialTicks);
      if (celestialFactor > 0.0F) {
         float celestialDimFactor = 1.0F - celestialFactor * 0.5F;
         this.fogColorRed *= celestialDimFactor;
         this.fogColorGreen *= celestialDimFactor;
         this.fogColorBlue *= celestialDimFactor;
      }

      Block blockAtViewpoint = ActiveRenderInfo.zcr(this.mc.theWorld, entity, partialTicks);
      if (this.cloudFog) {
         Vec3 cloudColor = world.getCloudColour(partialTicks);
         this.fogColorRed = (float)cloudColor.xCoord;
         this.fogColorGreen = (float)cloudColor.rea;
         this.fogColorBlue = (float)cloudColor.zCoord;
      } else if (blockAtViewpoint.getMaterial() == Material.PGa) {
         float underwaterTint = (float)EnchantmentHelper.bur(entity) * 0.2F;
         if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.wqa)) {
            underwaterTint = underwaterTint * 0.3F + 0.6F;
         }

         this.fogColorRed = 0.02F + underwaterTint;
         this.fogColorGreen = 0.02F + underwaterTint;
         this.fogColorBlue = 0.2F + underwaterTint;
         Vec3 underwaterColor = CustomColorizer.getUnderwaterColor(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (underwaterColor != null) {
            this.fogColorRed = (float)underwaterColor.xCoord;
            this.fogColorGreen = (float)underwaterColor.rea;
            this.fogColorBlue = (float)underwaterColor.zCoord;
         }
      } else if (blockAtViewpoint.getMaterial() == Material.Gta) {
         this.fogColorRed = 0.6F;
         this.fogColorGreen = 0.1F;
         this.fogColorBlue = 0.0F;
      }

      float interpolatedFog = this.prevFogColor1 + (this.fogColor1 - this.prevFogColor1) * partialTicks;
      this.fogColorRed *= interpolatedFog;
      this.fogColorGreen *= interpolatedFog;
      this.fogColorBlue *= interpolatedFog;
      double voidFogFactor = world.provider.getVoidFogYFactor();
      double voidFogLevel = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks) * voidFogFactor;
      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.ara)) {
         int potionDuration = ((EntityLivingBase)entity).getActivePotionEffect(Potion.ara).aNo();
         if (potionDuration < 20) {
            voidFogLevel *= (double)(1.0F - (float)potionDuration / 20.0F);
         } else {
            voidFogLevel = 0.0;
         }
      }

      if (voidFogLevel < 1.0) {
         if (voidFogLevel < 0.0) {
            voidFogLevel = 0.0;
         }

         voidFogLevel *= voidFogLevel;
         this.fogColorRed = (float)((double)this.fogColorRed * voidFogLevel);
         this.fogColorGreen = (float)((double)this.fogColorGreen * voidFogLevel);
         this.fogColorBlue = (float)((double)this.fogColorBlue * voidFogLevel);
      }

      if (this.bossColorModifier > 0.0F) {
         float bossBlend = this.prevBossColorModifier + (this.bossColorModifier - this.prevBossColorModifier) * partialTicks;
         this.fogColorRed = this.fogColorRed * (1.0F - bossBlend) + this.fogColorRed * 0.7F * bossBlend;
         this.fogColorGreen = this.fogColorGreen * (1.0F - bossBlend) + this.fogColorGreen * 0.6F * bossBlend;
         this.fogColorBlue = this.fogColorBlue * (1.0F - bossBlend) + this.fogColorBlue * 0.6F * bossBlend;
      }

      if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(Potion.nightVision)) {
         float nightVisionBrightness = this.getNightVisionBrightness((EntityLivingBase)entity, partialTicks);
         float nightVisionScale = 1.0F / this.fogColorRed;
         if (nightVisionScale > 1.0F / this.fogColorGreen) {
            nightVisionScale = 1.0F / this.fogColorGreen;
         }

         if (nightVisionScale > 1.0F / this.fogColorBlue) {
            nightVisionScale = 1.0F / this.fogColorBlue;
         }

         this.fogColorRed = this.fogColorRed * (1.0F - nightVisionBrightness) + this.fogColorRed * nightVisionScale * nightVisionBrightness;
         this.fogColorGreen = this.fogColorGreen * (1.0F - nightVisionBrightness) + this.fogColorGreen * nightVisionScale * nightVisionBrightness;
         this.fogColorBlue = this.fogColorBlue * (1.0F - nightVisionBrightness) + this.fogColorBlue * nightVisionScale * nightVisionBrightness;
      }

      if (this.mc.gameSettings.anaglyph) {
         float anaglyphRed = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
         float anaglyphGreen = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
         float anaglyphBlue = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
         this.fogColorRed = anaglyphRed;
         this.fogColorGreen = anaglyphGreen;
         this.fogColorBlue = anaglyphBlue;
      }

      if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
         Object fogColorsEvent = Reflector.newInstance(
            Reflector.EntityViewRenderEvent_FogColors_Constructor, this, entity, blockAtViewpoint, partialTicks, this.fogColorRed, this.fogColorGreen, this.fogColorBlue
         );
         Reflector.postForgeBusEvent(fogColorsEvent);
         this.fogColorRed = Reflector.getFieldValueFloat(fogColorsEvent, Reflector.EntityViewRenderEvent_FogColors_red, this.fogColorRed);
         this.fogColorGreen = Reflector.getFieldValueFloat(fogColorsEvent, Reflector.EntityViewRenderEvent_FogColors_green, this.fogColorGreen);
         this.fogColorBlue = Reflector.getFieldValueFloat(fogColorsEvent, Reflector.EntityViewRenderEvent_FogColors_blue, this.fogColorBlue);
      }

      GlStateManager.ano(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
   }

   public void updateShaderGroupSize(int width, int height) {
      if (OpenGlHelper.bza) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(width, height);
         }

         this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager manager) {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      if (this.shaderIndex != shaderCount) {
         this.loadShader(shaderResourceLocations[this.shaderIndex]);
      } else {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      }
   }

   public void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
      RenderGlobal renderGlobal = this.mc.renderGlobal;
      EffectRenderer effectRenderer = this.mc.effectRenderer;
      boolean shouldDrawOutline = this.isDrawBlockOutline();
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("clear");
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      this.updateFogColor(partialTicks);
      GlStateManager.Ier(16640);
      this.mc.mcProfiler.endStartSection("camera");
      this.setupCameraTransform(partialTicks, pass);
      ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
      this.mc.mcProfiler.endStartSection("frustum");
      ClippingHelperImpl.getInstance();
      this.mc.mcProfiler.endStartSection("culling");
      Frustum frustum = new Frustum();
      Entity viewEntity = this.mc.getRenderViewEntity();
      double viewX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double)partialTicks;
      double viewY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double)partialTicks;
      double viewZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double)partialTicks;
      frustum.setPosition(viewX, viewY, viewZ);
      if (!Config.isSkyEnabled() && !Config.isSunMoonEnabled() && !Config.isStarsEnabled()) {
         GlStateManager.disableBlend();
      } else {
         this.setupFog(-1, partialTicks);
         this.mc.mcProfiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.ZI();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
         GlStateManager.matrixMode(5888);
         renderGlobal.Elo(partialTicks, pass);
         GlStateManager.matrixMode(5889);
         GlStateManager.ZI();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
         GlStateManager.matrixMode(5888);
      }

      this.setupFog(0, partialTicks);
      GlStateManager.shadeModel(7425);
      if (viewEntity.posY + (double)viewEntity.getEyeHeight() < 128.0 + (double)(this.mc.gameSettings.cloudsHeight * 128.0F)) {
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      this.mc.mcProfiler.endStartSection("prepareterrain");
      this.setupFog(0, partialTicks);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      this.mc.mcProfiler.endStartSection("terrain_setup");
      renderGlobal.klr(viewEntity, (double)partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());
      if (pass == 0 || pass == 2) {
         this.mc.mcProfiler.endStartSection("updatechunks");
         Lagometer.timerChunkUpload.start();
         this.mc.renderGlobal.updateChunks(finishTimeNano);
         Lagometer.timerChunkUpload.end();
      }

      this.mc.mcProfiler.endStartSection("terrain");
      Lagometer.timerTerrain.start();
      if (this.mc.gameSettings.smoothFps && pass > 0) {
         GL11.glFinish();
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      renderGlobal.Kvr(EnumWorldBlockLayer.SOLID, (double)partialTicks, pass, viewEntity);
      GlStateManager.enableAlpha();
      renderGlobal.Kvr(EnumWorldBlockLayer.CUTOUT_MIPPED, (double)partialTicks, pass, viewEntity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
      renderGlobal.Kvr(EnumWorldBlockLayer.CUTOUT, (double)partialTicks, pass, viewEntity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      Lagometer.timerTerrain.end();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      if (!this.panoramaMode) {
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
         }

         renderGlobal.zcg(viewEntity, frustum, partialTicks);
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         }

         RenderHelper.disableStandardItemLighting();
         this.disableLightmap();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.mc.objectMouseOver != null && viewEntity.isInsideOfMaterial(Material.PGa) && shouldDrawOutline) {
            EntityPlayer player = (EntityPlayer)viewEntity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            boolean hasHighlightEvent = Reflector.ForgeHooksClient_onDrawBlockHighlight.exists();
            if ((
                  !hasHighlightEvent
                     || !Reflector.callBoolean(
                        Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal, player, this.mc.objectMouseOver, 0, player.getHeldItem(), partialTicks
                     )
               )
               && !this.mc.gameSettings.hideGUI) {
               renderGlobal.drawSelectionBox(player, this.mc.objectMouseOver, 0, partialTicks);
            }

            GlStateManager.enableAlpha();
         }
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (shouldDrawOutline && this.mc.objectMouseOver != null && !viewEntity.isInsideOfMaterial(Material.PGa)) {
         EntityPlayer player = (EntityPlayer)viewEntity;
         GlStateManager.disableAlpha();
         this.mc.mcProfiler.endStartSection("outline");
         boolean hasHighlightEvent = Reflector.ForgeHooksClient_onDrawBlockHighlight.exists();
         if ((
               !hasHighlightEvent
                  || !Reflector.callBoolean(Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal, player, this.mc.objectMouseOver, 0, player.getHeldItem(), partialTicks)
            )
            && !this.mc.gameSettings.hideGUI) {
            renderGlobal.drawSelectionBox(player, this.mc.objectMouseOver, 0, partialTicks);
         }

         GlStateManager.enableAlpha();
      }

      this.mc.mcProfiler.endStartSection("destroyProgress");
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
      renderGlobal.Vfr(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), viewEntity, partialTicks);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      GlStateManager.disableBlend();
      if (!this.panoramaMode) {
         this.enableLightmap();
         this.mc.mcProfiler.endStartSection("litParticles");
         effectRenderer.$to(viewEntity, partialTicks);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, partialTicks);
         this.mc.mcProfiler.endStartSection("particles");
         effectRenderer.Ut(viewEntity, partialTicks);
         this.disableLightmap();
      }

      GlStateManager.depthMask(false);
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("weather");
      this.renderRainSnow(partialTicks);
      GlStateManager.depthMask(true);
      renderGlobal.Hs(viewEntity, partialTicks);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      this.setupFog(0, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      this.mc.mcProfiler.endStartSection("translucent");
      renderGlobal.Kvr(EnumWorldBlockLayer.TRANSLUCENT, (double)partialTicks, pass, viewEntity);
      if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.panoramaMode) {
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
         this.mc.renderGlobal.zcg(viewEntity, frustum, partialTicks);
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (viewEntity.posY + (double)viewEntity.getEyeHeight() >= 128.0 + (double)(this.mc.gameSettings.cloudsHeight * 128.0F)) {
         this.mc.mcProfiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
         this.mc.mcProfiler.endStartSection("forge_render_last");
         Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, renderGlobal, partialTicks);
      }

      GL11.glPushAttrib(1048575);
      EventRender3D renderEvent = new EventRender3D(partialTicks);
      Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(renderEvent);
      GL11.glPopAttrib();
      this.mc.mcProfiler.endStartSection("hand");
      boolean forgeHandledHand = Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, this.mc.renderGlobal, partialTicks, pass);
      if (!forgeHandledHand && this.renderHandEnabled) {
         GlStateManager.Ier(256);
         this.renderHand(partialTicks, pass);
         this.renderWorldDirections(partialTicks);
      }
   }

   public void switchUseShader() {
      this.useShader = !this.useShader;
   }

   public void setupOverlayRendering() {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.Ier(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.ZI();
      GlStateManager.ortho(0.0, scaledResolution.Mor(), scaledResolution.LBr(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.ZI();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public float getNightVisionBrightness(EntityLivingBase entity, float partialTicks) {
      int duration = entity.getActivePotionEffect(Potion.nightVision).aNo();
      return duration > 200 ? 1.0F : 0.7F + MathHelper.STo(((float)duration - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
   }

   public ShaderGroup getShaderGroup() {
      return this.theShaderGroup;
   }

   public void getMouseOver(float partialTicks) {
      Entity viewEntity = this.mc.getRenderViewEntity();
      if (viewEntity != null && this.mc.theWorld != null) {
         this.mc.mcProfiler.kqr("pick");
         this.mc.pointedEntity = null;
         double reachDistance = (double)this.mc.playerController.getBlockReachDistance();
         this.mc.objectMouseOver = viewEntity.rayTrace(reachDistance, partialTicks);
         double blockHitDistance = 6.0;
         Vec3 eyePos = viewEntity.getPositionEyes(partialTicks);
         boolean longReach = false;
         if (this.mc.playerController.isInCreativeMode()) {
            reachDistance = 6.0;
            blockHitDistance = 6.0;
         } else if (reachDistance > 3.0) {
            longReach = true;
         }

         if (this.mc.objectMouseOver != null) {
            blockHitDistance = this.mc.objectMouseOver.hitVec.distanceTo(eyePos);
         }

         Vec3 lookVec = viewEntity.getLook(partialTicks);
         Vec3 reachEnd = eyePos.addVector(lookVec.xCoord * reachDistance, lookVec.rea * reachDistance, lookVec.zCoord * reachDistance);
         this.pointedEntity = null;
         Vec3 pointedHitVec = null;
         float expandAmount = 1.0F;
         List entities = this.mc
            .theWorld
            .nQ(
               viewEntity,
               viewEntity.getEntityBoundingBox().eqr(lookVec.xCoord * reachDistance, lookVec.rea * reachDistance, lookVec.zCoord * reachDistance).nJ((double)expandAmount, (double)expandAmount, (double)expandAmount),
               Predicates.and(EntitySelectors.kDc, new EntityRenderer$1(this))
            );
         double closestDistance = blockHitDistance;
         int entityIndex = 0;

         while (entityIndex < entities.size()) {
            Entity entity = (Entity)entities.get(entityIndex);
            float collisionBorder = entity.getCollisionBorderSize();
            AxisAlignedBB entityBox = entity.getEntityBoundingBox().nJ((double)collisionBorder, (double)collisionBorder, (double)collisionBorder);
            MovingObjectPosition intercept = entityBox.calculateIntercept(eyePos, reachEnd);
            if (entityBox.nYr(eyePos)) {
               if (closestDistance >= 0.0) {
                  this.pointedEntity = entity;
                  pointedHitVec = intercept == null ? eyePos : intercept.hitVec;
                  closestDistance = 0.0;
               }
            } else if (intercept != null) {
               double interceptDistance = eyePos.distanceTo(intercept.hitVec);
               if (interceptDistance < closestDistance || closestDistance == 0.0) {
                  boolean canRiderInteract = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     canRiderInteract = Reflector.callBoolean(entity, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (entity != viewEntity.ridingEntity || canRiderInteract) {
                     this.pointedEntity = entity;
                     pointedHitVec = intercept.hitVec;
                     closestDistance = interceptDistance;
                  } else if (closestDistance == 0.0) {
                     this.pointedEntity = entity;
                     pointedHitVec = intercept.hitVec;
                  }
               }
            }

            entityIndex += 48;
            entityIndex -= 47;
         }

         if (this.pointedEntity != null && longReach && eyePos.distanceTo(pointedHitVec) > 100.0) {
            this.pointedEntity = null;
            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectType.MISS, pointedHitVec, (EnumFacing)null, new BlockPos(pointedHitVec));
         }

         if (this.pointedEntity != null && (closestDistance < blockHitDistance || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, pointedHitVec);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public void checkGLError() {
      if (this.mc.theWorld != null) {
         long currentTime = System.currentTimeMillis();
         if (currentTime > this.lastErrorCheckTime + 10000L) {
            this.lastErrorCheckTime = currentTime;
            int errorCode = GL11.glGetError();
            if (errorCode != 0) {
               String errorString = GLU.gluErrorString(errorCode);
               ChatComponentText errorMessage = new ChatComponentText("§eOpenGL Error§f: " + errorCode + " (" + errorString + ")");
               this.mc.ingameGUI.getChatGUI().printChatMessage(errorMessage);
            }
         }
      }
   }

   public void disableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.gxp);
   }

   public EntityRenderer(Minecraft minecraft, IResourceManager resourceManagerIn) {
      this.mouseFilterXAxis = new MouseFilter();
      this.mouseFilterYAxis = new MouseFilter();
      this.thirdPersonDistance = 4.0F;
      this.thirdPersonDistanceTemp = 4.0F;
      this.renderHandEnabled = true;
      this.drawBlockOutline = true;
      this.prevFrameTime = Minecraft.getSystemTime();
      this.rainXCoords = new float[1024];
      this.rainYCoords = new float[1024];
      this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
      this.panoramaFace = 0;
      this.panoramaMode = false;
      this.cameraZoom = 1.0;
      this.optifineInitialized = false;
      this.lastWorld = null;
      this.debugViewActive = false;
      this.fogStandard = false;
      this.farPlaneDistance = 128.0F;
      this.lastServerTime = 0L;
      this.lastServerTicks = 0;
      this.serverWaitTime = 0;
      this.serverWaitTimeCurrent = 0;
      this.smoothWorldThreshold = 0.0F;
      this.smoothWorldFactor = 0.0F;
      this.lastErrorCheckTime = 0L;
      this.shaderIndex = shaderCount;
      this.useShader = false;
      this.frameCount = 0;
      this.mc = minecraft;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = minecraft.getItemRenderer();
      this.theMapItemRenderer = new MapItemRenderer(minecraft.getTextureManager());
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = minecraft.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
      this.theShaderGroup = null;

      for (int row = 0; row < 32; row++) {
         for (int col = 0; col < 32; col++) {
            float colOffset = (float)(col - 16);
            float rowOffset = (float)(row - 16);
            float offsetLength = MathHelper.Msr(colOffset * colOffset + rowOffset * rowOffset);
            this.rainXCoords[row << 5 | col] = -rowOffset / offsetLength;
            this.rainYCoords[row << 5 | col] = colOffset / offsetLength;
         }
      }
   }

   public void checkWorldChanged() {
      if (!this.optifineInitialized) {
         TextureUtils.registerResourceListener();
         RenderPlayerOF.register();
         if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
            Config.setNotify64BitJava(true);
         }

         this.optifineInitialized = true;
      }

      Config.isActing();
      Config.checkDisplayMode();
      WorldClient world = this.mc.theWorld;
      if (world != null) {
         if (Config.getNewRelease() != null) {
            String edition = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
            String releaseLabel = edition + " " + Config.getNewRelease();
            ChatComponentText releaseMessage = new ChatComponentText("A new §eOptiFine§f version is available: §e" + releaseLabel + "§f");
            this.mc.ingameGUI.getChatGUI().printChatMessage(releaseMessage);
            Config.setNewRelease((String)null);
         }

         if (Config.isNotify64BitJava()) {
            Config.setNotify64BitJava(false);
            ChatComponentText bitMessage = new ChatComponentText("You can install §e64-bit Java§f to increase performance");
            this.mc.ingameGUI.getChatGUI().printChatMessage(bitMessage);
         }
      }

      if (this.mc.currentScreen instanceof GuiMainMenu) {
         this.updateMainMenu((GuiMainMenu)this.mc.currentScreen);
      }

      if (this.lastWorld != world) {
         RandomMobs.worldChanged(this.lastWorld, world);
         Config.updateThreadPriorities();
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
         this.lastWorld = world;
      }
   }

   public void updateLightmap(float partialTicks) {
      if (this.lightmapUpdateNeeded) {
         this.mc.mcProfiler.kqr("lightTex");
         WorldClient world = this.mc.theWorld;
         if (world != null) {
            if (CustomColorizer.updateLightmap(world, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision))) {
               this.lightmapTexture.updateDynamicTexture();
               this.lightmapUpdateNeeded = false;
               this.mc.mcProfiler.endSection();
               return;
            }

            float sunBrightness = world.getSunBrightness(1.0F);
            float ambientBrightness = sunBrightness * 0.95F + 0.05F;
            int index = 0;

            while (index < 256) {
               float skyLight = world.provider.getLightBrightnessTable()[index / 16] * ambientBrightness;
               float blockLight = world.provider.getLightBrightnessTable()[index % 16] * (this.torchFlickerX * 0.1F + 1.5F);
               if (world.BRo() > 0) {
                  skyLight = world.provider.getLightBrightnessTable()[index / 16];
               }

               float skyRed = skyLight * (sunBrightness * 0.65F + 0.35F);
               float skyGreen = skyLight * (sunBrightness * 0.65F + 0.35F);
               float blockGreen = blockLight * ((blockLight * 0.6F + 0.4F) * 0.6F + 0.4F);
               float blockBlue = blockLight * (blockLight * blockLight * 0.6F + 0.4F);
               float red = skyRed + blockLight;
               float green = skyGreen + blockGreen;
               float blue = skyLight + blockBlue;
               red = red * 0.96F + 0.03F;
               green = green * 0.96F + 0.03F;
               blue = blue * 0.96F + 0.03F;
               if (this.bossColorModifier > 0.0F) {
                  float bossBlend = this.prevBossColorModifier + (this.bossColorModifier - this.prevBossColorModifier) * partialTicks;
                  red = red * (1.0F - bossBlend) + red * 0.7F * bossBlend;
                  green = green * (1.0F - bossBlend) + green * 0.6F * bossBlend;
                  blue = blue * (1.0F - bossBlend) + blue * 0.6F * bossBlend;
               }

               if (world.provider.Iy() == 1) {
                  red = 0.22F + blockLight * 0.75F;
                  green = 0.28F + blockGreen * 0.75F;
                  blue = 0.25F + blockBlue * 0.75F;
               }

               if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                  float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                  float nightVisionScale = 1.0F / red;
                  if (nightVisionScale > 1.0F / green) {
                     nightVisionScale = 1.0F / green;
                  }

                  if (nightVisionScale > 1.0F / blue) {
                     nightVisionScale = 1.0F / blue;
                  }

                  red = red * (1.0F - nightVisionBrightness) + red * nightVisionScale * nightVisionBrightness;
                  green = green * (1.0F - nightVisionBrightness) + green * nightVisionScale * nightVisionBrightness;
                  blue = blue * (1.0F - nightVisionBrightness) + blue * nightVisionScale * nightVisionBrightness;
               }

               if (red > 1.0F) {
                  red = 1.0F;
               }

               if (green > 1.0F) {
                  green = 1.0F;
               }

               if (blue > 1.0F) {
                  blue = 1.0F;
               }

               float gamma = this.mc.gameSettings.gamma;
               float gammaRed = 1.0F - red;
               float gammaGreen = 1.0F - green;
               float gammaBlue = 1.0F - blue;
               gammaRed = 1.0F - gammaRed * gammaRed * gammaRed * gammaRed;
               gammaGreen = 1.0F - gammaGreen * gammaGreen * gammaGreen * gammaGreen;
               gammaBlue = 1.0F - gammaBlue * gammaBlue * gammaBlue * gammaBlue;
               red = red * (1.0F - gamma) + gammaRed * gamma;
               green = green * (1.0F - gamma) + gammaGreen * gamma;
               blue = blue * (1.0F - gamma) + gammaBlue * gamma;
               red = red * 0.96F + 0.03F;
               green = green * 0.96F + 0.03F;
               blue = blue * 0.96F + 0.03F;
               if (red > 1.0F) {
                  red = 1.0F;
               }

               if (green > 1.0F) {
                  green = 1.0F;
               }

               if (blue > 1.0F) {
                  blue = 1.0F;
               }

               if (red < 0.0F) {
                  red = 0.0F;
               }

               if (green < 0.0F) {
                  green = 0.0F;
               }

               if (blue < 0.0F) {
                  blue = 0.0F;
               }

               int redComponent = (int)(red * 255.0F);
               int greenComponent = (int)(green * 255.0F);
               int blueComponent = (int)(blue * 255.0F);
               this.lightmapColors[index] = 0xFF000000 | redComponent << 16 | greenComponent << 8 | blueComponent;
               index -= 32;
               index += 33;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
            this.mc.mcProfiler.endSection();
         }
      }
   }

   public void orientCamera(float partialTicks) {
      float halfTurnDegrees = 180.0F;
      float pi = (float) Math.PI;
      Entity viewEntity = this.mc.getRenderViewEntity();
      float eyeHeight = viewEntity.getEyeHeight();
      double camX = viewEntity.prevPosX + (viewEntity.posX - viewEntity.prevPosX) * (double)partialTicks;
      double camY = viewEntity.prevPosY + (viewEntity.posY - viewEntity.prevPosY) * (double)partialTicks + (double)eyeHeight;
      double camZ = viewEntity.prevPosZ + (viewEntity.posZ - viewEntity.prevPosZ) * (double)partialTicks;
      if (viewEntity instanceof EntityLivingBase && ((EntityLivingBase)viewEntity).isSpawnForced()) {
         eyeHeight = (float)((double)eyeHeight + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.lockCameraRotation) {
            BlockPos bedPos = new BlockPos(viewEntity);
            IBlockState bedState = this.mc.theWorld.getBlockState(bedPos);
            Block bedBlock = bedState.getBlock();
            if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
               Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, bedPos, bedState, viewEntity);
            } else if (bedBlock == Blocks.bed) {
               int facingIndex = bedState.getValue(BlockBed.FACING).x$o();
               GlStateManager.rotate((float)(facingIndex * 90), 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(viewEntity.prevRotationYaw + (viewEntity.rotationYaw - viewEntity.prevRotationYaw) * partialTicks + halfTurnDegrees, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(viewEntity.prevRotationPitch + (viewEntity.rotationPitch - viewEntity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double cameraDistance = (double)(this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks);
         if (this.mc.gameSettings.lockCameraRotation) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-cameraDistance));
         } else {
            float yaw = viewEntity.rotationYaw;
            float pitch = viewEntity.rotationPitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               pitch += halfTurnDegrees;
            }

            double offsetX = (double)(-MathHelper.STo(yaw / halfTurnDegrees * pi) * MathHelper.imr(pitch / halfTurnDegrees * pi)) * cameraDistance;
            double offsetZ = (double)(MathHelper.imr(yaw / halfTurnDegrees * pi) * MathHelper.imr(pitch / halfTurnDegrees * pi)) * cameraDistance;
            double offsetY = (double)(-MathHelper.STo(pitch / halfTurnDegrees * pi)) * cameraDistance;
            int corner = 0;

            while (corner < 8) {
               float cornerX = (float)((corner & 1) * 2 - 1);
               float cornerY = (float)((corner >> 1 & 1) * 2 - 1);
               float cornerZ = (float)((corner >> 2 & 1) * 2 - 1);
               cornerX *= 0.1F;
               cornerY *= 0.1F;
               cornerZ *= 0.1F;
               MovingObjectPosition hit = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(camX + (double)cornerX, camY + (double)cornerY, camZ + (double)cornerZ),
                     new Vec3(camX - offsetX + (double)cornerX + (double)cornerZ, camY - offsetY + (double)cornerY, camZ - offsetZ + (double)cornerZ)
                  );
               if (hit != null) {
                  double hitDistance = hit.hitVec.distanceTo(new Vec3(camX, camY, camZ));
                  if (hitDistance < cameraDistance) {
                     cameraDistance = hitDistance;
                  }
               }

               corner -= 63;
               corner += 64;
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(halfTurnDegrees, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(viewEntity.rotationPitch - pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(viewEntity.rotationYaw - yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-cameraDistance));
            GlStateManager.rotate(yaw - viewEntity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(pitch - viewEntity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (!this.mc.gameSettings.lockCameraRotation) {
         GlStateManager.rotate(viewEntity.prevRotationPitch + (viewEntity.rotationPitch - viewEntity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
         if (viewEntity instanceof EntityAnimal) {
            EntityAnimal animal = (EntityAnimal)viewEntity;
            GlStateManager.rotate(animal.FMp + (animal.renderYawOffset - animal.FMp) * partialTicks + halfTurnDegrees, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(viewEntity.prevRotationYaw + (viewEntity.rotationYaw - viewEntity.prevRotationYaw) * partialTicks + halfTurnDegrees, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
      camX = viewEntity.prevPosX + (viewEntity.posX - viewEntity.prevPosX) * (double)partialTicks;
      camY = viewEntity.prevPosY + (viewEntity.posY - viewEntity.prevPosY) * (double)partialTicks + (double)eyeHeight;
      camZ = viewEntity.prevPosZ + (viewEntity.posZ - viewEntity.prevPosZ) * (double)partialTicks;
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(camX, camY, camZ, partialTicks);
   }

   public void renderWorldDirections(float partialTicks) {
      if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo
         )
       {
         Entity viewEntity = this.mc.getRenderViewEntity();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.ZI();
         this.orientCamera(partialTicks);
         GlStateManager.translate(0.0F, viewEntity.getEyeHeight(), 0.0F);
         RenderGlobal.drawOutlinedBoundingBox(
            new AxisAlignedBB(
               0.0,
               0.0,
               0.0,
               0.005,                0.0001,                0.0001),
            255,
            0,
            0,
            255
         );
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
         RenderGlobal.drawOutlinedBoundingBox(
            new AxisAlignedBB(
               0.0,
               0.0,
               0.0,                0.0001,
               0.0033,
               1.0E-4
            ),
            0,
            255,
            0,
            255
         );
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
      ((Buffer)this.fogColorBuffer).clear();
      this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
      ((Buffer)this.fogColorBuffer).flip();
      return this.fogColorBuffer;
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.theMapItemRenderer;
   }

   public void activateNextShader() {
      if (OpenGlHelper.bza && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
         if (this.shaderIndex != shaderCount) {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
         } else {
            this.theShaderGroup = null;
         }
      }
   }

   public void setupCameraTransform(float partialTicks, int pass) {
      this.clipDistance = (float)(this.mc.gameSettings.renderDistanceChunks * 16);
      if (Config.isFogFancy()) {
         this.clipDistance *= 0.95F;
      }

      if (Config.isFogFast()) {
         this.clipDistance *= 0.83F;
      }

      GlStateManager.matrixMode(5889);
      GlStateManager.ZI();
      float anaglyphOffset = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((float)(-(pass * 2 - 1)) * anaglyphOffset, 0.0F, 0.0F);
      }

      this.farPlaneDistance = this.clipDistance * 2.0F;
      if (this.farPlaneDistance < 173.0F) {
         this.farPlaneDistance = 173.0F;
      }

      if (this.mc.theWorld.provider.Iy() == 1) {
         this.farPlaneDistance = 256.0F;
      }

      if (this.cameraZoom != 1.0) {
         GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0);
      }

      Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.farPlaneDistance);
      GlStateManager.matrixMode(5888);
      GlStateManager.ZI();
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.setupViewBobbing(partialTicks);
      }

      float portalTime = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
      if (portalTime > 0.0F) {
         byte wobbleSpeed = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.bjp)) {
            wobbleSpeed = 7;
         }

         float portalScale = 5.0F / (portalTime * portalTime + 5.0F) - portalTime * 0.04F;
         portalScale *= portalScale;
         GlStateManager.rotate(((float)this.rendererUpdateCount + partialTicks) * (float)wobbleSpeed, 0.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F / portalScale, 1.0F, 1.0F);
         GlStateManager.rotate(-((float)this.rendererUpdateCount + partialTicks) * (float)wobbleSpeed, 0.0F, 1.0F, 1.0F);
      }

      this.orientCamera(partialTicks);
      if (this.panoramaMode) {
         switch (this.panoramaFace) {
            case 0:
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 1:
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 2:
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 3:
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
               break;
            case 4:
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   public void renderStreamIndicator(float partialTicks) {
      this.setupOverlayRendering();
      this.mc.ingameGUI.renderStreamIndicator(new ScaledResolution(this.mc));
   }

   public void updateSmoothWorld() {
      this.serverWaitTimeCurrent = 0;
      if (!Config.isSmoothWorld() || !Config.isSingleProcessor()) {
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
      } else if (this.mc.isJava64bit()) {
         IntegratedServer integratedServer = this.mc.getIntegratedServer();
         if (integratedServer != null) {
            boolean isDemo = this.mc.isDemo();
            if (!isDemo && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
               if (this.serverWaitTime > 0) {
                  Lagometer.timerServer.start();
                  Config.sleep((long)this.serverWaitTime);
                  Lagometer.timerServer.end();
                  this.serverWaitTimeCurrent = this.serverWaitTime;
               }

               long currentTimeMs = System.nanoTime() / 1000000L;
               if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                  long elapsedMs = currentTimeMs - this.lastServerTime;
                  if (elapsedMs < 0L) {
                     this.lastServerTime = currentTimeMs;
                     elapsedMs = 0L;
                  }

                  if (elapsedMs >= 50L) {
                     this.lastServerTime = currentTimeMs;
                     int serverTicks = integratedServer.wFo();
                     int tickDelta = serverTicks - this.lastServerTicks;
                     if (tickDelta < 0) {
                        this.lastServerTicks = serverTicks;
                        tickDelta = 0;
                     }

                     if (tickDelta < 1 && this.serverWaitTime < 100) {
                        this.serverWaitTime += 2;
                     }

                     if (tickDelta > 1 && this.serverWaitTime > 0) {
                        this.serverWaitTime--;
                     }

                     this.lastServerTicks = serverTicks;
                  }
               } else {
                  this.lastServerTime = currentTimeMs;
                  this.lastServerTicks = integratedServer.wFo();
                  this.smoothWorldFactor = 1.0F;
                  this.smoothWorldThreshold = 50.0F;
               }
            } else {
               if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
                  Config.sleep(20L);
               }

               this.lastServerTime = 0L;
               this.lastServerTicks = 0;
            }
         }
      }
   }

   public void updateRenderer() {
      if (OpenGlHelper.bza && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.updateTorchFlicker();
      this.prevFogColor1 = this.fogColor1;
      this.thirdPersonDistanceTemp = this.thirdPersonDistance;
      if (this.mc.gameSettings.smoothCamera) {
         float sensitivity = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float mouseMultiplier = sensitivity * sensitivity * sensitivity * 8.0F;
         this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * mouseMultiplier);
         this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * mouseMultiplier);
         this.smoothCamPartialTicks = 0.0F;
         this.smoothCamYaw = 0.0F;
         this.smoothCamPitch = 0.0F;
      } else {
         this.smoothCamFilterX = 0.0F;
         this.smoothCamFilterY = 0.0F;
         this.mouseFilterXAxis.reset();
         this.mouseFilterYAxis.reset();
      }

      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      float lightBrightness = this.mc.theWorld.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity()));
      float renderDistanceFraction = (float)this.mc.gameSettings.renderDistanceChunks / 32.0F;
      float targetFog = lightBrightness * (1.0F - renderDistanceFraction) + renderDistanceFraction;
      this.fogColor1 = this.fogColor1 + (targetFog - this.fogColor1) * 0.1F;
      this.rendererUpdateCount++;
      this.itemRenderer.updateEquippedItem();
      this.addRainParticles();
      this.prevBossColorModifier = this.bossColorModifier;
      if (BossStatus.hasColorModifier) {
         this.bossColorModifier += 0.05F;
         if (this.bossColorModifier > 1.0F) {
            this.bossColorModifier = 1.0F;
         }

         BossStatus.hasColorModifier = false;
      } else if (this.bossColorModifier > 0.0F) {
         this.bossColorModifier -= 0.0125F;
      }
   }

   public float getFOVModifier(float partialTicks, boolean changingFov) {
      if (this.panoramaMode) {
         return 90.0F;
      } else {
         Entity viewEntity = this.mc.getRenderViewEntity();
         float fov = 70.0F;
         if (changingFov) {
            fov = this.mc.gameSettings.fov;
            fov *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
         }

         boolean zoomKeyDown = false;
         if (this.mc.currentScreen == null) {
            GameSettings settings = this.mc.gameSettings;
            zoomKeyDown = GameSettings.isKeyDown(this.mc.gameSettings.keyBindZoom);
         }

         if (zoomKeyDown) {
            if (!Config.zoomMode) {
               Config.zoomMode = true;
               this.mc.gameSettings.smoothCamera = true;
            }

            if (Config.zoomMode) {
               fov /= 4.0F;
            }
         } else if (Config.zoomMode) {
            Config.zoomMode = false;
            this.mc.gameSettings.smoothCamera = false;
            this.mouseFilterXAxis = new MouseFilter();
            this.mouseFilterYAxis = new MouseFilter();
            this.mc.renderGlobal.Ggp = true;
         }

         if (viewEntity instanceof EntityLivingBase && ((EntityLivingBase)viewEntity).getHealth() <= 0.0F) {
            float deathProgress = (float)((EntityLivingBase)viewEntity).esa + partialTicks;
            fov /= (1.0F - 500.0F / (deathProgress + 500.0F)) * 2.0F + 1.0F;
         }

         Block blockAtViewpoint = ActiveRenderInfo.zcr(this.mc.theWorld, viewEntity, partialTicks);
         if (blockAtViewpoint.getMaterial() == Material.PGa) {
            fov = fov * 60.0F / 70.0F;
         }

         return fov;
      }
   }

   public void loadEntityShader(Entity entity) {
      if (OpenGlHelper.bza) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.theShaderGroup = null;
         if (entity instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entity instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entity instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         }
      }
   }

   public void updateFovModifierHand() {
      float targetFov = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer player = (AbstractClientPlayer)this.mc.getRenderViewEntity();
         targetFov = player.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand = this.fovModifierHand + (targetFov - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }
   }

   public void renderHand(float partialTicks, int pass) {
      if (!this.panoramaMode) {
         GlStateManager.matrixMode(5889);
         GlStateManager.ZI();
         float anaglyphOffset = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((float)(-(pass * 2 - 1)) * anaglyphOffset, 0.0F, 0.0F);
         }

         Project.gluPerspective(this.getFOVModifier(partialTicks, false), (float)this.mc.displayWidth / (float)this.mc.displayHeight, 0.05F, this.clipDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.ZI();
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((float)(pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         GlStateManager.pushMatrix();
         this.hurtCameraEffect(partialTicks);
         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(partialTicks);
         }

         boolean spawnForced = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isSpawnForced();
         if (this.mc.gameSettings.thirdPersonView == 0 && !spawnForced && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectatorMode()) {
            this.enableLightmap();
            this.itemRenderer.renderItemInFirstPerson(partialTicks);
            this.disableLightmap();
         }

         GlStateManager.popMatrix();
         if (this.mc.gameSettings.thirdPersonView == 0 && !spawnForced) {
            this.itemRenderer.renderOverlays(partialTicks);
            this.hurtCameraEffect(partialTicks);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.setupViewBobbing(partialTicks);
         }
      }
   }

   public void renderRainSnow(float partialTicks) {
      if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
         WorldProvider provider = this.mc.theWorld.provider;
         Object weatherRenderer = Reflector.call(provider, Reflector.ForgeWorldProvider_getWeatherRenderer);
         if (weatherRenderer != null) {
            Reflector.callVoid(weatherRenderer, Reflector.IRenderHandler_render, partialTicks, this.mc.theWorld, this.mc);
            return;
         }
      }

      float rainStrength = this.mc.theWorld.Oj(partialTicks);
      if (rainStrength > 0.0F) {
         if (Config.isRainOff()) {
            return;
         }

         this.enableLightmap();
         Entity viewEntity = this.mc.getRenderViewEntity();
         WorldClient world = this.mc.theWorld;
         int playerX = MathHelper.Vlg(viewEntity.posX);
         int playerY = MathHelper.Vlg(viewEntity.posY);
         int playerZ = MathHelper.Vlg(viewEntity.posZ);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldRenderer = tessellator.getWorldRenderer();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         double interpX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * (double)partialTicks;
         double interpY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * (double)partialTicks;
         double interpZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * (double)partialTicks;
         int floorY = MathHelper.Vlg(interpY);
         byte radius = 5;
         if (Config.isRainFancy()) {
            radius = 10;
         }

         byte precipType = -1;
         float animTime = (float)this.rendererUpdateCount + partialTicks;
         worldRenderer.h$o(-interpX, -interpY, -interpZ);
         if (Config.isRainFancy()) {
            radius = 10;
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         MutableBlockPos mutablePos = new MutableBlockPos();
         int scanZ = playerZ - radius;

         while (scanZ <= playerZ + radius) {
            for (int scanX = playerX - radius; scanX <= playerX + radius; scanX++) {
               int noiseIndex = (scanZ - playerZ + 16) * 32 + scanX - playerX + 16;
               double offsetX = (double)this.rainXCoords[noiseIndex] * 0.5;
               double offsetZ = (double)this.rainYCoords[noiseIndex] * 0.5;
               mutablePos.set(scanX, 0, scanZ);
               BiomeGenBase biome = world.getBiomeGenForCoords(mutablePos);
               if (biome.uM() || biome._or()) {
                  int groundY = world.PBr(mutablePos).getY();
                  int minY = playerY - radius;
                  int maxY = playerY + radius;
                  if (minY < groundY) {
                     minY = groundY;
                  }

                  if (maxY < groundY) {
                     maxY = groundY;
                  }

                  int spawnY = groundY;
                  if (groundY < floorY) {
                     spawnY = floorY;
                  }

                  if (minY != maxY) {
                     this.random.setSeed((long)(scanX * scanX * 3121 + scanX * 45238971 ^ scanZ * scanZ * 418711 + scanZ * 13761));
                     mutablePos.set(scanX, minY, scanZ);
                     float temperature = biome.getFloatTemperature(mutablePos);
                     if (world.getWorldChunkManager().getTemperatureAtHeight(temperature, groundY) >= 0.15F) {
                        if (precipType != 0) {
                           if (precipType >= 0) {
                              tessellator.draw();
                           }

                           precipType = 0;
                           this.mc.getTextureManager().bindTexture(locationRainPng);
                           worldRenderer.begin(7, DefaultVertexFormats.hsp);
                        }

                        double vOffset = (
                              (double)(this.rendererUpdateCount + scanX * scanX * 3121 + scanX * 45238971 + scanZ * scanZ * 418711 + scanZ * 13761 & 31)
                                 + (double)partialTicks
                           )
                           / 32.0
                           * (3.0 + this.random.nextDouble());
                        double rainDeltaX = (double)((float)scanX + 0.5F) - viewEntity.posX;
                        double rainDeltaZ = (double)((float)scanZ + 0.5F) - viewEntity.posZ;
                        float rainDistance = MathHelper.sqrt_double(rainDeltaX * rainDeltaX + rainDeltaZ * rainDeltaZ) / (float)radius;
                        float rainAlpha = ((1.0F - rainDistance * rainDistance) * 0.5F + 0.5F) * rainStrength;
                        mutablePos.set(scanX, spawnY, scanZ);
                        int combinedLight = world.getCombinedLight(mutablePos, 0);
                        int lightmapSky = combinedLight >> 16 & 65535;
                        int lightmapBlock = combinedLight & 65535;
                        worldRenderer.pos((double)scanX - offsetX + 0.5, (double)minY, (double)scanZ - offsetZ + 0.5)
                           .tex(0.0, (double)minY * 0.25 + vOffset)
                           .color(1.0F, 1.0F, 1.0F, rainAlpha)
                           .lightmap(lightmapSky, lightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX + offsetX + 0.5, (double)minY, (double)scanZ + offsetZ + 0.5)
                           .tex(1.0, (double)minY * 0.25 + vOffset)
                           .color(1.0F, 1.0F, 1.0F, rainAlpha)
                           .lightmap(lightmapSky, lightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX + offsetX + 0.5, (double)maxY, (double)scanZ + offsetZ + 0.5)
                           .tex(1.0, (double)maxY * 0.25 + vOffset)
                           .color(1.0F, 1.0F, 1.0F, rainAlpha)
                           .lightmap(lightmapSky, lightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX - offsetX + 0.5, (double)maxY, (double)scanZ - offsetZ + 0.5)
                           .tex(0.0, (double)maxY * 0.25 + vOffset)
                           .color(1.0F, 1.0F, 1.0F, rainAlpha)
                           .lightmap(lightmapSky, lightmapBlock)
                           .endVertex();
                     } else {
                        if (precipType != 1) {
                           if (precipType >= 0) {
                              tessellator.draw();
                           }

                           precipType = 1;
                           this.mc.getTextureManager().bindTexture(locationSnowPng);
                           worldRenderer.begin(7, DefaultVertexFormats.hsp);
                        }

                        double snowAnim = (double)(((float)(this.rendererUpdateCount & 511) + partialTicks) / 512.0F);
                        double snowDriftU = this.random.nextDouble()
                           + (double)animTime
                              * 0.01
                              * (double)((float)this.random.nextGaussian());
                        double snowDriftV = this.random.nextDouble() + (double)(animTime * (float)this.random.nextGaussian()) * 0.001;
                        double snowDeltaX = (double)((float)scanX + 0.5F) - viewEntity.posX;
                        double snowDeltaZ = (double)((float)scanZ + 0.5F) - viewEntity.posZ;
                        float snowDistance = MathHelper.sqrt_double(snowDeltaX * snowDeltaX + snowDeltaZ * snowDeltaZ) / (float)radius;
                        float snowAlpha = ((1.0F - snowDistance * snowDistance) * 0.3F + 0.5F) * rainStrength;
                        mutablePos.set(scanX, spawnY, scanZ);
                        int snowLight = (world.getCombinedLight(mutablePos, 0) * 3 + 15728880) / 4;
                        int snowLightmapSky = snowLight >> 16 & 65535;
                        int snowLightmapBlock = snowLight & 65535;
                        worldRenderer.pos((double)scanX - offsetX + 0.5, (double)minY, (double)scanZ - offsetZ + 0.5)
                           .tex(0.0 + snowDriftU, (double)minY * 0.25 + snowAnim + snowDriftV)
                           .color(1.0F, 1.0F, 1.0F, snowAlpha)
                           .lightmap(snowLightmapSky, snowLightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX + offsetX + 0.5, (double)minY, (double)scanZ + offsetZ + 0.5)
                           .tex(1.0 + snowDriftU, (double)minY * 0.25 + snowAnim + snowDriftV)
                           .color(1.0F, 1.0F, 1.0F, snowAlpha)
                           .lightmap(snowLightmapSky, snowLightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX + offsetX + 0.5, (double)maxY, (double)scanZ + offsetZ + 0.5)
                           .tex(1.0 + snowDriftU, (double)maxY * 0.25 + snowAnim + snowDriftV)
                           .color(1.0F, 1.0F, 1.0F, snowAlpha)
                           .lightmap(snowLightmapSky, snowLightmapBlock)
                           .endVertex();
                        worldRenderer.pos((double)scanX - offsetX + 0.5, (double)maxY, (double)scanZ - offsetZ + 0.5)
                           .tex(0.0 + snowDriftU, (double)maxY * 0.25 + snowAnim + snowDriftV)
                           .color(1.0F, 1.0F, 1.0F, snowAlpha)
                           .lightmap(snowLightmapSky, snowLightmapBlock)
                           .endVertex();
                     }
                  }
               }
            }

            scanZ += 65;
            scanZ -= 64;
         }

         if (precipType >= 0) {
            tessellator.draw();
         }

         worldRenderer.h$o(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void addRainParticles() {
      float rainStrength = this.mc.theWorld.Oj(1.0F);
      if (!Config.isRainFancy()) {
         rainStrength /= 2.0F;
      }

      if (rainStrength != 0.0F && Config.isRainSplash()) {
         this.random.setSeed((long)this.rendererUpdateCount * 312987231L);
         Entity viewEntity = this.mc.getRenderViewEntity();
         WorldClient world = this.mc.theWorld;
         BlockPos playerPos = new BlockPos(viewEntity);
         double splashX = 0.0;
         double splashY = 0.0;
         double splashZ = 0.0;
         int splashCount = 0;
         int particleCount = (int)(100.0F * rainStrength * rainStrength);
         if (this.mc.gameSettings.particleSetting == 1) {
            particleCount >>= 1;
         } else if (this.mc.gameSettings.particleSetting == 2) {
            particleCount = 0;
         }

         for (int particleIndex = 0; particleIndex < particleCount; particleIndex++) {
            BlockPos spawnPos = world.PBr(playerPos.add(this.random.nextInt(10) - this.random.nextInt(10), 0, this.random.nextInt(10) - this.random.nextInt(10)));
            BiomeGenBase biome = world.getBiomeGenForCoords(spawnPos);
            BlockPos belowPos = spawnPos.down();
            Block blockBelow = world.getBlockState(belowPos).getBlock();
            if (spawnPos.getY() <= playerPos.getY() + 10 && spawnPos.getY() >= playerPos.getY() - 10 && biome.uM() && biome.getFloatTemperature(spawnPos) >= 0.15F) {
               double randomOffsetX = this.random.nextDouble();
               double randomOffsetZ = this.random.nextDouble();
               if (blockBelow.getMaterial() == Material.Gta) {
                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.SMOKE_NORMAL,
                        (double)spawnPos.getX() + randomOffsetX,
                        (double)((float)spawnPos.getY() + 0.1F) - blockBelow.$$r(),
                        (double)spawnPos.getZ() + randomOffsetZ,
                        0.0,
                        0.0,
                        0.0,
                        new int[0]
                     );
               } else if (blockBelow.getMaterial() != Material.air) {
                  blockBelow.setBlockBoundsBasedOnState(world, belowPos);
                  splashCount += 15;
                  splashCount -= 14;
                  if (this.random.nextInt(splashCount) == 0) {
                     splashX = (double)belowPos.getX() + randomOffsetX;
                     splashY = (double)((float)belowPos.getY() + 0.1F) + blockBelow.fP() - 1.0;
                     splashZ = (double)belowPos.getZ() + randomOffsetZ;
                  }

                  this.mc
                     .theWorld
                     .spawnParticle(
                        EnumParticleTypes.WATER_DROP,
                        (double)belowPos.getX() + randomOffsetX,
                        (double)((float)belowPos.getY() + 0.1F) + blockBelow.fP(),
                        (double)belowPos.getZ() + randomOffsetZ,
                        0.0,
                        0.0,
                        0.0,
                        new int[0]
                     );
               }
            }
         }

         if (splashCount > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
            this.rainSoundCounter = 0;
            if (splashY > (double)(playerPos.getY() + 1) && world.PBr(playerPos).getY() > MathHelper.zor((float)playerPos.getY())) {
               this.mc.theWorld.playSound(splashX, splashY, splashZ, "ambient.weather.rain", 0.1F, 0.5F, false);
            } else {
               this.mc.theWorld.playSound(splashX, splashY, splashZ, "ambient.weather.rain", 0.2F, 1.0F, false);
            }
         }
      }
   }

   public void loadShader(ResourceLocation shaderLocation) {
      try {
         this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), shaderLocation);
         this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
         this.useShader = true;
      } catch (Exception exception) {
         logger.warn("Failed to load shader: " + shaderLocation, exception);
         this.shaderIndex = shaderCount;
         this.useShader = false;
      }
   }

   public void stopUseShader() {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      this.shaderIndex = shaderCount;
   }

   public boolean isDrawBlockOutline() {
      if (!this.drawBlockOutline) {
         return false;
      } else {
         Entity viewEntity = this.mc.getRenderViewEntity();
         boolean showOutline = viewEntity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
         if (showOutline && !((EntityPlayer)viewEntity).capabilities.pBg) {
            ItemStack heldItem = ((EntityPlayer)viewEntity).getCurrentInventoryItem();
            if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
               BlockPos hitPos = this.mc.objectMouseOver.getBlockPos();
               Block hitBlock = this.mc.theWorld.getBlockState(hitPos).getBlock();
               if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
                  boolean hasTileEntity;
                  if (Reflector.ForgeBlock_hasTileEntity.exists()) {
                     IBlockState hitState = this.mc.theWorld.getBlockState(hitPos);
                     hasTileEntity = Reflector.callBoolean(hitBlock, Reflector.ForgeBlock_hasTileEntity, hitState);
                  } else {
                     hasTileEntity = hitBlock.TSo();
                  }

                  showOutline = hasTileEntity && this.mc.theWorld.getTileEntity(hitPos) instanceof IInventory;
               } else {
                  showOutline = heldItem != null && (heldItem.pj(hitBlock) || heldItem.canDestroy(hitBlock));
               }
            }
         }

         return showOutline;
      }
   }
}
