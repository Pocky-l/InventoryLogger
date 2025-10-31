package com.pocky.invbackups.events;

import com.pocky.invbackups.utils.BackupCleanupService;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class ServerTickHandler {

    private static long tickCounter = 0;
    private static final long CLEANUP_INTERVAL = 72000L; // 1 hour in ticks (20 ticks/sec * 60 sec * 60 min)

    @SubscribeEvent
    public void onServerTick(ServerTickEvent.Post event) {

        tickCounter++;

        // Run cleanup every hour
        if (tickCounter >= CLEANUP_INTERVAL) {
            tickCounter = 0;
            BackupCleanupService.cleanupOldBackups();
        }
    }
}
