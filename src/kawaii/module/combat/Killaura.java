/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import java.util.ArrayList;
import kawaii.Kawaii;
import kawaii.command.FriendCommand;
import kawaii.event.CancellableEvent;
import kawaii.event.EventRender2D;
import kawaii.event.EventType;
import kawaii.event.MotionEvent;
import kawaii.module.Category;
import kawaii.module.Module;
import kawaii.module.combat.Targeting;
import kawaii.module.render.Blur;
import kawaii.module.render.Hud;
import kawaii.module.world.Scaffold;
import kawaii.render.BlurRenderer;
import kawaii.render.RenderUtils;
import kawaii.setting.BooleanSetting;
import kawaii.setting.ModeSetting;
import kawaii.setting.MultiBooleanSetting;
import kawaii.setting.NumberSetting;
import kawaii.ui.font.FontLoader;
import kawaii.util.ObjectList;
import kawaii.util.RotationUtil;
import kawaii.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.C02PacketUseEntity;
import org.apache.commons.lang3.RandomUtils;
import org.lwjgl.opengl.GL11;

public class Killaura
extends Module {
    public static BooleanSetting targetPlayers;
    public ModeSetting attackEvent;
    public static BooleanSetting targetMobs;
    public ModeSetting targetHud;
    public NumberSetting maxCps;
    public NumberSetting reach;
    public static BooleanSetting targetInvisibles;
    public Timer attackTimer = new Timer();
    public static BooleanSetting targetAnimals;
    public static BooleanSetting targetDead;
    public NumberSetting minCps;
    public ModeSetting sortMode;
    public static EntityLivingBase target;
    public BooleanSetting keepRotations;
    public BooleanSetting keepSprint;

    @Override
    public void onEvent(EventRender2D eventRender2D) {
        double textOffsetX = 55.0;
        boolean usingChatPreview = false;
        EntityLivingBase displayTarget = target;
        if (Killaura.mc.currentScreen instanceof GuiChat && displayTarget == null) {
            usingChatPreview = true;
            displayTarget = Killaura.mc.thePlayer;
        }
        if (displayTarget == null) {
            return;
        }
        ScaledResolution scaledResolution = new ScaledResolution(mc);
        double healthPercent = Math.floor(displayTarget.getHealth() / displayTarget.getMaxHealth() * 100.0f);
        double hudX = (float)scaledResolution.getScaledWidth() / 2.0f + 5.0f;
        double hudY = (float)scaledResolution.getScaledHeight() / 2.0f + 5.0f;
        double hudWidth = 160.0;
        double hudHeight = 50.0;
        switch (this.targetHud.getMode()) {
            case "Kawaii": {
                NetworkPlayerInfo networkPlayerInfo;
                if (eventRender2D.getEventType() == EventType.post) {
                    if (Blur.blurActive.booleanValue() && Blur.blurInfoElements.getValue().booleanValue()) {
                        BlurRenderer.queueBlur(hudX, hudY, hudX + hudWidth, hudY + hudHeight, 3.0);
                    }
                    RenderUtils.drawRoundedRect(hudX, hudY, hudWidth, hudHeight, 3.0, Hud.getHudColor().getRGB());
                }
                if (mc.getNetHandler() != null && displayTarget.getUniqueID() != null && (networkPlayerInfo = mc.getNetHandler().getPlayerInfo(displayTarget.getUniqueID())) != null) {
                    mc.getTextureManager().bindTexture(networkPlayerInfo.getLocationSkin());
                    GlStateManager.color(1.0f, 1.0f, 1.0f);
                    GL11.glEnable(3042);
                    Gui.drawModalRectWithCustomSizedTexture(hudX + 5.0, hudY + 5.0, 40.0f, 40.0f, 40, 40, 320.0f, 320.0f);
                    GL11.glDisable(3042);
                }
                FontLoader.popBold20.drawString(displayTarget.getName(), hudX + textOffsetX, hudY + 10.0, -1);
                Gui.drawRect(hudX + textOffsetX, hudY + 30.0, hudX + textOffsetX + 100.0, hudY + 45.0, -15658735);
                Gui.drawRect(hudX + textOffsetX, hudY + 30.0, hudX + textOffsetX + healthPercent, hudY + 45.0, -40093);
                FontLoader.popMed20.drawString(healthPercent + "%", hudX + textOffsetX + 50.0 - (double)((float)FontLoader.popMed20.getStringWidth(healthPercent + "%") / 2.0f), hudY + 31.5 + 7.5 - (double)((float)FontLoader.popMed20.getHeight() / 2.0f), -1);
            }
        }
        super.onEvent(eventRender2D);
    }

    @Override
    public void onEvent(CancellableEvent cancellableEvent) {
        this.setSuffix(this.minCps.getValue() + " | " + this.reach.getValue());
        if (cancellableEvent instanceof MotionEvent && !Kawaii.INSTANCE.getModuleManager().getModule(Scaffold.class).isEnabled()) {
            ArrayList sortedTargets;
            Object rawTargets;
            MotionEvent motionEvent = (MotionEvent)cancellableEvent;
            boolean attackOnPre = this.attackEvent.getMode().equalsIgnoreCase("PRE");
            if (target != null && this.keepRotations.getValue().booleanValue()) {
                float[] rotations = RotationUtil.getRotationsTo(target, motionEvent);
                motionEvent.setYaw(rotations[0]);
                motionEvent.setPitch(rotations[1]);
            }
            if (attackOnPre && motionEvent.getEventType() == EventType.post) {
                return;
            }
            if (!attackOnPre && motionEvent.getEventType() == EventType.pre) {
                return;
            }
            rawTargets = Killaura.getTargets(this.reach.getValue());
            target = null;
            switch (this.sortMode.getMode()) {
                case "Distance": {
                    sortedTargets = Targeting.sortTargetsByDistance((ArrayList)rawTargets, this.reach.getValue());
                    break;
                }
                case "Hurttime": {
                    sortedTargets = Targeting.sortTargetsByHurtTime((ArrayList)rawTargets, this.reach.getValue());
                    break;
                }
                case "Health": {
                    sortedTargets = Targeting.sortTargetsByHealth((ArrayList)rawTargets, this.reach.getValue());
                    break;
                }
                default: {
                    sortedTargets = Targeting.sortTargetsByRotation((ArrayList)rawTargets, this.reach.getValue());
                }
            }
            sortedTargets.removeIf(candidate -> FriendCommand.isFriend((EntityLivingBase)candidate));
            if (sortedTargets.size() > 0) {
                target = (EntityLivingBase)sortedTargets.get(0);
            }
            if (target != null) {
                float attackDelayMs = (float)(1000.0 / this.maxCps.getValue());
                if (this.minCps.getValue() < this.maxCps.getValue()) {
                    attackDelayMs = (float)(1000.0 / RandomUtils.nextDouble((int)this.minCps.getValue(), (int)this.maxCps.getValue()));
                }
                if (this.attackTimer.hasTimePassed(attackDelayMs)) {
                    float[] attackRotations = RotationUtil.getRotationsTo(target, motionEvent);
                    motionEvent.setYaw(attackRotations[0]);
                    motionEvent.setPitch(attackRotations[1]);
                    Killaura.mc.thePlayer.swingItem();
                    if (this.keepSprint.getValue().booleanValue()) {
                        Killaura.mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity((Entity)target, C02PacketUseEntity.Action.INTERACT));
                    } else {
                        Killaura.mc.playerController.attackEntity(Killaura.mc.thePlayer, target);
                    }
                    this.attackTimer.reset();
                }
            }
        }
    }

    public Killaura() {
        super("Killaura", "Automatically kills players", Category.combat);
        this.attackEvent = new ModeSetting("Attack Event", "Pre", "Post");
        this.sortMode = new ModeSetting("Sort", "Distance", "FOV", "Hurttime", "Health");
        this.minCps = new NumberSetting("Min CPS", 9.0, 0.5, 4.0, 20.0);
        this.maxCps = new NumberSetting("Max CPS", 13.0, 0.5, 4.0, 20.0);
        this.reach = new NumberSetting("Reach", 4.2, 0.1, 3.0, 6.0);
        this.keepSprint = new BooleanSetting("KeepSprint", true);
        this.keepRotations = new BooleanSetting("KeepRots", true);
        this.targetHud = new ModeSetting("TargetHud", "Kawaii", "None");
        this.addSettings(this.attackEvent, this.sortMode, this.minCps, this.maxCps, this.reach, this.keepSprint, this.keepRotations, this.targetHud, new MultiBooleanSetting("Targets", targetPlayers, targetAnimals, targetMobs, targetInvisibles, targetDead));
    }

    static {
        target = null;
        targetPlayers = new BooleanSetting("Players", true);
        targetAnimals = new BooleanSetting("Animals", true);
        targetMobs = new BooleanSetting("Mobs", true);
        targetInvisibles = new BooleanSetting("Invisibles", false);
        targetDead = new BooleanSetting("Dead", false);
    }

    public static ArrayList getTargets(double range) {
        ObjectList targetList = new ObjectList();
        for (Entity entity : Minecraft.getMinecraft().theWorld.loadedEntityList) {
            if (!(entity instanceof EntityLivingBase) || Minecraft.getMinecraft().thePlayer == entity || (double)entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > range || !Targeting.isValidTarget((EntityLivingBase)entity, targetPlayers.getValue(), targetAnimals.getValue(), targetMobs.getValue(), targetInvisibles.getValue(), false) || !(((EntityLivingBase)entity).getHealth() > 0.0f)) continue;
            targetList.add((EntityLivingBase)entity);
        }
        return targetList;
    }
}

