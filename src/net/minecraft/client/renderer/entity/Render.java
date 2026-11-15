/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import kawaii.Kawaii;
import kawaii.module.render.NameTags;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity> {
    public float shadowOpaque = 1.0f;
    public static ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
    public float shadowSize;
    public RenderManager renderManager;

    public void doRenderShadowAndFire(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        if (this.renderManager.options != null) {
            double distanceToCamera;
            float shadowAlpha;
            if (this.renderManager.options.entityShadows && this.shadowSize > 0.0f && !entity.isInvisible() && this.renderManager.gzo() && (shadowAlpha = (float)((1.0 - (distanceToCamera = this.renderManager.getDistanceToCamera(entity.posX, entity.posY, entity.posZ)) / 256.0) * (double)this.shadowOpaque)) > 0.0f) {
                this.renderShadow(entity, x, y, z, shadowAlpha, partialTicks);
            }
            if (!(!entity.canRenderOnFire() || entity instanceof EntityPlayer && ((EntityPlayer)entity).isSpectator())) {
                this.renderEntityOnFire(entity, x, y, z, partialTicks);
            }
        }
    }

    public void bindTexture(ResourceLocation resourceLocation) {
        this.renderManager.renderEngine.bindTexture(resourceLocation);
    }

    public boolean canRenderName(T entity) {
        return ((Entity)entity).getAlwaysRenderNameTagForRender() && ((Entity)entity).hasCustomName();
    }

    public void renderOffsetLivingLabel(T entity, double x, double y, double z, String label, float f, double labelOffset) {
        this.renderLivingLabel(entity, label, x, y, z, 64);
    }

    public RenderManager getRenderManager() {
        return this.renderManager;
    }

    public void renderName(T entity, double x, double y, double z) {
        if (this.canRenderName(entity)) {
            this.renderLivingLabel(entity, ((Entity)entity).getDisplayName().getFormattedText(), x, y, z, 64);
        }
    }

    public FontRenderer getFontRendererFromRenderManager() {
        return this.renderManager.getFontRenderer();
    }

    public void renderBlockDamage(Block block, double x, double y, double z, BlockPos blockPos, float shadowAlpha, float shadowRadius, double offsetX, double offsetY, double offsetZ) {
        double shadowDivisor = 2.0;
        if (block.isFullCube()) {
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            double damageAlpha = ((double)shadowAlpha - (y - ((double)blockPos.getY() + offsetY)) / shadowDivisor) * 0.5 * (double)this.getWorldFromRenderManager().getLightBrightness(blockPos);
            if (damageAlpha >= 0.0) {
                if (damageAlpha > 1.0) {
                    damageAlpha = 1.0;
                }
                double minX = (double)blockPos.getX() + block.UO() + offsetX;
                double maxX = (double)blockPos.getX() + block.kor() + offsetX;
                double vertexY = (double)blockPos.getY() + block.$$r() + offsetY + 0.015625;
                double minZ = (double)blockPos.getZ() + block.KVo() + offsetZ;
                double maxZ = (double)blockPos.getZ() + block.wuo() + offsetZ;
                float uForMinX = (float)((x - minX) / shadowDivisor / (double)shadowRadius + 0.5);
                float uForMaxX = (float)((x - maxX) / shadowDivisor / (double)shadowRadius + 0.5);
                float vForMinZ = (float)((z - minZ) / shadowDivisor / (double)shadowRadius + 0.5);
                float vForMaxZ = (float)((z - maxZ) / shadowDivisor / (double)shadowRadius + 0.5);
                worldRenderer.pos(minX, vertexY, minZ).tex(uForMinX, vForMinZ).color(1.0f, 1.0f, 1.0f, (float)damageAlpha).endVertex();
                worldRenderer.pos(minX, vertexY, maxZ).tex(uForMinX, vForMaxZ).color(1.0f, 1.0f, 1.0f, (float)damageAlpha).endVertex();
                worldRenderer.pos(maxX, vertexY, maxZ).tex(uForMaxX, vForMaxZ).color(1.0f, 1.0f, 1.0f, (float)damageAlpha).endVertex();
                worldRenderer.pos(maxX, vertexY, minZ).tex(uForMaxX, vForMinZ).color(1.0f, 1.0f, 1.0f, (float)damageAlpha).endVertex();
            }
        }
    }

    public abstract ResourceLocation getEntityTexture(T var1);

    public void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
        GlStateManager.disableLighting();
        TextureMap textureMap = Minecraft.getMinecraft().getTextureMapBlocks();
        TextureAtlasSprite textureAtlasSprite = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_0");
        TextureAtlasSprite textureAtlasSprite2 = textureMap.getAtlasSprite("minecraft:blocks/fire_layer_1");
        GlStateManager.pushMatrix();
        GlStateManager.translate((float)x, (float)y, (float)z);
        float fireScale = entity.width * 1.4f;
        GlStateManager.scale(fireScale, fireScale, fireScale);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        float halfWidth = 0.5f;
        float xOffset = 0.0f;
        float remainingHeight = entity.height / fireScale;
        float yOffset = (float)(entity.posY - entity.getEntityBoundingBox().Bxp);
        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.translate(0.0f, 0.0f, -0.3f + (float)((int)remainingHeight) * 0.02f);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float zOffset = 0.0f;
        int layerIndex = 0;
        worldRenderer.begin(7, DefaultVertexFormats.Tma);
        while (remainingHeight > 0.0f) {
            TextureAtlasSprite layerSprite = layerIndex % 2 == 0 ? textureAtlasSprite : textureAtlasSprite2;
            this.bindTexture(TextureMap.locationBlocksTexture);
            float minU = layerSprite.getMinU();
            float minV = layerSprite.getMinV();
            float maxU = layerSprite.getMaxU();
            float maxV = layerSprite.getMaxV();
            if (layerIndex / 2 % 2 == 0) {
                float swapU = maxU;
                maxU = minU;
                minU = swapU;
            }
            worldRenderer.pos(halfWidth - xOffset, 0.0f - yOffset, zOffset).tex(maxU, maxV).endVertex();
            worldRenderer.pos(-halfWidth - xOffset, 0.0f - yOffset, zOffset).tex(minU, maxV).endVertex();
            worldRenderer.pos(-halfWidth - xOffset, 1.4f - yOffset, zOffset).tex(minU, minV).endVertex();
            worldRenderer.pos(halfWidth - xOffset, 1.4f - yOffset, zOffset).tex(maxU, minV).endVertex();
            remainingHeight -= 0.45f;
            yOffset -= 0.45f;
            halfWidth *= 0.9f;
            zOffset += 0.03f;
            ++layerIndex;
        }
        tessellator.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    public boolean shouldRender(T entity, ICamera camera, double camX, double camY, double camZ) {
        double halfSize = 2.0;
        AxisAlignedBB axisAlignedBB = ((Entity)entity).getEntityBoundingBox();
        if (axisAlignedBB.hasNanCoordinates() || axisAlignedBB.getAverageEdgeLength() == 0.0) {
            axisAlignedBB = new AxisAlignedBB(((Entity)entity).posX - halfSize, ((Entity)entity).posY - halfSize, ((Entity)entity).posZ - halfSize, ((Entity)entity).posX + halfSize, ((Entity)entity).posY + halfSize, ((Entity)entity).posZ + halfSize);
        }
        return ((Entity)entity).isInRangeToRender3d(camX, camY, camZ) && (((Entity)entity).ignoreFrustumCheck || camera.isBoundingBoxInFrustum(axisAlignedBB));
    }

    public Render(RenderManager renderManager) {
        this.renderManager = renderManager;
    }

    public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
        this.renderName(entity, x, y, z);
    }

    public void renderLivingLabel(T entity, String label, double x, double y, double z, int maxDistance) {
        if (Kawaii.INSTANCE.getModuleManager().getModule(NameTags.class).isEnabled() && !((Entity)entity).isInvisible()) {
            return;
        }
        double distanceSq = ((Entity)entity).getDistanceSqToEntity(this.renderManager.xVg);
        if (distanceSq <= (double)(maxDistance * maxDistance)) {
            FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
            float scaleFactor = 1.6f;
            float labelScale = 0.016666668f * scaleFactor;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0f, (float)y + ((Entity)entity).height + 0.5f, (float)z);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-labelScale, -labelScale, labelScale);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldRenderer = tessellator.getWorldRenderer();
            int textYOffset = 0;
            if (label.equals("deadmau5")) {
                textYOffset = -10;
            }
            int halfTextWidth = fontRenderer.getStringWidth(label) / 2;
            GlStateManager.disableTexture2D();
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(-halfTextWidth - 1, -1 + textYOffset, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(-halfTextWidth - 1, 8 + textYOffset, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(halfTextWidth + 1, 8 + textYOffset, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            worldRenderer.pos(halfTextWidth + 1, -1 + textYOffset, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
            fontRenderer.drawString(label, -fontRenderer.getStringWidth(label) / 2, textYOffset, 0x20FFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            fontRenderer.drawString(label, -fontRenderer.getStringWidth(label) / 2, textYOffset, -1);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
    }

    public boolean bindEntityTexture(T entity) {
        ResourceLocation resourceLocation = this.getEntityTexture(entity);
        if (resourceLocation == null) {
            return false;
        }
        this.bindTexture(resourceLocation);
        return true;
    }

    public World getWorldFromRenderManager() {
        return this.renderManager.worldObj;
    }

    public void renderShadow(Entity entity, double x, double y, double z, float shadowAlpha, float partialTicks) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        this.renderManager.renderEngine.bindTexture(shadowTextures);
        World world = this.getWorldFromRenderManager();
        GlStateManager.depthMask(false);
        float shadowRadius = this.shadowSize;
        if (entity instanceof EntityLiving) {
            EntityLiving entityLiving = (EntityLiving)entity;
            shadowRadius *= entityLiving.getRenderSizeModifier();
            if (entityLiving.isChild()) {
                shadowRadius *= 0.5f;
            }
        }
        double interpolatedX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)partialTicks;
        double interpolatedY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)partialTicks;
        double interpolatedZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)partialTicks;
        int minBlockX = MathHelper.Vlg(interpolatedX - (double)shadowRadius);
        int maxBlockX = MathHelper.Vlg(interpolatedX + (double)shadowRadius);
        int minBlockY = MathHelper.Vlg(interpolatedY - (double)shadowRadius);
        int maxBlockY = MathHelper.Vlg(interpolatedY);
        int minBlockZ = MathHelper.Vlg(interpolatedZ - (double)shadowRadius);
        int maxBlockZ = MathHelper.Vlg(interpolatedZ + (double)shadowRadius);
        double offsetX = x - interpolatedX;
        double offsetY = y - interpolatedY;
        double offsetZ = z - interpolatedZ;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        worldRenderer.begin(7, DefaultVertexFormats.Rgp);
        for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(minBlockX, minBlockY, minBlockZ), new BlockPos(maxBlockX, maxBlockY, maxBlockZ))) {
            Block block = world.getBlockState(blockPos.down()).getBlock();
            if (block.getRenderType() == -1 || world.getLightFromNeighbors(blockPos) <= 3) continue;
            this.renderBlockDamage(block, x, y, z, blockPos, shadowAlpha, shadowRadius, offsetX, offsetY, offsetZ);
        }
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }

    public static void renderOffsetAABB(AxisAlignedBB axisAlignedBB, double x, double y, double z) {
        float normalSign = -1.0f;
        GlStateManager.disableTexture2D();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldRenderer = tessellator.getWorldRenderer();
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        worldRenderer.h$o(x, y, z);
        worldRenderer.begin(7, DefaultVertexFormats.WFg);
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(0.0f, 0.0f, normalSign).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(0.0f, 0.0f, normalSign).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(0.0f, 0.0f, normalSign).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(0.0f, 0.0f, normalSign).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(0.0f, 0.0f, 1.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(0.0f, normalSign, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(0.0f, normalSign, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(0.0f, normalSign, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(0.0f, normalSign, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(0.0f, 1.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(normalSign, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(normalSign, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(normalSign, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.Z$g, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(normalSign, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.$$g).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.$$g).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.aCa, axisAlignedBB.H_p).normal(1.0f, 0.0f, 0.0f).endVertex();
        worldRenderer.pos(axisAlignedBB.$Ha, axisAlignedBB.Bxp, axisAlignedBB.H_p).normal(1.0f, 0.0f, 0.0f).endVertex();
        tessellator.draw();
        worldRenderer.h$o(0.0, 0.0, 0.0);
        GlStateManager.enableTexture2D();
    }
}

