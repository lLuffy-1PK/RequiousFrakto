package com.bordlistian.requious.util.color;

import net.minecraft.item.ItemStack;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import com.bordlistian.requious.util.Misc;

import java.awt.*;
import java.util.List;

public class EnergyColor implements ICustomColor {
    List<Color> colors;
    boolean hsbMix;

    public EnergyColor(List<Color> colors, boolean hsbMix) {
        this.colors = colors;
        this.hsbMix = hsbMix;
    }

    @Override
    public Color get() {
        return colors.get(0);
    }

    @Override
    public Color get(ItemStack stack) {
        IEnergyStorage battery = stack.getCapability(CapabilityEnergy.ENERGY, null);
        double lerp = 0;
        if (battery != null)
            lerp = (double) battery.getEnergyStored() / battery.getMaxEnergyStored();

        if (hsbMix)
            return Misc.lerpColorHSB(colors, lerp);
        else
            return Misc.lerpColorRGB(colors, lerp);
    }
}
