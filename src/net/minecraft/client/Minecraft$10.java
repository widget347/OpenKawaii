/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client;

import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.MovingObjectPosition;

public class Minecraft$10 {
    public static int[] $SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType;
    public static int[] $SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType;

    static {
        $SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType = new int[EntityMinecart.EnumMinecartType.values().length];
        try {
            Minecraft$10.$SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType[EntityMinecart.EnumMinecartType.FURNACE.ordinal()] = 1;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType[EntityMinecart.EnumMinecartType.CHEST.ordinal()] = 2;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType[EntityMinecart.EnumMinecartType.TNT.ordinal()] = 3;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType[EntityMinecart.EnumMinecartType.HOPPER.ordinal()] = 4;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$entity$item$EntityMinecart$EnumMinecartType[EntityMinecart.EnumMinecartType.COMMAND_BLOCK.ordinal()] = 5;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        $SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType = new int[MovingObjectPosition.MovingObjectType.values().length];
        try {
            Minecraft$10.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[MovingObjectPosition.MovingObjectType.ENTITY.ordinal()] = 1;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[MovingObjectPosition.MovingObjectType.BLOCK.ordinal()] = 2;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
        try {
            Minecraft$10.$SwitchMap$net$minecraft$util$MovingObjectPosition$MovingObjectType[MovingObjectPosition.MovingObjectType.MISS.ordinal()] = 3;
        }
        catch (NoSuchFieldError error) {
            // empty catch block
        }
    }
}

