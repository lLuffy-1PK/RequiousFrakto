package com.bordlistian.requious.compat.crafttweaker.expand;

import crafttweaker.annotations.ZenRegister;
import com.bordlistian.requious.util.Parameter;
import stanhebben.zenscript.annotations.ZenCaster;
import stanhebben.zenscript.annotations.ZenExpansion;

@ZenExpansion("double")
@ZenRegister
public class ExpansionDouble {
    @ZenCaster
    public static Parameter asParameter(double value) {
        return new Parameter.Constant(value);
    }
}
