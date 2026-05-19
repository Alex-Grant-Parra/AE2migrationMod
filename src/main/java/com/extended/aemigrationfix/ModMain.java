package com.extended.aemigrationfix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod("aemigrationfix")
public class ModMain {
    public ModMain(IEventBus modEventBus) {
        System.out.println("AE Migration Fix: ModMain initializing");

        // Instantiate the previous mod class to wire its listeners
        new AEMigrationFix(modEventBus);

        // Try to register the item fix handler with the mod event bus.
        // If the bus implementation supports class registration this will wire @SubscribeEvent handlers.
        try {
            modEventBus.register(Ae2ItemFixHandler.class);
        } catch (NoSuchMethodError | Exception ignored) {
            try {
                // Fallback: register an instance
                modEventBus.register(new Ae2ItemFixHandler());
            } catch (NoSuchMethodError | Exception e) {
                System.err.println("AE Migration Fix: failed to register Ae2ItemFixHandler on modEventBus: " + e.getMessage());
            }
        }

        System.out.println("AE Migration Fix: ModMain initialized");
    }
}
