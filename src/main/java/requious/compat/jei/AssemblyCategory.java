package requious.compat.jei;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.*;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.util.Translator;
import requious.Requious;
import requious.compat.jei.ingredient.*;
import requious.compat.jei.slot.*;
import requious.data.AssemblyData;
import requious.recipe.AssemblyJEIWrapper;

import javax.annotation.Nonnull;

public class AssemblyCategory implements IRecipeCategory<AssemblyJEIWrapper> {

    @Nonnull
    private final String uid;
    @Nonnull
    private final IDrawable background;
    @Nonnull
    private final String localizedName;

    private final AssemblyData assembly;
    private final IGuiHelper guiHelper;

    SuperStackRenderer stackRenderer = new SuperStackRenderer();

    public AssemblyCategory(AssemblyData assembly, IGuiHelper guiHelper) {
        this.uid = "requious." + assembly.resourceName;
        this.localizedName = Translator.translateToLocal("requious.jei.recipe." + assembly.resourceName);
        this.assembly = assembly;
        this.guiHelper = guiHelper;
        this.background = guiHelper.createBlankDrawable(assembly.getJEIWidth() * 9, assembly.getJEIHeight() * 9);
    }

    @Override
    public String getUid() {
        return uid;
    }

    @Override
    public String getTitle() {
        return localizedName;
    }

    @Override
    public String getModName() {
        return Requious.MODNAME;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void setRecipe(IRecipeLayout recipeLayout, AssemblyJEIWrapper recipeWrapper, IIngredients ingredients) {
        IGuiItemStackGroup items = recipeLayout.getItemStacks();
        IGuiFluidStackGroup fluids = recipeLayout.getFluidStacks();
        IGuiIngredientGroup<Energy> energies = recipeLayout.getIngredientsGroup(IngredientTypes.ENERGY);
        IGuiIngredientGroup<Laser> lasers = recipeLayout.getIngredientsGroup(IngredientTypes.LASER);
        IGuiIngredientGroup<JEIInfo> infos = recipeLayout.getIngredientsGroup(IngredientTypes.INFO);

        int i = 0;
        int e = 0;
        int g = 0;
        int l = 0;
        int m = 0;

        recipeWrapper.recipe.generateJEI();
        for (JEISlot slot : recipeWrapper.recipe.jeiSlots) {
            if (slot instanceof ItemSlot) {
                items.init(i, slot.isInput(), stackRenderer, slot.x * 9, slot.y * 9, GuiItemStackGroup.getWidth(1), GuiItemStackGroup.getHeight(1), 1, 1);
                items.set(i, ((ItemSlot) slot).items);
                i++;
            } else if (slot instanceof FluidSlot) {
                fluids.init(
                        e,
                        slot.isInput(),
                        slot.x * 9 + 1,
                        slot.y * 9 + 1,
                        ((FluidSlot) slot).visual.getWidth() * 18 - 2,
                        ((FluidSlot) slot).visual.getHeight() * 18 - 2,
                        ((FluidSlot) slot).normalizer.get(),
                        false,
                        null
                );
                fluids.set(e, ((FluidSlot) slot).fluids);
                e++;
            } else if (slot instanceof EnergySlot) {
                energies.init(
                        g,
                        slot.isInput(),
                        new EnergyRenderer(),
                        slot.x * 9 + 1,
                        slot.y * 9 + 1,
                        16,
                        52,
                        0,
                        0);
                energies.set(g, ((EnergySlot) slot).getEnergy());
                g++;
            } else if (slot instanceof LaserSlot) {
                lasers.init(l, slot.isInput(), slot.x * 9 + 1, slot.y * 9 + 1);
                lasers.set(l, ((LaserSlot) slot).energies);
                l++;
            } else if (slot instanceof JEIInfoSlot) {
                infos.init(m, slot.isInput(), slot.x * 9 + 1, slot.y * 9 + 1);
                infos.set(m, ((JEIInfoSlot) slot).info);
                m++;
            }
        }
    }
}
