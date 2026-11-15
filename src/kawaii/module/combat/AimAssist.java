/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import java.util.ArrayList;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.ImageInitEvent;
import kawaii.event.SettingsUpdateEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.combat.Killaura;
import kawaii.module.combat.Targeting;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.NumberSetting;
import kawaii.util.RotationUtil;
import kawaii.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class AimAssist
extends Module {
    public NumberSetting speedSetting;
    public Timer aimTimer = new Timer();
    public ModeSetting sortingSetting = new ModeSetting("Sorting", "Fov", "Distance");
    public EntityLivingBase target = null;
    public NumberSetting reachSetting;
    public BooleanSetting drawLineSetting;
    public float[] rotationDelta;
    public NumberSetting fovLimitSetting;
    public BooleanSetting drawFovCircleSetting;

    @Override
    public void onEvent(SettingsUpdateEvent settingsUpdateEvent) {
        if (this.sortingSetting.getMode().equalsIgnoreCase("Fov")) {
            this.fovLimitSetting.setVisible(false);
            this.drawFovCircleSetting.setVisible(false);
        } else {
            this.fovLimitSetting.setVisible(true);
            this.drawFovCircleSetting.setVisible(true);
        }
    }

    public boolean isWithinFov(EntityLivingBase entityLivingBase) {
        return Math.abs(Targeting.getRotationsTo(entityLivingBase)[0]) > this.fovLimitSetting.getValue() / 2.0;
    }

    public AimAssist() {
        super("AimAssist", "Assists you to hit the target", Category.combat);
        this.speedSetting = new NumberSetting("Speed", 1.0, 0.01, 0.1, 10.0);
        this.reachSetting = new NumberSetting("Reach", 3.0, 1.0, 0.0, 6.0, "Blocks");
        this.fovLimitSetting = new NumberSetting("Fov Limit", 3.0, 1.0, 1.0, 360.0, "deg");
        this.drawFovCircleSetting = new BooleanSetting("Draw Fov Circle", false);
        this.drawLineSetting = new BooleanSetting("Draw Line", true);
        this.addSettings(this.sortingSetting, this.speedSetting, this.reachSetting, this.fovLimitSetting, this.drawFovCircleSetting, this.drawLineSetting);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        Object value;
        float halfCircle = 180.0f;
        float two = 2.0f;
        if (cancellableEvent instanceof EventRender2D) {
            value = new ScaledResolution(mc);
            if (!this.drawFovCircleSetting.isVisible() && this.drawFovCircleSetting.getValue().booleanValue()) {
                double circleDiameter = this.fovLimitSetting.getValue() * 4.0;
                RenderUtils.drawRoundedOutline((double)((float)((ScaledResolution)value).getScaledWidth() / two) - circleDiameter / 2.0, (double)((float)((ScaledResolution)value).getScaledHeight() / two) - circleDiameter / 2.0, circleDiameter, circleDiameter, circleDiameter, two, -1);
            }
            if (!this.drawLineSetting.isVisible() && this.drawLineSetting.getValue().booleanValue() && this.rotationDelta != null) {
                GL11.glPushMatrix();
                GL11.glDisable(3553);
                GL11.glEnable(2848);
                GL11.glEnable(2852);
                GL11.glBegin(3);
                GL11.glColor4d(0.5, 0.5, 0.5, 0.5);
                GL11.glVertex3d((float)((ScaledResolution)value).getScaledWidth() / two, (float)((ScaledResolution)value).getScaledHeight() / two, 0.0);
                GL11.glVertex3d((double)((float)((ScaledResolution)value).getScaledWidth() / two) + (double)this.rotationDelta[0] * 4.25, (double)((float)((ScaledResolution)value).getScaledHeight() / two) + (double)this.rotationDelta[1] * 4.25, 0.0);
                GL11.glEnd();
                GL11.glDisable(2852);
                GL11.glEnable(3553);
                GL11.glDisable(2848);
                GL11.glPopMatrix();
            }
        }
        if (cancellableEvent instanceof ImageInitEvent) {
            this.setSuffix("" + this.speedSetting.getValue());
            this.rotationDelta = null;
            if (Minecraft.getMinecraft().currentScreen == null && Mouse.isButtonDown(0)) {
                this.target = null;
                value = Killaura.getTargets(this.reachSetting.getValue());
                ((ArrayList<EntityLivingBase>)value).removeIf(this::isWithinFov);
                ArrayList sortedTargets = null;
                switch (this.sortingSetting.getMode()) {
                    case "Fov": {
                        sortedTargets = Targeting.sortTargetsByRotation((ArrayList)value, this.reachSetting.getValue());
                        break;
                    }
                    case "Distance": {
                        sortedTargets = Targeting.sortTargetsByDistance((ArrayList)value, this.reachSetting.getValue());
                    }
                }
                if (sortedTargets != null && sortedTargets.size() > 0 && (!this.sortingSetting.getMode().equalsIgnoreCase("Fov") || Targeting.getRotationsTo((EntityLivingBase)sortedTargets.get(0))[0] < this.fovLimitSetting.getValue() / 2.0 && Targeting.getRotationsTo((EntityLivingBase)sortedTargets.get(0))[1] < this.fovLimitSetting.getValue() / 2.0)) {
                    this.target = (EntityLivingBase)sortedTargets.get(0);
                }
                if (this.target != null) {
                    float playerYaw = MathHelper.wrapAngleTo180_float(AimAssist.mc.thePlayer.rotationYaw) + halfCircle;
                    float playerPitch = MathHelper.wrapAngleTo180_float(AimAssist.mc.thePlayer.rotationPitch) + halfCircle;
                    float targetYaw = MathHelper.wrapAngleTo180_float(RotationUtil.getRotationsToEntity(this.target)[0]) + halfCircle;
                    float targetPitch = MathHelper.wrapAngleTo180_float(RotationUtil.getRotationsToEntity(this.target)[1]) + halfCircle;
                    float yawDelta = targetYaw - playerYaw;
                    float pitchDelta = targetPitch - playerPitch;
                    if (yawDelta < -180.0f) {
                        yawDelta += 360.0f;
                    }
                    if (yawDelta > halfCircle) {
                        yawDelta -= 360.0f;
                    }
                    if (pitchDelta < -180.0f) {
                        pitchDelta += 360.0f;
                    }
                    if (pitchDelta > halfCircle) {
                        pitchDelta -= 360.0f;
                    }
                    this.rotationDelta = new float[]{yawDelta, pitchDelta};
                    double aimSpeed = this.speedSetting.getValue();
                    if (!(yawDelta > -2.0f) || !(yawDelta < two)) {
                        AimAssist.mc.thePlayer.rotationYaw = (float)((double)AimAssist.mc.thePlayer.rotationYaw + (yawDelta >= 0.0f ? aimSpeed : -aimSpeed));
                    }
                    if (!(pitchDelta > -2.0f) || !(pitchDelta < two)) {
                        AimAssist.mc.thePlayer.rotationPitch = (float)((double)AimAssist.mc.thePlayer.rotationPitch + (pitchDelta >= 0.0f ? aimSpeed : -aimSpeed));
                    }
                }
            }
        }
    }
}

