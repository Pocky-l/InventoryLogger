package com.pocky.inv.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import com.pocky.inv.InventoryLoggerMod;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    public static void debugMessageSaveInv(ServerPlayer player) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        InventoryLoggerMod.LOGGER.debug(String.format("The %s saved their inventory: %s",
                player.getScoreboardName(), formattedDateTime));
    }
}
