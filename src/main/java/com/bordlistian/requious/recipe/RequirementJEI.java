package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.ingredient.JEIInfo;
import com.bordlistian.requious.compat.jei.slot.JEIInfoSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.util.SlotVisual;

public class RequirementJEI extends RequirementBase {
    String[] tooltips;
    SlotVisual slotVisual;

    public RequirementJEI(String group, String[] tooltips, SlotVisual slotVisual) {
        super(group);
        this.tooltips = tooltips;
        this.slotVisual = slotVisual;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        return MatchResult.MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {

    }

    @Override
    public <T> void consume(ComponentBase.Slot slot, ConsumptionResult result) {

    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Long(this, 0L);
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
