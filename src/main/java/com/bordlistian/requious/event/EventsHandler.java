package com.bordlistian.requious.event;

import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import com.bordlistian.requious.Requious;
import com.bordlistian.requious.gui.ContainerAssembly;
import com.bordlistian.requious.tile.TileEntityAssembly;

@Mod.EventBusSubscriber(value = Side.SERVER, modid = Requious.MODID)
public class EventsHandler {

    @SubscribeEvent
    public static void onPlayerContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getContainer() instanceof ContainerAssembly) {
            ContainerAssembly containerAssembly = (ContainerAssembly) event.getContainer();
            TileEntityAssembly assembly = (TileEntityAssembly) containerAssembly.getProcessor().getTile();

            assembly.setGuiContainerOpen(true);
        }
    }

    @SubscribeEvent
    public static void onPlayerContainerClosed(PlayerContainerEvent.Close event) {
        if (event.getContainer() instanceof ContainerAssembly) {
            ContainerAssembly containerAssembly = (ContainerAssembly) event.getContainer();
            TileEntityAssembly assembly = (TileEntityAssembly) containerAssembly.getProcessor().getTile();

            assembly.setGuiContainerOpen(false);
        }
    }
}
