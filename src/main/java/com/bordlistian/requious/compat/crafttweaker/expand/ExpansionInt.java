package com.bordlistian.requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import com.bordlistian.requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("int")
@ZenRegister
public class ExpansionInt {
    @ZenCaster
    public static Parameter asParameter(int value) {
        return new Parameter.Constant(value);
    }
}
