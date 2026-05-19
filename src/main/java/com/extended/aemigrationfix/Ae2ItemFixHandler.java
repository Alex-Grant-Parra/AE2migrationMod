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
        System.out.println("[AE Migration Fix] PlayerLoggedInEvent for " + event.getEntity().getName().getString());
        System.out.println("[AE Migration Fix] Player inventory size = " + event.getEntity().getInventory().items.size());
        int[] slot = {0};
        event.getEntity().getInventory().items.replaceAll(stack ->
                {
                    int currentSlot = slot[0]++;
                    System.out.println("[AE Migration Fix] Checking player slot " + currentSlot + ": " + describeStack(stack));
                    ItemStack fixedStack = fixAe2Stack(stack, event.getEntity().level());
                    if (fixedStack != stack) {
                        System.out.println("[AE Migration Fix] Player slot " + currentSlot + " updated to: " + describeStack(fixedStack));
                    }
                    return fixedStack;
                }
        );
        System.out.println("[AE Migration Fix] Finished PlayerLoggedInEvent processing for " + event.getEntity().getName().getString());
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        System.out.println("[AE Migration Fix] EntityJoinLevelEvent for " + event.getEntity().getType().toShortString() + " on clientSide=" + event.getLevel().isClientSide());
        if (event.getEntity() instanceof ItemEntity itemEntity) {
            System.out.println("[AE Migration Fix] Item entity joined: " + describeStack(itemEntity.getItem()));
            itemEntity.setItem(
                    fixAe2Stack(itemEntity.getItem(), itemEntity.level())
            );
            System.out.println("[AE Migration Fix] Item entity after fix: " + describeStack(itemEntity.getItem()));
        } else {
            System.out.println("[AE Migration Fix] Entity is not an ItemEntity, skipping");
        }
    }

    private static ItemStack fixAe2Stack(ItemStack stack, Level level) {
        System.out.println("[AE Migration Fix] fixAe2Stack input = " + describeStack(stack));
        if (stack == null) {
            System.out.println("[AE Migration Fix] Stack is null, skipping");
            return stack;
        }

        if (stack.isEmpty()) {
            System.out.println("[AE Migration Fix] Stack is empty, skipping");
            return stack;
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        System.out.println("[AE Migration Fix] Stack item id = " + id);
        if (id == null) {
            System.out.println("[AE Migration Fix] No registry id found, skipping");
            return stack;
        }

        if (!isAeFamily(id.getNamespace())) {
            System.out.println("[AE Migration Fix] Namespace " + id.getNamespace() + " is not AE-family, skipping");
            return stack;
        }

        System.out.println("[AE Migration Fix] AE-family stack detected: " + id);

        try {
            var registryAccess = level.registryAccess();
            System.out.println("[AE Migration Fix] Using registryAccess = " + registryAccess);

            Tag tag = stack.save(registryAccess);
            System.out.println("[AE Migration Fix] Saved stack tag type = " + (tag == null ? "null" : tag.getClass().getName()));

            if (!(tag instanceof CompoundTag compoundTag)) {
                System.out.println("[AE Migration Fix] Saved tag was not a CompoundTag, skipping rebuild");
                return stack;
            }

            ItemStack rebuilt = ItemStack.parseOptional(registryAccess, compoundTag);
            System.out.println("[AE Migration Fix] Rebuilt stack = " + describeStack(rebuilt));

            if (!rebuilt.isEmpty()) {
                System.out.println("[AE Migration Fix] Returning rebuilt stack for " + id);
                return rebuilt;
            }

            System.out.println("[AE Migration Fix] Rebuilt stack was empty, keeping original");

        } catch (Exception e) {
            System.err.println(
                    "[AE Migration Fix] AE2 item fix failed for " + id + ": " + e.getMessage()
            );
            e.printStackTrace(System.err);
        }

        System.out.println("[AE Migration Fix] Returning original stack for " + id);
        return stack;
    }

    private static boolean isAeFamily(String namespace) {
        boolean result = namespace.equals("ae2")
                || namespace.equals("appliedenergistics2")
                || namespace.equals("extendedae")
                || namespace.equals("expatternprovider");
        System.out.println("[AE Migration Fix] isAeFamily(" + namespace + ") = " + result);
        return result;
    }

    private static String describeStack(ItemStack stack) {
        if (stack == null) {
            return "null";
        }

        if (stack.isEmpty()) {
            return "empty";
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return id + " x" + stack.getCount();
    }
}