/*
 * Decompiled with CFR 0.152.
 */
package kawaii.util;

import net.minecraft.client.renderer.texture.TextureUtil_2;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class BlockFace {
    public BlockPos pos;
    public EnumFacing facing;

    public BlockFace(BlockPos blockPos, EnumFacing facing) {
        this.pos = blockPos;
        this.facing = facing;
    }

    public BlockFace(BlockPos blockPos, EnumFacing facing, TextureUtil_2 textureUtil) {
        this(blockPos, facing);
    }
}

