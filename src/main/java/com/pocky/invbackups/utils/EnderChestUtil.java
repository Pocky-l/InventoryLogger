package com.pocky.invbackups.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.SimpleContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for working with player ender chests
 */
public class EnderChestUtil {

    /**
     * Collect all items from player's ender chest
     * Ender chest has 27 slots (3 rows of 9)
     */
    public static Map<Integer, ItemStack> collectEnderChest(ServerPlayer player) {
        Map<Integer, ItemStack> itemStackMap = new HashMap<>();
        SimpleContainer enderChestInventory = player.getEnderChestInventory();

        for (int i = 0; i < enderChestInventory.getContainerSize(); i++) {
            ItemStack stack = enderChestInventory.getItem(i);
            if (!stack.isEmpty()) {
                itemStackMap.put(i, stack.copy());
            }
        }

        return itemStackMap;
    }

    /**
     * Check if ender chest is empty
     */
    public static boolean isEmpty(ServerPlayer player) {
        SimpleContainer enderChestInventory = player.getEnderChestInventory();

        for (int i = 0; i < enderChestInventory.getContainerSize(); i++) {
            if (!enderChestInventory.getItem(i).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Restore ender chest from a map
     */
    public static void restoreEnderChest(ServerPlayer player, Map<Integer, ItemStack> items) {
        SimpleContainer enderChestInventory = player.getEnderChestInventory();

        // Clear current ender chest
        enderChestInventory.clearContent();

        // Restore items
        items.forEach((index, stack) -> {
            if (index >= 0 && index < enderChestInventory.getContainerSize()) {
                enderChestInventory.setItem(index, stack.copy());
            }
        });
    }
}
