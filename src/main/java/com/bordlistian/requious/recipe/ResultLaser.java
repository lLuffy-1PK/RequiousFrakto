package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.ingredient.Laser;
import com.bordlistian.requious.compat.jei.slot.LaserSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.data.component.ComponentLaser;
import com.bordlistian.requious.util.LaserVisual;
import com.bordlistian.requious.util.SlotVisual;

public class ResultLaser extends ResultBase {
    String type;
    int energy;
    LaserVisual visual;
    SlotVisual slotVisual;

    public ResultLaser(String group, String type, int energy, LaserVisual visual, SlotVisual slotVisual) {
        super(group);
        this.type = type;
        this.energy = energy;
        this.visual = visual;
        this.slotVisual = slotVisual;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if (slot instanceof ComponentLaser.Slot && slot.isGroup(group)) {
            ComponentLaser.Slot laserSlot = (ComponentLaser.Slot) slot;
            if (laserSlot.getEmitType() == null || laserSlot.getEmitType().equals(type))
                laserSlot.emit(type, energy, visual);
            return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentLaser.Slot) slot).emit(type, energy, visual);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof LaserSlot && slot.group.equals(group) && !slot.isFilled()) {
            LaserSlot laserSlot = (LaserSlot) slot;
            laserSlot.energies.add(new Laser(energy, type, slotVisual));
            laserSlot.setInput(true);
            return true;
        }

        return false;
    }
}
