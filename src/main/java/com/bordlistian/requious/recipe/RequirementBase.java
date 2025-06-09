package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;

public abstract class RequirementBase {
    public String group;

    public RequirementBase(String group) {
        this.group = group;
    }

    //Machine global matching
    public MatchResult matches(AssemblyProcessor assembly, ConsumptionResult result) {
        return MatchResult.NOT_MATCHED;
    }

    //Slot local matching
    public abstract MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container);

    public abstract <T> void consume(ComponentBase.Slot slot, ConsumptionResult result);

    public abstract ConsumptionResult createResult();

    public abstract boolean fillJEI(JEISlot slot);
}
