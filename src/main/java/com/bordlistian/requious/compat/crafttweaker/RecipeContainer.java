package com.bordlistian.requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.liquid.ILiquidStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import com.bordlistian.requious.recipe.*;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenGetter;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.requious.RecipeContainer")
public class RecipeContainer {
    public Map<String, Object> inputs = new HashMap<>();
    public List<ResultBase> outputs = new ArrayList<>();
    public MachineContainer container;
    public boolean jei;

    public RecipeContainer(MachineContainer container) {
        this.container = container;
    }

    public RecipeContainer(boolean jei) {
        this.jei = jei;
    }

    public void addInput(String mark, ItemStack object) {
        inputs.put(mark, object);
    }

    public void addInput(String mark, FluidStack object) {
        inputs.put(mark, object);
    }

    public void addInput(String mark, long object) {
        inputs.put(mark, object);
    }

    public List<ResultBase> getResults() {
        return outputs;
    }

    @ZenGetter("machine")
    public MachineContainer getContainer() {
        return container;
    }

    @ZenGetter("random")
    public RandomCT getRandom() {
        return container.getRandom();
    }

    @ZenGetter("jei")
    public boolean isJEI() {
        return jei;
    }

    @ZenMethod
    public void addItemOutput(String group, IItemStack istack) {
        ItemStack stack = CraftTweakerMC.getItemStack(istack);
        outputs.add(new ResultItem(group, stack));
    }

    @ZenMethod
    public void addItemOutput(String group, IItemStack istack, int minInsert) {
        ItemStack stack = CraftTweakerMC.getItemStack(istack);
        outputs.add(new ResultItem(group, stack, minInsert));
    }

    @ZenMethod
    public void addFluidOutput(String group, ILiquidStack istack) {
        FluidStack stack = CraftTweakerMC.getLiquidStack(istack);
        outputs.add(new ResultFluid(group, stack));
    }

    @ZenMethod
    public void addFluidOutput(String group, ILiquidStack istack, int minInsert) {
        FluidStack stack = CraftTweakerMC.getLiquidStack(istack);
        outputs.add(new ResultFluid(group, stack, minInsert));
    }

    @ZenMethod
    public void addEnergyOutput(String group, long energy) {
        outputs.add(new ResultEnergy(group, energy));
    }

    @ZenMethod
    public void addEnergyOutput(String group, long energy, int minInsert) {
        outputs.add(new ResultEnergy(group, energy, minInsert));
    }

    @ZenMethod
    public void addEUOutput(String group, int energy) {
        addEnergyOutput(group, energy * 4);
    }

    @ZenMethod
    public void addEUOutput(String group, int energy, int minInsert) {
        addEnergyOutput(group, energy * 4L, minInsert * 4);
    }

    @ZenMethod
    public void addLaserOutput(String group, String type, int amount, LaserVisualCT visual, @Optional SlotVisualCT slotVisual) {
        outputs.add(new ResultLaser(group, type, amount, visual.get(), SlotVisualCT.unpack(slotVisual)));
    }

    @ZenMethod
    public void addWorldOutput(IWorldFunction function) {
        outputs.add(new ResultWorld(function));
    }

    @ZenMethod
    public void addJEIInfo(String group, String[] tooltips, SlotVisualCT slotVisual) {
        outputs.add(new ResultJEI(group, tooltips, SlotVisualCT.unpack(slotVisual)));
    }

    @ZenMethod
    public IItemStack getItem(String mark) {
        ItemStack stack = (ItemStack) inputs.getOrDefault(mark, ItemStack.EMPTY);
        return CraftTweakerMC.getIItemStack(stack);
    }

    @ZenMethod
    public ILiquidStack getFluid(String mark) {
        FluidStack stack = (FluidStack) inputs.getOrDefault(mark, null);
        return CraftTweakerMC.getILiquidStack(stack);
    }

    @ZenMethod
    public long getEnergy(String mark) {
        return (long) inputs.getOrDefault(mark, 0L);
    }
}
