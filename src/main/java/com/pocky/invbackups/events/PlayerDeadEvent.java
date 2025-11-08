package com.pocky.invbackups.events;

import com.pocky.invbackups.config.InventoryConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.data.EnderChestData;
import com.pocky.invbackups.utils.InventoryUtil;
import com.pocky.invbackups.utils.EnderChestUtil;

public class PlayerDeadEvent {

    public static boolean deadSaveEnabled = false;
    public static boolean enderChestDeadSaveEnabled = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            ServerPlayer player = (ServerPlayer) event.getEntity();

            if (deadSaveEnabled) {
                saveInventory(player, true);
            }

            if (enderChestDeadSaveEnabled && InventoryConfig.general.enderChestEnabled.get()) {
                saveEnderChest(player, true);
            }
        }
    }

    private void saveInventory(ServerPlayer player, boolean isPlayerDead) {
        if (InventoryUtil.isEmpty(player)) return;

        InventoryData.encode(player.level().registryAccess(), InventoryUtil.collectInventory(player)).save(player.getUUID(), isPlayerDead);
    }

    private void saveEnderChest(ServerPlayer player, boolean isPlayerDead) {
        if (EnderChestUtil.isEmpty(player)) return;

        EnderChestData.encode(player.level().registryAccess(), EnderChestUtil.collectEnderChest(player)).save(player.getUUID(), isPlayerDead);
    }
}
