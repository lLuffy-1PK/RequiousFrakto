package com.bordlistian.requious.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.bordlistian.requious.data.RedEmitterData;
import com.bordlistian.requious.tile.TileEntityRedEmitter;

import javax.annotation.Nullable;

public class BlockRedEmitter extends BlockEmitter {
    public BlockRedEmitter(Material materialIn, RedEmitterData data) {
        super(materialIn, data);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityRedEmitter();
    }
}
