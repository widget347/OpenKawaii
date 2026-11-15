/*
 * Decompiled with CFR 0.152.
 */
package kawaii.module.combat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import kawaii.command.FriendCommand;
import kawaii.ui.UiElement;
import kawaii.util.RotationUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

public class Targeting
implements UiElement {
    public static boolean isOutOfRange(double range, EntityLivingBase entityLivingBase) {
        return (double)Targeting.mc.thePlayer.getDistanceToEntity(entityLivingBase) > range;
    }

    public static double getDistanceFromPlayer(EntityLivingBase entityLivingBase) {
        return Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entityLivingBase);
    }

    public static double getHurtTime(EntityLivingBase entityLivingBase) {
        return entityLivingBase.hurtTime;
    }

    public static ArrayList sortTargetsByRotation(ArrayList targets, double range) {
        ((java.util.ArrayList<EntityLivingBase>)targets).removeIf(target -> Targeting.isOutOfRange(range, target));
        targets.sort(Comparator.comparingDouble(Targeting::getRotationDifference));
        return targets;
    }

    public static ArrayList sortTargetsByHealth(ArrayList targets, double range) {
        ((java.util.ArrayList<EntityLivingBase>)targets).removeIf(target -> Targeting.isTooFarAway(range, target));
        targets.sort(Comparator.comparingDouble(EntityLivingBase::getHealth));
        return targets;
    }

    public static boolean isValidTarget(EntityLivingBase entityLivingBase, boolean attackPlayers, boolean attackAnimals, boolean attackMobs, boolean attackInvisible, boolean skipFriends) {
        if (entityLivingBase.isInvisible() && !attackInvisible) {
            return false;
        }
        if (entityLivingBase instanceof EntityPlayer && attackPlayers) {
            return !FriendCommand.isFriend(entityLivingBase) || !skipFriends;
        }
        if (entityLivingBase instanceof EntityAnimal && attackAnimals) {
            return true;
        }
        return entityLivingBase instanceof EntityMob && attackMobs;
    }

    public static EntityPlayer asPlayer(Entity entity) {
        return (EntityPlayer)entity;
    }

    public static boolean isOutOfReach(double reach, EntityLivingBase entityLivingBase) {
        return (double)Targeting.mc.thePlayer.getDistanceToEntity(entityLivingBase) > reach;
    }

    public static double getRotationDifference(EntityLivingBase entityLivingBase) {
        return Math.abs(Targeting.getRotationsTo(entityLivingBase)[0]);
    }

    public static double[] getRotationsTo(EntityLivingBase entityLivingBase) {
        float halfTurn = 180.0f;
        float playerYawShifted = MathHelper.wrapAngleTo180_float(Minecraft.getMinecraft().thePlayer.rotationYaw) + halfTurn;
        float playerPitchShifted = MathHelper.wrapAngleTo180_float(Minecraft.getMinecraft().thePlayer.rotationPitch) + halfTurn;
        float targetYawShifted = MathHelper.wrapAngleTo180_float(RotationUtil.getRotationsToEntity(entityLivingBase)[0]) + halfTurn;
        float targetPitchShifted = MathHelper.wrapAngleTo180_float(RotationUtil.getRotationsToEntity(entityLivingBase)[1]) + halfTurn;
        float yawDiff = targetYawShifted - playerYawShifted;
        float pitchDiff = targetPitchShifted - playerPitchShifted;
        if (yawDiff < -180.0f) {
            yawDiff += 360.0f;
        }
        if (yawDiff > halfTurn) {
            yawDiff -= 360.0f;
        }
        if (pitchDiff < -180.0f) {
            pitchDiff += 360.0f;
        }
        if (pitchDiff > halfTurn) {
            pitchDiff -= 360.0f;
        }
        return new double[]{yawDiff, pitchDiff};
    }

    public static boolean isBeyondRange(double range, EntityLivingBase entityLivingBase) {
        return (double)Targeting.mc.thePlayer.getDistanceToEntity(entityLivingBase) > range;
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof EntityPlayer;
    }

    public static boolean isTooFarAway(double range, EntityLivingBase entityLivingBase) {
        return (double)Targeting.mc.thePlayer.getDistanceToEntity(entityLivingBase) > range;
    }

    public static ArrayList sortTargetsByHurtTime(ArrayList targets, double range) {
        ((java.util.ArrayList<EntityLivingBase>)targets).removeIf(target -> Targeting.isBeyondRange(range, target));
        targets.sort(Comparator.comparingDouble(Targeting::getHurtTime));
        return targets;
    }

    public static ArrayList sortTargetsByDistance(ArrayList targets, double range) {
        ((java.util.ArrayList<EntityLivingBase>)targets).removeIf(target -> Targeting.isOutOfReach(range, target));
        targets.sort(Comparator.comparingDouble(Targeting::getDistanceFromPlayer));
        return targets;
    }

    public static ArrayList<EntityPlayer> getTargets() {
        return Targeting.mc.theWorld.loadedEntityList.stream().filter(Targeting::isPlayer).map(Targeting::asPlayer).collect(Collectors.toCollection(ArrayList::new));
    }
}

