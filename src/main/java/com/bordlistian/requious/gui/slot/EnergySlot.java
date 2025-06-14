package com.bordlistian.requious.gui.slot;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.energy.CapabilityEnergy;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentEnergy;
import com.bordlistian.requious.gui.GuiAssembly;
import com.bordlistian.requious.util.Fill;
import com.bordlistian.requious.util.Misc;
import com.bordlistian.requious.util.SlotVisual;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class EnergySlot extends BaseSlot<ComponentEnergy.Slot> {
    public EnergySlot(AssemblyProcessor assembly, ComponentEnergy.Slot binding, int xPosition, int yPosition) {
        super(assembly, binding, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.hasCapability(CapabilityEnergy.ENERGY, null);
    }

    @Override
    @Nonnull
    public ItemStack getStack() {
        return binding.getItem().getStack();
    }

    @Override
    public void putStack(ItemStack stack) {
        binding.getItem().setStack(stack);
        this.onSlotChanged();
    }

    @Override
    public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {

    }

    @Override
    public int getSlotStackLimit() {
        return binding.getItem().getCapacity();
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        if (!binding.canPut())
            return binding.getItem().getAmount();
        return binding.getItem().getCapacity();
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        if (!binding.canTake())
            return false;
        return !binding.getItem().extract(1, true).isEmpty();
    }

    @Override
    public void incrStack(int n) {
        binding.getItem().insert(n, false);
    }

    @Override
    public void renderBackground(GuiAssembly assembly, int x, int y, float partialTicks, int mousex, int mousey) {
        SlotVisual background = binding.getBackground();
        long energy = binding.getAmount();
        long capacity = binding.getCapacity();
        background.render(assembly.mc, x - 1, y - 1, 100, new Fill(energy, capacity));
    }

    @Override
    public void renderForeground(GuiAssembly assembly, int x, int y, int mousex, int mousey) {
        SlotVisual foreground = binding.getForeground();
        long energy = binding.getAmount();
        long capacity = binding.getCapacity();
        if (foreground != null)
            foreground.render(assembly.mc, x - 1, y - 1, 100, new Fill(energy, capacity));
    }

    @Override
    public boolean hasToolTip() {
        return shouldRender();
    }

    @Override
    public List<String> getTooltip() {
        List<String> tooltip = new ArrayList<>();
        String unit = binding.getUnit();

        if (unit != null && I18n.hasKey("requious.unit." + unit)) {
            long amount = binding.getEUConversion().getUnit(binding.getAmount());
            long capacity = binding.getEUConversion().getUnit(binding.getCapacity());

            if (unit.equals("eu")) {
                tooltip.add(I18n.format("requious.unit.eu", Misc.formatNumber(amount), Misc.formatNumber(capacity)));
            } else {
                tooltip.add(I18n.format("requious.unit." + unit, Misc.formatNumber(binding.getAmount()), Misc.formatNumber(binding.getCapacity())));
            }
        }
        return tooltip;
    }

    @Override
    public ItemStack decrStackSize(int amount) {
        return binding.getItem().extract(amount, false);
    }

    @Override
    public boolean isEnabled() {
        return !binding.isHidden() && binding.isBatteryAccepted();
    }

    @Override
    public boolean isHoverEnabled() {
        return true;
    }

    @Override
    public Vec3i getSize() {
        SlotVisual background = binding.getBackground();
        return new Vec3i(background.getWidth() * 18 - 2, background.getHeight() * 18 - 2, 0);
    }

    @Override
    public boolean canShiftPut() {
        return super.canShiftPut() && binding.canPut();
    }

    @Override
    public boolean canShiftTake() {
        return super.canShiftTake() && binding.canTake();
    }
}
