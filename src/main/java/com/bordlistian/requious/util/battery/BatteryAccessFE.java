package com.bordlistian.requious.util.battery;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.concurrent.atomic.AtomicLong;

public class BatteryAccessFE implements IBatteryAccess {
    ItemStack battery;
    IEnergyStorage storage;

    public BatteryAccessFE(ItemStack battery, IEnergyStorage storage) {
        this.battery = battery;
        this.storage = storage;
    }

    @Override
    public long getMaxEnergyStored() {
        return storage.getMaxEnergyStored();
    }

    @Override
    public long getEnergyStored() {
        return storage.getEnergyStored();
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        return storage.receiveEnergy(new AtomicLong(maxReceive).intValue(), simulate);
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        return storage.extractEnergy(new AtomicLong(maxExtract).intValue(), simulate);
    }

    @Override
    public ItemStack getStack() {
        return battery;
    }
}
