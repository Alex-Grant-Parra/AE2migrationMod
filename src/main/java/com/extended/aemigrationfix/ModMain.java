package com.extended.aemigrationfix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod("aemigrationfix")
public class ModMain {
    public ModMain(IEventBus modEventBus) {
        System.out.println("[AE Migration Fix] ModMain initializing");
        System.out.println("[AE Migration Fix] Mod event bus = " + modEventBus.getClass().getName());

        // Instantiate the previous mod class to wire its listeners
        System.out.println("[AE Migration Fix] Creating AEMigrationFix instance");
        new AEMigrationFix(modEventBus);

        // Gameplay events fire on the NeoForge bus, not the mod lifecycle bus.
        System.out.println("[AE Migration Fix] Registering Ae2ItemFixHandler on NeoForge.EVENT_BUS");
        NeoForge.EVENT_BUS.register(Ae2ItemFixHandler.class);

        System.out.println("[AE Migration Fix] ModMain initialized");
    }
}
