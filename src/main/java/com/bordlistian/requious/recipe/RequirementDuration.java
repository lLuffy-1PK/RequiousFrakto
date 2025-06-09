package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.slot.DurationSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.data.component.ComponentDuration;

public class RequirementDuration extends RequirementBase {
    int duration;

    public RequirementDuration(String group, int duration) {
        super(group);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentDuration.Slot && slot.isGroup(group)) {
            ComponentDuration.Slot selectionSlot = (ComponentDuration.Slot) slot;
            if (selectionSlot.getCurrentRecipe() != null && selectionSlot.getCurrentRecipe() != this)
                return MatchResult.NOT_MATCHED;
            selectionSlot.setCurrentRecipe(this);
            if (selectionSlot.isDone())
                return MatchResult.MATCHED;
            return MatchResult.CANCEL;
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {

    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentDuration.Slot && slot.isGroup(group)) {
            ComponentDuration.Slot selectionSlot = (ComponentDuration.Slot) slot;
            selectionSlot.reset();
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Long(this, 0L);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof DurationSlot) {
            DurationSlot durationSlot = (DurationSlot) slot;
            durationSlot.duration += duration;
            durationSlot.setInput(true);
        }

        return false;
    }
}
