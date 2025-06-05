package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.backup.InventoryBackupManager;
import com.foxycraft.invbackup.configs.ConfigHolder;
import dev.architectury.event.events.common.PlayerEvent;


public class PlayerJoinBackupEvent {
    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            if (ConfigHolder.getConfig().backupOnJoin()) {
                InventoryBackupManager.backupPlayerInventory(player, "Join Backup");
            }
        });
    }
}
