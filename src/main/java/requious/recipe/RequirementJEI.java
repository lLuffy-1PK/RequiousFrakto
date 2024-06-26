package requious.recipe;

import requious.compat.crafttweaker.RecipeContainer;
import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.JEIInfo;
import requious.compat.jei.slot.JEIInfoSlot;
import requious.data.component.ComponentBase;
import requious.util.SlotVisual;

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
