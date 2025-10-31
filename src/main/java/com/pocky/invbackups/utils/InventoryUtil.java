package com.pocky.invbackups.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class InventoryUtil {

    public static Map<Integer, ItemStack> collectInventory(Inventory inventory) {

        Map<Integer, ItemStack> itemStackMap = new HashMap<>();

        for (int i = 0; i < inventory.items.size(); i++) {
            itemStackMap.put(i, inventory.items.get(i));
        }

        itemStackMap.put(100, inventory.getArmor(0));
        itemStackMap.put(101, inventory.getArmor(1));
        itemStackMap.put(102, inventory.getArmor(2));
        itemStackMap.put(103, inventory.getArmor(3));

        itemStackMap.put(-106, inventory.offhand.get(0));
        return itemStackMap;
    }

    /**
     * Collect all inventory including Curios slots if available
     */
    public static Map<Integer, ItemStack> collectInventory(ServerPlayer player) {
        Map<Integer, ItemStack> itemStackMap = collectInventory(player.getInventory());

        // Add Curios items if Curios is loaded
        if (CuriosHelper.isCuriosLoaded()) {
            Map<Integer, ItemStack> curiosItems = CuriosHelper.collectCuriosItems(player);
            itemStackMap.putAll(curiosItems);
        }

        return itemStackMap;
    }

    public static boolean isEmpty(Inventory inventory) {
        boolean isEmpty = true;

        for (ItemStack stack : inventory.items) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        for (ItemStack stack : inventory.offhand) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        for (ItemStack stack : inventory.armor) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        return isEmpty;
    }

    /**
     * Check if inventory is empty including Curios slots
     */
    public static boolean isEmpty(ServerPlayer player) {
        if (!isEmpty(player.getInventory())) {
            return false;
        }

        // Check Curios inventory if available
        if (CuriosHelper.isCuriosLoaded()) {
            return CuriosHelper.isCuriosEmpty(player);
        }

        return true;
    }
}
