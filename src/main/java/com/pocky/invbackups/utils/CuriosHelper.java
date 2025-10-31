package com.pocky.invbackups.utils;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.ModList;
import com.pocky.invbackups.InventoryBackupsMod;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class for Curios API integration
 * Handles optional dependency safely - works with or without Curios installed
 */
public class CuriosHelper {

    private static final String CURIOS_MOD_ID = "curios";
    private static final int CURIOS_SLOT_START = 1000; // Start index for Curios slots to avoid conflicts

    private static Boolean curiosLoaded = null;

    /**
     * Check if Curios mod is loaded
     */
    public static boolean isCuriosLoaded() {
        if (curiosLoaded == null) {
            curiosLoaded = ModList.get().isLoaded(CURIOS_MOD_ID);
            if (curiosLoaded) {
                InventoryBackupsMod.LOGGER.info("Curios API detected - Curios inventory slots will be saved");
            } else {
                InventoryBackupsMod.LOGGER.debug("Curios API not found - skipping Curios slots");
            }
        }
        return curiosLoaded;
    }

    /**
     * Collect all items from Curios slots
     * Returns empty map if Curios is not loaded
     */
    public static Map<Integer, ItemStack> collectCuriosItems(Player player) {
        Map<Integer, ItemStack> curiosItems = new HashMap<>();

        if (!isCuriosLoaded()) {
            return curiosItems;
        }

        try {
            // Use reflection to safely access Curios API
            curiosItems = collectCuriosItemsInternal(player);
        } catch (Exception e) {
            InventoryBackupsMod.LOGGER.error("Failed to collect Curios items", e);
        }

        return curiosItems;
    }

    /**
     * Restore items to Curios slots
     * Does nothing if Curios is not loaded
     */
    public static void restoreCuriosItems(Player player, Map<Integer, ItemStack> curiosItems) {
        if (!isCuriosLoaded() || curiosItems.isEmpty()) {
            return;
        }

        try {
            restoreCuriosItemsInternal(player, curiosItems);
        } catch (Exception e) {
            InventoryBackupsMod.LOGGER.error("Failed to restore Curios items", e);
        }
    }

    /**
     * Check if Curios inventory is empty
     */
    public static boolean isCuriosEmpty(Player player) {
        if (!isCuriosLoaded()) {
            return true;
        }

        try {
            return collectCuriosItemsInternal(player).isEmpty();
        } catch (Exception e) {
            InventoryBackupsMod.LOGGER.error("Failed to check if Curios is empty", e);
            return true;
        }
    }

    /**
     * Internal method that uses reflection to call Curios API
     * This allows the mod to work without Curios on classpath
     */
    private static Map<Integer, ItemStack> collectCuriosItemsInternal(Player player) {
        Map<Integer, ItemStack> items = new HashMap<>();
        AtomicInteger slotIndex = new AtomicInteger(CURIOS_SLOT_START);

        try {
            // Use reflection to access Curios API: CuriosApi.getCuriosInventory(player)
            Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            var getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
            Object optionalInventory = getCuriosInventoryMethod.invoke(null, player);

            // Check if Optional is present
            java.util.Optional<?> opt = (java.util.Optional<?>) optionalInventory;
            if (opt.isPresent()) {
                Object curiosInventory = opt.get();

                // Get curios map: curiosInventory.getCurios()
                var getCuriosMethod = curiosInventory.getClass().getMethod("getCurios");
                java.util.Map<?, ?> curiosMap = (java.util.Map<?, ?>) getCuriosMethod.invoke(curiosInventory);

                // Iterate through all curio slots
                for (Object stacksHandler : curiosMap.values()) {
                    // Get the IItemHandlerModifiable: stacksHandler.getStacks()
                    var getStacksMethod = stacksHandler.getClass().getMethod("getStacks");
                    Object itemHandler = getStacksMethod.invoke(stacksHandler);

                    // Get slot count: itemHandler.getSlots()
                    var getSlotsMethod = itemHandler.getClass().getMethod("getSlots");
                    int slots = (int) getSlotsMethod.invoke(itemHandler);

                    // Get items from each slot: itemHandler.getStackInSlot(i)
                    var getStackInSlotMethod = itemHandler.getClass().getMethod("getStackInSlot", int.class);
                    for (int i = 0; i < slots; i++) {
                        ItemStack stack = (ItemStack) getStackInSlotMethod.invoke(itemHandler, i);
                        if (!stack.isEmpty()) {
                            items.put(slotIndex.getAndIncrement(), stack.copy());
                        } else {
                            slotIndex.getAndIncrement();
                        }
                    }
                }
            }

            InventoryBackupsMod.LOGGER.debug("Collected {} Curios items from player {}",
                items.size(), player.getScoreboardName());

        } catch (ClassNotFoundException e) {
            InventoryBackupsMod.LOGGER.debug("Curios API not found - skipping Curios items");
        } catch (Exception e) {
            InventoryBackupsMod.LOGGER.error("Failed to collect Curios items via reflection", e);
        }

        return items;
    }

    /**
     * Internal method that uses reflection to call Curios API to restore items
     */
    private static void restoreCuriosItemsInternal(Player player, Map<Integer, ItemStack> curiosItems) {
        AtomicInteger slotIndex = new AtomicInteger(CURIOS_SLOT_START);

        try {
            // Use reflection to access Curios API
            Class<?> curiosApiClass = Class.forName("top.theillusivec4.curios.api.CuriosApi");
            var getCuriosInventoryMethod = curiosApiClass.getMethod("getCuriosInventory", net.minecraft.world.entity.LivingEntity.class);
            Object optionalInventory = getCuriosInventoryMethod.invoke(null, player);

            java.util.Optional<?> opt = (java.util.Optional<?>) optionalInventory;
            if (opt.isPresent()) {
                Object curiosInventory = opt.get();

                var getCuriosMethod = curiosInventory.getClass().getMethod("getCurios");
                java.util.Map<?, ?> curiosMap = (java.util.Map<?, ?>) getCuriosMethod.invoke(curiosInventory);

                for (Object stacksHandler : curiosMap.values()) {
                    var getStacksMethod = stacksHandler.getClass().getMethod("getStacks");
                    Object itemHandler = getStacksMethod.invoke(stacksHandler);

                    var getSlotsMethod = itemHandler.getClass().getMethod("getSlots");
                    int slots = (int) getSlotsMethod.invoke(itemHandler);

                    var setStackInSlotMethod = itemHandler.getClass().getMethod("setStackInSlot", int.class, ItemStack.class);
                    for (int i = 0; i < slots; i++) {
                        int currentIndex = slotIndex.getAndIncrement();
                        ItemStack itemToRestore = curiosItems.get(currentIndex);

                        if (itemToRestore != null) {
                            setStackInSlotMethod.invoke(itemHandler, i, itemToRestore.copy());
                        } else {
                            setStackInSlotMethod.invoke(itemHandler, i, ItemStack.EMPTY);
                        }
                    }
                }
            }

            InventoryBackupsMod.LOGGER.debug("Restored {} Curios items to player {}",
                curiosItems.size(), player.getScoreboardName());

        } catch (ClassNotFoundException e) {
            InventoryBackupsMod.LOGGER.debug("Curios API not found - skipping restore");
        } catch (Exception e) {
            InventoryBackupsMod.LOGGER.error("Failed to restore Curios items via reflection", e);
        }
    }

    /**
     * Check if the slot index belongs to Curios
     */
    public static boolean isCuriosSlot(int slotIndex) {
        return slotIndex >= CURIOS_SLOT_START;
    }
}
