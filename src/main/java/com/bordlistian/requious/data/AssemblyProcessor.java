package com.bordlistian.requious.data;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import com.bordlistian.requious.compat.crafttweaker.IWorldFunction;
import com.bordlistian.requious.compat.crafttweaker.MachineContainer;
import com.bordlistian.requious.compat.crafttweaker.RecipeContainer;
import com.bordlistian.requious.data.component.*;
import com.bordlistian.requious.data.component.ComponentBase.Collector;
import com.bordlistian.requious.data.component.ComponentBase.Slot;
import com.bordlistian.requious.recipe.AssemblyRecipe;
import com.bordlistian.requious.recipe.ConsumptionResult;
import com.bordlistian.requious.tile.TileEntityAssembly;
import com.bordlistian.requious.util.CheckCache;
import com.bordlistian.requious.util.ILaserStorage;
import com.bordlistian.requious.util.MachineCommandSender;
import com.bordlistian.requious.util.MachineVisual;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class AssemblyProcessor {
    AssemblyData data;
    Slot[][] slots;
    List<Collector> collectors = new ArrayList<>();
    TileEntity tile;
    Map<String, CheckCache> cache = new HashMap<>();
    Map<String, Object> variables = new HashMap<>();
    Map<String, Object> variablesHistory = new HashMap<>();
    MachineContainer container;
    MachineCommandSender commandSender;
    boolean variablesDirty = false;

    public AssemblyProcessor(AssemblyData data) {
        this.data = data;
        this.slots = new Slot[data.slots.length][data.slots[0].length];
        container = new MachineContainer(this);
        commandSender = new MachineCommandSender(this);
    }

    public boolean isActive() {
        return container.getInteger("active") > 0;
    }

    public MachineCommandSender getCommandSender() {
        return commandSender;
    }

    public TileEntity getTile() {
        return tile;
    }

    public EnumFacing getFacing() {
        if (tile instanceof TileEntityAssembly)
            return ((TileEntityAssembly) tile).getFacing();
        return EnumFacing.UP;
    }

    public AssemblyData getData() {
        return data;
    }

    public void setOwner(EntityPlayer player) {
        setVariable("owner", player.getName());
        setVariable("ownerUUID", player.getGameProfile().getId().toString());
    }

    public String getCommandName() {
        Object commandName = getVariable("commandName");
        if (commandName != null)
            return commandName.toString();
        else
            return "@";
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
        variablesDirty = true;
    }

    public void stashVariable(String name) {
        variablesHistory.put(name, getVariable(name));
    }

    public Object getHistory(String name) {
        return variablesHistory.get(name);
    }

    public boolean isCacheInvalid(String type, long time, long interval) {
        CheckCache check = cache.get(type);
        return check == null || check.getTicksSinceLastCheck(time) > interval;
    }

    public boolean getCacheResult(String type) {
        CheckCache check = cache.get(type);
        return check.getResult();
    }

    public void setCacheResult(String type, boolean result, long time) {
        CheckCache check = cache.computeIfAbsent(type, k -> new CheckCache());
        check.setResult(result, time);
    }

    public MachineContainer getContainer() {
        return container;
    }

    public ItemStack insertItem(String group, ItemStack stack) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentItem.Slot && slot.isGroup(group)) {
                ItemStack remainder = ((ComponentItem.Slot) slot).getItem().insert(stack, false);
                if (remainder.getCount() < stack.getCount())
                    return remainder;
            }
        }
        return stack;
    }

    public ItemStack extractItem(String group, Predicate<ItemStack> filter, int n) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentItem.Slot && slot.isGroup(group)) {
                ComponentItem.Slot itemSlot = (ComponentItem.Slot) slot;
                ItemStack extracted = itemSlot.getItem().extract(n, true);
                if (extracted.getCount() >= n && filter.test(extracted)) {
                    itemSlot.getItem().extract(n, false);
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public FluidStack insertFluid(String group, FluidStack stack) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentFluid.Slot && slot.isGroup(group)) {
                int filled = ((ComponentFluid.Slot) slot).fill(stack, false);
                if (filled > 0) {
                    stack.amount = Math.max(0, stack.amount - filled);
                    if (stack.amount > 0)
                        return stack;
                    else
                        return null;
                }
            }
        }
        return stack;
    }

    public FluidStack extractFluid(String group, Predicate<FluidStack> filter, int n) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentFluid.Slot && slot.isGroup(group)) {
                ComponentFluid.Slot fluidSlot = (ComponentFluid.Slot) slot;
                FluidStack extracted = fluidSlot.drain(n, true);
                if (extracted != null && extracted.amount >= n && filter.test(extracted)) {
                    fluidSlot.drain(n, false);
                    return extracted;
                }
            }
        }
        return null;
    }

    public long insertEnergy(String group, long energy) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentEnergy.Slot && slot.isGroup(group)) {
                long filled = ((ComponentEnergy.Slot) slot).receive(energy, false, true);
                if (filled > 0)
                    return energy - filled;
            }
        }
        return energy;
    }

    public long extractEnergy(String group, long energy) {
        for (Slot slot : getSlots()) {
            if (slot instanceof ComponentEnergy.Slot && slot.isGroup(group)) {
                long filled = ((ComponentEnergy.Slot) slot).extract(energy, false, true);
                if (filled > 0)
                    return filled;
            }
        }
        return 0;
    }

    public Iterable<MachineVisual> getVisuals() {
        return data.getVisuals();
    }

    public boolean check(IWorldFunction worldCheck, String group, long interval) {
        if (tile == null || tile.getWorld() == null)
            return false;
        long time = tile.getWorld().getTotalWorldTime();
        if (isCacheInvalid(group, time, interval)) {
            boolean checkResult = worldCheck.run(container);
            setCacheResult(group, checkResult, time);
            return checkResult;
        } else {
            return getCacheResult(group);
        }
    }

    public boolean run(IWorldFunction worldCheck) {
        if (tile == null || tile.getWorld() == null)
            return false;
        return worldCheck.run(container);
    }

    private NBTBase serializeVariable(Object value) {
        if (value instanceof Integer) {
            return new NBTTagInt((int) value);
        }
        if (value instanceof Long) {
            return new NBTTagLong((long) value);
        }
        if (value instanceof Float) {
            return new NBTTagFloat((float) value);
        }
        if (value instanceof Double) {
            return new NBTTagDouble((double) value);
        }
        if (value instanceof String) {
            return new NBTTagString((String) value);
        }
        if (value instanceof ItemStack) {
            NBTTagCompound stackCompound = ((ItemStack) value).serializeNBT();
            stackCompound.setString("CompoundType", "ItemStack");
            return stackCompound;
        }
        if (value instanceof FluidStack) {
            NBTTagCompound stackCompound = ((FluidStack) value).writeToNBT(new NBTTagCompound());
            stackCompound.setString("CompoundType", "FluidStack");
            return stackCompound;
        }
        return null;
    }

    private Object deserializeVariable(NBTBase nbt) {
        if (nbt instanceof NBTTagInt) {
            return ((NBTTagInt) nbt).getInt();
        }
        if (nbt instanceof NBTTagLong) {
            return ((NBTTagLong) nbt).getLong();
        }
        if (nbt instanceof NBTTagFloat) {
            return ((NBTTagFloat) nbt).getFloat();
        }
        if (nbt instanceof NBTTagDouble) {
            return ((NBTTagDouble) nbt).getDouble();
        }
        if (nbt instanceof NBTTagString) {
            return ((NBTTagString) nbt).getString();
        }
        if (nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            String type = compound.getString("CompoundType");

            if (type.equals("ItemStack")) {
                return new ItemStack(compound);
            }
            if (type.equals("FluidStack")) {
                return FluidStack.loadFluidStackFromNBT(compound);
            }
        }
        return null;
    }

    private NBTTagCompound serializeVariables() {
        NBTTagCompound variableCompound = new NBTTagCompound();
        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            NBTBase serialized = serializeVariable(value);
            if (serialized != null)
                variableCompound.setTag(key, serialized);
        }
        return variableCompound;
    }

    private void deserializeVariables(NBTTagCompound variableCompound) {
        variables.clear();
        for (String key : variableCompound.getKeySet()) {
            NBTBase variableEntry = variableCompound.getTag(key);
            Object deserialized = deserializeVariable(variableEntry);
            if (deserialized != null)
                variables.put(key, deserialized);
        }
    }

    private NBTTagCompound serializeCache() {
        NBTTagCompound cacheCompound = new NBTTagCompound();
        for (Map.Entry<String, CheckCache> entry : cache.entrySet()) {
            NBTTagCompound cacheEntry = new NBTTagCompound();
            CheckCache cache = entry.getValue();
            cacheEntry.setLong("time", cache.getCheckTime());
            cacheEntry.setBoolean("result", cache.getResult());
            cacheCompound.setTag(entry.getKey(), cacheEntry);
        }
        return cacheCompound;
    }

    private void deserializeCache(NBTTagCompound cacheCompound) {
        cache.clear();
        for (String key : cacheCompound.getKeySet()) {
            NBTTagCompound cacheEntry = cacheCompound.getCompoundTag(key);
            cache.put(key, new CheckCache(cacheEntry.getBoolean("result"), cacheEntry.getLong("time")));
        }
    }

    public void setComponent(ComponentBase[][] components) {
        for (int x = 0; x < components.length; x++) {
            for (int y = 0; y < components[x].length; y++) {
                ComponentBase component = components[x][y];
                if (component != null)
                    slots[x][y] = component.createSlot();
            }
        }
    }

    private void addToCollector(Slot slot) {
        slot.addCollectors(collectors);
        for (Collector collector : collectors) {
            collector.accept(slot);
        }
    }

    public void setup() {
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null)
                    addToCollector(slot);
            }
        }
    }

    public void setTile(TileEntity tile) {
        this.tile = tile;
        for (Collector collector : collectors) {
            collector.setTile(tile);
        }
        setVariable("commandName", "@");
    }

    public Slot getSlot(int x, int y) {
        if (x < 0 || x >= slots.length || y < 0 || y >= slots[x].length)
            return null;
        return slots[x][y];
    }

    public List<Slot> getSlots() {
        List<Slot> rList = new ArrayList<>();
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null)
                    rList.add(slot);
            }
        }
        return rList;
    }

    public void update() {
        container.setInteger("active", container.getInteger("active") - 1);
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null) {
                    slot.update();
                }
            }
        }
        for (Collector collector : collectors) {
            collector.update();
        }
        boolean recipeCrafted = false;
        for (List<AssemblyRecipe> recipes : data.recipes.values()) {
            for (AssemblyRecipe recipe : recipes) {
                RecipeContainer container = new RecipeContainer(this.container);
                List<ConsumptionResult> results = recipe.matches(this, container);
                if (results != null && !recipeCrafted) {
                    recipe.calculate(container);
                    if (recipe.fitsResults(this, container)) {
                        recipe.consumeRequirements(results);
                        recipe.produceResults(this, container);
                        recipeCrafted = true;
                    }
                }
            }
        }
        for (Collector collector : collectors) {
            collector.updatePost(recipeCrafted);
        }
    }

    public void machineBroken(World world, Vec3d position) {
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null) {
                    slot.machineBroken(world, position);
                }
            }
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null)
                    compound.setTag(x + "_" + y, slot.serializeNBT());
            }
        }
        compound.setTag("variables", serializeVariables());
        compound.setTag("cache", serializeCache());
        return compound;
    }

    public void readFromNBT(NBTTagCompound compound) {
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null)
                    slot.deserializeNBT(compound.getCompoundTag(x + "_" + y));
            }
        }
        deserializeVariables(compound.getCompoundTag("variables"));
        deserializeCache(compound.getCompoundTag("cache"));
    }

    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
        for (Collector collector : collectors) {
            if (collector.hasCapability() && collector.hasCapability(capability, localSide, globalSide))
                return true;
        }
        return false;
    }

    @Nullable
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing localSide, @Nullable EnumFacing globalSide) {
        T instance = null;
        for (Collector collector : collectors) {
            if (collector.hasCapability())
                instance = collector.getCapability(capability, localSide, globalSide);
            if (instance != null)
                break;
        }
        return instance;
    }

    public ILaserStorage getLaserAcceptor(EnumFacing localSide, EnumFacing globalSide) {
        for (Collector collector : collectors) {
            if (collector instanceof ComponentLaser.Collector && ((ComponentLaser.Collector) collector).getFace().matches(localSide, globalSide))
                return (ILaserStorage) collector;
        }
        return null;
    }

    public ComponentEnergy.CollectorIC2 getIC2Handler() {
        for (Collector collector : collectors) {
            if (collector instanceof ComponentEnergy.CollectorIC2)
                return (ComponentEnergy.CollectorIC2) collector;
        }
        return null;
    }

    public boolean isDirty() {
        boolean dirty = false;
        for (int x = 0; x < slots.length; x++) {
            for (int y = 0; y < slots[x].length; y++) {
                Slot slot = slots[x][y];
                if (slot != null && slot.isDirty()) {
                    dirty = true;
                    slot.markClean();
                }
            }
        }
        if (variablesDirty) {
            dirty = true;
            variablesDirty = false;
        }
        return dirty;
    }
}
