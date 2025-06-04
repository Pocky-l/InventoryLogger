package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.config.BackupConfig;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDeathBackupEvent {
    public static void register() {
        PlayerEvent.PLAYER_RESPAWN.register((ServerPlayer player, boolean alive) -> {
            if (!alive) { // Only backup if the player actually died (respawn after death)
                backupInventory(player);
            }
        });
    }

    private static void backupInventory(ServerPlayer player) {
        // backup logic here
    }
}
