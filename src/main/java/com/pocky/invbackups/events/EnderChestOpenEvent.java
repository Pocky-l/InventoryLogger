package com.pocky.invbackups.events;

import com.pocky.invbackups.config.InventoryConfig;
import com.pocky.invbackups.data.EnderChestData;
import com.pocky.invbackups.utils.EnderChestUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerContainerEvent;

public class EnderChestOpenEvent {

    public static boolean enderChestOpenSaveEnabled = false;

    @SubscribeEvent
    public void onEnderChestOpen(PlayerContainerEvent.Open event) {
        if (!enderChestOpenSaveEnabled) return;
        if (!InventoryConfig.general.enderChestEnabled.get()) return;

        if (event.getEntity() instanceof ServerPlayer player) {
            // Check if the opened container is an ender chest (3 rows container)
            if (event.getContainer() instanceof ChestMenu chestMenu) {
                // Ender chest inventory is a ChestMenu with 3 rows
                if (chestMenu.getContainer().getContainerSize() == 27) {
                    saveEnderChest(player, "open");
                }
            }
        }
    }

    private void saveEnderChest(ServerPlayer player, String suffix) {
        if (EnderChestUtil.isEmpty(player)) return;

        EnderChestData.encode(player.level().registryAccess(), EnderChestUtil.collectEnderChest(player)).save(player.getUUID(), suffix);
    }
}
