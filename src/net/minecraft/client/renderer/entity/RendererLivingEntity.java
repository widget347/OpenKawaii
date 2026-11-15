/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.nio.FloatBuffer;
import java.util.List;
import kawaii.Kawaii;
import kawaii.command.FriendCommand;
import kawaii.module.combat.Targeting;
import kawaii.module.render.ESP;
import kawaii.render.ColorWrapper;
import kawaii.render.RenderUtils;
import kawaii.render.StencilUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity$1;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity<T extends EntityLivingBase>
extends Render<T> {
    public FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
    public boolean renderOutlines = false;
    public static DynamicTexture field_177096_e;
    public ModelBase mainModel;
    public List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
    public static Logger logger;

    public void transformHeldFull3DItemLayer() {
    }

    public boolean setBrightness(T t, float partialTicks, boolean combineTextures) {
        boolean isHurt;
        float brightness = ((Entity)t).getBrightness(partialTicks);
        int colorMultiplier = this.getColorMultiplier(t, brightness, partialTicks);
        boolean hasAlpha = (colorMultiplier >> 24 & 0xFF) > 0;
        boolean hurtResult = isHurt =((EntityLivingBase)t).hurtTime > 0 || ((EntityLivingBase)t).esa > 0;
        if (!hasAlpha && !isHurt) {
            return false;
        }
        if (!hasAlpha && !combineTextures) {
            return false;
        }
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, OpenGlHelper.gxp);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.yDc);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, OpenGlHelper.gxp);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, OpenGlHelper.GBa);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, OpenGlHelper.JYp);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper.vra, OpenGlHelper.JYp);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.Pea, 770);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        this.brightnessBuffer.position(0);
        if (isHurt) {
            this.brightnessBuffer.put(1.0f);
            this.brightnessBuffer.put(0.0f);
            this.brightnessBuffer.put(0.0f);
            this.brightnessBuffer.put(0.3f);
        } else {
            float alpha = (float)(colorMultiplier >> 24 & 0xFF) / 255.0f;
            float red = (float)(colorMultiplier >> 16 & 0xFF) / 255.0f;
            float green = (float)(colorMultiplier >> 8 & 0xFF) / 255.0f;
            float blue = (float)(colorMultiplier & 0xFF) / 255.0f;
            this.brightnessBuffer.put(red);
            this.brightnessBuffer.put(green);
            this.brightnessBuffer.put(blue);
            this.brightnessBuffer.put(1.0f - alpha);
        }
        this.brightnessBuffer.flip();
        GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
        GlStateManager.setActiveTexture(OpenGlHelper.ctc);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(field_177096_e.getGlTextureId());
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 7681);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        return true;
    }

    public void unsetScoreTeamColor() {
        GlStateManager.enableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
    }

    public float getDeathMaxRotation(T t) {
        return 90.0f;
    }

    public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
        return this.layerRenderers.add((LayerRenderer<T>)(LayerRenderer)layer);
    }

    public int getColorMultiplier(T t, float brightness, float partialTicks) {
        return 0;
    }

    public ModelBase getMainModel() {
        return this.mainModel;
    }

    public boolean setDoRenderBrightness(T t, float partialTicks) {
        return this.setBrightness(t, partialTicks, true);
    }

    public boolean setScoreTeamColor(T t) {
        String formatCode;
        ScorePlayerTeam scorePlayerTeam;
        int teamColor = 0xFFFFFF;
        if (t instanceof EntityPlayer && (scorePlayerTeam = (ScorePlayerTeam)((EntityLivingBase)t).getTeam()) != null && (formatCode = FontRenderer.getFormatFromString(scorePlayerTeam.eDr())).length() >= 2) {
            teamColor = this.getFontRendererFromRenderManager().getCharWidth(formatCode.charAt(1));
        }
        float red = (float)(teamColor >> 16 & 0xFF) / 255.0f;
        float green = (float)(teamColor >> 8 & 0xFF) / 255.0f;
        float blue = (float)(teamColor & 0xFF) / 255.0f;
        GlStateManager.disableLighting();
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        GlStateManager.color(red, green, blue, 1.0f);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.disableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        return true;
    }

    public float interpolateRotation(float previousRotation, float currentRotation, float partialTicks) {
        float rotationDelta;
        for (rotationDelta = currentRotation - previousRotation; rotationDelta < -180.0f; rotationDelta += 360.0f) {
        }
        while (rotationDelta >= 180.0f) {
            rotationDelta -= 360.0f;
        }
        return previousRotation + partialTicks * rotationDelta;
    }

    public float getSwingProgress(T t, float partialTicks) {
        return ((EntityLivingBase)t).getSwingProgress(partialTicks);
    }

    public void preRenderCallback(T t, float partialTicks) {
    }

    public void unsetBrightness() {
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        GlStateManager.enableTexture2D();
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, OpenGlHelper.gxp);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.yDc);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, OpenGlHelper.gxp);
        GL11.glTexEnvi(8960, OpenGlHelper.Uka, OpenGlHelper.yDc);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.Txa, 770);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, 5890);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, 5890);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.setActiveTexture(OpenGlHelper.ctc);
        GlStateManager.disableTexture2D();
        GlStateManager.bindTexture(0);
        GL11.glTexEnvi(8960, 8704, OpenGlHelper.Zea);
        GL11.glTexEnvi(8960, OpenGlHelper.cja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.Inp, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.qLg, 768);
        GL11.glTexEnvi(8960, OpenGlHelper.wfg, 5890);
        GL11.glTexEnvi(8960, OpenGlHelper.WMp, OpenGlHelper.P$a);
        GL11.glTexEnvi(8960, OpenGlHelper._ja, 8448);
        GL11.glTexEnvi(8960, OpenGlHelper.kKp, 770);
        GL11.glTexEnvi(8960, OpenGlHelper.Buc, 5890);
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
    }

    public void rotateCorpse(T t, float ageInTicks, float bodyYaw, float partialTicks) {
        GlStateManager.rotate(180.0f - bodyYaw, 0.0f, 1.0f, 0.0f);
        if (((EntityLivingBase)t).esa > 0) {
            float deathProgress = ((float)((EntityLivingBase)t).esa + partialTicks - 1.0f) / 20.0f * 1.6f;
            if ((deathProgress = MathHelper.Msr(deathProgress)) > 1.0f) {
                deathProgress = 1.0f;
            }
            GlStateManager.rotate(deathProgress * this.getDeathMaxRotation(t), 0.0f, 0.0f, 1.0f);
        } else {
            String entityName = EnumChatFormatting.getTextWithoutFormattingCodes(((Entity)t).getName());
            if (entityName != null && (entityName.equals("Dinnerbone") || entityName.equals("Grumm")) && (!(t instanceof EntityPlayer) || ((EntityPlayer)t).isWearing(EnumPlayerModelParts.CAPE))) {
                GlStateManager.translate(0.0f, ((EntityLivingBase)t).height + 0.1f, 0.0f);
                GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
            }
        }
    }

    public void setRenderOutlines(boolean outlines) {
        this.renderOutlines = outlines;
    }

    static {
        logger = LogManager.getLogger();
        field_177096_e = new DynamicTexture(16, 16);
        int[] textureData = field_177096_e.getTextureData();
        for (int i = 0; i < 256; ++i) {
            textureData[i] = -1;
        }
        field_177096_e.updateDynamicTexture();
    }

    @Override
    public void renderName(T t, double x, double y, double z) {
        if (this.canRenderName(t)) {
            float maxRenderDistance;
            double distanceSq = ((Entity)t).getDistanceSqToEntity(this.renderManager.xVg);
            float renderRange = maxRenderDistance = ((Entity)t).isEating() ? 32.0f : 64.0f;
            if (distanceSq < (double)(maxRenderDistance * maxRenderDistance)) {
                String displayName = ((Entity)t).getDisplayName().getFormattedText();
                float nameScale = 0.02666667f;
                GlStateManager.alphaFunc(516, 0.1f);
                if (((Entity)t).isEating()) {
                    FontRenderer fontRenderer = this.getFontRendererFromRenderManager();
                    GlStateManager.pushMatrix();
                    GlStateManager.translate((float)x, (float)y + ((EntityLivingBase)t).height + 0.5f - (((EntityLivingBase)t).isChild() ? ((EntityLivingBase)t).height / 2.0f : 0.0f), (float)z);
                    GL11.glNormal3f(0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(-this.renderManager.playerViewY, 0.0f, 1.0f, 0.0f);
                    GlStateManager.rotate(this.renderManager.playerViewX, 1.0f, 0.0f, 0.0f);
                    GlStateManager.scale(-0.02666667f, -0.02666667f, 0.02666667f);
                    GlStateManager.translate(0.0f, 9.374999f, 0.0f);
                    GlStateManager.disableLighting();
                    GlStateManager.depthMask(false);
                    GlStateManager.enableBlend();
                    GlStateManager.disableTexture2D();
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                    int halfWidth = fontRenderer.getStringWidth(displayName) / 2;
                    Tessellator tessellator = Tessellator.getInstance();
                    WorldRenderer worldRenderer = tessellator.getWorldRenderer();
                    worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                    worldRenderer.pos(-halfWidth - 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldRenderer.pos(-halfWidth - 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldRenderer.pos(halfWidth + 1, 8.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    worldRenderer.pos(halfWidth + 1, -1.0, 0.0).color(0.0f, 0.0f, 0.0f, 0.25f).endVertex();
                    tessellator.draw();
                    GlStateManager.enableTexture2D();
                    GlStateManager.depthMask(true);
                    fontRenderer.drawString(displayName, -fontRenderer.getStringWidth(displayName) / 2, 0, 0x20FFFFFF);
                    GlStateManager.enableLighting();
                    GlStateManager.disableBlend();
                    GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                    GlStateManager.popMatrix();
                } else {
                    this.renderOffsetLivingLabel(t, x, y - (((EntityLivingBase)t).isChild() ? (double)(((EntityLivingBase)t).height / 2.0f) : 0.0), z, displayName, 0.02666667f, distanceSq);
                }
            }
        }
    }

    public RendererLivingEntity(RenderManager renderManager, ModelBase modelBase, float shadowRadius) {
        super(renderManager);
        this.mainModel = modelBase;
        this.shadowSize = shadowRadius;
    }

    public void renderLivingAt(T t, double x, double y, double z) {
        GlStateManager.translate((float)x, (float)y, (float)z);
    }

    public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
        return this.layerRenderers.remove(layer);
    }

    public void renderModel(T t, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
        boolean renderTranslucent;
        boolean visibleToAll = !((Entity)t).isInvisible();
        boolean translucentResult = renderTranslucent =!visibleToAll && !((Entity)t).isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
        if (visibleToAll || renderTranslucent) {
            if (!this.bindEntityTexture(t)) {
                return;
            }
            if (renderTranslucent) {
                GlStateManager.pushMatrix();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 0.15f);
                GlStateManager.depthMask(false);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(770, 771);
                GlStateManager.alphaFunc(516, 0.003921569f);
            }
            if (Kawaii.INSTANCE.getModuleManager().getModule(ESP.class).isEnabled() && Targeting.isValidTarget(t, ESP.playersSetting.getValue(), ESP.animalsSetting.getValue(), ESP.mobsSetting.getValue(), ESP.invisiblesSetting.getValue(), false)) {
                GL11.glPushMatrix();
                GlStateManager.depthMask(true);
                int outlineColor = new ColorWrapper(255, 255, 255).rgbValue();
                if (FriendCommand.isFriend(t)) {
                    outlineColor = new ColorWrapper(0, 255, 0).rgbValue();
                }
                this.mainModel.render((Entity)t, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                StencilUtil.beginStencilWrite();
                RenderUtils.setColor(outlineColor);
                this.mainModel.render((Entity)t, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                StencilUtil.setupStencilClear();
                RenderUtils.setColor(outlineColor);
                this.mainModel.render((Entity)t, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                RenderUtils.setColor(outlineColor);
                StencilUtil.setupStencilOutline();
                StencilUtil.setupOutlinePass();
                RenderUtils.setColor(outlineColor);
                this.mainModel.render((Entity)t, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
                StencilUtil.restoreStencilState();
                GlStateManager.popMatrix();
            }
            this.mainModel.render((Entity)t, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
            if (renderTranslucent) {
                GlStateManager.disableBlend();
                GlStateManager.alphaFunc(516, 0.1f);
                GlStateManager.popMatrix();
                GlStateManager.depthMask(true);
            }
        }
    }

    @Override
    public void doRender(T t, double x, double y, double z, float entityYaw, float partialTicks) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();
        this.mainModel.swingProgress = this.getSwingProgress(t, partialTicks);
        this.mainModel.n$p = ((Entity)t).isRiding();
        this.mainModel.cpc = ((EntityLivingBase)t).isChild();
        try {
            float rotationValue;
            float bodyYaw = this.interpolateRotation(((EntityLivingBase)t).iFa, ((EntityLivingBase)t).eXp, partialTicks);
            float headYaw = this.interpolateRotation(((EntityLivingBase)t).FMp, ((EntityLivingBase)t).renderYawOffset, partialTicks);
            float netHeadYaw = headYaw - bodyYaw;
            if (((Entity)t).isRiding() && ((EntityLivingBase)t).ridingEntity instanceof EntityLivingBase) {
                EntityLivingBase mountEntity = (EntityLivingBase)((EntityLivingBase)t).ridingEntity;
                bodyYaw = this.interpolateRotation(mountEntity.iFa, mountEntity.eXp, partialTicks);
                netHeadYaw = headYaw - bodyYaw;
                rotationValue = MathHelper.wrapAngleTo180_float(netHeadYaw);
                if (rotationValue < -85.0f) {
                    rotationValue = -85.0f;
                }
                if (rotationValue >= 85.0f) {
                    rotationValue = 85.0f;
                }
                bodyYaw = headYaw - rotationValue;
                if (rotationValue * rotationValue > 2500.0f) {
                    bodyYaw += rotationValue * 0.2f;
                }
            }
            float headPitch = ((EntityLivingBase)t).prevRotationPitch + (((EntityLivingBase)t).rotationPitch - ((EntityLivingBase)t).prevRotationPitch) * partialTicks;
            this.renderLivingAt(t, x, y, z);
            rotationValue = this.handleRotationFloat(t, partialTicks);
            this.rotateCorpse(t, rotationValue, bodyYaw, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.scale(-1.0f, -1.0f, 1.0f);
            this.preRenderCallback(t, partialTicks);
            float modelScale = 0.0625f;
            GlStateManager.translate(0.0f, -1.5078125f, 0.0f);
            float limbSwingAmount = ((EntityLivingBase)t).Kja + (((EntityLivingBase)t).xmg - ((EntityLivingBase)t).Kja) * partialTicks;
            float limbSwing = ((EntityLivingBase)t).MPc - ((EntityLivingBase)t).xmg * (1.0f - partialTicks);
            if (((EntityLivingBase)t).isChild()) {
                limbSwing *= 3.0f;
            }
            if (limbSwingAmount > 1.0f) {
                limbSwingAmount = 1.0f;
            }
            GlStateManager.enableAlpha();
            this.mainModel.setLivingAnimations((EntityLivingBase)t, limbSwing, limbSwingAmount, partialTicks);
            this.mainModel.setRotationAngles(limbSwing, limbSwingAmount, rotationValue, netHeadYaw, headPitch, 0.0625f, (Entity)t);
            if (this.renderOutlines) {
                boolean teamColorApplied = this.setScoreTeamColor(t);
                this.renderModel(t, limbSwing, limbSwingAmount, rotationValue, netHeadYaw, headPitch, 0.0625f);
                if (teamColorApplied) {
                    this.unsetScoreTeamColor();
                }
            } else {
                boolean brightnessApplied = this.setDoRenderBrightness(t, partialTicks);
                this.renderModel(t, limbSwing, limbSwingAmount, rotationValue, netHeadYaw, headPitch, 0.0625f);
                if (brightnessApplied) {
                    this.unsetBrightness();
                }
                GlStateManager.depthMask(true);
                if (!(t instanceof EntityPlayer) || !((EntityPlayer)t).isSpectator()) {
                    this.renderLayers(t, limbSwing, limbSwingAmount, partialTicks, rotationValue, netHeadYaw, headPitch, 0.0625f);
                }
            }
            GlStateManager.disableRescaleNormal();
        }
        catch (Exception exception) {
            logger.error("Couldn't render entity", (Throwable)exception);
        }
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.enableTexture2D();
        GlStateManager.setActiveTexture(OpenGlHelper.gxp);
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
        if (!this.renderOutlines) {
            super.doRender(t, x, y, z, entityYaw, partialTicks);
        }
    }

    @Override
    public boolean canRenderName(T t) {
        EntityPlayerSP entityPlayerSP = Minecraft.getMinecraft().thePlayer;
        if (t instanceof EntityPlayer && t != entityPlayerSP) {
            Team team = ((EntityLivingBase)t).getTeam();
            Team viewerTeam = entityPlayerSP.getTeam();
            if (team != null) {
                Team.EnumVisible enumVisible = team.$I();
                switch (RendererLivingEntity$1.$SwitchMap$net$minecraft$scoreboard$Team$EnumVisible[enumVisible.ordinal()]) {
                    case 1: {
                        return true;
                    }
                    case 2: {
                        return false;
                    }
                    case 3: {
                        return viewerTeam == null || team.isSameTeam(viewerTeam);
                    }
                    case 4: {
                        return viewerTeam == null || !team.isSameTeam(viewerTeam);
                    }
                }
                return true;
            }
        }
        return Minecraft.isFancyGraphicsEnabled() && t != this.renderManager.xVg && !((Entity)t).isInvisibleToPlayer(entityPlayerSP) && ((EntityLivingBase)t).riddenByEntity == null;
    }

    public void renderLayers(T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        for (LayerRenderer<T> layerRenderer : this.layerRenderers) {
            boolean brightnessSet = this.setBrightness(t, partialTicks, layerRenderer.shouldCombineTextures());
            layerRenderer.doRenderLayer(t, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            if (!brightnessSet) continue;
            this.unsetBrightness();
        }
    }

    public float handleRotationFloat(T t, float partialTicks) {
        return (float)((EntityLivingBase)t).ticksExisted + partialTicks;
    }
}

