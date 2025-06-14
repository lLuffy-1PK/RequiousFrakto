package com.bordlistian.requious.data;

import com.bordlistian.requious.compat.jei.slot.DecorationSlot;
import com.bordlistian.requious.compat.jei.slot.DurationSlot;
import com.bordlistian.requious.compat.jei.slot.EnergySlot;
import com.bordlistian.requious.compat.jei.slot.FluidSlot;
import com.bordlistian.requious.compat.jei.slot.ItemSlot;
import com.bordlistian.requious.compat.jei.slot.JEIInfoSlot;
import com.bordlistian.requious.compat.jei.slot.LaserSlot;
import com.bordlistian.requious.compat.jei.slot.SelectionSlot;
import com.google.common.collect.Lists;
import com.google.gson.annotations.Expose;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IItemStack;
import crafttweaker.api.minecraft.CraftTweakerMC;
import net.minecraft.item.ItemStack;
import com.bordlistian.requious.Registry;
import com.bordlistian.requious.block.BlockAssembly;
import com.bordlistian.requious.compat.crafttweaker.ComponentFaceCT;
import com.bordlistian.requious.compat.crafttweaker.MachineVisualCT;
import com.bordlistian.requious.compat.crafttweaker.SlotVisualCT;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.component.*;
import com.bordlistian.requious.recipe.AssemblyJEIWrapper;
import com.bordlistian.requious.recipe.AssemblyRecipe;
import com.bordlistian.requious.util.LayerType;
import com.bordlistian.requious.util.MachineVisual;
import com.bordlistian.requious.util.PlaceType;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.*;
import java.util.stream.Collectors;

@ZenRegister
@ZenClass("mods.requious.Assembly")
public class AssemblyData extends BaseData {
    @Expose(serialize = false, deserialize = false)
    public transient ComponentBase[][] slots = new ComponentBase[9][5];
    @Expose(serialize = false, deserialize = false)
    public transient Map<String, List<AssemblyRecipe>> recipes = new HashMap<>();

    public PlaceType placeType = PlaceType.Any;
    public LayerType layerType = LayerType.Cutout;
    public boolean hasGUI = true;
    public String[] extraVariants = new String[0];
    public String pathTextureGui = "textures/gui/assembly.png";
    public int moveSlotPosX;
    public int moveSlotPosY;
    public int moveInvSlotsPosX;
    public int moveInvSlotsPosY;
    public int overGuiSizeX;
    public int overGuiSizeY;
    @Expose(serialize = false, deserialize = false)
    public transient List<JEISlot> jeiSlots = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    public transient List<AssemblyRecipe> jeiRecipes = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    public transient List<ItemStack> jeiCatalysts = new ArrayList<>();
    @Expose(serialize = false, deserialize = false)
    private transient BlockAssembly block;
    @Expose(serialize = false, deserialize = false)
    private transient List<MachineVisual> visuals = new ArrayList<>();

    public AssemblyProcessor constructProcessor() {
        AssemblyProcessor processor = new AssemblyProcessor(this);
        processor.setComponent(slots);
        processor.setup();
        return processor;
    }

    public Collection<AssemblyJEIWrapper> getJeiWrappers() {
        return jeiRecipes.stream().map(AssemblyJEIWrapper::new).collect(Collectors.toList());
    }

    public void setBlock(BlockAssembly block) {
        this.block = block;
    }

    public BlockAssembly getBlock() {
        return block;
    }

    public List<MachineVisual> getVisuals() {
        return visuals;
    }

    @ZenMethod
    public static AssemblyData get(String identifier) {
        return Registry.getAssemblyData(identifier);
    }

    @ZenMethod
    public void setSlots(int column, int row) {
        slots = new ComponentBase[column][row];
    }

    private void setSlot(int x, int y, ComponentBase component) {
        slots[x][y] = component;
        component.setPosition(x, y);
    }

    private void setJEISlot(JEISlot slot) {
        jeiSlots.removeIf(oldSlot -> slot.x == oldSlot.x && slot.y == oldSlot.y);
        jeiSlots.add(slot);
    }

    public int getJEIWidth() {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (JEISlot slot : jeiSlots) {
            if (slot instanceof FluidSlot) {
                min = Math.min(min, slot.x);
                max = Math.max(max, slot.x + ((FluidSlot) slot).visual.getWidth() * 2);
                continue;
            }
            min = Math.min(min, slot.x);
            max = Math.max(max, slot.x + 2);
        }
        return max - min;
    }

    public int getJEIHeight() {
        int min = Integer.MAX_VALUE;
        int max = 0;
        for (JEISlot slot : jeiSlots) {
            if (slot instanceof FluidSlot) {
                min = Math.min(min, slot.y);
                max = Math.max(max, slot.y + ((FluidSlot) slot).visual.getHeight() * 2);
            } else if (slot instanceof EnergySlot) {
                min = Math.min(min, slot.y);
                max = Math.max(max, slot.y + 6);
            } else {
                min = Math.min(min, slot.y);
                max = Math.max(max, slot.y + 2);
            }
        }
        return max - min;
    }

    @ZenMethod
    public ComponentItem setItemSlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentItem component = new ComponentItem(face.get(), capacity);
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentFluid setFluidSlot(int x, int y, ComponentFaceCT face, int capacity) {
        ComponentFluid component = new ComponentFluid(face.get(), capacity);
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentEnergy setEnergySlot(int x, int y, ComponentFaceCT face, long capacity) {
        ComponentEnergy component = new ComponentEnergy(face.get(), capacity);
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentEnergy setEUSlot(int x, int y, ComponentFaceCT face, int capacity) {
        return setEnergySlot(x, y, face, capacity * 4L).setUnit("eu").acceptEU(true);
    }

    @ZenMethod
    public ComponentLaser setLaserSlot(int x, int y, ComponentFaceCT face) {
        ComponentLaser component = new ComponentLaser(face.get());
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentSelection setSelectionSlot(int x, int y, String selectionGroup, int index) {
        ComponentSelection component = new ComponentSelection(selectionGroup, index);
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentDuration setDurationSlot(int x, int y) {
        ComponentDuration component = new ComponentDuration();
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentDecoration setDecorationSlot(int x, int y, SlotVisualCT visual) {
        ComponentDecoration component = new ComponentDecoration(SlotVisualCT.unpack(visual));
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public ComponentText setTextSlot(int x, int y) {
        ComponentText component = new ComponentText();
        setSlot(x, y, component);
        return component;
    }

    @ZenMethod
    public void addVisual(MachineVisualCT visual) {
        visuals.add(visual.get());
    }

    @ZenMethod
    public void addRecipe(AssemblyRecipe recipe) {
        recipes.computeIfAbsent(recipe.processGroup, k -> new ArrayList<>()).add(recipe);
    }

    @ZenMethod
    public void setJEIItemSlot(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new ItemSlot(x, y, group, SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIFluidSlot(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new FluidSlot(x, y, group, SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIEnergySlot(int x, int y, String group, @Optional String unit) {
        if (unit == null)
            unit = "none";
        setJEISlot(new EnergySlot(x, y, group, unit));
    }

    @ZenMethod
    public void setJEILaserSlot(int x, int y, String group) {
        setJEISlot(new LaserSlot(x, y, group));
    }

    @ZenMethod
    public void setJEISelectionSlot(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new SelectionSlot(x, y, group, SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIDecoration(int x, int y, String group, @Optional SlotVisualCT visual) {
        setJEISlot(new DecorationSlot(x, y, group, SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIDurationSlot(int x, int y, String group, SlotVisualCT visual) {
        setJEISlot(new DurationSlot(x, y, group, SlotVisualCT.unpack(visual)));
    }

    @ZenMethod
    public void setJEIInfoSlot(int x, int y, String group) {
        setJEISlot(new JEIInfoSlot(x, y, group));
    }

    @ZenMethod
    public void addJEIRecipe(AssemblyRecipe recipe) {
        if (recipe.hasJEICategory()) {
            CraftTweakerAPI.logError("Recipe already has a JEI category.");
            return;
        }
        jeiRecipes.add(recipe);
        recipe.setJEICategory(this);
    }

    @ZenMethod
    public void addJEICatalyst(IItemStack catalyst) {
        jeiCatalysts.add(CraftTweakerMC.getItemStack(catalyst));
    }

    public Iterable<ItemStack> getJEICatalysts() {
        if (jeiCatalysts.isEmpty())
            return Lists.newArrayList(new ItemStack(getBlock()));
        return jeiCatalysts;
    }

    public boolean hasJEIRecipes() {
        return !jeiRecipes.isEmpty();
    }

    public void compactJEI() {
        int minX = Integer.MAX_VALUE;
        int maxX = 0;
        int minY = Integer.MAX_VALUE;
        int maxY = 0;
        for (JEISlot slot : jeiSlots) {
            minX = Math.min(minX, slot.x);
            maxX = Math.max(maxX, slot.x + 1);
            minY = Math.min(minY, slot.y);
            maxY = Math.max(maxY, slot.y + 1);
        }
        for (JEISlot slot : jeiSlots) {
            slot.x -= minX;
            slot.y -= minY;
        }
    }
}
