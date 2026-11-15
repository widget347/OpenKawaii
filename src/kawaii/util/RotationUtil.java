/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import kawaii.event.MotionEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

public class RotationUtil {
    public static float[] getRotationsTo(Entity entity, MotionEvent motionEvent) {
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - motionEvent.getX();
        double deltaY = entity.posY + (double)entity.getEyeHeight() - (motionEvent.getY() + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()) - 0.1;
        double deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) - motionEvent.getZ();
        double horizontalDistance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / horizontalDistance)));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        yaw = (float)((double)yaw + (Math.random() * 5.0 - Math.random()));
        pitch = (float)((double)pitch + (Math.random() * 5.0 - Math.random()));
        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        if (yaw > 180.0f) {
            yaw = 180.0f;
        }
        if (yaw < -180.0f) {
            yaw = -180.0f;
        }
        return new float[]{yaw, pitch};
    }

    public static float[] getRotationsToEntity(Entity entity) {
        Minecraft minecraft = Minecraft.getMinecraft();
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - minecraft.thePlayer.posX;
        double deltaY = entity.posY + (double)entity.getEyeHeight() - (minecraft.thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()) - 0.1;
        double deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) - minecraft.thePlayer.posZ;
        double horizontalDistance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / horizontalDistance)));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        if (yaw > 180.0f) {
            yaw = 180.0f;
        }
        if (yaw < -180.0f) {
            yaw = -180.0f;
        }
        return new float[]{yaw, pitch};
    }

    public float[] getRotationsToEntityAdjusted(Entity entity) {
        double deltaX = entity.posX + (entity.posX - entity.lastTickPosX) - Minecraft.getMinecraft().thePlayer.posX;
        double deltaY = entity.posY + (double)entity.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() * 1.9);
        if (entity.posY - 1.0 > Minecraft.getMinecraft().thePlayer.posY) {
            deltaY = entity.posY + (double)entity.getEyeHeight() - (Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight() * 1.1);
        }
        double deltaZ = entity.posZ + (entity.posZ - entity.lastTickPosZ) - Minecraft.getMinecraft().thePlayer.posZ;
        double horizontalDistance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));
        float yaw = (float)Math.toDegrees(-Math.atan(deltaX / deltaZ));
        float pitch = (float)(-Math.toDegrees(Math.atan(deltaY / horizontalDistance)));
        if (deltaX < 0.0 && deltaZ < 0.0) {
            yaw = 90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if (deltaX > 0.0 && deltaZ < 0.0) {
            yaw = -90.0f + (float)Math.toDegrees(Math.atan(deltaZ / deltaX));
        }
        if (pitch > 90.0f) {
            pitch = 90.0f;
        }
        if (pitch < -90.0f) {
            pitch = -90.0f;
        }
        if (yaw > 180.0f) {
            yaw = 180.0f;
        }
        if (yaw < -180.0f) {
            yaw = -180.0f;
        }
        return new float[]{yaw, pitch};
    }
}

