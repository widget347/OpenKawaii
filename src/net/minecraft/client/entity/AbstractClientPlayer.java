/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.entity;

import com.mojang.authlib.GameProfile;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import kawaii.render.ImageCanvas;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer$1;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import optfine.Config;
import optfine.PlayerConfigurations;
import optfine.Reflector;
import org.apache.commons.io.FilenameUtils;

public abstract class AbstractClientPlayer
extends EntityPlayer {
    public ResourceLocation locationOfCape = null;
    public static String obfuscationId = "CL_00000935";
    public NetworkPlayerInfo playerInfo;

    public ResourceLocation getLocationCape() {
        if (!Config.isShowCapes()) {
            return null;
        }
        if (this.locationOfCape != null) {
            return this.locationOfCape;
        }
        NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
        return networkPlayerInfo == null ? null : networkPlayerInfo.getLocationCape();
    }

    public static ResourceLocation getLocationSkin(String playerName) {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(playerName));
    }

    public NetworkPlayerInfo getPlayerInfo() {
        if (this.playerInfo == null) {
            this.playerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getUniqueID());
        }
        return this.playerInfo;
    }

    @Override
    public boolean isSpectator() {
        NetworkPlayerInfo networkPlayerInfo = Minecraft.getMinecraft().getNetHandler().getPlayerInfo(this.getGameProfile().getId());
        return networkPlayerInfo != null && networkPlayerInfo.getGameType() == WorldSettings.GameType.SPECTATOR;
    }

    public static BufferedImage parseUserSkin(AbstractClientPlayer abstractClientPlayer, BufferedImage bufferedImage) {
        return abstractClientPlayer.padToPowerOfTwo(bufferedImage);
    }

    public BufferedImage padToPowerOfTwo(BufferedImage bufferedImage) {
        int canvasHeight;
        int canvasWidth = 64;
        int imageWidth = bufferedImage.getWidth();
        int imageHeight = bufferedImage.getHeight();
        for (canvasHeight = 32; canvasWidth < imageWidth || canvasHeight < imageHeight; canvasWidth *= 2, canvasHeight *= 2) {
        }
        ImageCanvas imageCanvas = new ImageCanvas(canvasWidth, canvasHeight, 2);
        Graphics graphics = imageCanvas.getImageGraphics();
        graphics.drawImage(bufferedImage, 0, 0, null);
        graphics.dispose();
        return imageCanvas;
    }

    public float getFovModifier() {
        float fovModifier = 1.0f;
        if (this.capabilities.allowFlying) {
            fovModifier *= 1.1f;
        }
        IAttributeInstance iAttributeInstance = this.getEntityAttribute(SharedMonsterAttributes.qja);
        fovModifier = (float)((double)fovModifier * ((iAttributeInstance.getAttributeValue() / (double)this.capabilities.iD() + 1.0) / 2.0));
        if (this.capabilities.iD() == 0.0f || Float.isNaN(fovModifier) || Float.isInfinite(fovModifier)) {
            fovModifier = 1.0f;
        }
        if (this.isUsingItem() && this.getItemInUse().getItem() == Items.bow) {
            int bowUseTicks = this.getItemInUseDuration();
            float bowPullProgress = (float)bowUseTicks / 20.0f;
            bowPullProgress = bowPullProgress > 1.0f ? 1.0f : (bowPullProgress *= bowPullProgress);
            fovModifier *= 1.0f - bowPullProgress * 0.15f;
        }
        return Reflector.ForgeHooksClient_getOffsetFOV.exists() ? Reflector.callFloat(Reflector.ForgeHooksClient_getOffsetFOV, this, Float.valueOf(fovModifier)) : fovModifier;
    }

    public boolean hasPlayerInfo() {
        return this.getPlayerInfo() != null;
    }

    public static ResourceLocation setLocationOfCape(AbstractClientPlayer abstractClientPlayer, ResourceLocation resourceLocation) {
        abstractClientPlayer.locationOfCape = resourceLocation;
        return abstractClientPlayer.locationOfCape;
    }

    public String getSkinType() {
        NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
        return networkPlayerInfo == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : networkPlayerInfo.getSkinType();
    }

    public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocation, String username) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
        if (iTextureObject == null) {
            iTextureObject = new ThreadDownloadImageData(null, String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)), DefaultPlayerSkin.getDefaultSkin(AbstractClientPlayer.getOfflineUUID(username)), new ImageBufferDownload());
            textureManager.loadTexture(resourceLocation, iTextureObject);
        }
        return (ThreadDownloadImageData)iTextureObject;
    }

    public ResourceLocation getLocationSkin() {
        NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
        return networkPlayerInfo == null ? DefaultPlayerSkin.getDefaultSkin(this.getUniqueID()) : networkPlayerInfo.getLocationSkin();
    }

    public boolean hasSkin() {
        NetworkPlayerInfo networkPlayerInfo = this.getPlayerInfo();
        return networkPlayerInfo != null && networkPlayerInfo.hasLocationSkin();
    }

    public AbstractClientPlayer(World world, GameProfile gameProfile) {
        super(world, gameProfile);
        String profileName = gameProfile.getName();
        this.loadOptifineCape(profileName);
        PlayerConfigurations.getPlayerConfiguration(this);
    }

    public void loadOptifineCape(String playerName) {
        if (playerName != null && !playerName.isEmpty()) {
            Object value;
            playerName = StringUtils.stripControlCodes(playerName);
            String capeUrl = "http://s.optifine.net/capes/" + playerName + ".png";
            String capeName = FilenameUtils.getBaseName(capeUrl);
            ResourceLocation resourceLocation = new ResourceLocation("capeof/" + capeName);
            TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
            ITextureObject iTextureObject = textureManager.getTexture(resourceLocation);
            if (iTextureObject != null && iTextureObject instanceof ThreadDownloadImageData) {
                value = (ThreadDownloadImageData)iTextureObject;
                if (((ThreadDownloadImageData)value).imageFound != null) {
                    if (((ThreadDownloadImageData)value).imageFound.booleanValue()) {
                        this.locationOfCape = resourceLocation;
                    }
                    return;
                }
            }
            value = new AbstractClientPlayer$1(this, resourceLocation);
            ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, capeUrl, null, (IImageBuffer)value);
            textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        }
    }
}

