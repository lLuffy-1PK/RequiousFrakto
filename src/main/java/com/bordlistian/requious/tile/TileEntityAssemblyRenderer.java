package com.bordlistian.requious.tile;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import com.bordlistian.requious.data.AssemblyProcessor;
import com.bordlistian.requious.util.MachineVisual;

public class TileEntityAssemblyRenderer extends TileEntitySpecialRenderer<TileEntityAssembly> {
    @Override
    public void render(TileEntityAssembly tile, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(tile, x, y, z, partialTicks, destroyStage, alpha);
        AssemblyProcessor assembly = tile.getProcessor();
        if (assembly != null) {
            for (MachineVisual visual : assembly.getVisuals()) {
                visual.render(tile, x, y, z, partialTicks, destroyStage, alpha);
            }
        }
    }
}
