package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.slot.EnergySlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;

public class RequirementEnergy extends RequirementBase {
    String mark;
    long min;
    long max;

    public RequirementEnergy(String group, long energy, String mark) {
        this(group, energy, energy, mark);
    }

    public RequirementEnergy(String group, long min, long max, String mark) {
        super(group);
        this.mark = mark;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentEnergy.Slot && slot.isGroup(group)) {
            long extracted = ((ComponentEnergy.Slot) slot).extract(max, true);
            if (extracted >= min) {
                result.add(extracted);
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        if (mark != null)
            container.addInput(mark, (long) result.consumed);
    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentEnergy.Slot && result instanceof ConsumptionResult.Long) {
            ((ComponentEnergy.Slot) slot).extract((long) result.getConsumed(), false);
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Long(this, 0L);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof EnergySlot && slot.group.equals(group) && ((EnergySlot) slot).input == null) {
            EnergySlot energySlot = (EnergySlot) slot;
            energySlot.input = new Energy(min, energySlot.unit);
            return true;
        }

        return false;
    }
}
