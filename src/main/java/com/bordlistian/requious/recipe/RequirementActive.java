package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;

public class RequirementActive extends RequirementBase {
    int time;

    public RequirementActive(int time) {
        super("active");
        this.time = time;
    }

    @Override
    public MatchResult matches(AssemblyProcessor assembly, ConsumptionResult result) {
        assembly.setVariable("active", time);
        return MatchResult.MATCHED;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        //NOOP
    }

    @Override
    public <T> void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        //NOOP
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Long(this, 0L);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
