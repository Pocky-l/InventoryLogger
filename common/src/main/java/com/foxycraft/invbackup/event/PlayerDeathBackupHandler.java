package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.backup.InventoryBackupManager;
import com.foxycraft.invbackup.configs.ConfigHolder;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDeathBackupHandler {

    public static void onPlayerDeath(ServerPlayer player) {
        if (!ConfigHolder.getConfig().backupOnDeath()) return;

        // Backup before inventory is cleared
        InventoryBackupManager.backupPlayerInventory(player, "Death Backup");
    }
}

