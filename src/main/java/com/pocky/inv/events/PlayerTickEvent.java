package com.pocky.inv.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.pocky.inv.data.InventoryData;
import com.pocky.inv.utils.InventoryUtil;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PlayerTickEvent {

    /**
     * ServerPlayer - игрок. Long - количество тиков.
     */
    Map<ServerPlayer, Long> map = new HashMap<>();
    Map<ServerPlayer, InventoryData> lastInventory = new HashMap<>();

    public static boolean tickSaveEnabled = false;

    public static Long PERIOD = 60L;

    /**
     * Раз в какое-то время даёт сигнал сохранить инвентарь в файл
     * @param event
     */
    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!tickSaveEnabled) return;
        if (event.side.isServer()) {
            ServerPlayer player = (ServerPlayer) event.player;

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

        Inventory inv = player.getInventory();

        if (InventoryUtil.isEmpty(inv)) return;

        Map<Integer, ItemStack> itemStackMap = InventoryUtil.collectInventory(inv);

        var data = InventoryData.encode(itemStackMap);

        if (data.equals(lastInventory.get(player))) {
            return;
        }
        lastInventory.put(player, data);
        data.save(player.getUUID(), false);
        InventoryUtil.debugMessageSaveInv(player);
    }
}
