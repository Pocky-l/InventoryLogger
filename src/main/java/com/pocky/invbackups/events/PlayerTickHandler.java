package com.pocky.invbackups.events;

import com.pocky.invbackups.InventoryBackupsMod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.utils.InventoryUtil;

import java.util.HashMap;
import java.util.Map;

public class PlayerTickHandler {

    /**
     * ServerPlayer - игрок. Long - количество тиков.
     */
    Map<ServerPlayer, Long> map = new HashMap<>();
    Map<ServerPlayer, InventoryData> lastInventory = new HashMap<>();

    public static boolean tickSaveEnabled = false;

    public static Long PERIOD = 60L;

    private static boolean loggedFirst = false;

    /**
     * Раз в какое-то время даёт сигнал сохранить инвентарь в файл
     * @param event
     */
    @SubscribeEvent
    public void onTickPlayerTick(PlayerTickEvent.Post event) {
        if (!loggedFirst) {
            loggedFirst = true;
        }

        if (!tickSaveEnabled) return;
        if (!event.getEntity().level().isClientSide()) {
            ServerPlayer player = (ServerPlayer) event.getEntity();

            if (!map.containsKey(player)) {
                map.put(player, 0L);
            }

            long newValue = map.get(player) + 1;
            map.put(player, newValue);

            if ((map.get(player) / 20) >= PERIOD) {
                saveInventory(player);
                map.put(player, 0L);
            }
        }
    }

    private void saveInventory(ServerPlayer player) {

        if (InventoryUtil.isEmpty(player)) {
            return;
        }

        Map<Integer, ItemStack> itemStackMap = InventoryUtil.collectInventory(player);

        var data = InventoryData.encode(player.level().registryAccess(), itemStackMap);

        if (data.equals(lastInventory.get(player))) {
            return;
        }
        lastInventory.put(player, data);
        data.save(player.getUUID(), false);
    }
}
