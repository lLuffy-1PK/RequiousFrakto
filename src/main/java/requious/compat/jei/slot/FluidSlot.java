package requious.compat.jei.slot;

import mezz.jei.api.ingredients.VanillaTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import requious.Requious;
import requious.compat.jei.IngredientCollector;
import requious.compat.jei.JEISlot;
import requious.util.Fill;
import requious.util.Misc;
import requious.util.SlotVisual;

import java.util.ArrayList;
import java.util.List;

public class FluidSlot extends JEISlot {
    public List<FluidStack> fluids = new ArrayList<>();
    public FillNormalizer normalizer = new FillNormalizer();
    public SlotVisual visual;

    public FluidSlot(int x, int y, String group, SlotVisual visual) {
        super(x, y, group);
        this.visual = visual;
    }

    @Override
    public boolean isFilled() {
        return !fluids.isEmpty();
    }

    @Override
    public void resetFill() {
        super.resetFill();
        fluids.clear();
    }

    public void addFluid(FluidStack fluid) {
        fluids.add(fluid);
        normalizer.add(fluid.amount);
    }

    @Override
    public JEISlot copy() {
        FluidSlot fluidSlot = new FluidSlot(x, y, group, visual);
        fluidSlot.normalizer = normalizer;
        return fluidSlot;
    }

    @Override
    public void getIngredients(IngredientCollector collector) {
        for (FluidStack fluid : fluids) {
            if (isInput())
                collector.addInput(VanillaTypes.FLUID, fluid);
            else
                collector.addOutput(VanillaTypes.FLUID, fluid);
        }
    }

    @Override
    public void render(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Requious.MODID, "textures/gui/assembly_slots_jei.png"));
        Misc.drawTexturedModalRect(x * 9, y * 9, 18, 0, 18, 18);
        visual.render(minecraft, x * 9, y * 9, 100, new Fill(0, 0));
    }

    public static class FillNormalizer {
        int highestFill;

        public void add(int amount) {
            if (amount > highestFill)
                highestFill = amount;
        }

        public int get() {
            return highestFill;
        }
    }
}
