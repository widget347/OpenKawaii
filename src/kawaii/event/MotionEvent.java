/*
 * Decompiled with CFR 0.152.
 */
package kawaii.event;

import kawaii.event.CancellableEvent;
import net.minecraft.client.Minecraft;

public class MotionEvent
extends CancellableEvent {
    public boolean onGround;
    public float pitch;
    public float yaw;
    public double x;
    public double z;
    public double y;

    public void setX(double x) {
        this.x = x;
    }

    public float getPitch() {
        return this.pitch;
    }

    public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public double getZ() {
        return this.z;
    }

    public double getX() {
        return this.x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public float getYaw() {
        return this.yaw;
    }

    public double getY() {
        return this.y;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        }
    }
}

