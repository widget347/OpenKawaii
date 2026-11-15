/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.render;

import kawaii.command.FriendCommand;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.render.Blur;
import kawaii.module.render.Hud;
import kawaii.render.BlurRenderer;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.ModulePanel;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class Radar
extends Module {
    public static NumberSetting sizeSetting;
    public ModulePanel panel = new ModulePanel("Radar", this, 15, 15, 125.0, 125.0);
    public static BooleanSetting animalsSetting;
    public static BooleanSetting invisiblesSetting;
    public static BooleanSetting playersSetting;
    public static BooleanSetting mobsSetting;

    public Radar() {
        super("Radar", "Allows you to see players via a radar", Category.visuals);
        this.addSettings(sizeSetting, new MultiBooleanSetting("Targets", playersSetting, animalsSetting, mobsSetting, invisiblesSetting));
    }

    @Override
    public void onEvent(EventRender2D eventRender2D) {
        double halfDivisor = 2.0;
        this.panel.beginRender();
        double sizeX = sizeSetting.getValue();
        double sizeY = sizeSetting.getValue();
        this.panel.setVisible(true);
        this.panel.setPosition(sizeX, sizeY);
        if (eventRender2D.getEventType() == EventType.post) {
            Gui.drawRect(0.0, 0.0, sizeX, sizeY, Hud.getHudColor().getRGB());
        }
        GlStateManager.translate(sizeX / halfDivisor, sizeY / halfDivisor, 0.0);
        GlStateManager.rotate(-Radar.mc.thePlayer.rotationYaw, 0.0f, 0.0f, 1.0f);
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        for (Entity entity : Radar.mc.theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase)) continue;
            int entityColor = 0;
            if (entity.isInvisible() && !invisiblesSetting.getValue().booleanValue()) continue;
            if (mobsSetting.getValue().booleanValue() && entity instanceof EntityMob) {
                entityColor = -51930;
            }
            if (animalsSetting.getValue().booleanValue() && entity instanceof EntityAnimal) {
                entityColor = -13041685;
            }
            if (playersSetting.getValue().booleanValue() && entity instanceof EntityPlayer) {
                entityColor = -51144;
                if (FriendCommand.isFriend((EntityLivingBase)entity)) {
                    entityColor = -13575376;
                }
            }
            double deltaX = Radar.mc.thePlayer.posX - entity.posX;
            double deltaZ = Radar.mc.thePlayer.posZ - entity.posZ;
            if (!(deltaX < sizeX) && !(deltaZ < sizeY)) continue;
            Gui.drawRect(deltaX - 1.0, deltaZ - 1.0, deltaX + 1.0, deltaZ + 1.0, entityColor);
        }
        GlStateManager.rotate(Radar.mc.thePlayer.rotationYaw, 0.0f, 0.0f, 1.0f);
        if (eventRender2D.getEventType() == EventType.post) {
            RenderUtils.drawImageWithAlpha(-3.0, -3.0, 6, 6, new ResourceLocation("kawaii/images/triangle.png"), 1.0);
        }
        GlStateManager.translate(-(sizeX / halfDivisor), -(sizeY / halfDivisor), 0.0);
        this.panel.endRender();
        if (Blur.blurActive.booleanValue() && Blur.blurRadar.getValue().booleanValue()) {
            BlurRenderer.queueBlur(this.panel.getPositionX() - 1, this.panel.getPositionY() - 1, (double)(this.panel.getPositionX() + 1) + sizeX, (double)(1 + this.panel.getPositionY()) + sizeY, halfDivisor);
        }
        super.onEvent(eventRender2D);
    }

    static {
        playersSetting = new BooleanSetting("Players", true);
        animalsSetting = new BooleanSetting("Animals", true);
        mobsSetting = new BooleanSetting("Mobs", true);
        invisiblesSetting = new BooleanSetting("Invisibles", false);
        sizeSetting = new NumberSetting("Size", 100.0, 1.0, 75.0, 200.0);
    }
}

