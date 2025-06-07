package com.foxycraft.invbackup.event;

import com.foxycraft.invbackup.backup.InventoryBackupManager;

import com.foxycraft.invbackup.configs.ConfigHolder;
import dev.architectury.event.events.common.TickEvent;

import net.minecraft.server.level.ServerPlayer;



public class PeriodicBackupTickEvent {
    private static int tickCounter = 0;

    public static void register() {

        TickEvent.SERVER_POST.register(server -> {
            tickCounter++;

            if (tickCounter >= ConfigHolder.getConfig().backupIntervalTicks()) {

                tickCounter = 0;
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    InventoryBackupManager.backupPlayerInventory(player, "Periodic Backup");
                }
            }
        });
    }
}

