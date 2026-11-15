package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;
import kawaii.event.HandlerMap;
import kawaii.render.ImageCanvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import optfine.Config;
import optfine.ConnectedTextures;
import optfine.Reflector;
import optfine.TextureUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   public static Logger logger = LogManager.getLogger();
   public IIconRegister iconCreator;
   public static ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
   public TextureAtlasSprite missingImage;
   public List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   public static ResourceLocation locationMissingTexture = new ResourceLocation("missingno");
   public static String logName = "CL_00001058";
   public String basePath;
   public Map<String, TextureAtlasSprite> mapRegisteredSprites = Maps.newHashMap();
   public Map<String, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   public int mipmapLevels;

   public int getMinSpriteSize() {
      int spriteSize = 1 << this.mipmapLevels;
      if (spriteSize < 16) {
         spriteSize = 16;
      }

      return spriteSize;
   }

   public int getCountRegisteredSprites() {
      return this.mapRegisteredSprites.size();
   }

   public TextureAtlasSprite getTextureExtry(String textureName) {
      ResourceLocation location = new ResourceLocation(textureName);
      return this.mapRegisteredSprites.get(location.toString());
   }

   public ResourceLocation completeResourceLocation(ResourceLocation baseLocation, int mipmapLevel) {
      return this.isSpecialResource(baseLocation)
         ? (mipmapLevel == 0 ? new ResourceLocation(baseLocation.ebr(), baseLocation.gHr() + ".png") : new ResourceLocation(baseLocation.ebr(), baseLocation.gHr() + "mipmap" + mipmapLevel + ".png"))
         : (
            mipmapLevel == 0
               ? new ResourceLocation(baseLocation.ebr(), String.format("%s/%s%s", this.basePath, baseLocation.gHr(), ".png"))
               : new ResourceLocation(baseLocation.ebr(), String.format("%s/mipmaps/%s.%d%s", this.basePath, baseLocation.gHr(), mipmapLevel, ".png"))
         );
   }

   public int computeMipmapLevel(Map sprites, IResourceManager resourceManager) {
      int spriteSize = this.computeSpriteSize(sprites, resourceManager, 20);
      if (spriteSize < 16) {
         spriteSize = 16;
      }

      spriteSize = MathHelper.jir(spriteSize);
      if (spriteSize > 16) {
         Config.log("Sprite size: " + spriteSize);
      }

      int mipmapLevel = MathHelper.calculateLogBaseTwoDeBruijn(spriteSize);
      if (mipmapLevel < 4) {
         mipmapLevel = 4;
      }

      return mipmapLevel;
   }

   @Override
   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int levels) {
      this.mipmapLevels = levels;
   }

   public boolean isAnimationEnabled(TextureAtlasSprite sprite) {
      return sprite == TextureUtils.iconWaterStill || sprite == TextureUtils.iconWaterFlow
         ? Config.isAnimatedWater()
         : (
            sprite != TextureUtils.iconLavaStill && sprite != TextureUtils.iconLavaFlow
               ? (
                  sprite != TextureUtils.iconFireLayer0 && sprite != TextureUtils.iconFireLayer1
                     ? (sprite == TextureUtils.iconPortal ? Config.isAnimatedPortal() : Config.isAnimatedTerrain())
                     : Config.isAnimatedFire()
               )
               : Config.isAnimatedLava()
         );
   }

   public TextureAtlasSprite getRegisteredSprite(String spriteName) {
      ResourceLocation location = new ResourceLocation(spriteName);
      return this.mapRegisteredSprites.get(location.toString());
   }

   public TextureMap(String atlasPath) {
      this(atlasPath, (IIconRegister)null);
   }

   // $VF: Could not inline inconsistent finally blocks
   // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
   public void loadTextureAtlas(IResourceManager resourceManager) {
      Config.dbg("Multitexture: " + Config.isMultiTexture());
      if (Config.isMultiTexture()) {
         for (Object uploadedSprite : this.mapUploadedSprites.values()) {
            ((TextureAtlasSprite)uploadedSprite).Pbo();
         }
      }

      ConnectedTextures.updateIcons(this);
      int maxTextureSize = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(maxTextureSize, maxTextureSize, true, 0, this.mipmapLevels);
      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      int minDimension = Integer.MAX_VALUE;
      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPre, this);
      int minSpriteSize = this.getMinSpriteSize();
      int sizeLimit = 1 << this.mipmapLevels;

      for (Object entry : this.mapRegisteredSprites.entrySet()) {
         TextureAtlasSprite sprite = (TextureAtlasSprite)((Entry)entry).getValue();
         ResourceLocation spriteLocation = new ResourceLocation(sprite.DAr());
         ResourceLocation fileLocation = this.completeResourceLocation(spriteLocation, 0);
         if (!sprite.cmr(resourceManager, spriteLocation)) {
            try {
               IResource resource = resourceManager.getResource(fileLocation);
               BufferedImage[] images = new BufferedImage[1 + this.mipmapLevels];
               images[0] = TextureUtil.readBufferedImage(resource.getInputStream());
               if (this.mipmapLevels > 0 && images != null) {
                  int originalWidth = images[0].getWidth();
                  images[0] = TextureUtils.scaleToPowerOfTwo(images[0], minSpriteSize);
                  int scaledWidth = images[0].getWidth();
                  if (!TextureUtils.isPowerOfTwo(originalWidth)) {
                     Config.log("Scaled non power of 2: " + sprite.DAr() + ", " + originalWidth + " -> " + scaledWidth);
                  }
               }

               TextureMetadataSection metadataSection = (TextureMetadataSection)resource.rQo("texture");
               if (metadataSection != null) {
                  List mipmapList = metadataSection.getListMipmaps();
                  if (!mipmapList.isEmpty()) {
                     int sourceWidth = images[0].getWidth();
                     int sourceHeight = images[0].getHeight();
                     if (MathHelper.jir(sourceWidth) != sourceWidth || MathHelper.jir(sourceHeight) != sourceHeight) {
                        throw new RuntimeException("Unable to load extra miplevels, source-texture is not power of two");
                     }
                  }

                  for (int mipLevel : (Iterable<Integer>)(Iterable)mipmapList) {
                     if (mipLevel > 0 && mipLevel < images.length - 1 && images[mipLevel] == null) {
                        ResourceLocation mipLocation = this.completeResourceLocation(spriteLocation, mipLevel);

                        try {
                           images[mipLevel] = TextureUtil.readBufferedImage(resourceManager.getResource(mipLocation).getInputStream());
                        } catch (Exception mipException) {
                           logger.error("Unable to load miplevel {} from: {}", mipLevel, mipLocation, mipException);
                        }
                     }
                  }
               }

               AnimationMetadataSection animationSection = (AnimationMetadataSection)resource.rQo("animation");
               sprite.loadSprite(images, animationSection);
            } catch (RuntimeException parseException) {
               logger.error("Unable to parse metadata from " + fileLocation, parseException);
               continue;
            } catch (Exception loadException) {
               logger.error("Using missing texture, unable to load " + fileLocation + ", " + loadException.getClass().getName());
               continue;
            }

            minDimension = Math.min(minDimension, Math.min(sprite.$No(), sprite.jJ()));
            int spriteSizeLimit = Math.min(Integer.lowestOneBit(sprite.$No()), Integer.lowestOneBit(sprite.jJ()));
            if (spriteSizeLimit < sizeLimit) {
               logger.warn(
                  "Texture {} with size {}x{} limits mip level from {} to {}",
                  fileLocation,
                  sprite.$No(),
                  sprite.jJ(),
                  MathHelper.calculateLogBaseTwoDeBruijn(sizeLimit),
                  MathHelper.calculateLogBaseTwoDeBruijn(spriteSizeLimit)
               );
               sizeLimit = spriteSizeLimit;
            }

            stitcher.addSprite(sprite);
         } else if (!sprite.LVr(resourceManager, spriteLocation)) {
            minDimension = Math.min(minDimension, Math.min(sprite.$No(), sprite.jJ()));
            stitcher.addSprite(sprite);
         }
      }

      int minPowerOfTwo = Math.min(minDimension, sizeLimit);
      int allowedMipLevel = MathHelper.calculateLogBaseTwoDeBruijn(minPowerOfTwo);
      if (allowedMipLevel < 0) {
         allowedMipLevel = 0;
      }

      if (allowedMipLevel < this.mipmapLevels) {
         logger.info("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, allowedMipLevel, minPowerOfTwo);
         this.mipmapLevels = allowedMipLevel;
      }

      for (Object registeredObject : this.mapRegisteredSprites.values()) {
         TextureAtlasSprite registeredSprite = (TextureAtlasSprite)registeredObject;

         try {
            registeredSprite.generateMipmaps(this.mipmapLevels);
         } catch (Throwable mipmapThrowable) {
            CrashReport crashReport = CrashReport.makeCrashReport(mipmapThrowable, "Applying mipmap");
            CrashReportCategory crashCategory = crashReport.makeCategory("Sprite being mipmapped");
            crashCategory.addCrashSectionCallable("Sprite name", new TextureMap$1(this, registeredSprite));
            crashCategory.addCrashSectionCallable("Sprite size", new TextureMap$2(this, registeredSprite));
            crashCategory.addCrashSectionCallable("Sprite frames", new TextureMap$3(this, registeredSprite));
            crashCategory.addCrashSection("Mipmap levels", this.mipmapLevels);
            throw new ReportedException(crashReport);
         }
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);

      try {
         stitcher.doStitch();
      } catch (StitcherException stitcherException) {
         throw stitcherException;
      }

      logger.info("Created: {}x{} {}-atlas", stitcher.njr(), stitcher.Hro(), this.basePath);
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.njr(), stitcher.Hro());
      HashMap unstitchedSprites = Maps.newHashMap(this.mapRegisteredSprites);

      for (Object stitchedObject : stitcher.getStichSlots()) {
         TextureAtlasSprite stitchedSprite = (TextureAtlasSprite)stitchedObject;
         String spriteName = stitchedSprite.DAr();
         unstitchedSprites.remove(spriteName);
         this.mapUploadedSprites.put(spriteName, stitchedSprite);

         try {
            TextureUtil.uploadTextureMipmap(stitchedSprite.getFrameTextureData(0), stitchedSprite.$No(), stitchedSprite.jJ(), stitchedSprite.lGo(), stitchedSprite.alo(), false, false);
         } catch (Throwable uploadThrowable) {
            CrashReport uploadCrashReport = CrashReport.makeCrashReport(uploadThrowable, "Stitching texture atlas");
            CrashReportCategory uploadCategory = uploadCrashReport.makeCategory("Texture being stitched together");
            uploadCategory.addCrashSection("Atlas path", this.basePath);
            uploadCategory.addCrashSection("Sprite", stitchedSprite);
            throw new ReportedException(uploadCrashReport);
         }

         if (stitchedSprite.hasAnimationMetadata()) {
            this.listAnimatedSprites.add(stitchedSprite);
         }
      }

      for (Object unstitchedObject : unstitchedSprites.values()) {
         ((TextureAtlasSprite)unstitchedObject).copyFrom(this.missingImage);
      }

      if (Config.isMultiTexture()) {
         int atlasWidth = stitcher.njr();
         int atlasHeight = stitcher.Hro();

         for (Object multiSlotObject : stitcher.getStichSlots()) {
            TextureAtlasSprite multiSprite = (TextureAtlasSprite)multiSlotObject;
            multiSprite.Uwp = atlasWidth;
            multiSprite.Nja = atlasHeight;
            multiSprite.rrp = this.mipmapLevels;
            TextureAtlasSprite spriteSingle = multiSprite.Qtp;
            if (spriteSingle != null) {
               spriteSingle.Uwp = atlasWidth;
               spriteSingle.Nja = atlasHeight;
               spriteSingle.rrp = this.mipmapLevels;
               multiSprite.Rnr();
               boolean blur = false;
               boolean clamp = true;
               TextureUtil.uploadTextureMipmap(spriteSingle.getFrameTextureData(0), spriteSingle.$No(), spriteSingle.jJ(), spriteSingle.lGo(), spriteSingle.alo(), blur, clamp);
            }
         }

         Config.getMinecraft().getTextureManager().bindTexture(locationBlocksTexture);
      }

      Reflector.callVoid(Reflector.ForgeHooksClient_onTextureStitchedPost, this);
      if (Config.equals(System.getProperty("saveTextureMap"), "true")) {
         TextureUtil.exportTextureToFiles(this.basePath.replaceAll("/", "_"), this.getGlTextureId(), this.mipmapLevels, stitcher.njr(), stitcher.Hro());
      }
   }

   public int[] createMissingTextureData(int size) {
      ImageCanvas canvas = new ImageCanvas(16, 16, 2);
      canvas.setRgbBlock(0, 0, 16, 16, TextureUtil.missingTextureData, 0, 16);
      BufferedImage scaledImage = TextureUtils.scaleToPowerOfTwo(canvas, size);
      int[] pixels = new int[size * size];
      scaledImage.getRGB(0, 0, size, size, pixels, 0, size);
      return pixels;
   }

   public TextureMap(String atlasPath, IIconRegister iconRegister) {
      this.missingImage = new TextureAtlasSprite("missingno");
      this.basePath = atlasPath;
      this.iconCreator = iconRegister;
   }

   public boolean isSpecialResource(ResourceLocation location) {
      String resourcePath = location.gHr();
      return this.isSpecialPath(resourcePath);
   }

   public TextureAtlasSprite getAtlasSprite(String spriteName) {
      TextureAtlasSprite sprite = this.mapUploadedSprites.get(spriteName);
      if (sprite == null) {
         sprite = this.missingImage;
      }

      return sprite;
   }

   public int computeSpriteSize(Map sprites, IResourceManager resourceManager, int percentage) {
      HandlerMap sizeCounts = new HandlerMap();

      for (Object entryObject : sprites.entrySet()) {
         TextureAtlasSprite sprite = (TextureAtlasSprite)((Entry)entryObject).getValue();
         ResourceLocation spriteLocation = new ResourceLocation(sprite.DAr());
         ResourceLocation fileLocation = this.completeResourceLocation(spriteLocation, 0);
         if (!sprite.cmr(resourceManager, spriteLocation)) {
            try {
               IResource resource = resourceManager.getResource(fileLocation);
               if (resource != null) {
                  InputStream inputStream = resource.getInputStream();
                  if (inputStream != null) {
                     Dimension imageSize = TextureUtils.getImageSize(inputStream, "png");
                     if (imageSize != null) {
                        int imageWidth = imageSize.width;
                        int powerOfTwoWidth = MathHelper.jir(imageWidth);
                        if (!sizeCounts.containsKey(powerOfTwoWidth)) {
                           sizeCounts.put(powerOfTwoWidth, 1);
                        } else {
                           int previousCount = (Integer)sizeCounts.get(powerOfTwoWidth);
                           sizeCounts.put(powerOfTwoWidth, previousCount + 1);
                        }
                     }
                  }
               }
            } catch (Exception exception) {
            }
         }
      }

      int totalCount = 0;
      Set sizeKeys = sizeCounts.keySet();
      TreeSet sortedSizes = new TreeSet(sizeKeys);

      for (int sizeKey : (Iterable<Integer>)(Iterable)sortedSizes) {
         int sizeCount = (Integer)sizeCounts.get(sizeKey);
         totalCount += sizeCount;
      }

      int spriteSize = 16;
      int cumulativeCount = 0;
      int threshold = totalCount * percentage / 100;

      for (int candidateSize : (Iterable<Integer>)(Iterable)sortedSizes) {
         int candidateCount = (Integer)sizeCounts.get(candidateSize);
         cumulativeCount += candidateCount;
         if (candidateSize > spriteSize) {
            spriteSize = candidateSize;
         }

         if (cumulativeCount > threshold) {
            return spriteSize;
         }
      }

      return spriteSize;
   }

   public void loadSprites(IResourceManager resourceManager, IIconRegister iconRegister) {
      this.mapRegisteredSprites.clear();
      iconRegister.registerSprites(this);
      if (this.mipmapLevels >= 4) {
         this.mipmapLevels = this.computeMipmapLevel(this.mapRegisteredSprites, resourceManager);
         Config.log("Mipmap levels: " + this.mipmapLevels);
      }

      this.initMissingImage();
      this.wio();
      this.loadTextureAtlas(resourceManager);
   }

   public void updateAnimations() {
      TextureUtil.bindTexture(this.getGlTextureId());

      for (Object animatedObject : this.listAnimatedSprites) {
         TextureAtlasSprite animatedSprite = (TextureAtlasSprite)animatedObject;
         if (this.isAnimationEnabled(animatedSprite)) {
            animatedSprite.updateAnimation();
         }
      }

      if (Config.isMultiTexture()) {
         for (Object multiAnimatedObject : this.listAnimatedSprites) {
            TextureAtlasSprite multiAnimatedSprite = (TextureAtlasSprite)multiAnimatedObject;
            if (this.isAnimationEnabled(multiAnimatedSprite)) {
               TextureAtlasSprite spriteSingle = multiAnimatedSprite.Qtp;
               if (spriteSingle != null) {
                  multiAnimatedSprite.Rnr();
                  spriteSingle.updateAnimation();
               }
            }
         }

         TextureUtil.bindTexture(this.getGlTextureId());
      }
   }

   public void initMissingImage() {
      int minSpriteSize = this.getMinSpriteSize();
      int[] missingData = this.createMissingTextureData(minSpriteSize);
      this.missingImage.p$r(minSpriteSize);
      this.missingImage.gYr(minSpriteSize);
      int[][] frames = new int[this.mipmapLevels + 1][];
      frames[0] = missingData;
      this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][]{frames}));
      this.missingImage.zWo(0);
   }

   public TextureAtlasSprite getMissingSprite() {
      return this.missingImage;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) {
      if (this.iconCreator != null) {
         this.loadSprites(resourceManager, this.iconCreator);
      }
   }

   public boolean setTextureEntry(String spriteName, TextureAtlasSprite sprite) {
      if (!this.mapRegisteredSprites.containsKey(spriteName)) {
         this.mapRegisteredSprites.put(spriteName, sprite);
         if (sprite.nUo() < 0) {
            sprite.zWo(this.mapRegisteredSprites.size());
         }

         return true;
      } else {
         return false;
      }
   }

   public TextureAtlasSprite registerSprite(ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         TextureAtlasSprite sprite = this.mapRegisteredSprites.get(location.toString());
         if (sprite == null && Reflector.ModLoader_getCustomAnimationLogic.exists()) {
            sprite = (TextureAtlasSprite)Reflector.call(Reflector.ModLoader_getCustomAnimationLogic, location);
         }

         if (sprite == null) {
            sprite = TextureAtlasSprite.makeAtlasSprite(location);
            this.mapRegisteredSprites.put(location.toString(), sprite);
            if (sprite instanceof TextureAtlasSprite && sprite.nUo() < 0) {
               sprite.zWo(this.mapRegisteredSprites.size());
            }
         }

         return sprite;
      }
   }

   public boolean isSpecialPath(String path) {
      String lowerPath = path.toLowerCase();
      return lowerPath.startsWith("mcpatcher/") || lowerPath.startsWith("optifine/");
   }
}
