package com.bordlistian.requious.base;

import net.minecraftforge.fml.common.Loader;
public enum Mods {
    CRAFTTWEAKER("crafttweaker"),
    JEI("jei"),
    REDSTONEFLUXAPI("redstoneflux"),
    FLUX_NETWORKS("fluxnetworks"),
    IC2("ic2"),
    ITEM_BORDERS("itemborders"),
    ;

    public final String modid;
    private final boolean loaded;

    Mods(String modName) {
        this.modid = modName;
        this.loaded = Loader.isModLoaded(this.modid);
    }

    public boolean isPresent() {
        return loaded;
    }

}
