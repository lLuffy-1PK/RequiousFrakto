package requious.compat.crafttweaker;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenClass("requious.fluid.ISlotChanged")
@ZenRegister
public interface ISlotChanged {
    void run(MachineContainer machineContainer);
}
