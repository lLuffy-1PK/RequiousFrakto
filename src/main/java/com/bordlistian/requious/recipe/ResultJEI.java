package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.ingredient.JEIInfo;
import com.bordlistian.requious.compat.jei.slot.JEIInfoSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.util.SlotVisual;

public class ResultJEI extends ResultBase {
    String[] tooltips;
    SlotVisual slotVisual;

    public ResultJEI(String group, String[] tooltips, SlotVisual slotVisual) {
        super(group);
        this.tooltips = tooltips;
        this.slotVisual = slotVisual;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        return true;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {

    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof JEIInfoSlot && slot.group.equals(group) && !slot.isFilled()) {
            JEIInfoSlot laserSlot = (JEIInfoSlot) slot;
            laserSlot.info = new JEIInfo(tooltips, slotVisual);
            return true;
        }

        return false;
    }
}
