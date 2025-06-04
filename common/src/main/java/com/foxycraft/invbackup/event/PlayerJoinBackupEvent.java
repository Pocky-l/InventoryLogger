package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.config.BackupConfig;
import dev.architectury.event.events.common.PlayerEvent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerJoinBackupEvent {
    public static void register() {
        PlayerEvent.PLAYER_JOIN.register(player -> {
            if (BackupConfig.backupOnJoin) {
                backupInventory(player);
            }
        });
    }

    private static void backupInventory(ServerPlayer player) {
        // backup logic here
    }
}
