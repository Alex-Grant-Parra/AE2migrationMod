package com.extended.aemigrationfix;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

public class Ae2ItemFixHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        event.getEntity().getInventory().items.replaceAll(stack ->
                fixAe2Stack(stack, event.getEntity().level())
        );
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            itemEntity.setItem(
                    fixAe2Stack(itemEntity.getItem(), itemEntity.level())
            );
        }
    }

    private static ItemStack fixAe2Stack(ItemStack stack, Level level) {
        if (stack == null || stack.isEmpty()) return stack;

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id == null) return stack;

        if (!isAeFamily(id.getNamespace())) return stack;

        try {
            var registryAccess = level.registryAccess();

            Tag tag = stack.save(registryAccess);

            if (!(tag instanceof CompoundTag compoundTag)) {
                return stack;
            }

            ItemStack rebuilt = ItemStack.parseOptional(registryAccess, compoundTag);

            if (!rebuilt.isEmpty()) {
                return rebuilt;
            }

        } catch (Exception e) {
            System.err.println(
                    "AE2 item fix failed for " + id + ": " + e.getMessage()
            );
        }

        return stack;
    }

    private static boolean isAeFamily(String namespace) {
        return namespace.equals("ae2")
                || namespace.equals("appliedenergistics2")
                || namespace.equals("extendedae")
                || namespace.equals("expatternprovider");
    }
}