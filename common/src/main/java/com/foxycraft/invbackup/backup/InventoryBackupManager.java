package com.foxycraft.invbackup.backup;


import com.foxycraft.invbackup.configs.ConfigHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static net.minecraft.nbt.NbtIo.readCompressed;
import static net.minecraft.nbt.NbtIo.writeCompressed;

public class InventoryBackupManager {

    private static final File BACKUP_DIR = new File("invbackup");

    static {
        BACKUP_DIR.mkdirs();
    }

    public static void backupPlayerInventory(ServerPlayer player, String reason) {
        CompoundTag fullData = new CompoundTag();

        CompoundTag playerData = new CompoundTag();
        player.saveWithoutId(playerData);

        if (playerData.contains("Inventory")) {
            fullData.put("Inventory", playerData.getList("Inventory", Tag.TAG_COMPOUND));
        }

        ListTag enderItems = new ListTag();
        for (int i = 0; i < player.getEnderChestInventory().getContainerSize(); i++) {
            ItemStack stack = player.getEnderChestInventory().getItem(i);
            if (!stack.isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putByte("Slot", (byte) i);
                stack.save(itemTag);
                enderItems.add(itemTag);
            }
        }
        fullData.put("EnderChest", enderItems);

        if (playerData.contains("ForgeCaps")) {
            fullData.put("ForgeCaps", playerData.getCompound("ForgeCaps"));
        }

        fullData.putString("uuid", player.getUUID().toString());
        fullData.putString("name", player.getName().getString());
        fullData.putString("timestamp", getTimestamp());
        fullData.putString("reason", reason);

        File playerDir = new File(BACKUP_DIR, player.getUUID().toString());
        if (!playerDir.exists()) playerDir.mkdirs();

        File backupFile = new File(playerDir, "save_" + System.currentTimeMillis() + ".nbt");

        try {
            writeCompressed(fullData, backupFile);
            // After saving, clean up old backups if maxBackupsPerPlayer is set and > -1
            cleanOldBackups(player.getUUID());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<PlayerBackup> getBackupsForPlayer(UUID playerUUID) {
        File playerDir = new File(BACKUP_DIR, playerUUID.toString());
        File[] files = playerDir.listFiles((dir, name) -> name.endsWith(".nbt")); // => C:/.../invbackup/<UUID>/save_<timestamp>.nbt
        if (files == null) return Collections.emptyList();

        List<PlayerBackup> backups = new ArrayList<>();
        for (File file : files) {
            try {
                CompoundTag tag = readCompressed(file);
                backups.add(new PlayerBackup(file, tag));
            } catch (IOException e) {
                System.err.println("[InvBackup] Failed to read: " + file.getName());
                e.printStackTrace();
            }
        }
        backups.sort(Comparator.comparing(PlayerBackup::getTimestamp).reversed());
        return backups;
    }

    private static void cleanOldBackups(UUID playerUUID) {
        int maxBackups = ConfigHolder.getConfig().maxBackupsPerPlayer();
        if (maxBackups < 0) {
            // -1 means infinite backups, no cleanup needed
            return;
        }

        File[] files = BACKUP_DIR.listFiles((dir, name) -> name.startsWith(playerUUID.toString()));
        if (files == null || files.length <= maxBackups) return;

        // Sort backups by timestamp ascending (oldest first)
        Arrays.sort(files, Comparator.comparingLong(File::lastModified));

        // Delete oldest files exceeding maxBackups count
        int filesToDelete = files.length - maxBackups;
        for (int i = 0; i < filesToDelete; i++) {
            files[i].delete();
        }
    }
    public static List<PlayerBackup> getBackupsForPlayer(String name, String dateFilter) {
        List<PlayerBackup> results = new ArrayList<>();
        File[] files = BACKUP_DIR.listFiles();
        if (files == null) return results;

        for (File file : files) {
            try {
                CompoundTag tag = readCompressed(file);
                if (!tag.getString("name").equalsIgnoreCase(name)) continue;
                if (!dateFilter.isEmpty() && !tag.getString("timestamp").startsWith(dateFilter)) continue;
                results.add(new PlayerBackup(file, tag));
            } catch (IOException ignored) {}
        }
        results.sort(Comparator.comparing(PlayerBackup::getTimestamp).reversed());
        return results;
    }

    public static void restoreBackup(ServerPlayer player, PlayerBackup backup) {
        CompoundTag tag = backup.getData();

        // Restore main inventory
        if (tag.contains("Inventory")) {
            ListTag inventoryTag = tag.getList("Inventory", Tag.TAG_COMPOUND);
            player.getInventory().clearContent();
            player.getInventory().load(inventoryTag);
        }

        // Restore Ender Chest
        if (tag.contains("EnderChest")) {
            ListTag enderItems = tag.getList("EnderChest", Tag.TAG_COMPOUND);
            player.getEnderChestInventory().clearContent();
            for (int i = 0; i < enderItems.size(); i++) {
                CompoundTag itemTag = enderItems.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot >= 0 && slot < player.getEnderChestInventory().getContainerSize()) {
                    player.getEnderChestInventory().setItem(slot, ItemStack.of(itemTag));
                }
            }
        }

        // Restore modded capabilities if any
        if (tag.contains("ForgeCaps")) {
            CompoundTag currentData = new CompoundTag();
            player.saveWithoutId(currentData);
            currentData.put("ForgeCaps", tag.getCompound("ForgeCaps"));
            player.load(currentData);
        }

        player.inventoryMenu.broadcastChanges();
    }

    private static String getTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
    }
}
