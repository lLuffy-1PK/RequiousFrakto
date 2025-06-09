package com.bordlistian.requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import com.bordlistian.requious.compat.jei.IngredientCollector;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.util.Fill;
import com.bordlistian.requious.util.SlotVisual;

public class DecorationSlot extends JEISlot {
    SlotVisual visual;

    public DecorationSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group);
        this.visual = visual;
    }

    @Override
    public JEISlot copy() {
        return new DecorationSlot(x, y, group, visual);
    }

    @Override
    public void getIngredients(IngredientCollector collector) {

    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        visual.render(minecraft, x * 9, y * 9, 100, new Fill(0, 0));
    }
}
