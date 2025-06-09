package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.IWorldFunction;
import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;

public class RequirementWorld extends RequirementBase {
    IWorldFunction worldCheck;
    long checkInterval;

    public RequirementWorld(String group, IWorldFunction worldCheck, long checkInterval) {
        super(group);
        this.worldCheck = worldCheck;
        this.checkInterval = checkInterval;
    }

    @Override
    public MatchResult matches(AssemblyProcessor assembly, ConsumptionResult result) {
        if (assembly.check(worldCheck, group, checkInterval))
            return MatchResult.MATCHED;
        else
            return MatchResult.CANCEL;
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
