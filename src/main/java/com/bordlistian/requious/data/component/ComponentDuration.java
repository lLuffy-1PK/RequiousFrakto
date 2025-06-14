package com.bordlistian.requious.data.component;

import crafttweaker.annotations.ZenRegister;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.bordlistian.requious.compat.crafttweaker.SlotVisualCT;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.gui.slot.DurationSlot;
import com.bordlistian.requious.recipe.RequirementDuration;
import com.bordlistian.requious.util.ComponentFace;
import com.bordlistian.requious.util.SlotVisual;
import stanhebben.zenscript.annotations.ReturnsSelf;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.List;

@ZenRegister
@ZenClass("mods.requious.DurationSlot")
public class ComponentDuration extends ComponentBase {
    public SlotVisual visual = SlotVisual.EMPTY;

    public ComponentDuration() {
        super(ComponentFace.None);
    }

    @ReturnsSelf
    @ZenMethod
    public ComponentDuration setVisual(SlotVisualCT visual) {
        this.visual = SlotVisualCT.unpack(visual);
        return this;
    }

    @Override
    public ComponentBase.Slot createSlot() {
        return new Slot(this);
    }

    public static class Slot extends ComponentBase.Slot<ComponentDuration> {
        RequirementDuration currentRecipe;
        int time, duration;
        boolean active;

        public Slot(ComponentDuration component) {
            super(component);
        }

        public RequirementDuration getCurrentRecipe() {
            return currentRecipe;
        }

        public void setCurrentRecipe(RequirementDuration recipe) {
            if (currentRecipe != null && currentRecipe != recipe) {
                time = 0;
            }
            active = true;
            currentRecipe = recipe;
            duration = recipe.getDuration();
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getDuration() {
            return duration;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public boolean isActive() {
            return active;
        }

        @Override
        public void addCollectors(List<ComponentBase.Collector> collectors) {
            //NOOP
        }

        @Override
        public net.minecraft.inventory.Slot createGui(AssemblyProcessor assembly, int x, int y) {
            return new DurationSlot(assembly, this, x, y);
        }

        @Override
        public void update() {
            if (active) {
                time++;
                if (time > duration)
                    time = duration;
            } else {
                reset();
            }
            active = false;
        }

        @Override
        public void machineBroken(World world, Vec3d position) {
            //NOOP
        }

        @Override
        public NBTTagCompound serializeNBT() {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setBoolean("active", active);
            compound.setInteger("time", time);
            compound.setInteger("duration", duration);
            return compound;
        }

        @Override
        public void deserializeNBT(NBTTagCompound compound) {
            active = compound.getBoolean("active");
            time = compound.getInteger("time");
            duration = compound.getInteger("duration");
        }

        public SlotVisual getVisual() {
            return component.visual;
        }

        public void reset() {
            currentRecipe = null;
            time = 0;
            duration = 0;
        }

        @Override
        public boolean isDirty() {
            return super.isDirty();
        }

        @Override
        public void markClean() {
            super.markClean();
        }

        public boolean isDone() {
            return time >= duration;
        }
    }
}
