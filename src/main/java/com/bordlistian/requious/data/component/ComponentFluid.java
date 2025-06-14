package com.bordlistian.requious.data.component;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import com.bordlistian.requious.compat.crafttweaker.IFluidCondition;
import com.bordlistian.requious.compat.crafttweaker.SlotVisualCT;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.gui.slot.FluidSlot;
import com.bordlistian.requious.tile.TileEntityAssembly;
import com.bordlistian.requious.util.ComponentFace;
import com.bordlistian.requious.util.IOParameters;
import com.bordlistian.requious.util.ItemComponentHelper;
import com.bordlistian.requious.util.SlotVisual;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@ZenRegister
@ZenClass("mods.requious.FluidSlot")
public class ComponentFluid extends ComponentBase {
    public boolean bucketAllowed;
    public boolean inputAllowed = true;
    public boolean outputAllowed = true;
    public boolean splitAllowed = true;
    public boolean putAllowed = true;
    public boolean takeAllowed = true;
    public boolean dropsOnBreak = true;
    public boolean canOverfill = false;
    public IOParameters pushFluid = new IOParameters();
    public IOParameters pushItem = new IOParameters();
    public IFluidCondition filter;
    public int capacity;

    public SlotVisual foreground = SlotVisual.EMPTY;
    public SlotVisual background = SlotVisual.FLUID_SLOT;

    public ComponentFluid(ComponentFace face, int capacity) {
        super(face);
        this.capacity = capacity;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid setAccess(boolean input, boolean output) {
        inputAllowed = input;
        outputAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid setFilter(IFluidCondition condition) {
        filter = condition;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid allowBucket(boolean input, boolean output) {
        bucketAllowed = true;
        putAllowed = input;
        takeAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid allowOverfill() {
        this.canOverfill = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid allowSplit() {
        this.splitAllowed = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid noDrop() {
        this.dropsOnBreak = false;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid pushItem(int size, int slot) {
        this.pushItem = new IOParameters(size, slot);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid pushItem(int size) {
        this.pushItem = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid pushFluid(int size) {
        this.pushFluid = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid setForeground(SlotVisualCT visual) {
        this.foreground = SlotVisualCT.unpack(visual);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentFluid setBackground(SlotVisualCT visual) {
        this.background = SlotVisualCT.unpack(visual);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentFluid> implements IFluidTankProperties {
        FluidStack fluid;
        ItemComponentHelper bucket;

        public Slot(ComponentFluid component) {
            super(component);
            bucket = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    return 1;
                }
            };
        }


        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            Collector fluid = new Collector(getFace());
            ComponentItem.Collector item = new ComponentItem.Collector(getFace());

            if (!collectors.contains(fluid))
                collectors.add(fluid);
            if (isBucketAccepted() && !collectors.contains(item))
                collectors.add(item);
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new FluidSlot(assembly, this, x, y);
        }

        @Override
        public void update() {

        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            if (component.dropsOnBreak) {
                bucket.spawnInWorld(world, position);
                bucket.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            if (getAmount() > 0) {
                compound.setTag("fluid", fluid.writeToNBT(new NBTTagCompound()));
            } else {
                compound.setString("fluid", "empty");
            }
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            fluid = FluidStack.loadFluidStackFromNBT(compound.getCompoundTag("fluid"));
        }

        /*@Override
        public boolean canInputItem() {
            return component.bucketAllowed;
        }

        @Override
        public boolean canOutputItem() {
            return component.bucketAllowed;
        }*/

        public boolean canInput() {
            return component.inputAllowed;
        }

        public boolean canOutput() {
            return component.outputAllowed;
        }

        public IOParameters getPushFluid() {
            return component.pushFluid;
        }

        public IOParameters getPushItem() {
            return component.pushItem;
        }

        public boolean canSplit() {
            return component.splitAllowed;
        }

        public boolean canPut() {
            return !component.hidden && component.bucketAllowed && component.putAllowed;
        }

        public boolean canTake() {
            return !component.hidden && component.bucketAllowed && component.takeAllowed;
        }

        public boolean canOverfill() {
            return component.canOverfill;
        }

        public boolean isBucketAccepted() {
            return component.bucketAllowed;
        }

        /*public ItemComponentHelper getItem() {
            return bucket;
        }*/

        @Nullable
        @Override
        public FluidStack getContents() {
            return fluid;
        }

        public void setContents(FluidStack fluid) {
            this.fluid = fluid;
        }

        public int getAmount() {
            if (fluid == null)
                return 0;
            else
                return fluid.amount;
        }

        public int getCapacity() {
            if (canOverfill() && getAmount() <= 0)
                return Integer.MAX_VALUE;
            return component.capacity;
        }

        @Override
        public boolean canFill() {
            return canInput();
        }

        @Override
        public boolean canDrain() {
            return canOutput();
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return canInput() && true;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return canOutput() && true;
        }

        public boolean acceptsFluid(FluidStack resource) {
            return component.filter == null || component.filter.matches(CraftTweakerMC.getILiquidStack(resource));
        }

        public int fill(FluidStack resource, boolean simulate) {
            if (resource == null || (fluid != null && !fluid.isFluidEqual(resource))) {
                return 0;
            }

            int amount = getAmount();
            int toInsert = Math.min(resource.amount, getCapacity() - amount);
            if (!simulate) {
                if (fluid == null)
                    fluid = resource.copy();
                fluid.amount = amount + toInsert;
                markDirty();
            }
            return toInsert;
        }

        public FluidStack drain(FluidStack resource, boolean simulate) {
            if (resource == null || (fluid != null && !fluid.isFluidEqual(resource))) {
                return null;
            }
            return drain(resource.amount, simulate);
        }

        public FluidStack drain(int amount, boolean simulate) {
            if (fluid == null) {
                return null;
            }

            FluidStack copy = fluid.copy();
            copy.amount = Math.min(amount, getAmount());
            if (!simulate) {
                fluid.amount -= copy.amount;
                if (fluid.amount <= 0) {
                    fluid = null;
                }
                markDirty();
            }
            return copy;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || bucket.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
            bucket.markClean();
        }

        public SlotVisual getForeground() {
            return component.foreground;
        }

        public SlotVisual getBackground() {
            return component.background;
        }
    }

    public static class Collector extends ComponentBase.Collector implements IFluidHandler {
        ComponentFace face;
        List<Slot> slots = new ArrayList<>();
        int pushIndex;

        public Collector(ComponentFace face) {
            this.face = face;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot.getFace() == face && slot instanceof Slot) {
                addSlot((Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public boolean hasCapability() {
            return true;
        }

        @Override
        public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && face.matches(localSide, globalSide))
                return true;
            return super.hasCapability(capability, localSide, globalSide);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && face.matches(localSide, globalSide))
                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this);
            return super.getCapability(capability, localSide, globalSide);
        }

        private boolean canAutoOutput() {
            for (Slot slot : slots) {
                if (slot.getPushFluid().active)
                    return true;
            }
            return false;
        }

        @Override
        public void update() {
            if (canAutoOutput() && tile instanceof TileEntityAssembly) {
                World world = tile.getWorld();
                BlockPos pos = tile.getPos();
                EnumFacing facing = TileEntityAssembly.toSide(((TileEntityAssembly) tile).getFacing(), face.getSide(pushIndex));

                TileEntity checkTile = world.getTileEntity(pos.offset(facing));
                if (checkTile != null && checkTile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite())) {

                    IFluidHandler tank = checkTile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite());
                    if (tank != null) {
                        for (Slot slot : slots) {
                            if (slot.getPushFluid().active) {
                                int maxSize = slot.getPushFluid().size;
                                FluidStack fluid = slot.drain(maxSize, true);
                                if (fluid != null) {
                                    int filled = tank.fill(fluid, true);
                                    if (filled > 0) {
                                        slot.drain(filled, false);
                                    }
                                }
                            }

                        }
                    }

                }
                pushIndex++;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return slots.toArray(new IFluidTankProperties[slots.size()]);
        }

        private boolean hasFluidStored(FluidStack stack) {
            for (Slot slot : slots) {
                if (slot.fluid != null && slot.fluid.isFluidEqual(stack))
                    return true;
            }
            return false;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            boolean hasFluidStored = hasFluidStored(resource);
            for (Slot slot : slots) {
                if (slot.canInput() && slot.acceptsFluid(resource) && (slot.canSplit() || !hasFluidStored || (slot.fluid != null && slot.fluid.isFluidEqual(resource)))) {
                    int filled = slot.fill(resource, !doFill);
                    if (filled > 0)
                        return filled;
                }
            }
            return 0;
        }

        @Nullable
        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            for (Slot slot : slots) {
                if (!slot.canOutput())
                    continue;
                FluidStack extracted = slot.drain(resource, !doDrain);
                if (extracted != null)
                    return extracted;
            }
            return null;
        }

        @Nullable
        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            for (Slot slot : slots) {
                if (!slot.canOutput())
                    continue;
                FluidStack extracted = slot.drain(maxDrain, !doDrain);
                if (extracted != null)
                    return extracted;
            }
            return null;
        }
    }
}
