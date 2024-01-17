package requious.data.component;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.minecraft.CraftTweakerMC;
import ic2.api.energy.EnergyNet;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import requious.base.Mods;
import requious.compat.crafttweaker.SlotVisualCT;
import requious.data.AssemblyProcessor;
import requious.gui.slot.EnergySlot;
import requious.tile.TileEntityAssembly;
import requious.util.*;
import requious.util.battery.BatteryAccessEmpty;
import requious.util.battery.BatteryAccessFE;
import requious.util.battery.IBatteryAccess;
import sonar.fluxnetworks.common.tileentity.TileFluxPlug;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@ZenRegister
@ZenClass("mods.requious.EnergySlot")
public class ComponentEnergy extends ComponentBase {
    public boolean batteryAllowed;
    public boolean inputAllowed = true;
    public boolean outputAllowed = true;
    public boolean shiftAllowed = true;
    public boolean putAllowed = true;
    public boolean takeAllowed = true;
    public boolean dropsOnBreak = true;
    public boolean canOverfill = false;
    public Ingredient filter = new IngredientAny();
    public IOParameters pushItem = new IOParameters();
    public IOParametersEnergy pushEnergy = new IOParametersEnergy();
    public long capacity;
    public float powerLoss;

    public long maxInput = Long.MAX_VALUE;
    public long maxOutput = 0;

    public boolean acceptsFE = true;
    public boolean acceptsEU = false;

    public RatioConversion conversionEU = new RatioConversion(4, 1);

    public String unit = "fe";

    public SlotVisual foreground = SlotVisual.EMPTY;
    public SlotVisual background = SlotVisual.ENERGY_SLOT;

    public ComponentEnergy(ComponentFace face, long capacity) {
        super(face);
        this.capacity = capacity;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setAccess(boolean input, boolean output) {
        inputAllowed = input;
        outputAllowed = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy allowBattery(boolean input, boolean output, @Optional(valueBoolean = true) boolean drops, @Optional(valueBoolean = true) boolean shift, @Optional IIngredient ingredient) {
        batteryAllowed = true;
        putAllowed = input;
        takeAllowed = output;
        dropsOnBreak = drops;
        shiftAllowed = shift;
        if (ingredient != null)
            filter = CraftTweakerMC.getIngredient(ingredient);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setPowerLoss(float loss) {
        powerLoss = loss;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setUnit(String unit) {
        this.unit = unit;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy allowOverfill() {
        this.canOverfill = true;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy noDrop() {
        this.dropsOnBreak = false;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setLimits(long input, long output) {
        this.maxInput = input;
        this.maxOutput = output;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushItem(int size, int slot) {
        this.pushItem = new IOParameters(size, slot);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushItem(int size) {
        this.pushItem = new IOParameters(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy pushEnergy(long size) {
        this.pushEnergy = new IOParametersEnergy(size);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy acceptFE(boolean accept) {
        acceptsFE = accept;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy acceptEU(boolean accept) {
        acceptsEU = accept;
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setForeground(SlotVisualCT visual) {
        this.foreground = SlotVisualCT.unpack(visual);
        return this;
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentEnergy setBackground(SlotVisualCT visual) {
        this.background = SlotVisualCT.unpack(visual);
        return this;
    }

    public static class Slot extends ComponentBase.Slot<ComponentEnergy> implements ComponentItem.IItemSlot {
        AtomicLong energy = new AtomicLong();
        long capacity;
        float powerLoss;
        ItemComponentHelper battery;
        boolean active;

        public Slot(ComponentEnergy component) {
            super(component);
            battery = new ItemComponentHelper() {
                @Override
                public int getCapacity() {
                    return 1;
                }
            };
            this.capacity = component.capacity;
        }

        public boolean acceptsFE() {
            return component.acceptsFE;
        }

        public boolean acceptsEU() {
            return component.acceptsEU;
        }

        public RatioConversion getEUConversion() {
            return component.conversionEU;
        }

        public long getMaxInput() {
            return component.maxInput;
        }

        public void setMaxInput(long input) {
            component.maxInput = input;
        }

        public long getMaxOutput() {
            return component.maxOutput;
        }

        public void setMaxOutput(long output) {
            component.maxOutput = output;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            if (isBatteryAccepted() && component.batteryAllowed) {
                ComponentItem.Collector item = new ComponentItem.Collector(getFace());
                if (!collectors.contains(item))
                    collectors.add(item);
            }

            if (acceptsFE()) {
                Collector energy = new Collector(getFace());
                if (!collectors.contains(energy))
                    collectors.add(energy);
            }

            if (acceptsEU()) {
                CollectorIC2 energy = new CollectorIC2();
                if (!collectors.contains(energy))
                    collectors.add(energy);
            }
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new EnergySlot(assembly, this, x, y);
        }

        @Override
        public void update() {
            if (!active && energy.get() > 0) {
                powerLoss += component.powerLoss;
                int intLoss = (int) powerLoss;
                if (intLoss > 0) {
                    energy.set(Math.max(0, energy.get() - intLoss));
                    powerLoss -= intLoss;
                }
                markDirty();
            }
            active = false;
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            if (component.dropsOnBreak) {
                battery.spawnInWorld(world, position);
                battery.setStack(ItemStack.EMPTY);
            }
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("capacity", capacity);
            compound.setLong("energy", energy.get());
            compound.setFloat("loss", powerLoss);
            compound.setTag("battery", battery.writeToNBT(new NBTTagCompound()));
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            capacity = compound.getLong("capacity");
            energy.set(compound.getLong("energy"));
            powerLoss = compound.getFloat("loss");
            battery.readFromNBT(compound.getCompoundTag("battery"));
        }

        @Override
        public boolean acceptsItem(ItemStack stack) {
            return component.filter.apply(stack);
        }

        @Override
        public boolean canInputItem() {
            return component.batteryAllowed;
        }

        @Override
        public boolean canOutputItem() {
            return component.batteryAllowed;
        }

        public boolean canInput() {
            return component.inputAllowed;
        }

        public boolean canOutput() {
            return component.outputAllowed;
        }

        public boolean canPut() {
            return !component.hidden && component.batteryAllowed && component.putAllowed;
        }

        public boolean canTake() {
            return !component.hidden && component.takeAllowed;
        }

        public boolean canOverfill() {
            return component.canOverfill;
        }

        public boolean isBatteryAccepted() {
            return component.batteryAllowed;
        }

        public long getCapacity() {
            if (canOverfill() && getAmount() <= 0)
                return Long.MAX_VALUE;
            IBatteryAccess battery = getBatteryStorage();
            return capacity + battery.getMaxEnergyStored();
        }

        public long getSlotCapacity() {
            return capacity;
        }

        public void setSlotCapacity(long capacity) {
            this.capacity = capacity;
        }

        public long getAmount() {
            IBatteryAccess battery = getBatteryStorage();
            return energy.get() + battery.getEnergyStored();
        }

        public void setAmount(int energy) {
            this.energy.set(energy);
        }

        private IBatteryAccess getBatteryStorage() {
            if (component.batteryAllowed) {
                ItemStack battery = getItem().getStack();
                if (battery.hasCapability(CapabilityEnergy.ENERGY, null)) {
                    return new BatteryAccessFE(battery, battery.getCapability(CapabilityEnergy.ENERGY, null));
                }
            }
            return BatteryAccessEmpty.INSTANCE;
        }

        public String getUnit() {
            return component.unit;
        }

        @Override
        public ItemComponentHelper getItem() {
            return battery;
        }

        public long receive(long amount, boolean simulate, boolean anyway) {
            if (!canInput() && !anyway) {
                return 0;
            }

            IBatteryAccess batteryAccess = getBatteryStorage();
            long internalReceived = Math.min(amount, getCapacity() - energy.get());
            long batteryReceived = batteryAccess.receiveEnergy(Math.max(amount - internalReceived, 0), simulate);
            if (!simulate) {
                energy.set(energy.get() + internalReceived);
                active = true;
                battery.setStack(batteryAccess.getStack());
                markDirty();
            }
            return internalReceived + batteryReceived;
        }

        public long receive(long amount, boolean simulate) {
            return receive(amount, simulate, false);
        }

        public long extract(long amount, boolean simulate, boolean anyway) {
            if (!canOutput() && !anyway) {
                return 0;
            }

            IBatteryAccess batteryAccess = getBatteryStorage();
            long internalExtracted = Math.min(amount, energy.get());
            long batteryExtracted = batteryAccess.extractEnergy(Math.max(amount - internalExtracted, 0), simulate);
            if (!simulate) {
                energy.set(energy.get() - internalExtracted);
                active = false;
                battery.setStack(batteryAccess.getStack());
                markDirty();
            }
            return internalExtracted + batteryExtracted;
        }

        public long extract(long amount, boolean simulate) {
            return extract(amount, simulate, false);
        }

        @Override
        public boolean isDirty() {
            return super.isDirty() || battery.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
            battery.markClean();
        }

        public IOParametersEnergy getPushEnergy() {
            return component.pushEnergy;
        }

        public void setPushEnergy(long energy) {
            if (component.pushEnergy.active) {
                component.pushEnergy.size = energy;
            }
        }

        @Override
        public IOParameters getPushItem() {
            return component.pushItem;
        }

        @Override
        public boolean canSplit() {
            return true;
        }

        @Override
        public boolean canShift() {
            return component.shiftAllowed;
        }

        public SlotVisual getForeground() {
            return component.foreground;
        }

        public SlotVisual getBackground() {
            return component.background;
        }
    }

    //This has a pretty good chance that it will work poorly.
    public static class CollectorIC2 extends ComponentBase.Collector {
        List<Slot> slots = new ArrayList<>();
        double extraDraw = 0; //We need to stash this

        public CollectorIC2() {
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot instanceof Slot && ((Slot) slot).acceptsEU()) {
                addSlot((Slot) slot);
                return true;
            }
            return false;
        }

        @Override
        public void update() {

        }

        public void draw(double amount) {
            amount += extraDraw;
            for (Slot slot : slots) {
                if (slot.canOutput()) {
                    long extracted = slot.extract(slot.getEUConversion().getBase((int) Math.ceil(amount)), false);
                    amount -= slot.getEUConversion().getUnit(extracted);
                }
            }
            extraDraw = amount;
        }

        public double inject(EnumFacing localSide, EnumFacing globalSide, double amount, double voltage) {
            for (Slot slot : slots) {
                if (slot.canInput() && slot.getFace().matches(localSide, globalSide)) {
                    long inserted = slot.receive(slot.getEUConversion().getBase((long) Math.floor(amount)), false);
                    amount -= slot.getEUConversion().getUnit(inserted);
                }
            }
            return amount;
        }

        public int getInputTier() {
            long maxVoltage = 0;
            for (Slot slot : slots) {
                maxVoltage = Math.max(maxVoltage, slot.getEUConversion().getUnit(slot.getMaxInput()));
            }
            return EnergyNet.instance.getTierFromPower(maxVoltage);
        }

        public int getOutputTier() {
            long maxVoltage = 0;
            for (Slot slot : slots) {
                maxVoltage = Math.max(maxVoltage, slot.getEUConversion().getUnit(slot.getMaxOutput()));
            }
            return EnergyNet.instance.getTierFromPower(maxVoltage);
        }

        public boolean canInputEnergy(EnumFacing localSide, EnumFacing globalSide) {
            for (Slot slot : slots) {
                if (slot.canInput() && slot.getFace().matches(localSide, globalSide)) {
                    return true;
                }
            }
            return false;
        }

        public boolean canOutputEnergy(EnumFacing localSide, EnumFacing globalSide) {
            for (Slot slot : slots) {
                if (slot.canOutput() && slot.getFace().matches(localSide, globalSide)) {
                    return true;
                }
            }
            return false;
        }

        public double getOutputEnergy() {
            long toSend = 0;
            for (Slot slot : slots) {
                if (slot.canOutput()) {
                    toSend += Math.min(slot.getEUConversion().getUnit(slot.getMaxOutput()), slot.energy.get());
                }
            }
            return toSend;
        }

        public double getInputEnergy() {
            long toReceive = 0;
            for (Slot slot : slots) {
                if (slot.canInput()) {
                    toReceive += slot.getEUConversion().getUnit(slot.getCapacity() - slot.energy.get());
                }
            }
            return toReceive;
        }
    }

    public static class Collector extends ComponentBase.Collector implements IEnergyStorage {
        ComponentFace face;
        List<Slot> slots = new ArrayList<>();

        public Collector(ComponentFace face) {
            this.face = face;
        }

        private void addSlot(Slot slot) {
            slots.add(slot);
        }

        @Override
        public boolean accept(ComponentBase.Slot slot) {
            if (slot.getFace() == face && slot instanceof Slot && ((Slot) slot).acceptsFE()) {
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
            if (capability == CapabilityEnergy.ENERGY && face.matches(localSide, globalSide))
                return true;
            return super.hasCapability(capability, localSide, globalSide);
        }

        @Nullable
        @Override
        public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
            if (capability == CapabilityEnergy.ENERGY && face.matches(localSide, globalSide))
                return CapabilityEnergy.ENERGY.cast(this);
            return super.getCapability(capability, localSide, globalSide);
        }

        private boolean canAutoOutput() {
            for (Slot slot : slots) {
                if (slot.getPushEnergy().active)
                    return true;
            }
            return false;
        }

        @Override
        public void update() {
            if (canAutoOutput() && tile instanceof TileEntityAssembly) {
                World world = tile.getWorld();
                BlockPos pos = tile.getPos();

                for (Slot slot : slots) {
                    if (slot.getPushEnergy().active) {
                        long maxCanExtract = Math.min(slot.getPushEnergy().size, slot.getAmount());
                        if (maxCanExtract <= 0) {
                            return;
                        }

                        for (Facing side : face.getSides()) {
                            EnumFacing facing = TileEntityAssembly.toSide(((TileEntityAssembly) tile).getFacing(), side);
                            TileEntity checkTile = world.getTileEntity(pos.offset(facing));

                            // Requious Machine Transfer
                            if (checkTile instanceof TileEntityAssembly) {
                                long filled = attemptRequiousMachineTransfer(facing, (TileEntityAssembly) checkTile, maxCanExtract);

                                if (filled > 0) {
                                    maxCanExtract -=slot.extract(filled, false);
                                    continue;
                                }
                            }

                            // Flux Network Transfer
                            if ((maxCanExtract > 0) && (Mods.FLUX_NETWORKS.isPresent())) {
                                long filled = attemptFluxNetworksTransfer(facing, checkTile, maxCanExtract);

                                if (filled > 0) {
                                    maxCanExtract -= slot.extract(filled, false);
                                    continue;
                                }
                            }

                            // Forge Energy Transfer
                            if ((maxCanExtract > 0) && (checkTile != null) && (checkTile.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite()))) {
                                IEnergyStorage battery = checkTile.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
                                int energy = (maxCanExtract >= Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) maxCanExtract;
                                int filled = battery.receiveEnergy(energy, false);

                                if (filled > 0) {
                                    maxCanExtract -= slot.extract(filled, false);
                                    continue;
                                }
                            }

                            if (maxCanExtract <= 0) {
                                break;
                            }
                        }
                    }
                }
            }
        }

        @net.minecraftforge.fml.common.Optional.Method(modid = "fluxnetworks")
        private long attemptFluxNetworksTransfer(EnumFacing facing, TileEntity checkTile, long maxCanExtract) {
            if (checkTile instanceof TileFluxPlug) {
                final TileFluxPlug plug = (TileFluxPlug) checkTile;
                long maxCanReceive = Math.min(plug.getMaxTransferLimit() - plug.getTransferBuffer(), maxCanExtract);
                return plug.getTransferHandler().receiveFromSupplier(maxCanReceive, facing.getOpposite(), false);
            } else {
                return 0;
            }
        }

        private long attemptRequiousMachineTransfer(EnumFacing facing, TileEntityAssembly assembly, long maxCanExtract) {
            long recievedSum = 0;
            final AssemblyProcessor processor = assembly.getProcessor();
            if (processor != null) {
                for (ComponentBase.Slot slot : processor.getSlots()) {
                    if (slot instanceof Slot) {
                        if (slot.getFace().matches(facing.getOpposite(), facing.getOpposite())) {
                            long maxCanReceive = Math.min(((Slot) slot).getCapacity(), maxCanExtract);
                            long received = ((Slot) slot).receive(maxCanReceive, false);
                            recievedSum += received;
                            maxCanExtract -= received;
                        }
                    }
                }
            }
            return recievedSum;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            int received = 0;
            for (Slot slot : slots) {
                received += slot.receive(Math.min(maxReceive - received, slot.getMaxInput()), simulate);
            }
            return received;
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            int extracted = 0;
            for (Slot slot : slots) {
                extracted += slot.extract(Math.min(maxExtract - extracted, slot.getMaxOutput()), simulate);
            }
            return extracted;
        }

        @Override
        public int getEnergyStored() {
            int energy = 0;
            for (Slot slot : slots) {
                energy += slot.getAmount();
            }
            return energy;
        }

        @Override
        public int getMaxEnergyStored() {
            int capacity = 0;
            for (Slot slot : slots) {
                capacity += slot.getCapacity();
            }
            return capacity;
        }

        @Override
        public boolean canExtract() {
            for (Slot slot : slots) {
                if (slot.canOutput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean canReceive() {
            for (Slot slot : slots) {
                if (slot.canInput())
                    return true;
            }
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Collector)
                return face.equals(((Collector) obj).face);
            return false;
        }

        public ComponentFace getFace() {
            return face;
        }
    }
}
