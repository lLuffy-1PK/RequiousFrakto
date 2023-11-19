package requious.compat.fluxnetworks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;
import requious.tile.TileEntityAssembly;
import sonar.fluxnetworks.api.energy.ITileEnergyHandler;

import javax.annotation.Nonnull;

public class RequiousEnergyHandler implements ITileEnergyHandler {
    public static final RequiousEnergyHandler INSTANCE = new RequiousEnergyHandler();

    private RequiousEnergyHandler() {
    }

    @Override
    public boolean hasCapability(@Nonnull TileEntity tile, EnumFacing side) {
        return !tile.isInvalid() && tile instanceof TileEntityAssembly;
    }

    @Override
    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (tile instanceof TileEntityAssembly) {
            final TileEntityAssembly assembly = (TileEntityAssembly) tile;
            for (ComponentBase.Slot<?> slot : assembly.getProcessor().getSlots()) {
                if (slot.getFace().matches(side, side) && (slot instanceof ComponentEnergy.Slot)) {
                    return ((ComponentEnergy.Slot) slot).canInput();
                }
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (tile instanceof TileEntityAssembly) {
            final TileEntityAssembly assembly = (TileEntityAssembly) tile;
            for (ComponentBase.Slot<?> slot : assembly.getProcessor().getSlots()) {
                if (slot.getFace().matches(side, side) && (slot instanceof ComponentEnergy.Slot)) {
                    return ((ComponentEnergy.Slot) slot).canOutput();
                }
            }
        }
        return false;
    }

    @Override
    public long addEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side, boolean simulate) {
        if (!(tile instanceof TileEntityAssembly)) {
            return 0;
        }
        final TileEntityAssembly assembly = (TileEntityAssembly) tile;

        for (ComponentBase.Slot<?> slot : assembly.getProcessor().getSlots()) {
            if (slot.getFace().matches(side, side) && (slot instanceof ComponentEnergy.Slot)) {
                ComponentEnergy.Slot energySlot = (ComponentEnergy.Slot) slot;
                long remainingCapacity = energySlot.getCapacity() - energySlot.getAmount();
                long maxInput = Math.min(energySlot.getMaxInput(), remainingCapacity);

                if (simulate) {
                    return Math.min(maxInput, amount);
                }

                if (maxInput < amount) {
                    return energySlot.receive(maxInput, false);
                } else {
                    return energySlot.receive(amount, false);
                }
            }
        }
        return 0;
    }

    @Override
    public long removeEnergy(long amount, @Nonnull TileEntity tile, EnumFacing side) {
        if (!(tile instanceof TileEntityAssembly)) {
            return 0;
        }
        final TileEntityAssembly assembly = (TileEntityAssembly) tile;

        for (ComponentBase.Slot<?> slot : assembly.getProcessor().getSlots()) {
            if (slot.getFace().matches(side, side) && (slot instanceof ComponentEnergy.Slot)) {
                ComponentEnergy.Slot energySlot = (ComponentEnergy.Slot) slot;
                long maxOutput = Math.min(energySlot.getMaxOutput(), energySlot.getAmount());

                if (maxOutput < amount) {
                    return energySlot.extract(maxOutput, false);
                } else {
                    return energySlot.extract(amount, false);
                }
            }
        }
        return 0;
    }
}
