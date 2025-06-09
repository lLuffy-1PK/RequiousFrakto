package com.bordlistian.requious.recipe;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.compat.jei.slot.ItemSlot;
import com.bordlistian.requious.data.component.ComponentBase;
import com.bordlistian.requious.data.component.ComponentItem;

public class RequirementIngredient extends RequirementBase {
    IIngredient ingredient;
    int min, max;

    public RequirementIngredient(String group, IIngredient ingredient) {
        this(group, ingredient, ingredient.getAmount(), ingredient.getAmount());
    }

    public RequirementIngredient(String group, IIngredient ingredient, int min, int max) {
        super(group);
        this.ingredient = ingredient;
        this.min = min;
        this.max = max;
    }

    @Override
    public MatchResult matches(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentItem.Slot && slot.isGroup(group)) {
            ItemStack stack = ((ComponentItem.Slot) slot).getItem().getStack();
            if (ingredient.matches(CraftTweakerMC.getIItemStack(stack)) && stack.getCount() >= min) {
                result.add((long) (Math.min(max, stack.getCount())));
                return MatchResult.MATCHED;
            }
        }
        return MatchResult.NOT_MATCHED;
    }

    @Override
    public void fillContainer(ComponentBase.Slot slot, ConsumptionResult result, RecipeContainer container) {
        if (ingredient.getMark() != null)
            container.addInput(ingredient.getMark(), ((ComponentItem.Slot) slot).getItem().extract((int) result.getConsumed(), true));
    }

    @Override
    public void consume(ComponentBase.Slot slot, ConsumptionResult result) {
        if (slot instanceof ComponentItem.Slot && result instanceof ConsumptionResult.Long) {
            ((ComponentItem.Slot) slot).getItem().extract((int) ((long) result.getConsumed()), false);
        }
    }

    @Override
    public ConsumptionResult createResult() {
        return new ConsumptionResult.Long(this, 0L);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        if (slot instanceof ItemSlot && slot.group.equals(group) && !slot.isFilled()) {
            ItemSlot itemSlot = (ItemSlot) slot;
            if (ingredient.getItems() != null) {
                for (IItemStack stack : ingredient.getItems()) {
                    ItemStack jeiStack = CraftTweakerMC.getItemStack(stack);
                    jeiStack.setCount(ingredient.getAmount());
                    itemSlot.items.add(jeiStack);
                }
            } else {
                //TODO: Mark wildcard
            }
            itemSlot.setInput(true);
            return true;
        }

        return false;
    }
}
