package com.bordlistian.requious.compat.jei.slot;

import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import com.bordlistian.requious.Requious;
import com.bordlistian.requious.compat.jei.IngredientCollector;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.util.Fill;
import com.bordlistian.requious.util.Misc;
import com.bordlistian.requious.util.SlotVisual;

import java.util.ArrayList;
import java.util.List;

public class ItemSlot extends JEISlot {
    public List<ItemStack> items = new ArrayList<>();
    public SlotVisual visual;

    public ItemSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group);
        this.visual = visual;
    }

    @Override
    public boolean isFilled() {
        return !items.isEmpty();
    }

    @Override
    public void resetFill() {
        super.resetFill();
        items.clear();
    }

    @Override
    public JEISlot copy() {
        ItemSlot itemSlot = new ItemSlot(x, y, group, visual);
        return itemSlot;
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        for (ItemStack item : items) {
            if (isInput())
                collector.addInput(VanillaTypes.ITEM, item);
            else
                collector.addOutput(VanillaTypes.ITEM, item);
        }
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots_jei.png"));
        Misc.drawTexturedModalRect(x * 9, y * 9, 0, 0, 18, 18);
        visual.render(minecraft, x * 9, y * 9, 100, new Fill(0, 0));
    }
}
