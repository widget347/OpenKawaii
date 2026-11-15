/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.gui;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Random;
import kawaii.Kawaii;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.module.render.Blur;
import kawaii.module.render.Hud;
import kawaii.render.BlurRenderer;
import kawaii.render.ColorThemes;
import kawaii.render.RenderUtils;
import kawaii.ui.font.FontLoader;
import kawaii.util.SeededRandom;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiIngame$1;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import net.minecraft.client.gui.GuiSpectator;
import net.minecraft.client.gui.GuiStreamIndicator;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.border.WorldBorder;
import optfine.Config;

public class GuiIngame
extends Gui {
    public ItemStack highlightingItemStack;
    public int lastPlayerHealth = 0;
    public int remainingHighlightTicks;
    public Minecraft mc;
    public static ResourceLocation pumpkinBlurTexPath;
    public int updateCounter;
    public GuiNewChat persistantChatGUI;
    public String displayedTitle = "";
    public long healthUpdateCounter = 0L;
    public int recordPlayingUpCount;
    public RenderItem itemRenderer;
    public int titleDisplayTime;
    public String recordPlaying = "";
    public String displayedSubTitle = "";
    public float prevVignetteBrightness = 1.0f;
    public GuiStreamIndicator streamIndicator;
    public int titleFadeOut;
    public int titleFadeIn;
    public GuiSpectator spectatorGui;
    public int titlesTimer;
    public static String obfuscationId;
    public int playerHealth = 0;
    public boolean recordIsPlaying;
    public ScaledResolution scaledResolution;
    public static ResourceLocation widgetsTexPath;
    public long lastSystemTime = 0L;
    public static ResourceLocation vignetteTexPath;
    public GuiOverlayDebug overlayDebug;
    public Random rand = new SeededRandom();
    public GuiPlayerTabOverlay overlayPlayerList;

    public void renderGameOverlay(float partialTicks) {
        ScoreObjective scoreObjective;
        int colorIndex;
        int value;
        float progress;
        float portalProgress;
        ScaledResolution scaledResolution = new ScaledResolution(this.mc);
        int scaledWidth = scaledResolution.getScaledWidth();
        int scaledHeight = scaledResolution.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        if (Config.isVignetteEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(partialTicks), scaledResolution);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
        ItemStack itemStack = this.mc.thePlayer.inventory.Dn(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            this.renderPumpkinOverlay(scaledResolution);
        }
        if (!this.mc.thePlayer.isPotionActive(Potion.bjp) && (portalProgress = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks) > 0.0f) {
            this.renderPortal(portalProgress, scaledResolution);
        }
        if (this.mc.playerController.isSpectatorMode()) {
            this.spectatorGui.renderTooltip(scaledResolution, partialTicks);
        } else {
            this.renderTooltip(scaledResolution, partialTicks);
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(scaledWidth / 2 - 7, scaledHeight / 2 - 7, 0, 0, 16, 16);
        }
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.kqr("bossHealth");
        this.renderBossHealth();
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.isSurvivalOrAdventure()) {
            this.renderPlayerStats(scaledResolution);
        }
        GlStateManager.disableBlend();
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.kqr("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            int sleepTimer = this.mc.thePlayer.getSleepTimer();
            progress = (float)sleepTimer / 100.0f;
            if (progress > 1.0f) {
                progress = 1.0f - (float)(sleepTimer - 100) / 10.0f;
            }
            value = (int)(220.0f * progress) << 24 | 0x101020;
            GuiIngame.drawRect(0.0, 0.0, scaledWidth, scaledHeight, value);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        int hotbarLeft = scaledWidth / 2 - 91;
        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(scaledResolution, hotbarLeft);
        } else if (this.mc.playerController.shouldDrawHUD()) {
            this.renderExpBar(scaledResolution, hotbarLeft);
        }
        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectatorMode()) {
            this.renderSelectedItem(scaledResolution);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSpectatorMenu(scaledResolution);
        }
        if (this.mc.isGamePaused()) {
            this.renderDemo(scaledResolution);
        }
        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(scaledResolution);
        }
        if (this.recordPlayingUpCount > 0) {
            this.mc.mcProfiler.kqr("overlayMessage");
            progress = (float)this.recordPlayingUpCount - partialTicks;
            value = (int)(progress * 255.0f / 20.0f);
            if (value > 255) {
                value = 255;
            }
            if (value > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledWidth / 2, scaledHeight - 68, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                int recordColor = 0xFFFFFF;
                if (this.recordIsPlaying) {
                    recordColor = MathHelper.hsvToRGB(progress / 50.0f, 0.7f, 0.6f) & 0xFFFFFF;
                }
                this.getFontRenderer().drawString(this.recordPlaying, -this.getFontRenderer().getStringWidth(this.recordPlaying) / 2, -4, recordColor + (value << 24 & 0xFF000000));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.kqr("titleAndSubtitle");
            progress = (float)this.titlesTimer - partialTicks;
            value = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float fadeElapsed = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - progress;
                value = (int)(fadeElapsed * 255.0f / (float)this.titleFadeIn);
            }
            if (this.titlesTimer <= this.titleFadeOut) {
                value = (int)(progress * 255.0f / (float)this.titleFadeOut);
            }
            if ((value = MathHelper.Zog(value, 0, 255)) > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate(scaledWidth / 2, scaledHeight / 2, 0.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0f, 4.0f, 4.0f);
                int titleAlphaBits = value << 24 & 0xFF000000;
                this.getFontRenderer().drawString(this.displayedTitle, -this.getFontRenderer().getStringWidth(this.displayedTitle) / 2, -10.0f, 0xFFFFFF | titleAlphaBits, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                this.getFontRenderer().drawString(this.displayedSubTitle, -this.getFontRenderer().getStringWidth(this.displayedSubTitle) / 2, 5.0f, 0xFFFFFF | titleAlphaBits, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
            this.mc.mcProfiler.endSection();
        }
        Scoreboard scoreboard = this.mc.theWorld.getScoreboard();
        ScoreObjective scoreObjective2 = null;
        ScorePlayerTeam scorePlayerTeam = scoreboard.Ys(this.mc.thePlayer.getName());
        if (scorePlayerTeam != null && (colorIndex = scorePlayerTeam.getChatFormat().getColorIndex()) >= 0) {
            scoreObjective2 = scoreboard.getObjectiveInDisplaySlot(3 + colorIndex);
        }
        ScoreObjective scoreObjective3 = scoreObjective = scoreObjective2 != null ? scoreObjective2 : scoreboard.getObjectiveInDisplaySlot(1);
        if (scoreObjective != null) {
            this.renderScoreboard(scoreObjective, scaledResolution);
        }
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0f, scaledHeight - 48, 0.0f);
        this.mc.mcProfiler.kqr("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        scoreObjective = scoreboard.getObjectiveInDisplaySlot(0);
        if (!this.mc.gameSettings.keyBindPlayerList.f$() || this.mc.isJava64bit() && this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() <= 1 && scoreObjective == null) {
            this.overlayPlayerList.updatePlayerList(false);
        } else {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(scaledWidth, scoreboard, scoreObjective);
        }
        ScaledResolution scaledResolution2 = new ScaledResolution(this.mc);
        if (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiChat) {
            Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new EventRender2D(EventType.pre, scaledResolution2));
            if (Blur.blurActive.booleanValue()) {
                BlurRenderer.renderQueued();
            }
        }
        Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new EventRender2D(EventType.post, scaledResolution2));
        if (this.mc.currentScreen != null && !(this.mc.currentScreen instanceof GuiChat)) {
            Kawaii.INSTANCE.getModuleKeyHandler().onKeyInput(new EventRender2D(EventType.pre, scaledResolution2));
            if (Blur.blurActive.booleanValue()) {
                BlurRenderer.renderQueued();
            }
        }
        Kawaii.INSTANCE.getInfoElementManager().renderElements(scaledResolution2);
        this.renderSelectedItem(scaledResolution2);
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        if (this.mc.thePlayer != null && this.scaledResolution != null) {
            for (int i = 0; i < 9; i += 36) {
                int slotX = this.scaledResolution.getScaledWidth() / 2 - 90 + i * 20 + 2;
                int slotY = this.scaledResolution.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(i, slotX, slotY, partialTicks, this.mc.thePlayer);
                i -= 35;
            }
        }
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    }

    public void renderVignette(float vignetteStrength, ScaledResolution scaledResolution) {
        if (Config.isVignetteEnabled()) {
            vignetteStrength = 1.0f - vignetteStrength;
            vignetteStrength = MathHelper.clamp_float(vignetteStrength, 0.0f, 1.0f);
            WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
            float borderFactor = (float)worldBorder.mfr(this.mc.thePlayer);
            double resizeWarningThreshold = Math.min(worldBorder.zrg() * (double)worldBorder.Qar() * 1000.0, Math.abs(worldBorder.bar() - worldBorder.NB()));
            double warningRadius = Math.max((double)worldBorder.Aer(), resizeWarningThreshold);
            borderFactor = (double)borderFactor < warningRadius ? 1.0f - (float)((double)borderFactor / warningRadius) : 0.0f;
            this.prevVignetteBrightness = (float)((double)this.prevVignetteBrightness + (double)(vignetteStrength - this.prevVignetteBrightness) * 0.01);
            GlStateManager.disableDepth();
            GlStateManager.depthMask(false);
            GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
            if (borderFactor > 0.0f) {
                GlStateManager.color(0.0f, borderFactor, borderFactor, 1.0f);
            } else {
                GlStateManager.color(this.prevVignetteBrightness, this.prevVignetteBrightness, this.prevVignetteBrightness, 1.0f);
            }
            this.mc.getTextureManager().bindTexture(vignetteTexPath);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            worldRenderer.begin(7, DefaultVertexFormats.Tma);
            worldRenderer.pos(0.0, scaledResolution.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
            worldRenderer.pos(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(scaledResolution.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
            worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
            tessellator.draw();
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }
    }

    public void displayTitle(String title, String subTitle, int fadeIn, int displayTime, int fadeOut) {
        if (title == null && subTitle == null && fadeIn < 0 && displayTime < 0 && fadeOut < 0) {
            this.displayedTitle = "";
            this.displayedSubTitle = "";
            this.titlesTimer = 0;
        } else if (title != null) {
            this.displayedTitle = title;
            this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
        } else if (subTitle != null) {
            this.displayedSubTitle = subTitle;
        } else {
            if (fadeIn >= 0) {
                this.titleFadeIn = fadeIn;
            }
            if (displayTime >= 0) {
                this.titleDisplayTime = displayTime;
            }
            if (fadeOut >= 0) {
                this.titleFadeOut = fadeOut;
            }
            if (this.titlesTimer > 0) {
                this.titlesTimer = this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut;
            }
        }
    }

    public GuiIngame(Minecraft minecraft) {
        this.mc = minecraft;
        this.itemRenderer = minecraft.getRenderItem();
        this.overlayDebug = new GuiOverlayDebug(minecraft);
        this.spectatorGui = new GuiSpectator(minecraft);
        this.persistantChatGUI = new GuiNewChat(minecraft);
        this.streamIndicator = new GuiStreamIndicator(minecraft);
        this.overlayPlayerList = new GuiPlayerTabOverlay(minecraft, this);
        this.setDefaultTitlesTimes();
    }

    public void renderTooltip(ScaledResolution scaledResolution, float partialTicks) {
        double slotSize = 18.0;
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int hotbarCenterX = scaledResolution.getScaledWidth() / 2;
            float savedZLevel = zLevel;
            zLevel = -90.0f;
            if (Hud.hotbarStyle.getMode().equalsIgnoreCase("Float")) {
                int floatSlotY;
                int floatSlotX;
                int floatSlotIndex;
                zLevel = savedZLevel;
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();
                for (floatSlotIndex = 0; floatSlotIndex < 9; floatSlotIndex += 29) {
                    floatSlotX = scaledResolution.getScaledWidth() / 2 - 90 + floatSlotIndex * 20 + 2;
                    floatSlotY = scaledResolution.getScaledHeight() - 16 - 13;
                    if (entityPlayer.inventory.currentItem == floatSlotIndex) {
                        floatSlotY -= 5;
                    }
                    this.renderHotbarItem(floatSlotIndex, floatSlotX, floatSlotY, partialTicks, entityPlayer);
                    floatSlotIndex -= 28;
                }
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
                for (floatSlotIndex = 0; floatSlotIndex < 9; ++floatSlotIndex) {
                    floatSlotX = scaledResolution.getScaledWidth() / 2 - 90 + floatSlotIndex * 20 + 2;
                    floatSlotY = scaledResolution.getScaledHeight() - 16 - 13;
                    if (entityPlayer.inventory.currentItem == floatSlotIndex) {
                        floatSlotY += 21;
                        floatSlotY -= 26;
                    }
                    Gui.drawRect(floatSlotX - 1, floatSlotY + 16, floatSlotX + 17, floatSlotY + 18, -1);
                    Gui.drawRect(floatSlotX - 1, floatSlotY + 1 - 2, floatSlotX + 17, floatSlotY + 18 - 2, 0x40000000);
                    if (!Blur.blurActive.booleanValue() || !Blur.blurHotbar.getValue().booleanValue()) continue;
                    BlurRenderer.queueBlur(floatSlotX - 1, floatSlotY + 1 - 2, floatSlotX + 17, floatSlotY + 18 - 2, 0.0);
                }
            } else {
                if (Blur.blurActive.booleanValue() && Blur.blurHotbar.getValue().booleanValue()) {
                    BlurRenderer.queueBlur(hotbarCenterX - 91, scaledResolution.getScaledHeight() - 22 - 1, hotbarCenterX - 91 + 182, scaledResolution.getScaledHeight() - 22 + 22 + 22, 5.0);
                } else {
                    RenderUtils.drawRoundedRect(hotbarCenterX - 91, scaledResolution.getScaledHeight() - 22, 182.0, 66.0, 5.0, Integer.MIN_VALUE);
                }
                if (Hud.hotbarStyle.getMode().equalsIgnoreCase("Filled")) {
                    RenderUtils.drawRoundedRect(hotbarCenterX - 91 + entityPlayer.inventory.currentItem * 20 + 2, scaledResolution.getScaledHeight() - 21, slotSize, slotSize, slotSize, ColorThemes.getStaticThemeColor(scaledResolution.getScaledHeight() - 22));
                } else {
                    RenderUtils.drawRoundedOutline(hotbarCenterX - 91 + entityPlayer.inventory.currentItem * 20 + 2, scaledResolution.getScaledHeight() - 22 + 2, slotSize, slotSize, 2.0, 7.0f, ColorThemes.getStaticThemeColor(scaledResolution.getScaledHeight() - 22));
                }
                zLevel = savedZLevel;
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                RenderHelper.enableGUIStandardItemLighting();
                for (int i = 0; i < 9; ++i) {
                    int slotX = scaledResolution.getScaledWidth() / 2 - 90 + i * 20 + 2;
                    int slotY = scaledResolution.getScaledHeight() - 16 - 3;
                    this.renderHotbarItem(i, slotX, slotY, partialTicks, entityPlayer);
                }
                RenderHelper.disableStandardItemLighting();
                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }
    }

    static {
        obfuscationId = "CL_00000661";
        vignetteTexPath = new ResourceLocation("textures/misc/vignette.png");
        widgetsTexPath = new ResourceLocation("textures/gui/widgets.png");
        pumpkinBlurTexPath = new ResourceLocation("textures/misc/pumpkinblur.png");
    }

    public int getUpdateCounter() {
        return this.updateCounter;
    }

    public void renderSelectedItem(ScaledResolution scaledResolution) {
        this.mc.mcProfiler.kqr("selectedItemName");
        if (this.remainingHighlightTicks > 0 && this.highlightingItemStack != null) {
            int alpha;
            String itemName = this.highlightingItemStack.getDisplayName();
            if (this.highlightingItemStack.hasDisplayName()) {
                itemName = (Object)((Object)EnumChatFormatting.ITALIC) + itemName;
            }
            int textX = (scaledResolution.getScaledWidth() - FontLoader.val20.getStringWidth(itemName)) / 2;
            int textY = scaledResolution.getScaledHeight() - 59;
            if (!this.mc.playerController.isSurvivalOrAdventure()) {
                textY -= 8;
                textY += 22;
            }
            if ((alpha = (int)((float)this.remainingHighlightTicks * 256.0f / 10.0f)) > 255) {
                alpha = 255;
            }
            if (alpha > 0) {
                if (Hud.hotbarStyle.getMode().equalsIgnoreCase("Float")) {
                    textY -= 5;
                }
                RenderUtils.drawRoundedRect(textX - 2, textY - 3, FontLoader.val20.getStringWidth(itemName) + 4, FontLoader.val20.getHeight() + 4, 2.0, 0x50000000);
                if (Blur.blurActive.booleanValue() && Blur.blurHotbar.getValue().booleanValue()) {
                    BlurRenderer.queueBlur(textX - 2, textY - 4, textX + FontLoader.val20.getStringWidth(itemName) + 2, textY + FontLoader.val20.getHeight() + 1, 2.0);
                }
                GlStateManager.pushMatrix();
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                FontLoader.val20.drawStringWithShadow(itemName, textX, textY, 0xFFFFFF + (alpha << 24));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }
        }
        this.mc.mcProfiler.endSection();
    }

    public void resetPlayerList() {
        this.overlayPlayerList.resetTabOverlay();
    }

    public FontRenderer getFontRenderer() {
        return this.mc.fontRendererObj;
    }

    public void setRecordPlaying(IChatComponent iChatComponent, boolean isPlaying) {
        this.setRecordPlaying(iChatComponent.getUnformattedText(), isPlaying);
    }

    public boolean showCrosshair() {
        if (this.mc.gameSettings.showDebugInfo && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo) {
            return false;
        }
        if (this.mc.playerController.isSpectatorMode()) {
            BlockPos blockPos;
            if (this.mc.pointedEntity != null) {
                return true;
            }
            return this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && this.mc.theWorld.getTileEntity(blockPos = this.mc.objectMouseOver.getBlockPos()) instanceof IInventory;
        }
        return true;
    }

    public void renderHorseJumpBar(ScaledResolution scaledResolution, int barX) {
        this.mc.mcProfiler.kqr("jumpBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        float jumpPower = this.mc.thePlayer.getHorseJumpPower();
        int filledWidth = (int)(jumpPower * 183.0f);
        int barY = scaledResolution.getScaledHeight() - 32 + 3;
        this.drawTexturedModalRect(barX, barY, 0, 84, 182, 5);
        if (filledWidth > 0) {
            this.drawTexturedModalRect(barX, barY, 0, 89, filledWidth, 5);
        }
        this.mc.mcProfiler.endSection();
    }

    public void setDefaultTitlesTimes() {
        this.titleFadeIn = 10;
        this.titleDisplayTime = 70;
        this.titleFadeOut = 20;
    }

    public void renderHotbarItem(int slotIndex, int slotX, int slotY, float partialTicks, EntityPlayer entityPlayer) {
        ItemStack itemStack = entityPlayer.inventory.mainInventory[slotIndex];
        if (itemStack != null) {
            float animationTime = (float)itemStack.Auc - partialTicks;
            if (animationTime > 0.0f) {
                GlStateManager.pushMatrix();
                float bounceScale = 1.0f + animationTime / 5.0f;
                GlStateManager.translate(slotX + 8, slotY + 12, 0.0f);
                GlStateManager.scale(1.0f / bounceScale, (bounceScale + 1.0f) / 2.0f, 1.0f);
                GlStateManager.translate(-(slotX + 8), -(slotY + 12), 0.0f);
            }
            this.itemRenderer.renderItemAndEffectIntoGUI(itemStack, slotX, slotY);
            if (animationTime > 0.0f) {
                GlStateManager.popMatrix();
            }
            this.itemRenderer.renderItemOverlays(this.mc.fontRendererObj, itemStack, slotX, slotY);
        }
    }

    public GuiPlayerTabOverlay getTabList() {
        return this.overlayPlayerList;
    }

    public void renderStreamIndicator(ScaledResolution scaledResolution) {
        this.streamIndicator.render(scaledResolution.getScaledWidth() - 10, 10);
    }

    public void renderDemo(ScaledResolution scaledResolution) {
        this.mc.mcProfiler.kqr("demo");
        String demoText = "";
        demoText = this.mc.theWorld.yar() >= 120500L ? I18n.format("demo.demoExpired", new Object[0]) : I18n.format("demo.remainingTime", StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.yar())));
        int textWidth = this.getFontRenderer().getStringWidth(demoText);
        this.getFontRenderer().drawStringWithShadow(demoText, scaledResolution.getScaledWidth() - textWidth - 10, 5.0f, 0xFFFFFF);
        this.mc.mcProfiler.endSection();
    }

    public void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution scaledResolution) {
        Scoreboard scoreboard = scoreObjective.getScoreboard();
        Collection<Score> collection = scoreboard.getSortedScores(scoreObjective);
        ArrayList<Score> filteredScores = Lists.newArrayList(Iterables.filter(collection, new GuiIngame$1(this)));
        ArrayList<Score> visibleScores = filteredScores.size() > 15 ? Lists.newArrayList(Iterables.skip(filteredScores, collection.size() - 15)) : filteredScores;
        int maxWidth = this.getFontRenderer().getStringWidth(scoreObjective.oE());
        Iterator<Score> widthIterator = visibleScores.iterator();
        while (widthIterator.hasNext()) {
            Score score;
            Score widthScore = score = widthIterator.next();
            ScorePlayerTeam scorePlayerTeam = scoreboard.Ys(widthScore.getPlayerName());
            String scoreLine = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, widthScore.getPlayerName()) + ": " + (Object)((Object)EnumChatFormatting.RED) + widthScore.getScorePoints();
            maxWidth = Math.max(maxWidth, this.getFontRenderer().getStringWidth(scoreLine));
        }
        int totalHeight = visibleScores.size() * this.getFontRenderer().FONT_HEIGHT;
        int startY = scaledResolution.getScaledHeight() / 2 + totalHeight / 3;
        int padding = 3;
        int nameX = scaledResolution.getScaledWidth() - maxWidth - padding;
        int rowIndex = 0;
        Iterator<Score> drawIterator = visibleScores.iterator();
        while (drawIterator.hasNext()) {
            Score score;
            Score drawScore = score = drawIterator.next();
            ScorePlayerTeam scorePlayerTeam = scoreboard.Ys(drawScore.getPlayerName());
            String playerName = ScorePlayerTeam.formatPlayerName(scorePlayerTeam, drawScore.getPlayerName());
            String scoreText = (Object)((Object)EnumChatFormatting.RED) + "" + drawScore.getScorePoints();
            int rowY = startY - ++rowIndex * this.getFontRenderer().FONT_HEIGHT;
            int rightX = scaledResolution.getScaledWidth() - padding + 2;
            GuiIngame.drawRect(nameX - 2, rowY, rightX, rowY + this.getFontRenderer().FONT_HEIGHT, 0x50000000);
            this.getFontRenderer().drawString(playerName, nameX, rowY, 0x20FFFFFF);
            this.getFontRenderer().drawString(scoreText, rightX - this.getFontRenderer().getStringWidth(scoreText), rowY, 0x20FFFFFF);
            if (rowIndex != visibleScores.size()) continue;
            String objectiveName = scoreObjective.oE();
            GuiIngame.drawRect(nameX - 2, rowY - this.getFontRenderer().FONT_HEIGHT - 1, rightX, rowY - 1, 0x60000000);
            GuiIngame.drawRect(nameX - 2, rowY - 1, rightX, rowY, 0x50000000);
            this.getFontRenderer().drawString(objectiveName, nameX + maxWidth / 2 - this.getFontRenderer().getStringWidth(objectiveName) / 2, rowY - this.getFontRenderer().FONT_HEIGHT, 0x20FFFFFF);
        }
    }

    public GuiSpectator getSpectatorGui() {
        return this.spectatorGui;
    }

    public void renderPumpkinOverlay(ScaledResolution scaledResolution) {
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableAlpha();
        this.mc.getTextureManager().bindTexture(pumpkinBlurTexPath);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        worldRenderer.pos(0.0, scaledResolution.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
        worldRenderer.pos(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
        worldRenderer.pos(scaledResolution.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
        worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void updateTick() {
        if (this.recordPlayingUpCount > 0) {
            --this.recordPlayingUpCount;
        }
        if (this.titlesTimer > 0) {
            --this.titlesTimer;
            if (this.titlesTimer <= 0) {
                this.displayedTitle = "";
                this.displayedSubTitle = "";
            }
        }
        ++this.updateCounter;
        this.streamIndicator.drawIndicator();
        if (this.mc.thePlayer != null) {
            ItemStack itemStack = this.mc.thePlayer.inventory.Tdr();
            if (itemStack == null) {
                this.remainingHighlightTicks = 0;
            } else if (this.highlightingItemStack != null && itemStack.getItem() == this.highlightingItemStack.getItem() && ItemStack.areItemStackTagsEqual(itemStack, this.highlightingItemStack) && (itemStack.isItemStackDamageable() || itemStack.Xs() == this.highlightingItemStack.Xs())) {
                if (this.remainingHighlightTicks > 0) {
                    --this.remainingHighlightTicks;
                }
            } else {
                this.remainingHighlightTicks = 40;
            }
            this.highlightingItemStack = itemStack;
        }
    }

    public void setRecordPlaying(String recordName, boolean isPlaying) {
        this.recordPlaying = recordName;
        this.recordPlayingUpCount = 60;
        this.recordIsPlaying = isPlaying;
    }

    public void renderExpBar(ScaledResolution scaledResolution, int barX) {
        int offset;
        int temp;
        this.mc.mcProfiler.kqr("expBar");
        this.mc.getTextureManager().bindTexture(Gui.icons);
        int xpCapacity = this.mc.thePlayer.xpBarCap();
        if (xpCapacity > 0) {
            temp = 182;
            int filledWidth = (int)(this.mc.thePlayer.experience * (float)(temp + 1));
            offset = scaledResolution.getScaledHeight() - 32 + 3;
            if (Hud.hotbarStyle.getMode().equalsIgnoreCase("Float")) {
                offset -= 12;
                --offset;
            }
            this.drawTexturedModalRect(barX, offset, 0, 64, temp, 5);
            if (filledWidth > 0) {
                this.drawTexturedModalRect(barX, offset, 0, 69, filledWidth, 5);
            }
        }
        this.mc.mcProfiler.endSection();
        if (this.mc.thePlayer.experienceLevel > 0) {
            this.mc.mcProfiler.kqr("expLevel");
            temp = 8453920;
            String levelText = "" + this.mc.thePlayer.experienceLevel;
            offset = (scaledResolution.getScaledWidth() - this.getFontRenderer().getStringWidth(levelText)) / 2;
            int levelTextY = scaledResolution.getScaledHeight() - 31 - 4;
            this.getFontRenderer().drawString(levelText, offset + 1, levelTextY, 0);
            this.getFontRenderer().drawString(levelText, offset - 1, levelTextY, 0);
            this.getFontRenderer().drawString(levelText, offset, levelTextY + 1, 0);
            this.getFontRenderer().drawString(levelText, offset, levelTextY - 1, 0);
            this.getFontRenderer().drawString(levelText, offset, levelTextY, temp);
            this.mc.mcProfiler.endSection();
        }
    }

    public void renderPortal(float portalAlpha, ScaledResolution scaledResolution) {
        if (portalAlpha < 1.0f) {
            portalAlpha *= portalAlpha;
            portalAlpha *= portalAlpha;
            portalAlpha = portalAlpha * 0.8f + 0.2f;
        }
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(1.0f, 1.0f, 1.0f, portalAlpha);
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        TextureAtlasSprite textureAtlasSprite = this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(Blocks.portal.getDefaultState());
        float minU = textureAtlasSprite.getMinU();
        float minV = textureAtlasSprite.getMinV();
        float maxU = textureAtlasSprite.getMaxU();
        float maxV = textureAtlasSprite.getMaxV();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        worldRenderer.pos(0.0, scaledResolution.getScaledHeight(), -90.0).tex(minU, maxV).endVertex();
        worldRenderer.pos(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -90.0).tex(maxU, maxV).endVertex();
        worldRenderer.pos(scaledResolution.getScaledWidth(), 0.0, -90.0).tex(maxU, minV).endVertex();
        worldRenderer.pos(0.0, 0.0, -90.0).tex(minU, minV).endVertex();
        tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void renderPlayerStats(ScaledResolution scaledResolution) {
        if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
            int offset;
            int value;
            int temp;
            int current;
            int result;
            int textureOffset;
            int heartIndex;
            boolean isHealthBlinking;
            EntityPlayer entityPlayer = (EntityPlayer)this.mc.getRenderViewEntity();
            int currentHealth = MathHelper.ceiling_float_int(entityPlayer.getHealth());
            boolean blinkResult = isHealthBlinking = this.healthUpdateCounter > (long)this.updateCounter && (this.healthUpdateCounter - (long)this.updateCounter) / 3L % 2L == 1L;
            if (currentHealth < this.lastPlayerHealth && entityPlayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 20;
            } else if (currentHealth > this.lastPlayerHealth && entityPlayer.hurtResistantTime > 0) {
                this.lastSystemTime = Minecraft.getSystemTime();
                this.healthUpdateCounter = this.updateCounter + 10;
            }
            if (Minecraft.getSystemTime() - this.lastSystemTime > 1000L) {
                this.lastPlayerHealth = currentHealth;
                this.playerHealth = currentHealth;
                this.lastSystemTime = Minecraft.getSystemTime();
            }
            this.lastPlayerHealth = currentHealth;
            int shownHealth = this.playerHealth;
            this.rand.setSeed(this.updateCounter * 312871);
            FoodStats foodStats = entityPlayer.getFoodStats();
            int foodLevel = foodStats.KYr();
            int previousFoodLevel = foodStats.Pqr();
            IAttributeInstance iAttributeInstance = entityPlayer.getEntityAttribute(SharedMonsterAttributes.aZg);
            int barLeft = scaledResolution.getScaledWidth() / 2 - 91;
            int barRight = scaledResolution.getScaledWidth() / 2 + 91;
            int statusY = scaledResolution.getScaledHeight() - 39;
            if (Hud.hotbarStyle.getMode().equalsIgnoreCase("Float")) {
                statusY -= 14;
            }
            float attributeHealth = (float)iAttributeInstance.getAttributeValue();
            float absorption = entityPlayer.getAbsorptionAmount();
            int totalRows = MathHelper.ceiling_float_int((attributeHealth + absorption) / 2.0f / 10.0f);
            int rowSpacing = Math.max(10 - (totalRows - 2), 3);
            int armorRowY = statusY - (totalRows - 1) * rowSpacing - 10;
            float remainingAbsorption = absorption;
            int armorValue = entityPlayer.getTotalArmorValue();
            int regenIndex = -1;
            if (entityPlayer.isPotionActive(Potion.Kea)) {
                regenIndex = this.updateCounter % MathHelper.ceiling_float_int(attributeHealth + 5.0f);
            }
            this.mc.mcProfiler.kqr("armor");
            for (heartIndex = 0; heartIndex < 10; heartIndex -= 51) {
                if (armorValue > 0) {
                    textureOffset = barLeft + heartIndex * 8;
                    if (heartIndex * 2 + 1 < armorValue) {
                        this.drawTexturedModalRect(textureOffset, armorRowY, 34, 9, 9, 9);
                    }
                    if (heartIndex * 2 + 1 == armorValue) {
                        this.drawTexturedModalRect(textureOffset, armorRowY, 25, 9, 9, 9);
                    }
                    if (heartIndex * 2 + 1 > armorValue) {
                        this.drawTexturedModalRect(textureOffset, armorRowY, 16, 9, 9, 9);
                    }
                }
                heartIndex += 52;
            }
            this.mc.mcProfiler.endStartSection("health");
            for (heartIndex = MathHelper.ceiling_float_int((attributeHealth + absorption) / 2.0f) - 1; heartIndex >= 0; --heartIndex) {
                textureOffset = 16;
                if (entityPlayer.isPotionActive(Potion.DBg)) {
                    textureOffset += 15;
                    textureOffset += 21;
                } else if (entityPlayer.isPotionActive(Potion.tFg)) {
                    textureOffset += 72;
                }
                result = 0;
                if (isHealthBlinking) {
                    result = 1;
                }
                current = MathHelper.ceiling_float_int((float)(heartIndex + 1) / 10.0f) - 1;
                temp = barLeft + heartIndex % 10 * 8;
                value = statusY - current * rowSpacing;
                if (currentHealth <= 4) {
                    value += this.rand.nextInt(2);
                }
                if (heartIndex == regenIndex) {
                    value += 62;
                    value -= 64;
                }
                offset = 0;
                if (entityPlayer.worldObj.getWorldInfo().fYo()) {
                    offset = 5;
                }
                this.drawTexturedModalRect(temp, value, 16 + result * 9, 9 * offset, 9, 9);
                if (isHealthBlinking) {
                    if (heartIndex * 2 + 1 < shownHealth) {
                        this.drawTexturedModalRect(temp, value, textureOffset + 54, 9 * offset, 9, 9);
                    }
                    if (heartIndex * 2 + 1 == shownHealth) {
                        this.drawTexturedModalRect(temp, value, textureOffset + 63, 9 * offset, 9, 9);
                    }
                }
                if (remainingAbsorption <= 0.0f) {
                    if (heartIndex * 2 + 1 < currentHealth) {
                        this.drawTexturedModalRect(temp, value, textureOffset + 36, 9 * offset, 9, 9);
                    }
                    if (heartIndex * 2 + 1 != currentHealth) continue;
                    this.drawTexturedModalRect(temp, value, textureOffset + 45, 9 * offset, 9, 9);
                    continue;
                }
                if (remainingAbsorption == absorption && absorption % 2.0f == 1.0f) {
                    this.drawTexturedModalRect(temp, value, textureOffset + 153, 9 * offset, 9, 9);
                } else {
                    this.drawTexturedModalRect(temp, value, textureOffset + 144, 9 * offset, 9, 9);
                }
                remainingAbsorption -= 2.0f;
            }
            Entity entity = entityPlayer.ridingEntity;
            if (entity == null) {
                this.mc.mcProfiler.endStartSection("food");
                for (textureOffset = 0; textureOffset < 10; textureOffset -= 10) {
                    result = statusY;
                    current = 16;
                    temp = 0;
                    if (entityPlayer.isPotionActive(Potion.rda)) {
                        current += 36;
                        temp = 13;
                    }
                    if (entityPlayer.getFoodStats().getSaturationLevel() <= 0.0f && this.updateCounter % (foodLevel * 3 + 1) == 0) {
                        result = statusY + (this.rand.nextInt(3) - 1);
                    }
                    value = barRight - textureOffset * 8 - 9;
                    this.drawTexturedModalRect(value, result, 16 + temp * 9, 27, 9, 9);
                    if (textureOffset * 2 + 1 < foodLevel) {
                        this.drawTexturedModalRect(value, result, current + 36, 27, 9, 9);
                    }
                    if (textureOffset * 2 + 1 == foodLevel) {
                        this.drawTexturedModalRect(value, result, current + 45, 27, 9, 9);
                    }
                    textureOffset += 11;
                }
            } else if (entity instanceof EntityLivingBase) {
                this.mc.mcProfiler.endStartSection("mountHealth");
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                result = (int)Math.ceil(entityLivingBase.getHealth());
                float maxHealth = entityLivingBase.getMaxHealth();
                temp = (int)(maxHealth + 0.5f) / 2;
                if (temp > 30) {
                    temp = 30;
                }
                value = statusY;
                offset = 0;
                while (temp > 0) {
                    int heartsInRow = Math.min(temp, 10);
                    temp -= heartsInRow;
                    for (int i = 0; i < heartsInRow; i += 39) {
                        int containerOffset = 0;
                        int heartX = barRight - i * 8 - 9;
                        this.drawTexturedModalRect(heartX, value, 52 + containerOffset * 9, 9, 9, 9);
                        if (i * 2 + 1 + offset < result) {
                            this.drawTexturedModalRect(heartX, value, 88, 9, 9, 9);
                        }
                        if (i * 2 + 1 + offset == result) {
                            this.drawTexturedModalRect(heartX, value, 97, 9, 9, 9);
                        }
                        i -= 38;
                    }
                    value -= 10;
                    offset += 55;
                    offset -= 35;
                }
            }
            this.mc.mcProfiler.endStartSection("air");
            if (entityPlayer.isInsideOfMaterial(Material.PGa)) {
                int airLevel = this.mc.thePlayer.getAir();
                result = MathHelper.tko((double)(airLevel - 2) * 10.0 / 300.0);
                int bubbleCount = MathHelper.tko((double)airLevel * 10.0 / 300.0) - result;
                for (temp = 0; temp < result + bubbleCount; temp += 54) {
                    if (temp < result) {
                        this.drawTexturedModalRect(barRight - temp * 8 - 9, armorRowY, 16, 18, 9, 9);
                    } else {
                        this.drawTexturedModalRect(barRight - temp * 8 - 9, armorRowY, 25, 18, 9, 9);
                    }
                    temp -= 53;
                }
            }
            this.mc.mcProfiler.endSection();
        }
    }

    public GuiNewChat getChatGUI() {
        return this.persistantChatGUI;
    }

    public void setRecordPlayingMessage(String trackName) {
        this.setRecordPlaying(I18n.format("record.nowPlaying", trackName), true);
    }

    public void renderBossHealth() {
        if (BossStatus.bossName != null && BossStatus.statusBarTime > 0) {
            --BossStatus.statusBarTime;
            FontRenderer fontRenderer = this.mc.fontRendererObj;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc);
            int screenWidth = scaledResolution.getScaledWidth();
            int barLeft = screenWidth / 2 - 91;
            int filledPixels = (int)(BossStatus.healthScale * 183.0f);
            this.drawTexturedModalRect(barLeft, 12, 0, 74, 182, 5);
            this.drawTexturedModalRect(barLeft, 12, 0, 74, 182, 5);
            if (filledPixels > 0) {
                this.drawTexturedModalRect(barLeft, 12, 0, 79, filledPixels, 5);
            }
            String bossDisplayName = BossStatus.bossName;
            this.getFontRenderer().drawStringWithShadow(bossDisplayName, screenWidth / 2 - this.getFontRenderer().getStringWidth(bossDisplayName) / 2, 2.0f, 0xFFFFFF);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.mc.getTextureManager().bindTexture(icons);
        }
    }
}

