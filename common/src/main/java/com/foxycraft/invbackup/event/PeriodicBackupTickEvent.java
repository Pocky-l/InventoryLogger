package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.backup.InventoryBackupManager;

import com.foxycraft.invbackup.configs.ConfigHolder;
import dev.architectury.event.events.common.TickEvent;

import net.minecraft.server.level.ServerPlayer;

public class PeriodicBackupTickEvent {
    private static int tickCounter = 0;

    public static void register() {
        TickEvent.SERVER_LEVEL_POST.register(serverLevel -> {
            if (serverLevel.isClientSide()) return;

            tickCounter++;
            if (tickCounter >= ConfigHolder.getConfig().backupIntervalTicks()) {
                tickCounter = 0;
                for (ServerPlayer player : serverLevel.players()) {
                    InventoryBackupManager.backupPlayerInventory(player, "Periodic Backup");
                }
            }
        });
    }
}
