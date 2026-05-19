package com.extended.aemigrationfix;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.IRegistryExtension;

public class AEMigrationFix {
    private static final String MODID = "aemigrationfix";
    private static final String OLD_NS = "expatternprovider";
    private static final String NEW_NS = "extendedae";

    public AEMigrationFix(IEventBus modEventBus) {
        System.out.println("AE Migration Fix loaded");
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            System.out.println("AE Migration Fix: Registering aliases...");
            
            registerAliases(BuiltInRegistries.BLOCK);
            registerAliases(BuiltInRegistries.ITEM);
            registerAliases(BuiltInRegistries.BLOCK_ENTITY_TYPE);
            
            System.out.println("AE Migration Fix: Aliases registered successfully!");
        });
    }

    private static <T> void registerAliases(Registry<T> registry) {
        IRegistryExtension<T> extension = (IRegistryExtension<T>) registry;
        
        registry.stream().toList().forEach(value -> {
            ResourceLocation key = registry.getKey(value);
            if (key != null && NEW_NS.equals(key.getNamespace())) {
                ResourceLocation oldId = ResourceLocation.fromNamespaceAndPath(OLD_NS, key.getPath());
                extension.addAlias(oldId, key);
                System.out.println("AE Migration Fix: Created alias " + oldId + " -> " + key);
            }
        });
    }
}