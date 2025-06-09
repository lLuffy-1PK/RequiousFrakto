package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.crafttweaker.IWorldFunction;
import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;

public class ResultWorld extends ResultBase {
    IWorldFunction worldCheck;

    public ResultWorld(IWorldFunction worldCheck) {
        super("world");
        this.worldCheck = worldCheck;
    }

    @Override
    public boolean matches(ComponentBase.Slot slot) {
        return false;
    }

    @Override
    public void produce(ComponentBase.Slot slot) {
        //NOOP
    }

    @Override
    public boolean matches(AssemblyProcessor assembly) {
        if (assembly.getTile().getWorld().isRemote)
            return true;
        return assembly.run(worldCheck);
    }

    @Override
    public boolean fillJEI(JEISlot slot) {
        return false;
    }
}
