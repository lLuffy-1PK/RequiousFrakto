package com.bordlistian.requious.compat.jei.slot;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import com.bordlistian.requious.Requious;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.util.Fill;
import com.bordlistian.requious.util.Misc;
import com.bordlistian.requious.util.SlotVisual;

public class SelectionSlot extends ItemSlot {
    public SelectionSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group, visual);
    }

    @Override
    public JEISlot copy() {
        SelectionSlot selectionSlot = new SelectionSlot(x, y, group, visual);
        return selectionSlot;
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots_jei.png"));
        Misc.drawTexturedModalRect(x * 9, y * 9, 0, 0, 18, 18);
        visual.render(minecraft, x * 9, y * 9, 100, new Fill(0, 0));
        Misc.drawTexturedModalRect(x * 9, y * 9, 18, 18, 18, 18);
    }
}
