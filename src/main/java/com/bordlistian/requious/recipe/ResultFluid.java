package com.bordlistian.requious.recipe;

import net.minecraftforge.fluids.FluidStack;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.slot.FluidSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.data.component.ComponentFluid;

import javax.annotation.Nonnull;

public class ResultFluid extends ResultBase {
    @Nonnull
    FluidStack stack;
    int minInsert;

    public ResultFluid(String group, @Nonnull FluidStack stack) {
        this(group, stack, stack.amount);
    }

    public ResultFluid(String group, @Nonnull FluidStack stack, int minInsert) {
        super(group);
        this.stack = stack;
        this.minInsert = minInsert;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if (slot instanceof ComponentFluid.Slot && slot.isGroup(group)) {
            int filled = ((ComponentFluid.Slot) slot).fill(stack, true);
            if (filled >= minInsert)
                return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentFluid.Slot) slot).fill(stack, false);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof FluidSlot && slot.group.equals(group) && !slot.isFilled()) {
            FluidSlot fluidSlot = (FluidSlot) slot;
            fluidSlot.addFluid(stack);
            return true;
        }

        return false;
    }
}
