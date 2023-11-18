package requious.util.battery;

import net.minecraft.item.ItemStack;

public interface IBatteryAccess {
    long getMaxEnergyStored();

    long getEnergyStored();

    long receiveEnergy(long maxReceive, boolean simulate);

    long extractEnergy(long maxExtract, boolean simulate);

    ItemStack getStack();
}
