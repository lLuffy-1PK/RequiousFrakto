package com.bordlistian.requious.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import com.bordlistian.requious.Requious;
import com.bordlistian.requious.data.AssemblyData;
import com.bordlistian.requious.gui.GuiHandler;
import com.bordlistian.requious.tile.TileEntityAssembly;
import com.bordlistian.requious.util.Misc;

import javax.annotation.Nullable;
import java.awt.*;

public class BlockAssembly extends Block implements IDynamicModel {
    public static final PropertyDirection facing = PropertyDirection.create("facing");
    public static final PropertyBool active = PropertyBool.create("active");

    AssemblyData data;

    AxisAlignedBB[] aabbs = new AxisAlignedBB[6];

    public BlockAssembly(Material materialIn, AssemblyData data) {
        super(materialIn);
        this.data = data;
        for (EnumFacing facing : EnumFacing.VALUES) {
            aabbs[facing.getIndex()] = Misc.rotateAABB(data.aabb, facing);
        }
        setDefaultState(getBlockState().getBaseState().withProperty(active, false));
    }

    public AssemblyData getData() {
        return data;
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, facing, active);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(facing).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(facing, EnumFacing.byIndex(meta));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        state = super.getActualState(state, world, pos);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityAssembly)
            return state.withProperty(active, ((TileEntityAssembly) tile).isActive());
        return state;
    }

    @Override
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos) {
        return data.hardness;
    }

    @Override
    public float getExplosionResistance(World world, BlockPos pos, @Nullable Entity exploder, Explosion explosion) {
        return data.blastResistance;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing facing = state.getValue(BlockAssembly.facing);
        return aabbs[facing.getIndex()];
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing face, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        double d0 = placer.posY + (double) placer.getEyeHeight();

        switch (data.placeType) {

            case Any:
                face = EnumFacing.getDirectionFromEntityLiving(pos, placer);
                break;
            case Up:
                face = EnumFacing.UP;
                break;
            case Down:
                face = EnumFacing.DOWN;
                break;
            case Horizontal:
                face = placer.getHorizontalFacing().getOpposite();
                break;
            case Vertical:
                if (d0 - (double) pos.getY() < 0.0D)
                    face = EnumFacing.DOWN;
                else
                    face = EnumFacing.UP;
                break;
            case HorizontalUp:
                if (Math.abs(placer.posX - (double) ((float) pos.getX() + 0.5F)) < 2.0D && Math.abs(placer.posZ - (double) ((float) pos.getZ() + 0.5F)) < 2.0D) {
                    if (d0 - (double) pos.getY() > 2.0D)
                        face = EnumFacing.UP;
                    if ((double) pos.getY() - d0 > 0.0D)
                        face = placer.getHorizontalFacing().getOpposite();
                } else {
                    face = placer.getHorizontalFacing().getOpposite();
                }
                break;
            case HorizontalDown:
                if (Math.abs(placer.posX - (double) ((float) pos.getX() + 0.5F)) < 2.0D && Math.abs(placer.posZ - (double) ((float) pos.getZ() + 0.5F)) < 2.0D) {
                    if (d0 - (double) pos.getY() > 2.0D)
                        face = placer.getHorizontalFacing().getOpposite();
                    if ((double) pos.getY() - d0 > 0.0D)
                        face = EnumFacing.DOWN;
                } else {
                    face = placer.getHorizontalFacing().getOpposite();
                }
                break;
        }

        return getDefaultState().withProperty(facing, face);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(world, pos, state, placer, stack);

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityAssembly && placer instanceof EntityPlayer) {
            ((TileEntityAssembly) tile).setOwner((EntityPlayer) placer);
        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos) {
        /*BlockPos attachPos = pos.offset(state.getValue(facing), -1);
        IBlockState attachState = world.getBlockState(attachPos);
        if (attachState.getBlock().isReplaceable(world, attachPos)){
            world.setBlockToAir(pos);
            this.dropBlockAsItem(world, pos, state, 0);
        }*/
        super.neighborChanged(state, world, pos, block, fromPos);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public BlockRenderLayer getRenderLayer() {
        switch (data.layerType) {
            case Cutout:
                return BlockRenderLayer.CUTOUT;
            case CutoutMipped:
                return BlockRenderLayer.CUTOUT_MIPPED;
            case Translucent:
                return BlockRenderLayer.TRANSLUCENT;
            default:
                return BlockRenderLayer.SOLID;
        }
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileEntityAssembly) {
            ((TileEntityAssembly) tile).breakBlock(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && data.hasGUI) {
            playerIn.openGui(Requious.MODID, GuiHandler.ASSEMBLY, worldIn, pos.getX(), pos.getY(), pos.getZ());
        }

        return data.hasGUI;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        TileEntityAssembly assembly = new TileEntityAssembly();
        assembly.setBlock(this);
        return assembly;
    }

    @Override
    public ResourceLocation getRedirect() {
        return data.model;
    }

    @Override
    public Color getTint(int tintIndex) {
        return data.getColor(tintIndex);
    }
}
