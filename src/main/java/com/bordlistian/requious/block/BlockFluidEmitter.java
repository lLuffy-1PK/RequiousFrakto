package com.bordlistian.requious.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import com.bordlistian.requious.data.FluidEmitterData;
import com.bordlistian.requious.tile.TileEntityFluidEmitter;

import javax.annotation.Nullable;

public class BlockFluidEmitter extends BlockEmitter {
    public BlockFluidEmitter(Material materialIn, FluidEmitterData data) {
        super(materialIn, data);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityFluidEmitter();
    }
}
