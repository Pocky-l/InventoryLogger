package com.pocky.invbackups.events;

import com.pocky.invbackups.config.InventoryConfig;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.data.EnderChestData;
import com.pocky.invbackups.utils.InventoryUtil;
import com.pocky.invbackups.utils.EnderChestUtil;

public class PlayerConnectionEvent {

    public static boolean joinSaveEnabled = false;
    public static boolean quitSaveEnabled = false;
    public static boolean enderChestJoinSaveEnabled = false;
    public static boolean enderChestQuitSaveEnabled = false;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (joinSaveEnabled) {
                saveInventory(player, "join");
            }

            if (enderChestJoinSaveEnabled && InventoryConfig.general.enderChestEnabled.get()) {
                saveEnderChest(player, "join");
            }
        }
    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (quitSaveEnabled) {
                saveInventory(player, "quit");
            }

            if (enderChestQuitSaveEnabled && InventoryConfig.general.enderChestEnabled.get()) {
                saveEnderChest(player, "quit");
            }
        }
    }

    private void saveInventory(ServerPlayer player, String suffix) {
        if (InventoryUtil.isEmpty(player)) return;

        InventoryData.encode(player.level().registryAccess(), InventoryUtil.collectInventory(player)).save(player.getUUID(), suffix);
    }

    private void saveEnderChest(ServerPlayer player, String suffix) {
        if (EnderChestUtil.isEmpty(player)) return;

        EnderChestData.encode(player.level().registryAccess(), EnderChestUtil.collectEnderChest(player)).save(player.getUUID(), suffix);
    }
}
