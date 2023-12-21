package requious.base;

import net.minecraftforge.fml.common.Loader;
public enum Mods {
    CRAFTTWEAKER("crafttweaker"),
    JEI("jei"),
    REDSTONEFLUXAPI("redstoneflux"),
    FLUX_NETWORKS("fluxnetworks"),

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
