package com.pocky.invbackups.events;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.utils.InventoryUtil;

public class PlayerConnectionEvent {

    public static boolean joinSaveEnabled = false;
    public static boolean quitSaveEnabled = false;

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!joinSaveEnabled) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            saveInventory(player, "join");
        }
    }

    @SubscribeEvent
    public void onPlayerQuit(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!quitSaveEnabled) return;
        if (event.getEntity() instanceof ServerPlayer player) {
            saveInventory(player, "quit");
        }
    }

    private void saveInventory(ServerPlayer player, String suffix) {
        if (InventoryUtil.isEmpty(player)) return;

        InventoryData.encode(player.level().registryAccess(), InventoryUtil.collectInventory(player)).save(player.getUUID(), suffix);
    }
}
