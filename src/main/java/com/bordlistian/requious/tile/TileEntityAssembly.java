package com.bordlistian.requious.tile;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional;
import com.bordlistian.requious.base.Mods;
import com.bordlistian.requious.block.BlockAssembly;
import com.bordlistian.requious.data.AssemblyData;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentEnergy;
import com.bordlistian.requious.util.Facing;
import com.bordlistian.requious.util.ILaserStorage;
import com.bordlistian.requious.util.MachineVisual;
import com.bordlistian.requious.util.Misc;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Random;

@Optional.InterfaceList({
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySink", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergySource", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyAcceptor", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyEmitter", modid = "ic2"),
        @Optional.Interface(iface = "ic2.api.energy.tile.IEnergyTile", modid = "ic2")
})
public class TileEntityAssembly extends TileEntity implements ITickable, ILaserAcceptor, IEnergyTile, IEnergySink, IEnergySource, IEnergyEmitter, IEnergyAcceptor {
    Random random = new Random();
    AssemblyProcessor processor;
    ResourceLocation block;
    boolean shouldSync;
    EntityPlayer owner;
    boolean active;
    boolean addedToEnet = false;
    boolean guiContainerOpen = false;

    public void setBlock(BlockAssembly block) {
        this.block = block.getRegistryName();
    }

    public AssemblyProcessor getProcessor() {
        return processor;
    }

    public AssemblyData getData() {
        Object objectBlock = Block.REGISTRY.getObject(block);
        if (objectBlock instanceof BlockAssembly) {
            return ((BlockAssembly) objectBlock).getData();
        }
        return null;
    }

    public void setGuiContainerOpen(boolean guiContainerOpen) {
        this.guiContainerOpen = guiContainerOpen;
    }

    public boolean isActive() {
        return active;
    }

    public static EnumFacing toLocalSide(EnumFacing facing, EnumFacing side) {
        if ((facing == null) || (side == null))
            return null;
        switch (facing) {
            case DOWN:
                return side.getOpposite();
            case UP:
                return side;
            case SOUTH:
                return side.rotateAround(EnumFacing.Axis.X);
            case NORTH:
                return side.getOpposite().rotateAround(EnumFacing.Axis.X);
            case WEST:
                return side.rotateAround(EnumFacing.Axis.Z);
            case EAST:
                return side.getOpposite().rotateAround(EnumFacing.Axis.Z);
            default:
                return null;
        }
    }

    public static EnumFacing toGlobalSide(EnumFacing facing, EnumFacing side) {
        if ((facing == null) || (side == null))
            return null;
        switch (facing) {
            case DOWN:
                return side.getOpposite();
            case UP:
                return side;
            case SOUTH:
                return side.getOpposite().rotateAround(EnumFacing.Axis.X);
            case NORTH:
                return side.rotateAround(EnumFacing.Axis.X);
            case WEST:
                return side.getOpposite().rotateAround(EnumFacing.Axis.Z);
            case EAST:
                return side.rotateAround(EnumFacing.Axis.Z);
            default:
                return null;
        }
    }

    public static EnumFacing toSide(EnumFacing facing, Facing side) {
        if (side.isGlobal())
            return side.getFacing();
        else
            return toGlobalSide(facing, side.getFacing());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (this.getWorld().isAirBlock(this.getPos())) {
            return false;
        }
        boolean hasCapability = false;
        if (processor != null)
            hasCapability = processor.hasCapability(capability, toLocalSide(getFacing(), facing), facing);
        if (!hasCapability)
            hasCapability = super.hasCapability(capability, facing);
        return hasCapability;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        T instance = null;
        if (processor != null)
            instance = processor.getCapability(capability, toLocalSide(getFacing(), facing), facing);
        if (instance == null)
            instance = super.getCapability(capability, facing);
        return instance;
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public void breakBlock(World world, BlockPos pos) {
        if (processor == null) {
            return;
        }
        processor.machineBroken(world, new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setString("assembly", block.toString());
        if (processor != null)
            processor.writeToNBT(tag);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        block = new ResourceLocation(tag.getString("assembly"));
        if (processor == null)
            initProcessor();
        processor.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        shouldSync = true;
    }

    boolean init = true;
    @Override
    public void update() {
        if (processor == null)
            initProcessor();
        else {
            for (MachineVisual visual : processor.getVisuals()) {
                visual.update(this);
            }
            processor.update();
            if (processor.isDirty())
                markDirty();
            boolean newActive = processor.isActive();
            if (newActive != active) {
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 2);
                active = newActive;
            }
        }
        if (shouldSync && guiContainerOpen) {
            Misc.syncTE(this, false);
            shouldSync = false;
        }
        if (!world.isRemote && !addedToEnet && Objects.nonNull(processor.getIC2Handler()) && Mods.IC2.isPresent()) {
            addedToEnet = true;
            addToIC2Enet();
        }
        if (init){
            world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock(), true);
            init = false;
        }
    }

    private void initProcessor() {
        AssemblyData data = getData();
        if (data != null) {
            processor = data.constructProcessor();
            processor.setTile(this);
            if (owner != null) {
                processor.setOwner(owner);
            }
        }
    }

    public EnumFacing getFacing() {
        IBlockState state = getWorld().getBlockState(getPos());
        if (state.getPropertyKeys().contains(BlockAssembly.facing)) {
            return state.getValue(BlockAssembly.facing);
        }
        return null;
    }

    public void setOwner(EntityPlayer player) {
        if (player == null)
            return;

        if (processor != null)
            processor.setOwner(player);
        else
            owner = player;
    }

    @Override
    public ILaserStorage getLaserStorage(EnumFacing laserDirection) {
        if (processor == null) return null;
        return processor.getLaserAcceptor(toLocalSide(getFacing(), laserDirection.getOpposite()), laserDirection.getOpposite());
    }

    @Override
    public boolean isValid() {
        return !isInvalid();
    }

    @Override
    public BlockPos getPosition() {
        return getPos();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean emitsEnergyTo(IEnergyAcceptor acceptor, EnumFacing side) {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.canOutputEnergy(toLocalSide(getFacing(), side), side);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double getDemandedEnergy() {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.getInputEnergy();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSinkTier() {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.getInputTier();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double injectEnergy(EnumFacing side, double amount, double voltage) {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.inject(toLocalSide(getFacing(), side), side, amount, voltage);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.canInputEnergy(toLocalSide(getFacing(), side), side);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public double getOfferedEnergy() {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.getOutputEnergy();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void drawEnergy(double amount) {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        handler.draw(amount);
    }

    @Override
    @Optional.Method(modid = "ic2")
    public int getSourceTier() {
        ComponentEnergy.CollectorIC2 handler = processor.getIC2Handler();
        return handler.getOutputTier();
    }

    @Override
    public String toString() {
        String dataName = "NO DATA";
        if (getData() != null)
            dataName = getData().resourceName;
        return String.format("%s (%s)", super.toString(), dataName);
    }

    @Optional.Method(modid = "ic2")
    public void addToIC2Enet() {
        MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent(this));
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void invalidate() {
        if (!world.isRemote && addedToEnet) {
            addedToEnet = false;
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        }
        super.invalidate();
    }

    @Override
    @Optional.Method(modid = "ic2")
    public void onChunkUnload() {
        if (!world.isRemote && addedToEnet) {
            addedToEnet = false;
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent(this));
        }
        super.onChunkUnload();
    }
}
