package com.bordlistian.requious.recipe;

import com.bordlistian.requious.compat.jei.JEISlot;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.data.component.ComponentBase;

public abstract class ResultBase {
    String group;

    public ResultBase(String group) {
        this.group = group;
    }

    //Machine global matching
    public boolean matches(AssemblyProcessor assembly) {
        return false;
    }

    public void produce(AssemblyProcessor assembly) {
        //NOOP
    }

    //Slot local matching
    public abstract boolean matches(ComponentBase.Slot slot);

    public abstract void produce(ComponentBase.Slot slot);

    public abstract boolean fillJEI(JEISlot slot);
}
