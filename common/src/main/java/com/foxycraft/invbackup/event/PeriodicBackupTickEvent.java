package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.config.BackupConfig;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.level.ServerPlayer;

public class PeriodicBackupTickEvent {
    private static int tickCounter = 0;

    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(level -> {
            tickCounter++;
            if (tickCounter >= BackupConfig.backupIntervalTicks) {
                tickCounter = 0;
                for (ServerPlayer player : level.players()) {
                    backupInventory(player);
                }
            }
        });
    }

    private static void backupInventory(ServerPlayer player) {
        // backup logic here
    }
}
