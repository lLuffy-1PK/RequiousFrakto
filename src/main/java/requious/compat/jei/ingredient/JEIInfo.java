package requious.compat.jei.ingredient;

import requious.util.SlotVisual;

public class JEIInfo implements IFakeIngredient {
    public String[] tooltips;
    public SlotVisual visual;

    public JEIInfo(String[] tooltips, SlotVisual visual) {
        this.tooltips = tooltips;
        this.visual = visual;
    }

    @Override
    public String getDisplayName() {
        return "Info";
    }

    @Override
    public String getUniqueID() {
        return "info";
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
