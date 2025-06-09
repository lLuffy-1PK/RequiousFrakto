package com.bordlistian.requious.compat.jei;

import com.bordlistian.requious.compat.jei.ingredient.EnergyRenderer;
import com.bordlistian.requious.compat.jei.ingredient.FakeIngredientHelper;
import com.bordlistian.requious.compat.jei.ingredient.IngredientTypes;
import com.bordlistian.requious.compat.jei.ingredient.JEIInfoRenderer;
import com.bordlistian.requious.compat.jei.ingredient.LaserRenderer;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;
import com.bordlistian.requious.Registry;
import com.bordlistian.requious.compat.jei.ingredient.*;
import com.bordlistian.requious.data.AssemblyData;

import java.util.ArrayList;

@JEIPlugin
public class Plugin implements IModPlugin {
    public static IJeiHelpers HELPER;

    @Override
    public void registerIngredients(IModIngredientRegistration registry) {
        registry.register(IngredientTypes.ENERGY, new ArrayList<>(), new FakeIngredientHelper<>(), new EnergyRenderer());
        registry.register(IngredientTypes.LASER, new ArrayList<>(), new FakeIngredientHelper<>(), new LaserRenderer());
        registry.register(IngredientTypes.INFO, new ArrayList<>(), new FakeIngredientHelper<>(), new JEIInfoRenderer());
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        for (AssemblyData assembly : Registry.ASSEMBLY_DATA) {
            if (assembly.hasJEIRecipes()) {
                assembly.compactJEI();
                registry.addRecipeCategories(new AssemblyCategory(assembly, guiHelper));
            }
        }
    }

    @Override
    public void register(IModRegistry reg) {
        HELPER = reg.getJeiHelpers();

        for (AssemblyData assembly : Registry.ASSEMBLY_DATA) {
            if (assembly.hasJEIRecipes()) {
                reg.addRecipes(assembly.getJeiWrappers(), "requious." + assembly.resourceName);
                //reg.addRecipeCatalyst(new ItemStack(assembly.getBlock()), "requious." + assembly.resourceName);
                for (ItemStack catalyst : assembly.getJEICatalysts()) {
                    reg.addRecipeCatalyst(catalyst, "requious." + assembly.resourceName);
                }
            }
        }
    }
}
