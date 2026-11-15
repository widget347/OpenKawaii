package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kawaii.config.ConfigReader;
import kawaii.util.TextFileWriter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import optfine.ClearWater;
import optfine.Config;
import optfine.CustomColorizer;
import optfine.CustomSky;
import optfine.NaturalTextures;
import optfine.RandomMobs;
import optfine.Reflector;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameSettings {
   public int ambientOcclusion;
   public static int defaultChatFilterIndex = 0;
   public float streamMicVolume;
   public boolean showFps;
   public boolean smoothCamera;
   public float streamKbps;
   public List<String> resourcePacks;
   public float chatOpacity;
   public boolean snooperEnabled;
   public static int defaultSoundIndex = 0;
   public KeyBinding keyBindRight;
   public boolean showCapes;
   public KeyBinding keyBindBack;
   public boolean advancedItemTooltips;
   public KeyBinding keyBindFullscreen;
   public KeyBinding keyBindPlayerList;
   public int aaLevel;
   public KeyBinding keyBindUseItem;
   public int mipmapLevels;
   public boolean chunkUpdatesDynamic;
   public boolean naturalTextures;
   public boolean animatedExplosion;
   public boolean randomMobs;
   public KeyBinding keyBindSpectatorOutlines;
   public KeyBinding keyBindStreamStartStop;
   public KeyBinding keyBindAttack;
   public static int defaultGuiScale = 2;
   public boolean profiler;
   public boolean showInventoryAchievementHint;
   public static String[] guiScaleNames = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
   public float gamma;
   public boolean fastMath;
   public boolean showDebugProfilerChart;
   public int guiScale;
   public int mipmapType;
   public KeyBinding keyBindCommand;
   public boolean fireworkParticles;
   public static String[] streamChatUserFilterNames = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   public String fullscreenMode;
   public boolean forceUnicodeFont;
   public static String[] cloudsNames = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public EnumDifficulty difficulty;
   public float streamSystemVolume;
   public KeyBinding[] keyBindings;
   public boolean sky;
   public int anisotropicLevel;
   public float fogStart;
   public boolean potionParticles;
   public int streamCompression;
   public int translucentBlocks;
   public boolean viewBobbing;
   public int renderDistanceChunks;
   public KeyBinding keyBindSmoothCamera;
   public boolean invertMouse;
   public float mouseSensitivity = 0.5F;
   public boolean fullScreen;
   public KeyBinding keyBindForward;
   public boolean animatedTerrain;
   public boolean animatedFire;
   public Set<EnumPlayerModelParts> setModelParts;
   public KeyBinding keyBindLeft;
   public KeyBinding keyBindSneak;
   public KeyBinding keyBindScreenshot;
   public static String[] ambientOcclusionNames = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   public boolean sunMoon;
   public float chatScale;
   public boolean smoothWorld;
   public int maxFps;
   public static int defaultQualityIndex = 1;
   public static String defaultFullscreenMode = "Default";
   public float streamBytesPerPixel;
   public boolean rainSplash;
   public boolean hideGUI;
   public int timeOfDay;
   public boolean fastRender;
   public String lastServer;
   public static Gson gson = new Gson();
   public boolean fboEnable;
   public float chatHeightUnfocused;
   public boolean voidParticles;
   public boolean chatLinksPrompt;
   public boolean occlusionFancy;
   public int rain;
   public Map<SoundCategory, Float> mapSoundLevels;
   public boolean useVbo;
   public static String[] particleNames = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   public boolean swampColors;
   public File optionsFile;
   public int betterGrass;
   public float streamFps;
   public boolean portalParticles;
   public float chatWidth;
   public boolean customFonts;
   public KeyBinding keyBindZoom;
   public static int defaultParticleIndex = 0;
   public boolean stars;
   public int trees;
   public KeyBinding keyBindStreamToggleMic;
   public boolean allowBlockAlternatives;
   public int streamChatUserFilter;
   public int overrideHeight;
   public boolean animatedFlame;
   public int animatedLava;
   public int streamMicToggleBehavior;
   public boolean anaglyph;
   public static String[] streamCompressionNames = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   public boolean heldItemTooltips;
   public KeyBinding keyBindStreamCommercial;
   public int streamChatEnabled;
   public int connectedTextures;
   public boolean lockCameraRotation;
   public boolean enableVsync;
   public float saturation;
   public boolean hideServerAddress;
   public int thirdPersonView;
   public boolean weather;
   public KeyBinding keyBindStreamPauseUnpause;
   public int overrideWidth;
   public int particleSetting;
   public String language;
   public KeyBinding keyBindSprint;
   public boolean drippingWaterLava;
   public boolean reducedDebugInfo;
   public static String[] micToggleNames = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   public int animatedWater;
   public int chunkLoading;
   public boolean clearWater;
   public static String classIdentifier = "CL_00000650";
   public static Logger logger = LogManager.getLogger();
   public int autoSaveTicks;
   public KeyBinding[] keyBindsHotbar;
   public EnumChatVisibility chatVisibility;
   public boolean animatedSmoke;
   public boolean showDebugInfo;
   public Minecraft mc;
   public boolean fancyGraphics;
   public KeyBinding keyBindChat;
   public int fogType;
   public File optionsOfFile;
   public KeyBinding keyBindInventory;
   public int cloudMode;
   public boolean debugCtrlHeld;
   public float fov;
   public boolean lagometer;
   public static int defaultAoIndex = 2;
   public KeyBinding keyBindJump;
   public boolean streamSendMetadata;
   public KeyBinding keyBindTogglePerspective;
   public float aoLevel;
   public boolean waterParticles;
   public KeyBinding keyBindPickBlock;
   public static ParameterizedType typeListString = new GameSettings$1();
   public int vignette;
   public boolean animatedPortal;
   public String streamPreferredServer;
   public boolean customSky;
   public float chatHeightFocused;
   public static int defaultMicIndex = 1;
   public boolean animatedTextures;
   public boolean smoothBiomes;
   public boolean betterSnow;
   public static int defaultStreamIndex = 1;
   public boolean touchscreen;
   public List<String> incompatibleResourcePacks;
   public boolean chatLinks;
   public boolean useNativeTransport;
   public boolean entityShadows;
   public KeyBinding keyBindDrop;
   public static int defaultChatVisibilityIndex = 2;
   public boolean smoothFps;
   public boolean chatColours;
   public int droppedItems;
   public boolean pauseOnLostFocus;
   public static int defaultLimitIndex = 3;
   public boolean animatedRedstone;
   public boolean customColors;
   public int chunkUpdates;
   public float cloudsHeight;
   public boolean lazyChunkLoading;
   public static String[] streamChatEnabledNames = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   public int clouds;

   public void setSoundLevel(SoundCategory category, float volume) {
      this.mc.getSoundHandler().setSoundLevel(category, volume);
      this.mapSoundLevels.put(category, volume);
   }

   public float getOptionFloatValue(GameSettings.Options option) {
      return option == GameSettings.Options.CLOUD_HEIGHT
         ? this.cloudsHeight
         : (
            option == GameSettings.Options.AO_LEVEL
               ? this.aoLevel
               : (
                  option == GameSettings.Options.AA_LEVEL
                     ? (float)this.aaLevel
                     : (
                        option == GameSettings.Options.AF_LEVEL
                           ? (float)this.anisotropicLevel
                           : (
                              option == GameSettings.Options.MIPMAP_TYPE
                                 ? (float)this.mipmapType
                                 : (
                                    option == GameSettings.Options.FRAMERATE_LIMIT
                                       ? (
                                          (float)this.maxFps == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync
                                             ? 0.0F
                                             : (float)this.maxFps
                                       )
                                       : (
                                          option == GameSettings.Options.FOV
                                             ? this.fov
                                             : (
                                                option == GameSettings.Options.GAMMA
                                                   ? this.gamma
                                                   : (
                                                      option == GameSettings.Options.SATURATION
                                                         ? this.saturation
                                                         : (
                                                            option == GameSettings.Options.SENSITIVITY
                                                               ? this.mouseSensitivity
                                                               : (
                                                                  option == GameSettings.Options.CHAT_OPACITY
                                                                     ? this.chatOpacity
                                                                     : (
                                                                        option == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                           ? this.chatHeightFocused
                                                                           : (
                                                                              option == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                                 ? this.chatHeightUnfocused
                                                                                 : (
                                                                                    option == GameSettings.Options.CHAT_SCALE
                                                                                       ? this.chatScale
                                                                                       : (
                                                                                          option == GameSettings.Options.CHAT_WIDTH
                                                                                             ? this.chatWidth
                                                                                             : (
                                                                                                option == GameSettings.Options.FRAMERATE_LIMIT
                                                                                                   ? (float)this.maxFps
                                                                                                   : (
                                                                                                      option == GameSettings.Options.MIPMAP_LEVELS
                                                                                                         ? (float)this.mipmapLevels
                                                                                                         : (
                                                                                                            option == GameSettings.Options.RENDER_DISTANCE
                                                                                                               ? (float)this.renderDistanceChunks
                                                                                                               : (
                                                                                                                  option
                                                                                                                        == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                     ? this.streamBytesPerPixel
                                                                                                                     : (
                                                                                                                        option
                                                                                                                              == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                                                           ? this.streamMicVolume
                                                                                                                           : (
                                                                                                                              option
                                                                                                                                    == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                                                 ? this.streamSystemVolume
                                                                                                                                 : (
                                                                                                                                    option
                                                                                                                                          == GameSettings.Options.STREAM_KBPS
                                                                                                                                       ? this.streamKbps
                                                                                                                                       : (
                                                                                                                                          option
                                                                                                                                                == GameSettings.Options.STREAM_FPS
                                                                                                                                             ? this.streamFps
                                                                                                                                             : 0.0F
                                                                                                                                       )
                                                                                                                                 )
                                                                                                                           )
                                                                                                                     )
                                                                                                               )
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public float parseFloat(String text) {
      return text.equals("true") ? 1.0F : (text.equals("false") ? 0.0F : Float.parseFloat(text));
   }

   public static String getKeyDisplayString(int keyCode) {
      return keyCode < 0
         ? I18n.format("key.mouseButton", keyCode + 101)
         : (keyCode < 256 ? Keyboard.getKeyName(keyCode) : String.format("%c", Character.valueOf((char)(keyCode - 256 & 65535))).toUpperCase());
   }

   public void saveOfOptions() {
      try {
         PrintWriter writer = new PrintWriter(new TextFileWriter(this.optionsOfFile));
         writer.println("ofRenderDistanceChunks:" + this.renderDistanceChunks);
         writer.println("ofFogType:" + this.fogType);
         writer.println("ofFogStart:" + this.fogStart);
         writer.println("ofMipmapType:" + this.mipmapType);
         writer.println("ofOcclusionFancy:" + this.occlusionFancy);
         writer.println("ofSmoothFps:" + this.smoothFps);
         writer.println("ofSmoothWorld:" + this.smoothWorld);
         writer.println("ofAoLevel:" + this.aoLevel);
         writer.println("ofClouds:" + this.cloudMode);
         writer.println("ofCloudsHeight:" + this.cloudsHeight);
         writer.println("ofTrees:" + this.trees);
         writer.println("ofDroppedItems:" + this.droppedItems);
         writer.println("ofRain:" + this.rain);
         writer.println("ofAnimatedWater:" + this.animatedWater);
         writer.println("ofAnimatedLava:" + this.animatedLava);
         writer.println("ofAnimatedFire:" + this.animatedFire);
         writer.println("ofAnimatedPortal:" + this.animatedPortal);
         writer.println("ofAnimatedRedstone:" + this.animatedRedstone);
         writer.println("ofAnimatedExplosion:" + this.animatedExplosion);
         writer.println("ofAnimatedFlame:" + this.animatedFlame);
         writer.println("ofAnimatedSmoke:" + this.animatedSmoke);
         writer.println("ofVoidParticles:" + this.voidParticles);
         writer.println("ofWaterParticles:" + this.waterParticles);
         writer.println("ofPortalParticles:" + this.portalParticles);
         writer.println("ofPotionParticles:" + this.potionParticles);
         writer.println("ofFireworkParticles:" + this.fireworkParticles);
         writer.println("ofDrippingWaterLava:" + this.drippingWaterLava);
         writer.println("ofAnimatedTerrain:" + this.animatedTerrain);
         writer.println("ofAnimatedTextures:" + this.animatedTextures);
         writer.println("ofRainSplash:" + this.rainSplash);
         writer.println("ofLagometer:" + this.lagometer);
         writer.println("ofShowFps:" + this.showFps);
         writer.println("ofAutoSaveTicks:" + this.autoSaveTicks);
         writer.println("ofBetterGrass:" + this.betterGrass);
         writer.println("ofConnectedTextures:" + this.connectedTextures);
         writer.println("ofWeather:" + this.weather);
         writer.println("ofSky:" + this.sky);
         writer.println("ofStars:" + this.stars);
         writer.println("ofSunMoon:" + this.sunMoon);
         writer.println("ofVignette:" + this.vignette);
         writer.println("ofChunkUpdates:" + this.chunkUpdates);
         writer.println("ofChunkLoading:" + this.chunkLoading);
         writer.println("ofChunkUpdatesDynamic:" + this.chunkUpdatesDynamic);
         writer.println("ofTime:" + this.timeOfDay);
         writer.println("ofClearWater:" + this.clearWater);
         writer.println("ofAaLevel:" + this.aaLevel);
         writer.println("ofAfLevel:" + this.anisotropicLevel);
         writer.println("ofProfiler:" + this.profiler);
         writer.println("ofBetterSnow:" + this.betterSnow);
         writer.println("ofSwampColors:" + this.swampColors);
         writer.println("ofRandomMobs:" + this.randomMobs);
         writer.println("ofSmoothBiomes:" + this.smoothBiomes);
         writer.println("ofCustomFonts:" + this.customFonts);
         writer.println("ofCustomColors:" + this.customColors);
         writer.println("ofCustomSky:" + this.customSky);
         writer.println("ofShowCapes:" + this.showCapes);
         writer.println("ofNaturalTextures:" + this.naturalTextures);
         writer.println("ofLazyChunkLoading:" + this.lazyChunkLoading);
         writer.println("ofFullscreenMode:" + this.fullscreenMode);
         writer.println("ofFastMath:" + this.fastMath);
         writer.println("ofFastRender:" + this.fastRender);
         writer.println("ofTranslucentBlocks:" + this.translucentBlocks);
         writer.println("key_" + this.keyBindZoom.AOr() + ":" + this.keyBindZoom.getKeyCode());
         writer.close();
      } catch (Exception exception) {
         Config.warn("Failed to save options");
         exception.printStackTrace();
      }
   }

   public void loadOfOptions() {
      try {
         File file = this.optionsOfFile;
         if (!file.exists()) {
            file = this.optionsFile;
         }

         if (!file.exists()) {
            return;
         }

         BufferedReader reader = new BufferedReader(new ConfigReader(file));
         String line = "";

         while ((line = reader.readLine()) != null) {
            try {
               String[] parts = line.split(":");
               if (parts[0].equals("ofRenderDistanceChunks") && parts.length >= 2) {
                  this.renderDistanceChunks = Integer.valueOf(parts[1]);
                  this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 32);
               }

               if (parts[0].equals("ofFogType") && parts.length >= 2) {
                  this.fogType = Integer.valueOf(parts[1]);
                  this.fogType = Config.limit(this.fogType, 1, 3);
               }

               if (parts[0].equals("ofFogStart") && parts.length >= 2) {
                  this.fogStart = Float.valueOf(parts[1]);
                  if (this.fogStart < 0.2F) {
                     this.fogStart = 0.2F;
                  }

                  if (this.fogStart > 0.81F) {
                     this.fogStart = 0.8F;
                  }
               }

               if (parts[0].equals("ofMipmapType") && parts.length >= 2) {
                  this.mipmapType = Integer.valueOf(parts[1]);
                  this.mipmapType = Config.limit(this.mipmapType, 0, 3);
               }

               if (parts[0].equals("ofOcclusionFancy") && parts.length >= 2) {
                  this.occlusionFancy = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSmoothFps") && parts.length >= 2) {
                  this.smoothFps = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSmoothWorld") && parts.length >= 2) {
                  this.smoothWorld = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAoLevel") && parts.length >= 2) {
                  this.aoLevel = Float.valueOf(parts[1]);
                  this.aoLevel = Config.limit(this.aoLevel, 0.0F, 1.0F);
               }

               if (parts[0].equals("ofClouds") && parts.length >= 2) {
                  this.cloudMode = Integer.valueOf(parts[1]);
                  this.cloudMode = Config.limit(this.cloudMode, 0, 3);
                  this.updateClouds();
               }

               if (parts[0].equals("ofCloudsHeight") && parts.length >= 2) {
                  this.cloudsHeight = Float.valueOf(parts[1]);
                  this.cloudsHeight = Config.limit(this.cloudsHeight, 0.0F, 1.0F);
               }

               if (parts[0].equals("ofTrees") && parts.length >= 2) {
                  this.trees = Integer.valueOf(parts[1]);
                  this.trees = Config.limit(this.trees, 0, 2);
               }

               if (parts[0].equals("ofDroppedItems") && parts.length >= 2) {
                  this.droppedItems = Integer.valueOf(parts[1]);
                  this.droppedItems = Config.limit(this.droppedItems, 0, 2);
               }

               if (parts[0].equals("ofRain") && parts.length >= 2) {
                  this.rain = Integer.valueOf(parts[1]);
                  this.rain = Config.limit(this.rain, 0, 3);
               }

               if (parts[0].equals("ofAnimatedWater") && parts.length >= 2) {
                  this.animatedWater = Integer.valueOf(parts[1]);
                  this.animatedWater = Config.limit(this.animatedWater, 0, 2);
               }

               if (parts[0].equals("ofAnimatedLava") && parts.length >= 2) {
                  this.animatedLava = Integer.valueOf(parts[1]);
                  this.animatedLava = Config.limit(this.animatedLava, 0, 2);
               }

               if (parts[0].equals("ofAnimatedFire") && parts.length >= 2) {
                  this.animatedFire = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedPortal") && parts.length >= 2) {
                  this.animatedPortal = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedRedstone") && parts.length >= 2) {
                  this.animatedRedstone = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedExplosion") && parts.length >= 2) {
                  this.animatedExplosion = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedFlame") && parts.length >= 2) {
                  this.animatedFlame = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedSmoke") && parts.length >= 2) {
                  this.animatedSmoke = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofVoidParticles") && parts.length >= 2) {
                  this.voidParticles = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofWaterParticles") && parts.length >= 2) {
                  this.waterParticles = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofPortalParticles") && parts.length >= 2) {
                  this.portalParticles = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofPotionParticles") && parts.length >= 2) {
                  this.potionParticles = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofFireworkParticles") && parts.length >= 2) {
                  this.fireworkParticles = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofDrippingWaterLava") && parts.length >= 2) {
                  this.drippingWaterLava = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedTerrain") && parts.length >= 2) {
                  this.animatedTerrain = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAnimatedTextures") && parts.length >= 2) {
                  this.animatedTextures = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofRainSplash") && parts.length >= 2) {
                  this.rainSplash = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofLagometer") && parts.length >= 2) {
                  this.lagometer = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofShowFps") && parts.length >= 2) {
                  this.showFps = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofAutoSaveTicks") && parts.length >= 2) {
                  this.autoSaveTicks = Integer.valueOf(parts[1]);
                  this.autoSaveTicks = Config.limit(this.autoSaveTicks, 40, 40000);
               }

               if (parts[0].equals("ofBetterGrass") && parts.length >= 2) {
                  this.betterGrass = Integer.valueOf(parts[1]);
                  this.betterGrass = Config.limit(this.betterGrass, 1, 3);
               }

               if (parts[0].equals("ofConnectedTextures") && parts.length >= 2) {
                  this.connectedTextures = Integer.valueOf(parts[1]);
                  this.connectedTextures = Config.limit(this.connectedTextures, 1, 3);
               }

               if (parts[0].equals("ofWeather") && parts.length >= 2) {
                  this.weather = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSky") && parts.length >= 2) {
                  this.sky = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofStars") && parts.length >= 2) {
                  this.stars = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSunMoon") && parts.length >= 2) {
                  this.sunMoon = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofVignette") && parts.length >= 2) {
                  this.vignette = Integer.valueOf(parts[1]);
                  this.vignette = Config.limit(this.vignette, 0, 2);
               }

               if (parts[0].equals("ofChunkUpdates") && parts.length >= 2) {
                  this.chunkUpdates = Integer.valueOf(parts[1]);
                  this.chunkUpdates = Config.limit(this.chunkUpdates, 1, 5);
               }

               if (parts[0].equals("ofChunkLoading") && parts.length >= 2) {
                  this.chunkLoading = Integer.valueOf(parts[1]);
                  this.chunkLoading = Config.limit(this.chunkLoading, 0, 2);
                  this.reloadRenderers();
               }

               if (parts[0].equals("ofChunkUpdatesDynamic") && parts.length >= 2) {
                  this.chunkUpdatesDynamic = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofTime") && parts.length >= 2) {
                  this.timeOfDay = Integer.valueOf(parts[1]);
                  this.timeOfDay = Config.limit(this.timeOfDay, 0, 3);
               }

               if (parts[0].equals("ofClearWater") && parts.length >= 2) {
                  this.clearWater = Boolean.valueOf(parts[1]);
                  this.updateWaterOpacity();
               }

               if (parts[0].equals("ofAaLevel") && parts.length >= 2) {
                  this.aaLevel = Integer.valueOf(parts[1]);
                  this.aaLevel = Config.limit(this.aaLevel, 0, 16);
               }

               if (parts[0].equals("ofAfLevel") && parts.length >= 2) {
                  this.anisotropicLevel = Integer.valueOf(parts[1]);
                  this.anisotropicLevel = Config.limit(this.anisotropicLevel, 1, 16);
               }

               if (parts[0].equals("ofProfiler") && parts.length >= 2) {
                  this.profiler = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofBetterSnow") && parts.length >= 2) {
                  this.betterSnow = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSwampColors") && parts.length >= 2) {
                  this.swampColors = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofRandomMobs") && parts.length >= 2) {
                  this.randomMobs = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofSmoothBiomes") && parts.length >= 2) {
                  this.smoothBiomes = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofCustomFonts") && parts.length >= 2) {
                  this.customFonts = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofCustomColors") && parts.length >= 2) {
                  this.customColors = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofCustomSky") && parts.length >= 2) {
                  this.customSky = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofShowCapes") && parts.length >= 2) {
                  this.showCapes = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofNaturalTextures") && parts.length >= 2) {
                  this.naturalTextures = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofLazyChunkLoading") && parts.length >= 2) {
                  this.lazyChunkLoading = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofFullscreenMode") && parts.length >= 2) {
                  this.fullscreenMode = parts[1];
               }

               if (parts[0].equals("ofFastMath") && parts.length >= 2) {
                  this.fastMath = Boolean.valueOf(parts[1]);
                  MathHelper.hhp = this.fastMath;
               }

               if (parts[0].equals("ofFastRender") && parts.length >= 2) {
                  this.fastRender = Boolean.valueOf(parts[1]);
               }

               if (parts[0].equals("ofTranslucentBlocks") && parts.length >= 2) {
                  this.translucentBlocks = Integer.valueOf(parts[1]);
                  this.translucentBlocks = Config.limit(this.translucentBlocks, 0, 2);
               }

               if (parts[0].equals("key_" + this.keyBindZoom.AOr())) {
                  this.keyBindZoom.setKeyCode(Integer.parseInt(parts[1]));
               }
            } catch (Exception optionException) {
               Config.dbg("Skipping bad option: " + line);
               optionException.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         reader.close();
      } catch (Exception loadException) {
         Config.warn("Failed to load options");
         loadException.printStackTrace();
      }
   }

   public void saveOptions() {
      if (Reflector.FMLClientHandler.exists()) {
         Object fmlClientInstance = Reflector.call(Reflector.FMLClientHandler_instance);
         if (fmlClientInstance != null && Reflector.callBoolean(fmlClientInstance, Reflector.FMLClientHandler_isLoading)) {
            return;
         }
      }

      try {
         PrintWriter writer = new PrintWriter(new TextFileWriter(this.optionsFile));
         writer.println("invertYMouse:" + this.invertMouse);
         writer.println("mouseSensitivity:" + this.mouseSensitivity);
         writer.println("fov:" + (this.fov - 70.0F) / 40.0F);
         writer.println("gamma:" + this.gamma);
         writer.println("saturation:" + this.saturation);
         writer.println("renderDistance:" + this.renderDistanceChunks);
         writer.println("guiScale:" + this.guiScale);
         writer.println("particles:" + this.particleSetting);
         writer.println("bobView:" + this.viewBobbing);
         writer.println("anaglyph3d:" + this.anaglyph);
         writer.println("maxFps:" + this.maxFps);
         writer.println("fboEnable:" + this.fboEnable);
         writer.println("difficulty:" + this.difficulty.getDifficultyId());
         writer.println("fancyGraphics:" + this.fancyGraphics);
         writer.println("ao:" + this.ambientOcclusion);
         switch (this.clouds) {
            case 0:
               writer.println("renderClouds:false");
               break;
            case 1:
               writer.println("renderClouds:fast");
               break;
            case 2:
               writer.println("renderClouds:true");
         }

         writer.println("resourcePacks:" + gson.toJson(this.resourcePacks));
         writer.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
         writer.println("lastServer:" + this.lastServer);
         writer.println("lang:" + this.language);
         writer.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         writer.println("chatColors:" + this.chatColours);
         writer.println("chatLinks:" + this.chatLinks);
         writer.println("chatLinksPrompt:" + this.chatLinksPrompt);
         writer.println("chatOpacity:" + this.chatOpacity);
         writer.println("snooperEnabled:" + this.snooperEnabled);
         writer.println("fullscreen:" + this.fullScreen);
         writer.println("enableVsync:" + this.enableVsync);
         writer.println("useVbo:" + this.useVbo);
         writer.println("hideServerAddress:" + this.hideServerAddress);
         writer.println("advancedItemTooltips:" + this.advancedItemTooltips);
         writer.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         writer.println("touchscreen:" + this.touchscreen);
         writer.println("overrideWidth:" + this.overrideWidth);
         writer.println("overrideHeight:" + this.overrideHeight);
         writer.println("heldItemTooltips:" + this.heldItemTooltips);
         writer.println("chatHeightFocused:" + this.chatHeightFocused);
         writer.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         writer.println("chatScale:" + this.chatScale);
         writer.println("chatWidth:" + this.chatWidth);
         writer.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         writer.println("mipmapLevels:" + this.mipmapLevels);
         writer.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         writer.println("streamMicVolume:" + this.streamMicVolume);
         writer.println("streamSystemVolume:" + this.streamSystemVolume);
         writer.println("streamKbps:" + this.streamKbps);
         writer.println("streamFps:" + this.streamFps);
         writer.println("streamCompression:" + this.streamCompression);
         writer.println("streamSendMetadata:" + this.streamSendMetadata);
         writer.println("streamPreferredServer:" + this.streamPreferredServer);
         writer.println("streamChatEnabled:" + this.streamChatEnabled);
         writer.println("streamChatUserFilter:" + this.streamChatUserFilter);
         writer.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         writer.println("forceUnicodeFont:" + this.forceUnicodeFont);
         writer.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         writer.println("reducedDebugInfo:" + this.reducedDebugInfo);
         writer.println("useNativeTransport:" + this.useNativeTransport);
         writer.println("entityShadows:" + this.entityShadows);

         for (KeyBinding keyBinding : this.keyBindings) {
            writer.println("key_" + keyBinding.AOr() + ":" + keyBinding.getKeyCode());
         }

         for (SoundCategory soundCategory : SoundCategory.values()) {
            writer.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
         }

         for (EnumPlayerModelParts modelPart : EnumPlayerModelParts.values()) {
            writer.println("modelPart_" + modelPart.getPartName() + ":" + this.setModelParts.contains(modelPart));
         }

         writer.close();
      } catch (Exception exception) {
         logger.error("Failed to save options", exception);
      }

      this.saveOfOptions();
      this.sendSettingsToServer();
   }

   public void setOfOptionValue(GameSettings.Options option, int value) {
      if (option == GameSettings.Options.FOG_FANCY) {
         switch (this.fogType) {
            case 1:
               this.fogType = 2;
               if (!Config.isFancyFogAvailable()) {
                  this.fogType = 3;
               }
               break;
            case 2:
               this.fogType = 3;
               break;
            case 3:
               this.fogType = 1;
               break;
            default:
               this.fogType = 1;
         }
      }

      if (option == GameSettings.Options.FOG_START) {
         this.fogStart += 0.2F;
         if (this.fogStart > 0.81F) {
            this.fogStart = 0.2F;
         }
      }

      if (option == GameSettings.Options.SMOOTH_FPS) {
         this.smoothFps = !this.smoothFps;
      }

      if (option == GameSettings.Options.SMOOTH_WORLD) {
         this.smoothWorld = !this.smoothWorld;
         Config.updateThreadPriorities();
      }

      if (option == GameSettings.Options.CLOUDS) {
         this.cloudMode++;
         if (this.cloudMode > 3) {
            this.cloudMode = 0;
         }

         this.updateClouds();
         this.mc.renderGlobal.Ok();
      }

      if (option == GameSettings.Options.TREES) {
         this.trees++;
         if (this.trees > 2) {
            this.trees = 0;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.DROPPED_ITEMS) {
         this.droppedItems++;
         if (this.droppedItems > 2) {
            this.droppedItems = 0;
         }
      }

      if (option == GameSettings.Options.RAIN) {
         this.rain++;
         if (this.rain > 3) {
            this.rain = 0;
         }
      }

      if (option == GameSettings.Options.ANIMATED_WATER) {
         this.animatedWater++;
         if (this.animatedWater > 2) {
            this.animatedWater = 0;
         }
      }

      if (option == GameSettings.Options.ANIMATED_LAVA) {
         this.animatedLava++;
         if (this.animatedLava > 2) {
            this.animatedLava = 0;
         }
      }

      if (option == GameSettings.Options.ANIMATED_FIRE) {
         this.animatedFire = !this.animatedFire;
      }

      if (option == GameSettings.Options.ANIMATED_PORTAL) {
         this.animatedPortal = !this.animatedPortal;
      }

      if (option == GameSettings.Options.ANIMATED_REDSTONE) {
         this.animatedRedstone = !this.animatedRedstone;
      }

      if (option == GameSettings.Options.ANIMATED_EXPLOSION) {
         this.animatedExplosion = !this.animatedExplosion;
      }

      if (option == GameSettings.Options.ANIMATED_FLAME) {
         this.animatedFlame = !this.animatedFlame;
      }

      if (option == GameSettings.Options.ANIMATED_SMOKE) {
         this.animatedSmoke = !this.animatedSmoke;
      }

      if (option == GameSettings.Options.VOID_PARTICLES) {
         this.voidParticles = !this.voidParticles;
      }

      if (option == GameSettings.Options.WATER_PARTICLES) {
         this.waterParticles = !this.waterParticles;
      }

      if (option == GameSettings.Options.PORTAL_PARTICLES) {
         this.portalParticles = !this.portalParticles;
      }

      if (option == GameSettings.Options.POTION_PARTICLES) {
         this.potionParticles = !this.potionParticles;
      }

      if (option == GameSettings.Options.FIREWORK_PARTICLES) {
         this.fireworkParticles = !this.fireworkParticles;
      }

      if (option == GameSettings.Options.DRIPPING_WATER_LAVA) {
         this.drippingWaterLava = !this.drippingWaterLava;
      }

      if (option == GameSettings.Options.ANIMATED_TERRAIN) {
         this.animatedTerrain = !this.animatedTerrain;
      }

      if (option == GameSettings.Options.ANIMATED_TEXTURES) {
         this.animatedTextures = !this.animatedTextures;
      }

      if (option == GameSettings.Options.RAIN_SPLASH) {
         this.rainSplash = !this.rainSplash;
      }

      if (option == GameSettings.Options.LAGOMETER) {
         this.lagometer = !this.lagometer;
      }

      if (option == GameSettings.Options.SHOW_FPS) {
         this.showFps = !this.showFps;
      }

      if (option == GameSettings.Options.AUTOSAVE_TICKS) {
         this.autoSaveTicks *= 10;
         if (this.autoSaveTicks > 40000) {
            this.autoSaveTicks = 40;
         }
      }

      if (option == GameSettings.Options.BETTER_GRASS) {
         this.betterGrass++;
         if (this.betterGrass > 3) {
            this.betterGrass = 1;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.CONNECTED_TEXTURES) {
         this.connectedTextures++;
         if (this.connectedTextures > 3) {
            this.connectedTextures = 1;
         }

         if (this.connectedTextures != 2) {
            this.mc.refreshResources();
         }
      }

      if (option == GameSettings.Options.WEATHER) {
         this.weather = !this.weather;
      }

      if (option == GameSettings.Options.SKY) {
         this.sky = !this.sky;
      }

      if (option == GameSettings.Options.STARS) {
         this.stars = !this.stars;
      }

      if (option == GameSettings.Options.SUN_MOON) {
         this.sunMoon = !this.sunMoon;
      }

      if (option == GameSettings.Options.VIGNETTE) {
         this.vignette++;
         if (this.vignette > 2) {
            this.vignette = 0;
         }
      }

      if (option == GameSettings.Options.CHUNK_UPDATES) {
         this.chunkUpdates++;
         if (this.chunkUpdates > 5) {
            this.chunkUpdates = 1;
         }
      }

      if (option == GameSettings.Options.CHUNK_LOADING) {
         this.chunkLoading++;
         if (this.chunkLoading > 2) {
            this.chunkLoading = 0;
         }

         this.reloadRenderers();
      }

      if (option == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         this.chunkUpdatesDynamic = !this.chunkUpdatesDynamic;
      }

      if (option == GameSettings.Options.TIME) {
         this.timeOfDay++;
         if (this.timeOfDay > 3) {
            this.timeOfDay = 0;
         }
      }

      if (option == GameSettings.Options.CLEAR_WATER) {
         this.clearWater = !this.clearWater;
         this.updateWaterOpacity();
      }

      if (option == GameSettings.Options.PROFILER) {
         this.profiler = !this.profiler;
      }

      if (option == GameSettings.Options.BETTER_SNOW) {
         this.betterSnow = !this.betterSnow;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.SWAMP_COLORS) {
         this.swampColors = !this.swampColors;
         CustomColorizer.updateUseDefaultColorMultiplier();
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.RANDOM_MOBS) {
         this.randomMobs = !this.randomMobs;
         RandomMobs.resetTextures();
      }

      if (option == GameSettings.Options.SMOOTH_BIOMES) {
         this.smoothBiomes = !this.smoothBiomes;
         CustomColorizer.updateUseDefaultColorMultiplier();
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.CUSTOM_FONTS) {
         this.customFonts = !this.customFonts;
         this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
         this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
      }

      if (option == GameSettings.Options.CUSTOM_COLORS) {
         this.customColors = !this.customColors;
         CustomColorizer.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.CUSTOM_SKY) {
         this.customSky = !this.customSky;
         CustomSky.update();
      }

      if (option == GameSettings.Options.SHOW_CAPES) {
         this.showCapes = !this.showCapes;
      }

      if (option == GameSettings.Options.NATURAL_TEXTURES) {
         this.naturalTextures = !this.naturalTextures;
         NaturalTextures.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.FAST_MATH) {
         this.fastMath = !this.fastMath;
         MathHelper.hhp = this.fastMath;
      }

      if (option == GameSettings.Options.FAST_RENDER) {
         this.fastRender = !this.fastRender;
         Config.updateFramebufferSize();
      }

      if (option == GameSettings.Options.TRANSLUCENT_BLOCKS) {
         if (this.translucentBlocks == 0) {
            this.translucentBlocks = 1;
         } else if (this.translucentBlocks == 1) {
            this.translucentBlocks = 2;
         } else if (this.translucentBlocks == 2) {
            this.translucentBlocks = 0;
         } else {
            this.translucentBlocks = 0;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.LAZY_CHUNK_LOADING) {
         this.lazyChunkLoading = !this.lazyChunkLoading;
         Config.updateAvailableProcessors();
         if (!Config.isSingleProcessor()) {
            this.lazyChunkLoading = false;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.FULLSCREEN_MODE) {
         List fullscreenModes = Arrays.asList(Config.getFullscreenModes());
         if (this.fullscreenMode.equals("Default")) {
            this.fullscreenMode = (String)fullscreenModes.get(0);
         } else {
            int modeIndex = fullscreenModes.indexOf(this.fullscreenMode);
            if (modeIndex < 0) {
               this.fullscreenMode = "Default";
            } else if (++modeIndex >= fullscreenModes.size()) {
               this.fullscreenMode = "Default";
            } else {
               this.fullscreenMode = (String)fullscreenModes.get(modeIndex);
            }
         }
      }

      if (option == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         this.heldItemTooltips = !this.heldItemTooltips;
      }
   }

   public void switchModelPartEnabled(EnumPlayerModelParts modelPart) {
      if (!this.getModelParts().contains(modelPart)) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public static String getTranslation(String[] translationKeys, int index) {
      if (index < 0 || index >= translationKeys.length) {
         index = 0;
      }

      return I18n.format(translationKeys[index]);
   }

   public void updateClouds() {
      switch (this.cloudMode) {
         case 1:
            this.clouds = 1;
            break;
         case 2:
            this.clouds = 2;
            break;
         case 3:
            this.clouds = 0;
            break;
         default:
            if (this.fancyGraphics) {
               this.clouds = 2;
            } else {
               this.clouds = 1;
            }
      }
   }

   public String getKeyBinding(GameSettings.Options option) {
      String ofOptionString = this.getOfOptionString(option);
      if (ofOptionString != null) {
         return ofOptionString;
      } else {
         String prefix = I18n.format(option.getEnumString()) + ": ";
         if (option.getEnumBoolean()) {
            float optionFloat = this.getOptionFloatValue(option);
            float normalizedValue = option.normalizeValue(optionFloat);
            return option == GameSettings.Options.SENSITIVITY
               ? (
                  normalizedValue == 0.0F
                     ? prefix + I18n.format("options.sensitivity.min")
                     : (normalizedValue == 1.0F ? prefix + I18n.format("options.sensitivity.max") : prefix + (int)(normalizedValue * 200.0F) + "%")
               )
               : (
                  option == GameSettings.Options.FOV
                     ? (optionFloat == 70.0F ? prefix + I18n.format("options.fov.min") : (optionFloat == 110.0F ? prefix + I18n.format("options.fov.max") : prefix + (int)optionFloat))
                     : (
                        option == GameSettings.Options.FRAMERATE_LIMIT
                           ? (optionFloat == GameSettings.Options.getValueMaxOf(option) ? prefix + I18n.format("options.framerateLimit.max") : prefix + (int)optionFloat + " fps")
                           : (
                              option == GameSettings.Options.RENDER_CLOUDS
                                 ? (optionFloat == GameSettings.Options.getValueMinOf(option) ? prefix + I18n.format("options.cloudHeight.min") : prefix + ((int)optionFloat + 128))
                                 : (
                                    option == GameSettings.Options.GAMMA
                                       ? (
                                          normalizedValue == 0.0F
                                             ? prefix + I18n.format("options.gamma.min")
                                             : (normalizedValue == 1.0F ? prefix + I18n.format("options.gamma.max") : prefix + "+" + (int)(normalizedValue * 100.0F) + "%")
                                       )
                                       : (
                                          option == GameSettings.Options.SATURATION
                                             ? prefix + (int)(normalizedValue * 400.0F) + "%"
                                             : (
                                                option == GameSettings.Options.CHAT_OPACITY
                                                   ? prefix + (int)(normalizedValue * 90.0F + 10.0F) + "%"
                                                   : (
                                                      option == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                         ? prefix + GuiNewChat.calculateChatboxHeight(normalizedValue) + "px"
                                                         : (
                                                            option == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                               ? prefix + GuiNewChat.calculateChatboxHeight(normalizedValue) + "px"
                                                               : (
                                                                  option == GameSettings.Options.CHAT_WIDTH
                                                                     ? prefix + GuiNewChat.calculateChatboxWidth(normalizedValue) + "px"
                                                                     : (
                                                                        option == GameSettings.Options.RENDER_DISTANCE
                                                                           ? prefix + (int)optionFloat + " chunks"
                                                                           : (
                                                                              option == GameSettings.Options.MIPMAP_LEVELS
                                                                                 ? (optionFloat == 0.0F ? prefix + I18n.format("options.off") : prefix + (int)optionFloat)
                                                                                 : (
                                                                                    option == GameSettings.Options.STREAM_FPS
                                                                                       ? prefix + TwitchStream.lQ(normalizedValue) + " fps"
                                                                                       : (
                                                                                          option == GameSettings.Options.STREAM_KBPS
                                                                                             ? prefix + TwitchStream.m$r(normalizedValue) + " Kbps"
                                                                                             : (
                                                                                                option == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                   ? prefix
                                                                                                      + String.format(
                                                                                                         "%.3f bpp", TwitchStream.formatStreamBps(normalizedValue)
                                                                                                      )
                                                                                                   : (
                                                                                                      normalizedValue == 0.0F
                                                                                                         ? prefix + I18n.format("options.off")
                                                                                                         : prefix + (int)(normalizedValue * 100.0F) + "%"
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               );
         } else if (option.getEnumFloat()) {
            boolean ordinalValue = this.getOptionOrdinalValue(option);
            return ordinalValue ? prefix + I18n.format("options.on") : prefix + I18n.format("options.off");
         } else if (option == GameSettings.Options.GUI_SCALE) {
            return prefix + getTranslation(guiScaleNames, this.guiScale);
         } else if (option == GameSettings.Options.CHAT_VISIBILITY) {
            return prefix + I18n.format(this.chatVisibility.getResourceKey());
         } else if (option == GameSettings.Options.PARTICLES) {
            return prefix + getTranslation(particleNames, this.particleSetting);
         } else if (option == GameSettings.Options.AMBIENT_OCCLUSION) {
            return prefix + getTranslation(ambientOcclusionNames, this.ambientOcclusion);
         } else if (option == GameSettings.Options.STREAM_COMPRESSION) {
            return prefix + getTranslation(streamCompressionNames, this.streamCompression);
         } else if (option == GameSettings.Options.STREAM_CHAT_ENABLED) {
            return prefix + getTranslation(streamChatEnabledNames, this.streamChatEnabled);
         } else if (option == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
            return prefix + getTranslation(streamChatUserFilterNames, this.streamChatUserFilter);
         } else if (option == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            return prefix + getTranslation(micToggleNames, this.streamMicToggleBehavior);
         } else if (option == GameSettings.Options.RENDER_CLOUDS) {
            return prefix + getTranslation(cloudsNames, this.clouds);
         } else if (option == GameSettings.Options.GRAPHICS) {
            if (this.fancyGraphics) {
               return prefix + I18n.format("options.graphics.fancy");
            } else {
               String fastGraphicsKey = "options.graphics.fast";
               return prefix + I18n.format("options.graphics.fast");
            }
         } else {
            return prefix;
         }
      }
   }

   public GameSettings() {
      this.renderDistanceChunks = -1;
      this.viewBobbing = true;
      this.fboEnable = true;
      this.maxFps = 120;
      this.clouds = 2;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = EnumChatVisibility.FULL;
      this.chatColours = true;
      this.chatLinks = true;
      this.chatLinksPrompt = true;
      this.chatOpacity = 1.0F;
      this.snooperEnabled = true;
      this.enableVsync = true;
      this.useVbo = false;
      this.allowBlockAlternatives = true;
      this.reducedDebugInfo = false;
      this.pauseOnLostFocus = true;
      this.setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
      this.heldItemTooltips = true;
      this.chatScale = 1.0F;
      this.chatWidth = 1.0F;
      this.chatHeightUnfocused = 0.44366196F;
      this.chatHeightFocused = 1.0F;
      this.showInventoryAchievementHint = true;
      this.mipmapLevels = 4;
      this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
      this.streamBytesPerPixel = 0.5F;
      this.streamMicVolume = 1.0F;
      this.streamSystemVolume = 1.0F;
      this.streamKbps = 0.5412844F;
      this.streamFps = 0.31690142F;
      this.streamCompression = 1;
      this.streamSendMetadata = true;
      this.streamPreferredServer = "";
      this.streamChatEnabled = 0;
      this.streamChatUserFilter = 0;
      this.streamMicToggleBehavior = 0;
      this.useNativeTransport = true;
      this.entityShadows = true;
      this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
      this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
      this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
      this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
      this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
      this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
      this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
      this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
      this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
      this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
      this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
      this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
      this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
      this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
      this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
      this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
      this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
      this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
      this.keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
      this.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
      this.keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
      this.keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
      this.keyBindStreamCommercial = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
      this.keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
      this.keyBindsHotbar = new KeyBinding[]{
         new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
         new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
         new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
         new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
         new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
         new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
         new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
         new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
         new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
      };
      this.fogType = 1;
      this.fogStart = 0.8F;
      this.mipmapType = 0;
      this.occlusionFancy = false;
      this.smoothFps = false;
      this.smoothWorld = Config.isSingleProcessor();
      this.lazyChunkLoading = Config.isSingleProcessor();
      this.aoLevel = 1.0F;
      this.aaLevel = 0;
      this.anisotropicLevel = 1;
      this.cloudMode = 0;
      this.cloudsHeight = 0.0F;
      this.trees = 0;
      this.rain = 0;
      this.droppedItems = 0;
      this.betterGrass = 3;
      this.autoSaveTicks = 4000;
      this.lagometer = false;
      this.profiler = false;
      this.showFps = false;
      this.weather = true;
      this.sky = true;
      this.stars = true;
      this.sunMoon = true;
      this.vignette = 0;
      this.chunkUpdates = 1;
      this.chunkLoading = 0;
      this.chunkUpdatesDynamic = false;
      this.timeOfDay = 0;
      this.clearWater = false;
      this.betterSnow = false;
      this.fullscreenMode = "Default";
      this.swampColors = true;
      this.randomMobs = true;
      this.smoothBiomes = true;
      this.customFonts = true;
      this.customColors = true;
      this.customSky = true;
      this.showCapes = true;
      this.connectedTextures = 2;
      this.naturalTextures = false;
      this.fastMath = false;
      this.fastRender = false;
      this.translucentBlocks = 0;
      this.animatedWater = 0;
      this.animatedLava = 0;
      this.animatedFire = true;
      this.animatedPortal = true;
      this.animatedRedstone = true;
      this.animatedExplosion = true;
      this.animatedFlame = true;
      this.animatedSmoke = true;
      this.voidParticles = true;
      this.waterParticles = true;
      this.rainSplash = true;
      this.portalParticles = true;
      this.potionParticles = true;
      this.fireworkParticles = true;
      this.drippingWaterLava = true;
      this.animatedTerrain = true;
      this.animatedTextures = true;
      this.keyBindings = ArrayUtils.addAll(
         new KeyBinding[]{
            this.keyBindAttack,
            this.keyBindUseItem,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.keyBindSprint,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.keyBindPlayerList,
            this.keyBindPickBlock,
            this.keyBindCommand,
            this.keyBindScreenshot,
            this.keyBindTogglePerspective,
            this.keyBindSmoothCamera,
            this.keyBindStreamStartStop,
            this.keyBindStreamPauseUnpause,
            this.keyBindStreamCommercial,
            this.keyBindStreamToggleMic,
            this.keyBindFullscreen,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.fov = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
   }

   public GameSettings(Minecraft minecraft, File dataDir) {
      this.renderDistanceChunks = -1;
      this.viewBobbing = true;
      this.fboEnable = true;
      this.maxFps = 120;
      this.clouds = 2;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.resourcePacks = Lists.newArrayList();
      this.incompatibleResourcePacks = Lists.newArrayList();
      this.chatVisibility = EnumChatVisibility.FULL;
      this.chatColours = true;
      this.chatLinks = true;
      this.chatLinksPrompt = true;
      this.chatOpacity = 1.0F;
      this.snooperEnabled = true;
      this.enableVsync = true;
      this.useVbo = false;
      this.allowBlockAlternatives = true;
      this.reducedDebugInfo = false;
      this.pauseOnLostFocus = true;
      this.setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
      this.heldItemTooltips = true;
      this.chatScale = 1.0F;
      this.chatWidth = 1.0F;
      this.chatHeightUnfocused = 0.44366196F;
      this.chatHeightFocused = 1.0F;
      this.showInventoryAchievementHint = true;
      this.mipmapLevels = 4;
      this.mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
      this.streamBytesPerPixel = 0.5F;
      this.streamMicVolume = 1.0F;
      this.streamSystemVolume = 1.0F;
      this.streamKbps = 0.5412844F;
      this.streamFps = 0.31690142F;
      this.streamCompression = 1;
      this.streamSendMetadata = true;
      this.streamPreferredServer = "";
      this.streamChatEnabled = 0;
      this.streamChatUserFilter = 0;
      this.streamMicToggleBehavior = 0;
      this.useNativeTransport = true;
      this.entityShadows = true;
      this.keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
      this.keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
      this.keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
      this.keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
      this.keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
      this.keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
      this.keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
      this.keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
      this.keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
      this.keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
      this.keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
      this.keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
      this.keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
      this.keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
      this.keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
      this.keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
      this.keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
      this.keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
      this.keyBindFullscreen = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
      this.keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
      this.keyBindStreamStartStop = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
      this.keyBindStreamPauseUnpause = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
      this.keyBindStreamCommercial = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
      this.keyBindStreamToggleMic = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
      this.keyBindsHotbar = new KeyBinding[]{
         new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
         new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
         new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
         new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
         new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
         new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
         new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
         new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
         new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
      };
      this.fogType = 1;
      this.fogStart = 0.8F;
      this.mipmapType = 0;
      this.occlusionFancy = false;
      this.smoothFps = false;
      this.smoothWorld = Config.isSingleProcessor();
      this.lazyChunkLoading = Config.isSingleProcessor();
      this.aoLevel = 1.0F;
      this.aaLevel = 0;
      this.anisotropicLevel = 1;
      this.cloudMode = 0;
      this.cloudsHeight = 0.0F;
      this.trees = 0;
      this.rain = 0;
      this.droppedItems = 0;
      this.betterGrass = 3;
      this.autoSaveTicks = 4000;
      this.lagometer = false;
      this.profiler = false;
      this.showFps = false;
      this.weather = true;
      this.sky = true;
      this.stars = true;
      this.sunMoon = true;
      this.vignette = 0;
      this.chunkUpdates = 1;
      this.chunkLoading = 0;
      this.chunkUpdatesDynamic = false;
      this.timeOfDay = 0;
      this.clearWater = false;
      this.betterSnow = false;
      this.fullscreenMode = "Default";
      this.swampColors = true;
      this.randomMobs = true;
      this.smoothBiomes = true;
      this.customFonts = true;
      this.customColors = true;
      this.customSky = true;
      this.showCapes = true;
      this.connectedTextures = 2;
      this.naturalTextures = false;
      this.fastMath = false;
      this.fastRender = false;
      this.translucentBlocks = 0;
      this.animatedWater = 0;
      this.animatedLava = 0;
      this.animatedFire = true;
      this.animatedPortal = true;
      this.animatedRedstone = true;
      this.animatedExplosion = true;
      this.animatedFlame = true;
      this.animatedSmoke = true;
      this.voidParticles = true;
      this.waterParticles = true;
      this.rainSplash = true;
      this.portalParticles = true;
      this.potionParticles = true;
      this.fireworkParticles = true;
      this.drippingWaterLava = true;
      this.animatedTerrain = true;
      this.animatedTextures = true;
      this.keyBindings = ArrayUtils.addAll(
         new KeyBinding[]{
            this.keyBindAttack,
            this.keyBindUseItem,
            this.keyBindForward,
            this.keyBindLeft,
            this.keyBindBack,
            this.keyBindRight,
            this.keyBindJump,
            this.keyBindSneak,
            this.keyBindSprint,
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.keyBindPlayerList,
            this.keyBindPickBlock,
            this.keyBindCommand,
            this.keyBindScreenshot,
            this.keyBindTogglePerspective,
            this.keyBindSmoothCamera,
            this.keyBindStreamStartStop,
            this.keyBindStreamPauseUnpause,
            this.keyBindStreamCommercial,
            this.keyBindStreamToggleMic,
            this.keyBindFullscreen,
            this.keyBindSpectatorOutlines
         },
         this.keyBindsHotbar
      );
      this.difficulty = EnumDifficulty.NORMAL;
      this.lastServer = "";
      this.fov = 70.0F;
      this.language = "en_US";
      this.forceUnicodeFont = false;
      this.mc = minecraft;
      this.optionsFile = new File(dataDir, "options.txt");
      this.optionsOfFile = new File(dataDir, "optionsof.txt");
      this.maxFps = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.keyBindZoom = new KeyBinding("Zoom", 46, "key.categories.misc");
      this.keyBindings = ArrayUtils.add(this.keyBindings, this.keyBindZoom);
      GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
      this.renderDistanceChunks = 8;
      this.loadOptions();
      Config.initGameSettings(this);
   }

   public int getEffectiveClouds() {
      return this.renderDistanceChunks >= 4 ? this.clouds : 0;
   }

   public void setOptionFloatValue(GameSettings.Options option, float value) {
      this.setOfOptionFloatValue(option, value);
      if (option == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = value;
      }

      if (option == GameSettings.Options.FOV) {
         this.fov = value;
      }

      if (option == GameSettings.Options.GAMMA) {
         this.gamma = value;
      }

      if (option == GameSettings.Options.FRAMERATE_LIMIT) {
         this.maxFps = (int)value;
         this.enableVsync = false;
         if (this.maxFps <= 0) {
            this.maxFps = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
            this.enableVsync = true;
         }

         this.updateVsync();
      }

      if (option == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (option == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (option == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (option == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (option == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (option == GameSettings.Options.MIPMAP_LEVELS) {
         int previousMipmapLevels = this.mipmapLevels;
         this.mipmapLevels = (int)value;
         if ((float)previousMipmapLevels != value) {
            this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.mc.getTextureMapBlocks().bBo(false, this.mipmapLevels > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (option == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)value;
         this.mc.renderGlobal.cw();
      }

      if (option == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.streamBytesPerPixel = value;
      }

      if (option == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.streamMicVolume = value;
         this.mc.getTwitchStream().updateStreamVolume();
      }

      if (option == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.streamSystemVolume = value;
         this.mc.getTwitchStream().updateStreamVolume();
      }

      if (option == GameSettings.Options.STREAM_KBPS) {
         this.streamKbps = value;
      }

      if (option == GameSettings.Options.STREAM_FPS) {
         this.streamFps = value;
      }
   }

   public void resetOptionsToDefaults() {
      this.renderDistanceChunks = 8;
      this.viewBobbing = true;
      this.anaglyph = false;
      this.maxFps = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.enableVsync = false;
      this.updateVsync();
      this.mipmapLevels = 4;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.clouds = 2;
      this.fov = 70.0F;
      this.gamma = 0.0F;
      this.guiScale = 0;
      this.particleSetting = 0;
      this.heldItemTooltips = true;
      this.useVbo = false;
      this.allowBlockAlternatives = true;
      this.forceUnicodeFont = false;
      this.fogType = 1;
      this.fogStart = 0.8F;
      this.mipmapType = 0;
      this.occlusionFancy = false;
      this.smoothFps = false;
      Config.updateAvailableProcessors();
      this.smoothWorld = Config.isSingleProcessor();
      this.lazyChunkLoading = Config.isSingleProcessor();
      this.fastMath = false;
      this.fastRender = false;
      this.translucentBlocks = 0;
      this.aoLevel = 1.0F;
      this.aaLevel = 0;
      this.anisotropicLevel = 1;
      this.cloudMode = 0;
      this.cloudsHeight = 0.0F;
      this.trees = 0;
      this.rain = 0;
      this.betterGrass = 3;
      this.autoSaveTicks = 4000;
      this.lagometer = false;
      this.showFps = false;
      this.profiler = false;
      this.weather = true;
      this.sky = true;
      this.stars = true;
      this.sunMoon = true;
      this.vignette = 0;
      this.chunkUpdates = 1;
      this.chunkLoading = 0;
      this.chunkUpdatesDynamic = false;
      this.timeOfDay = 0;
      this.clearWater = false;
      this.betterSnow = false;
      this.fullscreenMode = "Default";
      this.swampColors = true;
      this.randomMobs = true;
      this.smoothBiomes = true;
      this.customFonts = true;
      this.customColors = true;
      this.customSky = true;
      this.showCapes = true;
      this.connectedTextures = 2;
      this.naturalTextures = false;
      this.animatedWater = 0;
      this.animatedLava = 0;
      this.animatedFire = true;
      this.animatedPortal = true;
      this.animatedRedstone = true;
      this.animatedExplosion = true;
      this.animatedFlame = true;
      this.animatedSmoke = true;
      this.voidParticles = true;
      this.waterParticles = true;
      this.rainSplash = true;
      this.portalParticles = true;
      this.potionParticles = true;
      this.fireworkParticles = true;
      this.drippingWaterLava = true;
      this.animatedTerrain = true;
      this.animatedTextures = true;
      this.updateWaterOpacity();
      this.mc.refreshResources();
      this.saveOptions();
   }

   public boolean isUseNativeTransport() {
      return this.useNativeTransport;
   }

   public void setAllAnimations(boolean enabled) {
      int animationLevel = enabled ? 0 : 2;
      this.animatedWater = animationLevel;
      this.animatedLava = animationLevel;
      this.animatedFire = enabled;
      this.animatedPortal = enabled;
      this.animatedRedstone = enabled;
      this.animatedExplosion = enabled;
      this.animatedFlame = enabled;
      this.animatedSmoke = enabled;
      this.voidParticles = enabled;
      this.waterParticles = enabled;
      this.rainSplash = enabled;
      this.portalParticles = enabled;
      this.potionParticles = enabled;
      this.fireworkParticles = enabled;
      this.particleSetting = enabled ? 0 : 2;
      this.drippingWaterLava = enabled;
      this.animatedTerrain = enabled;
      this.animatedTextures = enabled;
   }

   public void reloadRenderers() {
      if (this.mc.renderGlobal != null) {
         this.mc.renderGlobal.loadRenderers();
      }
   }

   public void setOfOptionFloatValue(GameSettings.Options option, float value) {
      if (option == GameSettings.Options.CLOUD_HEIGHT) {
         this.cloudsHeight = value;
         this.mc.renderGlobal.Ok();
      }

      if (option == GameSettings.Options.AO_LEVEL) {
         this.aoLevel = value;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.AA_LEVEL) {
         int[] aaLevels = new int[]{0, 2, 4, 6, 8, 12, 16};
         this.aaLevel = 0;
         int requestedAaLevel = (int)value;
         int index = 0;

         while (index < aaLevels.length) {
            if (requestedAaLevel >= aaLevels[index]) {
               this.aaLevel = aaLevels[index];
            }

            index -= 17;
            index += 18;
         }

         this.aaLevel = Config.limit(this.aaLevel, 0, 16);
      }

      if (option == GameSettings.Options.AF_LEVEL) {
         int requestedAfLevel = (int)value;
         this.anisotropicLevel = 1;

         while (this.anisotropicLevel * 2 <= requestedAfLevel) {
            this.anisotropicLevel *= 2;
         }

         this.anisotropicLevel = Config.limit(this.anisotropicLevel, 1, 16);
         this.mc.refreshResources();
      }

      if (option == GameSettings.Options.MIPMAP_TYPE) {
         int mipmapTypeValue = (int)value;
         this.mipmapType = Config.limit(mipmapTypeValue, 0, 3);
         this.mc.refreshResources();
      }
   }

   public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enabled) {
      if (enabled) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public static boolean isKeyDown(KeyBinding keyBinding) {
      int keyCode = keyBinding.getKeyCode();
      return keyCode < -100 || keyCode > 255
         ? false
         : (keyBinding.getKeyCode() == 0 ? false : (keyBinding.getKeyCode() < 0 ? Mouse.isButtonDown(keyBinding.getKeyCode() + 100) : Keyboard.isKeyDown(keyBinding.getKeyCode())));
   }

   public void updateWaterOpacity() {
      if (this.mc.isJava64bit() && this.mc.getIntegratedServer() != null) {
         Config.waterOpacityChanged = true;
      }

      ClearWater.updateWaterOpacity(this, this.mc.theWorld);
   }

   public void setOptionKeyBinding(KeyBinding keyBinding, int keyCode) {
      keyBinding.setKeyCode(keyCode);
      this.saveOptions();
   }

   public void updateVsync() {
      Display.setVSyncEnabled(this.enableVsync);
   }

   public void setOptionValue(GameSettings.Options option, int step) {
      this.setOfOptionValue(option, step);
      if (option == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (option == GameSettings.Options.GUI_SCALE) {
         this.guiScale = this.guiScale + step & 3;
      }

      if (option == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + step) % 3;
      }

      if (option == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (option == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + step) % 3;
      }

      if (option == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.fontRendererObj.setBidiFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
      }

      if (option == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (option == GameSettings.Options.ANAGLYPH) {
         this.anaglyph = !this.anaglyph;
         this.mc.refreshResources();
      }

      if (option == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.updateClouds();
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + step) % 3;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + step) % 3);
      }

      if (option == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + step) % 3;
      }

      if (option == GameSettings.Options.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if (option == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + step) % 3;
      }

      if (option == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + step) % 3;
      }

      if (option == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + step) % 2;
      }

      if (option == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (option == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (option == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (option == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (option == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (option == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if (this.mc.isIntegratedServerRunning() != this.fullScreen) {
            this.mc.toggleFullscreen();
         }
      }

      if (option == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if (option == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (option == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (option == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadows = !this.entityShadows;
      }

      this.saveOptions();
   }

   // $VF: Unable to simplify switch on enum
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public boolean getOptionOrdinalValue(GameSettings.Options option) {
      switch (GameSettings$2.$SwitchMap$net$minecraft$client$settings$GameSettings$Options[option.ordinal()]) {
         case 1:
            return this.invertMouse;
         case 2:
            return this.viewBobbing;
         case 3:
            return this.anaglyph;
         case 4:
            return this.fboEnable;
         case 5:
            return this.chatColours;
         case 6:
            return this.chatLinks;
         case 7:
            return this.chatLinksPrompt;
         case 8:
            return this.snooperEnabled;
         case 9:
            return this.fullScreen;
         case 10:
            return this.enableVsync;
         case 11:
            return this.useVbo;
         case 12:
            return this.touchscreen;
         case 13:
            return this.streamSendMetadata;
         case 14:
            return this.forceUnicodeFont;
         case 15:
            return this.allowBlockAlternatives;
         case 16:
            return this.reducedDebugInfo;
         case 17:
            return this.entityShadows;
         default:
            return false;
      }
   }

   public String getOfOptionString(GameSettings.Options option) {
      String label = I18n.format(option.getEnumString()) + ": ";
      if (label == null) {
         label = option.getEnumString();
      }

      if (option == GameSettings.Options.RENDER_DISTANCE) {
         int renderDistance = (int)this.getOptionFloatValue(option);
         String renderDistanceName = "Tiny";
         byte renderDistanceValue = 2;
         if (renderDistance >= 4) {
            renderDistanceName = "Short";
            renderDistanceValue = 4;
         }

         if (renderDistance >= 8) {
            renderDistanceName = "Normal";
            renderDistanceValue = 8;
         }

         if (renderDistance >= 16) {
            renderDistanceName = "Far";
            renderDistanceValue = 16;
         }

         if (renderDistance >= 32) {
            renderDistanceName = "Extreme";
            renderDistanceValue = 32;
         }

         int chunkDelta = this.renderDistanceChunks - renderDistanceValue;
         String renderDistanceLabel = renderDistanceName;
         if (chunkDelta > 0) {
            renderDistanceLabel = renderDistanceName + "+";
         }

         return label + renderDistance + " " + renderDistanceLabel + "";
      } else if (option == GameSettings.Options.FOG_FANCY) {
         switch (this.fogType) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            case 3:
               return label + "OFF";
            default:
               return label + "OFF";
         }
      } else if (option == GameSettings.Options.FOG_START) {
         return label + this.fogStart;
      } else if (option == GameSettings.Options.MIPMAP_TYPE) {
         switch (this.mipmapType) {
            case 0:
               return label + "Nearest";
            case 1:
               return label + "Linear";
            case 2:
               return label + "Bilinear";
            case 3:
               return label + "Trilinear";
            default:
               return label + "Nearest";
         }
      } else if (option == GameSettings.Options.SMOOTH_FPS) {
         return this.smoothFps ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SMOOTH_WORLD) {
         return this.smoothWorld ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.CLOUDS) {
         switch (this.cloudMode) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            case 3:
               return label + "OFF";
            default:
               return label + "Default";
         }
      } else if (option == GameSettings.Options.TREES) {
         switch (this.trees) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            default:
               return label + "Default";
         }
      } else if (option == GameSettings.Options.DROPPED_ITEMS) {
         switch (this.droppedItems) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            default:
               return label + "Default";
         }
      } else if (option == GameSettings.Options.RAIN) {
         switch (this.rain) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            case 3:
               return label + "OFF";
            default:
               return label + "Default";
         }
      } else if (option == GameSettings.Options.ANIMATED_WATER) {
         switch (this.animatedWater) {
            case 1:
               return label + "Dynamic";
            case 2:
               return label + "OFF";
            default:
               return label + "ON";
         }
      } else if (option == GameSettings.Options.ANIMATED_LAVA) {
         switch (this.animatedLava) {
            case 1:
               return label + "Dynamic";
            case 2:
               return label + "OFF";
            default:
               return label + "ON";
         }
      } else if (option == GameSettings.Options.ANIMATED_FIRE) {
         return this.animatedFire ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_PORTAL) {
         return this.animatedPortal ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_REDSTONE) {
         return this.animatedRedstone ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_EXPLOSION) {
         return this.animatedExplosion ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_FLAME) {
         return this.animatedFlame ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_SMOKE) {
         return this.animatedSmoke ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.VOID_PARTICLES) {
         return this.voidParticles ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.WATER_PARTICLES) {
         return this.waterParticles ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.PORTAL_PARTICLES) {
         return this.portalParticles ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.POTION_PARTICLES) {
         return this.potionParticles ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.FIREWORK_PARTICLES) {
         return this.fireworkParticles ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.DRIPPING_WATER_LAVA) {
         return this.drippingWaterLava ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_TERRAIN) {
         return this.animatedTerrain ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.ANIMATED_TEXTURES) {
         return this.animatedTextures ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.RAIN_SPLASH) {
         return this.rainSplash ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.LAGOMETER) {
         return this.lagometer ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SHOW_FPS) {
         return this.showFps ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.AUTOSAVE_TICKS) {
         return this.autoSaveTicks <= 40
            ? label + "Default (2s)"
            : (this.autoSaveTicks <= 400 ? label + "20s" : (this.autoSaveTicks <= 4000 ? label + "3min" : label + "30min"));
      } else if (option == GameSettings.Options.BETTER_GRASS) {
         switch (this.betterGrass) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            default:
               return label + "OFF";
         }
      } else if (option == GameSettings.Options.CONNECTED_TEXTURES) {
         switch (this.connectedTextures) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            default:
               return label + "OFF";
         }
      } else if (option == GameSettings.Options.WEATHER) {
         return this.weather ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SKY) {
         return this.sky ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.STARS) {
         return this.stars ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SUN_MOON) {
         return this.sunMoon ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.VIGNETTE) {
         switch (this.vignette) {
            case 1:
               return label + "Fast";
            case 2:
               return label + "Fancy";
            default:
               return label + "Default";
         }
      } else if (option == GameSettings.Options.CHUNK_UPDATES) {
         return label + this.chunkUpdates;
      } else if (option == GameSettings.Options.CHUNK_LOADING) {
         return this.chunkLoading == 1 ? label + "Smooth" : (this.chunkLoading == 2 ? label + "Multi-Core" : label + "Default");
      } else if (option == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         return this.chunkUpdatesDynamic ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.TIME) {
         return this.timeOfDay == 1 ? label + "Day Only" : (this.timeOfDay == 3 ? label + "Night Only" : label + "Default");
      } else if (option == GameSettings.Options.CLEAR_WATER) {
         return this.clearWater ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.AA_LEVEL) {
         String restartSuffix = "";
         if (this.aaLevel != Config.getAntialiasingLevel()) {
            restartSuffix = " (restart)";
         }

         return this.aaLevel == 0 ? label + "OFF" + restartSuffix : label + this.aaLevel + restartSuffix;
      } else if (option == GameSettings.Options.AF_LEVEL) {
         return this.anisotropicLevel == 1 ? label + "OFF" : label + this.anisotropicLevel;
      } else if (option == GameSettings.Options.PROFILER) {
         return this.profiler ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.BETTER_SNOW) {
         return this.betterSnow ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SWAMP_COLORS) {
         return this.swampColors ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.RANDOM_MOBS) {
         return this.randomMobs ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SMOOTH_BIOMES) {
         return this.smoothBiomes ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.CUSTOM_FONTS) {
         return this.customFonts ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.CUSTOM_COLORS) {
         return this.customColors ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.CUSTOM_SKY) {
         return this.customSky ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.SHOW_CAPES) {
         return this.showCapes ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.NATURAL_TEXTURES) {
         return this.naturalTextures ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.FAST_MATH) {
         return this.fastMath ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.FAST_RENDER) {
         return this.fastRender ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.TRANSLUCENT_BLOCKS) {
         return this.translucentBlocks == 1 ? label + "Fast" : (this.translucentBlocks == 2 ? label + "Fancy" : label + "Default");
      } else if (option == GameSettings.Options.LAZY_CHUNK_LOADING) {
         return this.lazyChunkLoading ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.FULLSCREEN_MODE) {
         return label + this.fullscreenMode;
      } else if (option == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         return this.heldItemTooltips ? label + "ON" : label + "OFF";
      } else if (option == GameSettings.Options.FRAMERATE_LIMIT) {
         float optionFloat = this.getOptionFloatValue(option);
         return optionFloat == 0.0F
            ? label + "VSync"
            : (optionFloat == GameSettings.Options.getValueMaxOf(option) ? label + I18n.format("options.framerateLimit.max") : label + (int)optionFloat + " fps");
      } else {
         return null;
      }
   }

   public void loadOptions() {
      try {
         if (!this.optionsFile.exists()) {
            return;
         }

         BufferedReader reader = new BufferedReader(new ConfigReader(this.optionsFile));
         String line = "";
         this.mapSoundLevels.clear();

         while ((line = reader.readLine()) != null) {
            try {
               String[] parts = line.split(":");
               if (parts[0].equals("mouseSensitivity")) {
                  this.mouseSensitivity = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("fov")) {
                  this.fov = this.parseFloat(parts[1]) * 40.0F + 70.0F;
               }

               if (parts[0].equals("gamma")) {
                  this.gamma = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("saturation")) {
                  this.saturation = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("invertYMouse")) {
                  this.invertMouse = parts[1].equals("true");
               }

               if (parts[0].equals("renderDistance")) {
                  this.renderDistanceChunks = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("guiScale")) {
                  this.guiScale = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("particles")) {
                  this.particleSetting = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("bobView")) {
                  this.viewBobbing = parts[1].equals("true");
               }

               if (parts[0].equals("anaglyph3d")) {
                  this.anaglyph = parts[1].equals("true");
               }

               if (parts[0].equals("maxFps")) {
                  this.maxFps = Integer.parseInt(parts[1]);
                  this.enableVsync = false;
                  if (this.maxFps <= 0) {
                     this.maxFps = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                     this.enableVsync = true;
                  }

                  this.updateVsync();
               }

               if (parts[0].equals("fboEnable")) {
                  this.fboEnable = parts[1].equals("true");
               }

               if (parts[0].equals("difficulty")) {
                  this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(parts[1]));
               }

               if (parts[0].equals("fancyGraphics")) {
                  this.fancyGraphics = parts[1].equals("true");
                  this.updateClouds();
               }

               if (parts[0].equals("ao")) {
                  if (parts[1].equals("true")) {
                     this.ambientOcclusion = 2;
                  } else if (parts[1].equals("false")) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(parts[1]);
                  }
               }

               if (parts[0].equals("renderClouds")) {
                  if (parts[1].equals("true")) {
                     this.clouds = 2;
                  } else if (parts[1].equals("false")) {
                     this.clouds = 0;
                  } else if (parts[1].equals("fast")) {
                     this.clouds = 1;
                  }
               }

               if (parts[0].equals("resourcePacks")) {
                  this.resourcePacks = gson.fromJson(line.substring(line.indexOf(58) + 1), typeListString);
                  if (this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if (parts[0].equals("incompatibleResourcePacks")) {
                  this.incompatibleResourcePacks = gson.fromJson(line.substring(line.indexOf(58) + 1), typeListString);
                  if (this.incompatibleResourcePacks == null) {
                     this.incompatibleResourcePacks = Lists.newArrayList();
                  }
               }

               if (parts[0].equals("lastServer") && parts.length >= 2) {
                  this.lastServer = line.substring(line.indexOf(58) + 1);
               }

               if (parts[0].equals("lang") && parts.length >= 2) {
                  this.language = parts[1];
               }

               if (parts[0].equals("chatVisibility")) {
                  this.chatVisibility = EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(parts[1]));
               }

               if (parts[0].equals("chatColors")) {
                  this.chatColours = parts[1].equals("true");
               }

               if (parts[0].equals("chatLinks")) {
                  this.chatLinks = parts[1].equals("true");
               }

               if (parts[0].equals("chatLinksPrompt")) {
                  this.chatLinksPrompt = parts[1].equals("true");
               }

               if (parts[0].equals("chatOpacity")) {
                  this.chatOpacity = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("snooperEnabled")) {
                  this.snooperEnabled = parts[1].equals("true");
               }

               if (parts[0].equals("fullscreen")) {
                  this.fullScreen = parts[1].equals("true");
               }

               if (parts[0].equals("enableVsync")) {
                  this.enableVsync = parts[1].equals("true");
                  this.updateVsync();
               }

               if (parts[0].equals("useVbo")) {
                  this.useVbo = parts[1].equals("true");
               }

               if (parts[0].equals("hideServerAddress")) {
                  this.hideServerAddress = parts[1].equals("true");
               }

               if (parts[0].equals("advancedItemTooltips")) {
                  this.advancedItemTooltips = parts[1].equals("true");
               }

               if (parts[0].equals("pauseOnLostFocus")) {
                  this.pauseOnLostFocus = parts[1].equals("true");
               }

               if (parts[0].equals("touchscreen")) {
                  this.touchscreen = parts[1].equals("true");
               }

               if (parts[0].equals("overrideHeight")) {
                  this.overrideHeight = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("overrideWidth")) {
                  this.overrideWidth = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("heldItemTooltips")) {
                  this.heldItemTooltips = parts[1].equals("true");
               }

               if (parts[0].equals("chatHeightFocused")) {
                  this.chatHeightFocused = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("chatHeightUnfocused")) {
                  this.chatHeightUnfocused = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("chatScale")) {
                  this.chatScale = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("chatWidth")) {
                  this.chatWidth = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("showInventoryAchievementHint")) {
                  this.showInventoryAchievementHint = parts[1].equals("true");
               }

               if (parts[0].equals("mipmapLevels")) {
                  this.mipmapLevels = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("streamBytesPerPixel")) {
                  this.streamBytesPerPixel = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("streamMicVolume")) {
                  this.streamMicVolume = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("streamSystemVolume")) {
                  this.streamSystemVolume = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("streamKbps")) {
                  this.streamKbps = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("streamFps")) {
                  this.streamFps = this.parseFloat(parts[1]);
               }

               if (parts[0].equals("streamCompression")) {
                  this.streamCompression = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("streamSendMetadata")) {
                  this.streamSendMetadata = parts[1].equals("true");
               }

               if (parts[0].equals("streamPreferredServer") && parts.length >= 2) {
                  this.streamPreferredServer = line.substring(line.indexOf(58) + 1);
               }

               if (parts[0].equals("streamChatEnabled")) {
                  this.streamChatEnabled = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("streamChatUserFilter")) {
                  this.streamChatUserFilter = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("streamMicToggleBehavior")) {
                  this.streamMicToggleBehavior = Integer.parseInt(parts[1]);
               }

               if (parts[0].equals("forceUnicodeFont")) {
                  this.forceUnicodeFont = parts[1].equals("true");
               }

               if (parts[0].equals("allowBlockAlternatives")) {
                  this.allowBlockAlternatives = parts[1].equals("true");
               }

               if (parts[0].equals("reducedDebugInfo")) {
                  this.reducedDebugInfo = parts[1].equals("true");
               }

               if (parts[0].equals("useNativeTransport")) {
                  this.useNativeTransport = parts[1].equals("true");
               }

               if (parts[0].equals("entityShadows")) {
                  this.entityShadows = parts[1].equals("true");
               }

               for (KeyBinding keyBinding : this.keyBindings) {
                  if (parts[0].equals("key_" + keyBinding.AOr())) {
                     keyBinding.setKeyCode(Integer.parseInt(parts[1]));
                  }
               }

               SoundCategory[] soundCategories = SoundCategory.values();
               int count = soundCategories.length;
               int index = 0;

               while (index < count) {
                  SoundCategory soundCategory = soundCategories[index];
                  if (parts[0].equals("soundCategory_" + soundCategory.getCategoryName())) {
                     this.mapSoundLevels.put(soundCategory, this.parseFloat(parts[1]));
                  }

                  index -= 47;
                  index += 48;
               }

               EnumPlayerModelParts[] modelParts = EnumPlayerModelParts.values();
               count = modelParts.length;
               index = 0;

               while (index < count) {
                  EnumPlayerModelParts modelPart = modelParts[index];
                  if (parts[0].equals("modelPart_" + modelPart.getPartName())) {
                     this.setModelPartEnabled(modelPart, parts[1].equals("true"));
                  }

                  index += 33;
                  index -= 32;
               }
            } catch (Exception optionException) {
               logger.warn("Skipping bad option: " + line);
               optionException.printStackTrace();
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         reader.close();
      } catch (Exception loadException) {
         logger.error("Failed to load options", loadException);
      }

      this.loadOfOptions();
   }

   public float getSoundLevel(SoundCategory category) {
      return this.mapSoundLevels.containsKey(category) ? this.mapSoundLevels.get(category) : 1.0F;
   }

   public void sendSettingsToServer() {
      if (this.mc.thePlayer != null) {
         int modelPartFlags = 0;

         for (Object modelPartObject : this.setModelParts) {
            modelPartFlags |= ((EnumPlayerModelParts)modelPartObject).JPo();
         }

         this.mc
            .thePlayer
            .sendQueue
            .sendPacket(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, modelPartFlags));
      }
   }

   public static enum Options {
      INVERT_MOUSE("INVERT_MOUSE", 0, "options.invertMouse", false, true),
      SENSITIVITY("SENSITIVITY", 1, "options.sensitivity", true, false),
      FOV("FOV", 2, "options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("GAMMA", 3, "options.gamma", true, false),
      SATURATION("SATURATION", 4, "options.saturation", true, false),
      RENDER_DISTANCE("RENDER_DISTANCE", 5, "options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("VIEW_BOBBING", 6, "options.viewBobbing", false, true),
      ANAGLYPH("ANAGLYPH", 7, "options.anaglyph", false, true),
      FRAMERATE_LIMIT("FRAMERATE_LIMIT", 8, "options.framerateLimit", true, false, 0.0F, 360.0F, 5.0F),
      FBO_ENABLE("FBO_ENABLE", 9, "options.fboEnable", false, true),
      RENDER_CLOUDS("RENDER_CLOUDS", 10, "options.renderClouds", false, false),
      GRAPHICS("GRAPHICS", 11, "options.graphics", false, false),
      AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 12, "options.ao", false, false),
      GUI_SCALE("GUI_SCALE", 13, "options.guiScale", false, false),
      PARTICLES("PARTICLES", 14, "options.particles", false, false),
      CHAT_VISIBILITY("CHAT_VISIBILITY", 15, "options.chat.visibility", false, false),
      CHAT_COLOR("CHAT_COLOR", 16, "options.chat.color", false, true),
      CHAT_LINKS("CHAT_LINKS", 17, "options.chat.links", false, true),
      CHAT_OPACITY("CHAT_OPACITY", 18, "options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("CHAT_LINKS_PROMPT", 19, "options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("SNOOPER_ENABLED", 20, "options.snooper", false, true),
      USE_FULLSCREEN("USE_FULLSCREEN", 21, "options.fullscreen", false, true),
      ENABLE_VSYNC("ENABLE_VSYNC", 22, "options.vsync", false, true),
      USE_VBO("USE_VBO", 23, "options.vbo", false, true),
      TOUCHSCREEN("TOUCHSCREEN", 24, "options.touchscreen", false, true),
      CHAT_SCALE("CHAT_SCALE", 25, "options.chat.scale", true, false),
      CHAT_WIDTH("CHAT_WIDTH", 26, "options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("CHAT_HEIGHT_FOCUSED", 27, "options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("CHAT_HEIGHT_UNFOCUSED", 28, "options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("MIPMAP_LEVELS", 29, "options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("FORCE_UNICODE_FONT", 30, "options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("STREAM_BYTES_PER_PIXEL", 31, "options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("STREAM_VOLUME_MIC", 32, "options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("STREAM_VOLUME_SYSTEM", 33, "options.stream.systemVolume", true, false),
      STREAM_KBPS("STREAM_KBPS", 34, "options.stream.kbps", true, false),
      STREAM_FPS("STREAM_FPS", 35, "options.stream.fps", true, false),
      STREAM_COMPRESSION("STREAM_COMPRESSION", 36, "options.stream.compression", false, false),
      STREAM_SEND_METADATA("STREAM_SEND_METADATA", 37, "options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("STREAM_CHAT_ENABLED", 38, "options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("STREAM_CHAT_USER_FILTER", 39, "options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("STREAM_MIC_TOGGLE_BEHAVIOR", 40, "options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("BLOCK_ALTERNATIVES", 41, "options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("REDUCED_DEBUG_INFO", 42, "options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("ENTITY_SHADOWS", 43, "options.entityShadows", false, true),
      FOG_FANCY("FOG", 999, "Fog", false, false),
      FOG_START("", 999, "Fog Start", false, false),
      MIPMAP_TYPE("", 999, "Mipmap Type", true, false, 0.0F, 3.0F, 1.0F),
      SMOOTH_FPS("", 999, "Smooth FPS", false, false),
      CLOUDS("", 999, "Clouds", false, false),
      CLOUD_HEIGHT("", 999, "Cloud Height", true, false),
      TREES("", 999, "Trees", false, false),
      RAIN("", 999, "Rain & Snow", false, false),
      ANIMATED_WATER("", 999, "Water Animated", false, false),
      ANIMATED_LAVA("", 999, "Lava Animated", false, false),
      ANIMATED_FIRE("", 999, "Fire Animated", false, false),
      ANIMATED_PORTAL("", 999, "Portal Animated", false, false),
      AO_LEVEL("", 999, "Smooth Lighting Level", true, false),
      LAGOMETER("", 999, "Lagometer", false, false),
      SHOW_FPS("", 999, "Show FPS", false, false),
      AUTOSAVE_TICKS("", 999, "Autosave", false, false),
      BETTER_GRASS("", 999, "Better Grass", false, false),
      ANIMATED_REDSTONE("", 999, "Redstone Animated", false, false),
      ANIMATED_EXPLOSION("", 999, "Explosion Animated", false, false),
      ANIMATED_FLAME("", 999, "Flame Animated", false, false),
      ANIMATED_SMOKE("", 999, "Smoke Animated", false, false),
      WEATHER("", 999, "Weather", false, false),
      SKY("", 999, "Sky", false, false),
      STARS("", 999, "Stars", false, false),
      SUN_MOON("", 999, "Sun & Moon", false, false),
      VIGNETTE("", 999, "Vignette", false, false),
      CHUNK_UPDATES("", 999, "Chunk Updates", false, false),
      CHUNK_UPDATES_DYNAMIC("", 999, "Dynamic Updates", false, false),
      TIME("", 999, "Time", false, false),
      CLEAR_WATER("", 999, "Clear Water", false, false),
      SMOOTH_WORLD("", 999, "Smooth World", false, false),
      VOID_PARTICLES("", 999, "Void Particles", false, false),
      WATER_PARTICLES("", 999, "Water Particles", false, false),
      RAIN_SPLASH("", 999, "Rain Splash", false, false),
      PORTAL_PARTICLES("", 999, "Portal Particles", false, false),
      POTION_PARTICLES("", 999, "Potion Particles", false, false),
      FIREWORK_PARTICLES("", 999, "Firework Particles", false, false),
      PROFILER("", 999, "Debug Profiler", false, false),
      DRIPPING_WATER_LAVA("", 999, "Dripping Water/Lava", false, false),
      BETTER_SNOW("", 999, "Better Snow", false, false),
      FULLSCREEN_MODE("", 999, "Fullscreen Mode", false, false),
      ANIMATED_TERRAIN("", 999, "Terrain Animated", false, false),
      SWAMP_COLORS("", 999, "Swamp Colors", false, false),
      RANDOM_MOBS("", 999, "Random Mobs", false, false),
      SMOOTH_BIOMES("", 999, "Smooth Biomes", false, false),
      CUSTOM_FONTS("", 999, "Custom Fonts", false, false),
      CUSTOM_COLORS("", 999, "Custom Colors", false, false),
      SHOW_CAPES("", 999, "Show Capes", false, false),
      CONNECTED_TEXTURES("", 999, "Connected Textures", false, false),
      AA_LEVEL("", 999, "Antialiasing", true, false, 0.0F, 16.0F, 1.0F),
      AF_LEVEL("", 999, "Anisotropic Filtering", true, false, 1.0F, 16.0F, 1.0F),
      ANIMATED_TEXTURES("", 999, "Textures Animated", false, false),
      NATURAL_TEXTURES("", 999, "Natural Textures", false, false),
      CHUNK_LOADING("", 999, "Chunk Loading", false, false),
      HELD_ITEM_TOOLTIPS("", 999, "Held Item Tooltips", false, false),
      DROPPED_ITEMS("", 999, "Dropped Items", false, false),
      LAZY_CHUNK_LOADING("", 999, "Lazy Chunk Loading", false, false),
      CUSTOM_SKY("", 999, "Custom Sky", false, false),
      FAST_MATH("", 999, "Fast Math", false, false),
      FAST_RENDER("", 999, "Fast Render", false, false),
      TRANSLUCENT_BLOCKS("", 999, "Translucent Blocks", false, false);

      public float valueStep;
      public static String obfuscationId = "CL_00000653";
      public static GameSettings.Options[] enumValues = new GameSettings.Options[]{
         GameSettings.Options.INVERT_MOUSE,
         GameSettings.Options.SENSITIVITY,
         GameSettings.Options.FOV,
         GameSettings.Options.GAMMA,
         GameSettings.Options.SATURATION,
         GameSettings.Options.RENDER_DISTANCE,
         VIEW_BOBBING,
         ANAGLYPH,
         GameSettings.Options.FRAMERATE_LIMIT,
         GameSettings.Options.FBO_ENABLE,
         GameSettings.Options.RENDER_CLOUDS,
         GameSettings.Options.GRAPHICS,
         GameSettings.Options.AMBIENT_OCCLUSION,
         GameSettings.Options.GUI_SCALE,
         GameSettings.Options.PARTICLES,
         GameSettings.Options.CHAT_VISIBILITY,
         GameSettings.Options.CHAT_COLOR,
         CHAT_LINKS,
         CHAT_OPACITY,
         GameSettings.Options.CHAT_LINKS_PROMPT,
         SNOOPER_ENABLED,
         GameSettings.Options.USE_FULLSCREEN,
         GameSettings.Options.ENABLE_VSYNC,
         GameSettings.Options.USE_VBO,
         GameSettings.Options.TOUCHSCREEN,
         GameSettings.Options.CHAT_SCALE,
         GameSettings.Options.CHAT_WIDTH,
         GameSettings.Options.CHAT_HEIGHT_FOCUSED,
         GameSettings.Options.CHAT_HEIGHT_UNFOCUSED,
         MIPMAP_LEVELS,
         GameSettings.Options.FORCE_UNICODE_FONT,
         GameSettings.Options.STREAM_BYTES_PER_PIXEL,
         GameSettings.Options.STREAM_VOLUME_MIC,
         GameSettings.Options.STREAM_VOLUME_SYSTEM,
         GameSettings.Options.STREAM_KBPS,
         STREAM_FPS,
         GameSettings.Options.STREAM_COMPRESSION,
         GameSettings.Options.STREAM_SEND_METADATA,
         GameSettings.Options.STREAM_CHAT_ENABLED,
         GameSettings.Options.STREAM_CHAT_USER_FILTER,
         STREAM_MIC_TOGGLE_BEHAVIOR,
         GameSettings.Options.BLOCK_ALTERNATIVES,
         GameSettings.Options.REDUCED_DEBUG_INFO,
         GameSettings.Options.ENTITY_SHADOWS
      };
      public float valueMin;
      public boolean enumBoolean;
      public float valueMax;
      public static GameSettings.Options[] enumValuesCopy = new GameSettings.Options[]{
         INVERT_MOUSE,
         SENSITIVITY,
         FOV,
         GameSettings.Options.GAMMA,
         GameSettings.Options.SATURATION,
         RENDER_DISTANCE,
         VIEW_BOBBING,
         ANAGLYPH,
         FRAMERATE_LIMIT,
         FBO_ENABLE,
         RENDER_CLOUDS,
         GRAPHICS,
         GameSettings.Options.AMBIENT_OCCLUSION,
         GUI_SCALE,
         PARTICLES,
         CHAT_VISIBILITY,
         CHAT_COLOR,
         CHAT_LINKS,
         CHAT_OPACITY,
         CHAT_LINKS_PROMPT,
         SNOOPER_ENABLED,
         USE_FULLSCREEN,
         GameSettings.Options.ENABLE_VSYNC,
         USE_VBO,
         TOUCHSCREEN,
         GameSettings.Options.CHAT_SCALE,
         GameSettings.Options.CHAT_WIDTH,
         CHAT_HEIGHT_FOCUSED,
         CHAT_HEIGHT_UNFOCUSED,
         MIPMAP_LEVELS,
         FORCE_UNICODE_FONT,
         GameSettings.Options.STREAM_BYTES_PER_PIXEL,
         STREAM_VOLUME_MIC,
         STREAM_VOLUME_SYSTEM,
         GameSettings.Options.STREAM_KBPS,
         STREAM_FPS,
         STREAM_COMPRESSION,
         STREAM_SEND_METADATA,
         STREAM_CHAT_ENABLED,
         STREAM_CHAT_USER_FILTER,
         STREAM_MIC_TOGGLE_BEHAVIOR,
         GameSettings.Options.BLOCK_ALTERNATIVES,
         REDUCED_DEBUG_INFO,
         ENTITY_SHADOWS,
         FOG_FANCY,
         FOG_START,
         MIPMAP_TYPE,
         GameSettings.Options.SMOOTH_FPS,
         CLOUDS,
         GameSettings.Options.CLOUD_HEIGHT,
         TREES,
         GameSettings.Options.RAIN,
         GameSettings.Options.ANIMATED_WATER,
         ANIMATED_LAVA,
         ANIMATED_FIRE,
         ANIMATED_PORTAL,
         AO_LEVEL,
         GameSettings.Options.LAGOMETER,
         SHOW_FPS,
         AUTOSAVE_TICKS,
         BETTER_GRASS,
         GameSettings.Options.ANIMATED_REDSTONE,
         ANIMATED_EXPLOSION,
         ANIMATED_FLAME,
         ANIMATED_SMOKE,
         WEATHER,
         SKY,
         GameSettings.Options.STARS,
         SUN_MOON,
         VIGNETTE,
         GameSettings.Options.CHUNK_UPDATES,
         CHUNK_UPDATES_DYNAMIC,
         TIME,
         CLEAR_WATER,
         SMOOTH_WORLD,
         GameSettings.Options.VOID_PARTICLES,
         WATER_PARTICLES,
         RAIN_SPLASH,
         PORTAL_PARTICLES,
         POTION_PARTICLES,
         FIREWORK_PARTICLES,
         PROFILER,
         DRIPPING_WATER_LAVA,
         BETTER_SNOW,
         GameSettings.Options.FULLSCREEN_MODE,
         GameSettings.Options.ANIMATED_TERRAIN,
         SWAMP_COLORS,
         RANDOM_MOBS,
         SMOOTH_BIOMES,
         CUSTOM_FONTS,
         CUSTOM_COLORS,
         SHOW_CAPES,
         CONNECTED_TEXTURES,
         AA_LEVEL,
         AF_LEVEL,
         ANIMATED_TEXTURES,
         NATURAL_TEXTURES,
         CHUNK_LOADING,
         HELD_ITEM_TOOLTIPS,
         DROPPED_ITEMS,
         LAZY_CHUNK_LOADING,
         GameSettings.Options.CUSTOM_SKY,
         FAST_MATH,
         GameSettings.Options.FAST_RENDER,
         TRANSLUCENT_BLOCKS
      };
      public String enumString;
      public boolean enumFloat;

      Options(String enumName, int enumOrdinal, String translationKey, boolean isBoolean, boolean isFloat) {
         this(enumName, enumOrdinal, translationKey, isBoolean, isFloat, 0.0F, 1.0F, 0.0F);
      }

      public String getEnumString() {
         return this.enumString;
      }

      public float getValueMax() {
         return this.valueMax;
      }

      public float snapToStep(float value) {
         if (this.valueStep > 0.0F) {
            value = this.valueStep * (float)Math.round(value / this.valueStep);
         }

         return value;
      }

      public static GameSettings.Options getEnumOptions(int enumOrdinal) {
         GameSettings.Options[] optionValues = values();
         int optionCount = optionValues.length;
         int index = 0;

         while (index < optionCount) {
            GameSettings.Options option = optionValues[index];
            if (option.returnEnumOrdinal() == enumOrdinal) {
               return option;
            }

            index += 7;
            index -= 6;
         }

         return null;
      }

      public int returnEnumOrdinal() {
         return this.ordinal();
      }

      public float snapToStepClamp(float value) {
         value = this.snapToStep(value);
         return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
      }

      public void setValueMax(float newValueMax) {
         this.valueMax = newValueMax;
      }

      public float normalizeValue(float value) {
         return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
      }

      public static float getValueMinOf(GameSettings.Options option) {
         return option.valueMin;
      }

      Options(String enumName, int enumOrdinal, String translationKey, boolean isBoolean, boolean isFloat, float minValue, float maxValue, float stepValue) {
         this.enumString = translationKey;
         this.enumBoolean = isBoolean;
         this.enumFloat = isFloat;
         this.valueMin = minValue;
         this.valueMax = maxValue;
         this.valueStep = stepValue;
      }

      public float denormalizeValue(float normalizedValue) {
         return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(normalizedValue, 0.0F, 1.0F));
      }

      public boolean getEnumFloat() {
         return this.enumFloat;
      }

      public static float getValueMaxOf(GameSettings.Options option) {
         return option.valueMax;
      }

      public boolean getEnumBoolean() {
         return this.enumBoolean;
      }
   }
}
