package com.bordlistian.requious.compat.fluxnetworks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.data.component.ComponentEnergy;
import com.bordlistian.requious.tile.TileEntityAssembly;
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

    public boolean canAddEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (!(tile instanceof TileEntityAssembly)) {
            return false;
        }
        final TileEntityAssembly assembly = (TileEntityAssembly) tile;
        AssemblyProcessor processor = assembly.getProcessor();
        if (processor == null) {
            return false;
        }
        for (ComponentBase.Slot<?> slot : processor.getSlots()) {
            if (slot != null && slot.getFace().matches(side, side) && slot instanceof ComponentEnergy.Slot) {
                return ((ComponentEnergy.Slot) slot).canInput();
            }
        }
        return false;
    }

    @Override
    public boolean canRemoveEnergy(@Nonnull TileEntity tile, EnumFacing side) {
        if (!(tile instanceof TileEntityAssembly)) {
            return false;
        }
        final TileEntityAssembly assembly = (TileEntityAssembly) tile;
        AssemblyProcessor processor = assembly.getProcessor();
        if (processor == null) {
            return false;
        }
        for (ComponentBase.Slot<?> slot : processor.getSlots()) {
            if (slot != null && slot.getFace().matches(side, side) && slot instanceof ComponentEnergy.Slot) {
                return ((ComponentEnergy.Slot) slot).canOutput();
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
        AssemblyProcessor processor = assembly.getProcessor();
        if (processor == null){
            return 0;
        }

        for (ComponentBase.Slot<?> slot : processor.getSlots()) {
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
        AssemblyProcessor processor = assembly.getProcessor();
        if (processor == null){
            return 0;
        }

        for (ComponentBase.Slot<?> slot : processor.getSlots()) {
            if (slot.getFace().matches(side, side) && (slot instanceof ComponentEnergy.Slot)) {
                ComponentEnergy.Slot energySlot = (ComponentEnergy.Slot) slot;
                long maxOutput = Math.min(energySlot.getPushEnergySize(), energySlot.getAmount());

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
