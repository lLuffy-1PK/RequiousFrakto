package requious.recipe;

import requious.compat.jei.JEISlot;
import requious.compat.jei.ingredient.Energy;
import requious.compat.jei.slot.EnergySlot;
import requious.data.component.ComponentBase;
import requious.data.component.ComponentEnergy;

public class ResultEnergy extends ResultBase {
    long energy;
    long minInsert;

    public ResultEnergy(String group, long energy) {
        this(group, energy, energy);
    }

    public ResultEnergy(String group, long energy, long minInsert) {
        super(group);
        this.energy = energy;
        this.minInsert = minInsert;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        if (slot instanceof ComponentEnergy.Slot && slot.isGroup(group)) {
            long filled = ((ComponentEnergy.Slot) slot).receive(energy, true);
            if (filled >= minInsert)
                return true;
        }
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        ((ComponentEnergy.Slot) slot).receive(energy, false);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof EnergySlot && slot.group.equals(group) && ((EnergySlot) slot).output == null) {
            EnergySlot energySlot = (EnergySlot) slot;
            energySlot.output = new Energy(energy, energySlot.unit);
            return true;
        }

        return false;
    }
}
