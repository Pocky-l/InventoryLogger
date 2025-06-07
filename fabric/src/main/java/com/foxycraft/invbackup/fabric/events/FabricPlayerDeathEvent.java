package com.foxycraft.invbackup.fabric.events;


import com.foxycraft.invbackup.event.PlayerDeathBackupHandler;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerPlayer;

public class FabricPlayerDeathEvent {
    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (entity instanceof ServerPlayer player && player.isDeadOrDying()) {
                PlayerDeathBackupHandler.onPlayerDeath(player);
            }
        });
    }
}
