package com.bordlistian.requious.gui.slot;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentDuration;
import com.bordlistian.requious.gui.GuiAssembly;
import com.bordlistian.requious.util.Fill;
import com.bordlistian.requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class DurationSlot extends BaseSlot<ComponentDuration.Slot> {
    public DurationSlot(AssemblyProcessor assembly, ComponentDuration.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void putStack(ItemStack stack) {
        //NOOP
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getSlotStackLimit() {
        return 0;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 0;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }

    @Override
    public void incrStack(int n) {
        //NOOP
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        SlotVisual visual = binding.getVisual();
        int energy = binding.getTime();
        int capacity = binding.getDuration();
        visual.render(assembly.mc, x - 1, y - 1, 100, new Fill(energy, capacity));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
    }

    @Override
    public boolean hasToolTip() {
        return shouldRender();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        /*String unit = binding.getUnit();

        if(unit != null && I18n.hasKey("unit."+unit))
            tooltip.add(I18n.format("unit."+unit,binding.getAmount(),binding.getCapacity()));*/

        return tooltip;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public boolean isHoverEnabled() {
        return true;
    }
}
