package com.pocky.invbackups.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.utils.InventoryUtil;

public class PlayerDeadEvent {

    public static boolean deadSaveEnabled = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            if (!deadSaveEnabled) return;
            ServerPlayer player = (ServerPlayer) event.getEntity();
            saveInventory(player, true);
        }
    }

    private void saveInventory(ServerPlayer player, boolean isPlayerDead) {
        if (InventoryUtil.isEmpty(player)) return;

        InventoryData.encode(player.level().registryAccess(), InventoryUtil.collectInventory(player)).save(player.getUUID(), isPlayerDead);
    }
}
