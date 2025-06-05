package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.backup.InventoryBackupManager;

import com.foxycraft.invbackup.configs.ConfigHolder;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDeathBackupEvent {

    //FIXME DOES NOT WORK MAKES BACKUP BUT INVENTORY IS BLANK AS ITS TAKING IT AFTER YOU ARE DEAD NEED TO SNIP INVENTORY BEFORE DEATH THEN BACK THAT UP
    public static void register() {
        PlayerEvent.PLAYER_RESPAWN.register((ServerPlayer player, boolean alive) -> {
            if (!alive && ConfigHolder.getConfig().backupOnDeath()) {
                InventoryBackupManager.backupPlayerInventory(player, "Death Backup");
            }
        });
    }
}
