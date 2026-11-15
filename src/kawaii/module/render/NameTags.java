/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.event.EventRender3D;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.combat.Targeting;
import kawaii.render.RenderUtils;
import kawaii.setting.NumberSetting;
import kawaii.ui.font.CFontRenderer;
import kawaii.ui.font.FontLoader;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

public class NameTags
extends Module {
    public NumberSetting scale = new NumberSetting("Scale", 1.0, 0.1, 0.7, 1.5);

    @Override
    public void onEvent(EventRender3D eventRender3D) {
        boolean lightingWasEnabled = GL11.glIsEnabled(2896);
        for (EntityPlayer entityPlayer : Targeting.getTargets()) {
            if (entityPlayer == NameTags.mc.thePlayer || entityPlayer.isInvisible()) continue;
            String displayName = entityPlayer.getDisplayName().getFormattedText();
            CFontRenderer cFontRenderer = FontLoader.latoBlack40;
            float baseSize = 0.6f;
            float textScale = 0.016666668f * baseSize;
            textScale = (float)((double)textScale * this.scale.getValue());
            GlStateManager.pushMatrix();
            double renderX = (double)RenderUtils.lerp(entityPlayer.posX, entityPlayer.lastTickPosX, eventRender3D.getValue()) - NameTags.mc.getRenderManager().renderPosX;
            double renderY = (double)RenderUtils.lerp(entityPlayer.posY + (double)entityPlayer.height + 0.5, entityPlayer.lastTickPosY + (double)entityPlayer.height + 0.5, eventRender3D.getValue()) - NameTags.mc.getRenderManager().renderPosY;
            double renderZ = (double)RenderUtils.lerp(entityPlayer.posZ, entityPlayer.lastTickPosZ, eventRender3D.getValue()) - NameTags.mc.getRenderManager().renderPosZ;
            GlStateManager.translate(renderX, renderY, renderZ);
            GL11.glNormal3f(0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(-NameTags.mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(NameTags.mc.getRenderManager().playerViewX, 1.0f, 0.0f, 0.0f);
            GlStateManager.scale(-textScale, -textScale, textScale);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            int nameYOffset = 0;
            if (displayName.equals("deadmau5")) {
                nameYOffset = -10;
            }
            int halfWidth = cFontRenderer.getStringWidth(displayName) / 2;
            GlStateManager.disableTexture2D();
            RenderUtils.drawRoundedRect((float)(-cFontRenderer.getStringWidth(displayName)) / 2.0f - 3.0f, -3.0, cFontRenderer.getStringWidth(displayName) + 6, cFontRenderer.getHeight() + 6, 5.0, Integer.MIN_VALUE);
            GlStateManager.enableTexture2D();
            cFontRenderer.drawString(displayName, (float)(-cFontRenderer.getStringWidth(displayName)) / 2.0f, nameYOffset, 0x20FFFFFF);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            cFontRenderer.drawString(displayName, (float)(-cFontRenderer.getStringWidth(displayName)) / 2.0f, nameYOffset, -1);
            if (lightingWasEnabled) {
                GlStateManager.enableLighting();
            }
            GlStateManager.disableBlend();
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            GlStateManager.popMatrix();
        }
        super.onEvent(eventRender3D);
    }

    public NameTags() {
        super("Nametags", "Render Nametags above players head", Category.visuals);
        this.addSettings(this.scale);
    }
}

