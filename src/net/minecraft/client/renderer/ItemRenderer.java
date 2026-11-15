/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer;

import kawaii.Kawaii;
import kawaii.module.combat.Killaura;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer$1;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
    public ItemStack itemToRender;
    public int equippedItemSlot = -1;
    public float equippedProgress;
    public Minecraft mc;
    public static ResourceLocation mapBackgroundTexture = new ResourceLocation("textures/map/map_background.png");
    public RenderItem itemRenderer;
    public static ResourceLocation underwaterTexture = new ResourceLocation("textures/misc/underwater.png");
    public RenderManager renderManager;
    public float prevEquippedProgress;

    public void renderRightArm(RenderPlayer renderPlayer) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(54.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(64.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-62.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(0.25f, -0.85f, 0.75f);
        renderPlayer.erg(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    public void renderLeftArm(RenderPlayer renderPlayer) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(92.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(45.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(41.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.translate(-0.3f, -1.1f, 0.45f);
        renderPlayer.Bno(this.mc.thePlayer);
        GlStateManager.popMatrix();
    }

    public void renderItemInFirstPerson(float partialTicks) {
        float equipOffset = 1.0f - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
        EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
        float swingProgress = entityPlayerSP.getSwingProgress(partialTicks);
        float interpolatedPitch = entityPlayerSP.prevRotationPitch + (entityPlayerSP.rotationPitch - entityPlayerSP.prevRotationPitch) * partialTicks;
        float interpolatedYaw = entityPlayerSP.prevRotationYaw + (entityPlayerSP.rotationYaw - entityPlayerSP.prevRotationYaw) * partialTicks;
        this.applyPitchYawRotation(interpolatedPitch, interpolatedYaw);
        this.setLightmapCoords(entityPlayerSP);
        this.applyArmSway(entityPlayerSP, partialTicks);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        if (this.itemToRender != null) {
            if (this.itemToRender.getItem() == Items.filled_map) {
                this.renderItemMap(entityPlayerSP, interpolatedPitch, equipOffset, swingProgress);
            } else if (entityPlayerSP.getXPSeed() > 0 || Killaura.target != null && Kawaii.INSTANCE.getModuleManager().getModule(Killaura.class).isEnabled()) {
                S38PacketPlayerListItem.Action action = this.itemToRender.XC();
                switch (ItemRenderer$1.$SwitchMap$net$minecraft$network$play$server$S38PacketPlayerListItem$Action[action.ordinal()]) {
                    case 1: {
                        this.transformFirstPersonItem(equipOffset, swingProgress);
                        break;
                    }
                    case 2:
                    case 3: {
                        this.transformEatAnimation(entityPlayerSP, partialTicks);
                        this.transformFirstPersonItem(equipOffset, swingProgress);
                        break;
                    }
                    case 4: {
                        this.transformFirstPersonItem(equipOffset, swingProgress);
                        this.transformBlockAnimation();
                        break;
                    }
                    case 5: {
                        this.transformFirstPersonItem(equipOffset, swingProgress);
                        this.transformBowAnimation(partialTicks, entityPlayerSP);
                    }
                }
            } else {
                this.transformSwingProgress(swingProgress);
                this.transformFirstPersonItem(equipOffset, swingProgress);
            }
            this.renderItem(entityPlayerSP, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
        } else if (!entityPlayerSP.isInvisible()) {
            this.renderEmptyHand(entityPlayerSP, equipOffset, swingProgress);
        }
        GlStateManager.popMatrix();
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
    }

    public void renderItemMap(AbstractClientPlayer abstractClientPlayer, float pitch, float equipOffset, float swingProgress) {
        float pi = (float)Math.PI;
        float mapOffsetX = -0.4f * MathHelper.STo(MathHelper.Msr(swingProgress) * pi);
        float mapOffsetY = 0.2f * MathHelper.STo(MathHelper.Msr(swingProgress) * pi * 2.0f);
        float mapOffsetZ = -0.2f * MathHelper.STo(swingProgress * pi);
        GlStateManager.translate(mapOffsetX, mapOffsetY, mapOffsetZ);
        float pitchFactor = this.getMapPitchFactor(pitch);
        GlStateManager.translate(0.0f, 0.04f, -0.72f);
        GlStateManager.translate(0.0f, equipOffset * -1.2f, 0.0f);
        GlStateManager.translate(0.0f, pitchFactor * -0.5f, 0.0f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(pitchFactor * -85.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        this.renderPlayerArms(abstractClientPlayer);
        float swayYaw = MathHelper.STo(swingProgress * swingProgress * pi);
        float swayRoll = MathHelper.STo(MathHelper.Msr(swingProgress) * pi);
        GlStateManager.rotate(swayYaw * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(swayRoll * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(swayRoll * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.38f, 0.38f, 0.38f);
        GlStateManager.rotate(90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(0.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-1.0f, -1.0f, 0.0f);
        GlStateManager.scale(0.015625f, 0.015625f, 0.015625f);
        this.mc.getTextureManager().bindTexture(mapBackgroundTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GL11.glNormal3f(0.0f, 0.0f, -1.0f);
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        worldRenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
        worldRenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
        worldRenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
        worldRenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
        tessellator.draw();
        MapData mapData = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
        if (mapData != null) {
            this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
        }
    }

    public void transformFirstPersonItem(float equipOffset, float swingProgress) {
        GlStateManager.translate(0.56f, -0.52f, -0.71999997f);
        GlStateManager.translate(0.0f, equipOffset * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float swayYaw = MathHelper.STo(swingProgress * swingProgress * (float)Math.PI);
        float swayRoll = MathHelper.STo(MathHelper.Msr(swingProgress) * (float)Math.PI);
        GlStateManager.rotate(swayYaw * -20.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(swayRoll * -20.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(swayRoll * -80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(0.4f, 0.4f, 0.4f);
    }

    public boolean isBlockTranslucent(Block block) {
        return block != null && block.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
    }

    public void renderFireInFirstPerson(float partialTicks) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 0.9f);
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        float fireSize = 1.0f;
        for (int i = 0; i < 2; i += 63) {
            GlStateManager.pushMatrix();
            TextureAtlasSprite textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            float minU = textureAtlasSprite.getMinU();
            float maxU = textureAtlasSprite.getMaxU();
            float minV = textureAtlasSprite.getMinV();
            float maxV = textureAtlasSprite.getMaxV();
            float left = (0.0f - fireSize) / 2.0f;
            float right = left + fireSize;
            float bottom = 0.0f - fireSize / 2.0f;
            float top = bottom + fireSize;
            float quadZ = -0.5f;
            GlStateManager.translate((float)(-(i * 2 - 1)) * 0.24f, -0.3f, 0.0f);
            GlStateManager.rotate((float)(i * 2 - 1) * 10.0f, 0.0f, 1.0f, 0.0f);
            worldRenderer.begin(7, DefaultVertexFormats.Tma);
            worldRenderer.pos(left, bottom, quadZ).tex(maxU, maxV).endVertex();
            worldRenderer.pos(right, bottom, quadZ).tex(minU, maxV).endVertex();
            worldRenderer.pos(right, top, quadZ).tex(minU, minV).endVertex();
            worldRenderer.pos(left, top, quadZ).tex(maxU, minV).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
            i -= 62;
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
    }

    public void renderPlayerArms(AbstractClientPlayer abstractClientPlayer) {
        this.mc.getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
        Render render = this.renderManager.xfr(this.mc.thePlayer);
        RenderPlayer renderPlayer = (RenderPlayer)render;
        if (!abstractClientPlayer.isInvisible()) {
            GlStateManager.disableCull();
            this.renderRightArm(renderPlayer);
            this.renderLeftArm(renderPlayer);
            GlStateManager.enableCull();
        }
    }

    public void renderBlockOverlay(float partialTicks, TextureAtlasSprite textureAtlasSprite) {
        this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        float overlayShade = 0.1f;
        GlStateManager.color(0.1f, 0.1f, 0.1f, 0.5f);
        GlStateManager.pushMatrix();
        float minX = -1.0f;
        float maxX = 1.0f;
        float minY = -1.0f;
        float maxY = 1.0f;
        float quadZ = -0.5f;
        float minU = textureAtlasSprite.getMinU();
        float maxU = textureAtlasSprite.getMaxU();
        float minV = textureAtlasSprite.getMinV();
        float maxV = textureAtlasSprite.getMaxV();
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        worldRenderer.pos(-1.0, -1.0, -0.5).tex(maxU, maxV).endVertex();
        worldRenderer.pos(1.0, -1.0, -0.5).tex(minU, maxV).endVertex();
        worldRenderer.pos(1.0, 1.0, -0.5).tex(minU, minV).endVertex();
        worldRenderer.pos(-1.0, 1.0, -0.5).tex(maxU, minV).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void applyPitchYawRotation(float pitch, float yaw) {
        GlStateManager.pushMatrix();
        GlStateManager.rotate(pitch, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(yaw, 0.0f, 1.0f, 0.0f);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.popMatrix();
    }

    public void transformEatAnimation(AbstractClientPlayer abstractClientPlayer, float partialTicks) {
        float remainingUseTicks = (float)abstractClientPlayer.getXPSeed() - partialTicks + 1.0f;
        float remainingUseFraction = remainingUseTicks / (float)this.itemToRender.getMaxItemUseDuration();
        float bobOffset = MathHelper.yk(MathHelper.imr(remainingUseTicks / 4.0f * (float)Math.PI) * 0.1f);
        if (remainingUseFraction >= 0.8f) {
            bobOffset = 0.0f;
        }
        GlStateManager.translate(0.0f, bobOffset, 0.0f);
        float eatProgress = 1.0f - (float)Math.pow(remainingUseFraction, 27.0);
        GlStateManager.translate(eatProgress * 0.6f, eatProgress * -0.5f, eatProgress * 0.0f);
        GlStateManager.rotate(eatProgress * 90.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(eatProgress * 10.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(eatProgress * 30.0f, 0.0f, 0.0f, 1.0f);
    }

    public void resetEquippedProgress() {
        this.equippedProgress = 0.0f;
    }

    public void renderItem(EntityLivingBase entityLivingBase, ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        if (itemStack != null) {
            Item item = itemStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            if (this.itemRenderer.shouldRenderItemIn3D(itemStack)) {
                GlStateManager.scale(2.0f, 2.0f, 2.0f);
                if (this.isBlockTranslucent(block)) {
                    GlStateManager.depthMask(false);
                }
            }
            this.itemRenderer.renderItemModelForEntity(itemStack, entityLivingBase, transformType);
            if (this.isBlockTranslucent(block)) {
                GlStateManager.depthMask(true);
            }
            GlStateManager.popMatrix();
        }
    }

    public void applyArmSway(EntityPlayerSP entityPlayerSP, float partialTicks) {
        float armPitch = entityPlayerSP.prevRenderArmPitch + (entityPlayerSP.renderArmPitch - entityPlayerSP.prevRenderArmPitch) * partialTicks;
        float armYaw = entityPlayerSP.prevRenderArmYaw + (entityPlayerSP.renderArmYaw - entityPlayerSP.prevRenderArmYaw) * partialTicks;
        GlStateManager.rotate((entityPlayerSP.rotationPitch - armPitch) * 0.1f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate((entityPlayerSP.rotationYaw - armYaw) * 0.1f, 0.0f, 1.0f, 0.0f);
    }

    public float getMapPitchFactor(float pitch) {
        float pitchFactor = 1.0f - pitch / 45.0f + 0.1f;
        pitchFactor = MathHelper.clamp_float(pitchFactor, 0.0f, 1.0f);
        pitchFactor = -MathHelper.imr(pitchFactor * (float)Math.PI) * 0.5f + 0.5f;
        return pitchFactor;
    }

    public void updateEquippedItem() {
        this.prevEquippedProgress = this.equippedProgress;
        EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
        ItemStack itemStack = entityPlayerSP.inventory.Tdr();
        boolean itemChanged = false;
        if (this.itemToRender != null && itemStack != null) {
            if (!this.itemToRender.getIsItemStackEqual(itemStack)) {
                itemChanged = true;
            }
        } else {
            itemChanged = this.itemToRender != null || itemStack != null;
        }
        float maxProgressStep = 0.4f;
        float targetProgress = itemChanged ? 0.0f : 1.0f;
        float progressStep = MathHelper.clamp_float(targetProgress - this.equippedProgress, -maxProgressStep, maxProgressStep);
        this.equippedProgress += progressStep;
        if (this.equippedProgress < 0.1f) {
            this.itemToRender = itemStack;
            this.equippedItemSlot = entityPlayerSP.inventory.currentItem;
        }
    }

    public void transformSwingProgress(float swingProgress) {
        float swingX = -0.4f * MathHelper.STo(MathHelper.Msr(swingProgress) * (float)Math.PI);
        float swingY = 0.2f * MathHelper.STo(MathHelper.Msr(swingProgress) * (float)Math.PI * 2.0f);
        float swingZ = -0.2f * MathHelper.STo(swingProgress * (float)Math.PI);
        GlStateManager.translate(swingX, swingY, swingZ);
    }

    public ItemRenderer(Minecraft minecraft) {
        this.mc = minecraft;
        this.renderManager = minecraft.getRenderManager();
        this.itemRenderer = minecraft.getRenderItem();
    }

    public void renderWaterOverlayTexture(float partialTicks) {
        float uvScale = 4.0f;
        this.mc.getTextureManager().bindTexture(underwaterTexture);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        float brightness = this.mc.thePlayer.getBrightness(partialTicks);
        GlStateManager.color(brightness, brightness, brightness, 0.5f);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.pushMatrix();
        float uvScaleCopy = uvScale;
        float minX = -1.0f;
        float maxX = 1.0f;
        float minY = -1.0f;
        float maxY = 1.0f;
        float quadZ = -0.5f;
        float uOffset = -this.mc.thePlayer.rotationYaw / 64.0f;
        float vOffset = this.mc.thePlayer.rotationPitch / 64.0f;
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        worldRenderer.pos(-1.0, -1.0, -0.5).tex(uvScale + uOffset, uvScale + vOffset).endVertex();
        worldRenderer.pos(1.0, -1.0, -0.5).tex(0.0f + uOffset, uvScale + vOffset).endVertex();
        worldRenderer.pos(1.0, 1.0, -0.5).tex(0.0f + uOffset, 0.0f + vOffset).endVertex();
        worldRenderer.pos(-1.0, 1.0, -0.5).tex(uvScale + uOffset, 0.0f + vOffset).endVertex();
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
    }

    public void transformBlockAnimation() {
        GlStateManager.translate(-0.5f, 0.2f, 0.0f);
        GlStateManager.rotate(30.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-80.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(60.0f, 0.0f, 1.0f, 0.0f);
    }

    public void renderOverlays(float partialTicks) {
        GlStateManager.disableAlpha();
        if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
            IBlockState iBlockState = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
            EntityPlayerSP entityPlayerSP = this.mc.thePlayer;
            for (int i = 0; i < 8; ++i) {
                double sampleX = entityPlayerSP.posX + (double)(((float)((i >> 0) % 2) - 0.5f) * entityPlayerSP.width * 0.8f);
                double sampleY = entityPlayerSP.posY + (double)(((float)((i >> 1) % 2) - 0.5f) * 0.1f);
                double sampleZ = entityPlayerSP.posZ + (double)(((float)((i >> 2) % 2) - 0.5f) * entityPlayerSP.width * 0.8f);
                BlockPos blockPos = new BlockPos(sampleX, sampleY + (double)entityPlayerSP.getEyeHeight(), sampleZ);
                IBlockState iBlockState2 = this.mc.theWorld.getBlockState(blockPos);
                if (!iBlockState2.getBlock().Ij()) continue;
                iBlockState = iBlockState2;
            }
            if (iBlockState.getBlock().getRenderType() != -1) {
                this.renderBlockOverlay(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(iBlockState));
            }
        }
        if (!this.mc.thePlayer.isSpectator()) {
            if (this.mc.thePlayer.isInsideOfMaterial(Material.PGa)) {
                this.renderWaterOverlayTexture(partialTicks);
            }
            if (this.mc.thePlayer.isBurning()) {
                this.renderFireInFirstPerson(partialTicks);
            }
        }
        GlStateManager.enableAlpha();
    }

    public void transformBowAnimation(float partialTicks, AbstractClientPlayer abstractClientPlayer) {
        GlStateManager.rotate(-18.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(-12.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(-8.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.translate(-0.9f, 0.2f, 0.0f);
        float ticksDrawn = (float)this.itemToRender.getMaxItemUseDuration() - ((float)abstractClientPlayer.getXPSeed() - partialTicks + 1.0f);
        float drawProgress = ticksDrawn / 20.0f;
        drawProgress = (drawProgress * drawProgress + drawProgress * 2.0f) / 3.0f;
        if (drawProgress > 1.0f) {
            drawProgress = 1.0f;
        }
        if (drawProgress > 0.1f) {
            float wobble = MathHelper.STo((ticksDrawn - 0.1f) * 1.3f);
            float drawExcess = drawProgress - 0.1f;
            float wobbleOffset = wobble * drawExcess;
            GlStateManager.translate(wobbleOffset * 0.0f, wobbleOffset * 0.01f, wobbleOffset * 0.0f);
        }
        GlStateManager.translate(drawProgress * 0.0f, drawProgress * 0.0f, drawProgress * 0.1f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f + drawProgress * 0.2f);
    }

    public void setLightmapCoords(AbstractClientPlayer abstractClientPlayer) {
        int combinedLight = this.mc.theWorld.getCombinedLight(new BlockPos(abstractClientPlayer.posX, abstractClientPlayer.posY + (double)abstractClientPlayer.getEyeHeight(), abstractClientPlayer.posZ), 0);
        float blockLight = combinedLight & 0xFFFF;
        float skyLight = combinedLight >> 16;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.defaultTexUnit, blockLight, skyLight);
    }

    public void renderEmptyHand(AbstractClientPlayer abstractClientPlayer, float equipOffset, float swingProgress) {
        float pi = (float)Math.PI;
        float swingOffsetX = -0.3f * MathHelper.STo(MathHelper.Msr(swingProgress) * pi);
        float swingOffsetY = 0.4f * MathHelper.STo(MathHelper.Msr(swingProgress) * pi * 2.0f);
        float swingOffsetZ = -0.4f * MathHelper.STo(swingProgress * pi);
        GlStateManager.translate(swingOffsetX, swingOffsetY, swingOffsetZ);
        GlStateManager.translate(0.64000005f, -0.6f, -0.71999997f);
        GlStateManager.translate(0.0f, equipOffset * -0.6f, 0.0f);
        GlStateManager.rotate(45.0f, 0.0f, 1.0f, 0.0f);
        float swayRoll = MathHelper.STo(swingProgress * swingProgress * pi);
        float swayYaw = MathHelper.STo(MathHelper.Msr(swingProgress) * pi);
        GlStateManager.rotate(swayYaw * 70.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(swayRoll * -20.0f, 0.0f, 0.0f, 1.0f);
        this.mc.getTextureManager().bindTexture(abstractClientPlayer.getLocationSkin());
        GlStateManager.translate(-1.0f, 3.6f, 3.5f);
        GlStateManager.rotate(120.0f, 0.0f, 0.0f, 1.0f);
        GlStateManager.rotate(200.0f, 1.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-135.0f, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(1.0f, 1.0f, 1.0f);
        GlStateManager.translate(5.6f, 0.0f, 0.0f);
        Render render = this.renderManager.xfr(this.mc.thePlayer);
        GlStateManager.disableCull();
        RenderPlayer renderPlayer = (RenderPlayer)render;
        renderPlayer.erg(this.mc.thePlayer);
        GlStateManager.enableCull();
    }

    public void resetEquippedProgress2() {
        this.equippedProgress = 0.0f;
    }
}

