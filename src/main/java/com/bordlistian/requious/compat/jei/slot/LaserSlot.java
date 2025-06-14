package com.bordlistian.requious.compat.jei.slot;

import com.bordlistian.requious.compat.jei.ingredient.IngredientTypes;
import com.bordlistian.requious.compat.jei.ingredient.Laser;
import net.minecraft.client.Minecraft;
import com.bordlistian.requious.compat.jei.IngredientCollector;
import com.bordlistian.requious.compat.jei.JEISlot;

import java.util.ArrayList;
import java.util.List;

public class LaserSlot extends JEISlot {
    public List<Laser> energies = new ArrayList<>();

    public LaserSlot(int x, int y, String group) {
        super(x, y, group);
    }

    @Override
    public boolean isFilled() {
        return !energies.isEmpty();
    }

    @Override
    public JEISlot copy() {
        return new LaserSlot(x, y, group);
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        for (Laser energy : energies) {
            if (isInput())
                collector.addInput(IngredientTypes.LASER, energy);
            else
                collector.addOutput(IngredientTypes.LASER, energy);
        }
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
