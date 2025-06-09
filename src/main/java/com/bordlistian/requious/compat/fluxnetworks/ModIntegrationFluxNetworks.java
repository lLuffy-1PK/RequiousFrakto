package com.bordlistian.requious.compat.fluxnetworks;

import sonar.fluxnetworks.common.handler.TileEntityHandler;

public class ModIntegrationFluxNetworks {
    public static void preInit() {
        TileEntityHandler.tileEnergyHandlers.add(0, RequiousEnergyHandler.INSTANCE);
    }
}
