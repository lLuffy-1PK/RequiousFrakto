package requious.recipe;

import requious.compat.crafttweaker.IWorldFunction;
import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentWorld;

public class RequirementWorld extends RequirementBase {
    IWorldFunction worldCheck;
    long checkInterval;

    public RequirementWorld(String group, IWorldFunction worldCheck, long checkInterval) {
        super(group);
        this.worldCheck = worldCheck;
        this.checkInterval = checkInterval;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if(slot instanceof ComponentWorld.Slot) {
            ComponentWorld.Slot worldSlot = (ComponentWorld.Slot) slot;
            if(worldSlot.check(worldCheck,group,checkInterval))
                return MatchResult.MATCHED;
            else
                return MatchResult.CANCEL;
        }
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
        return new ConsumptionResult.Integer(this,0);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
