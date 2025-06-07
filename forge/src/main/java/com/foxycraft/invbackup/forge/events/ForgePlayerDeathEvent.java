package com.foxycraft.invbackup.forge.events;

import com.foxycraft.invbackup.event.PlayerDeathBackupHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgePlayerDeathEvent {

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerDeathBackupHandler.onPlayerDeath(player);
        }
    }
}
