package requious.util.battery;

import net.minecraft.item.ItemStack;

public class BatteryAccessEmpty implements IBatteryAccess {
    public static final IBatteryAccess INSTANCE = new BatteryAccessEmpty();

    private BatteryAccessEmpty() {
    }

    @Override
    public long getMaxEnergyStored() {
        return 0;
    }

    @Override
    public long getEnergyStored() {
        return 0;
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        return 0;
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }
}
