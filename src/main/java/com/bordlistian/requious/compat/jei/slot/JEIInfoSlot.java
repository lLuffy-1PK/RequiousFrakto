package com.bordlistian.requious.compat.jei.slot;

import com.bordlistian.requious.compat.jei.ingredient.JEIInfo;
import net.minecraft.client.Minecraft;
import com.bordlistian.requious.compat.jei.IngredientCollector;
import com.bordlistian.requious.compat.jei.JEISlot;

public class JEIInfoSlot extends JEISlot {
    public JEIInfo info;

    public JEIInfoSlot(int x, int y, String group) {
        super(x, y, group);
    }

    @Override
    public boolean isFilled() {
        return info != null;
    }

    @Override
    public JEISlot copy() {
        return new JEIInfoSlot(x, y, group);
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        //NOOP
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {

    }
}
