package su.gamepoint.pocky.inv.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.data.InventoryData;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PlayerTickEvent {

    /**
     * ServerPlayer - игрок. Long - количество тиков.
     */
    Map<ServerPlayer, Long> map = new HashMap<>();
    Map<ServerPlayer, InventoryData> lastInventory = new HashMap<>();

    private static final Long PERIOD = InventoryConfig.general.preservationPeriod.get();

    /**
     * Раз в какое-то время даёт сигнал сохранить инвентарь в файл
     * @param event
     */
    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
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
        boolean isEmpty = true;

        Inventory inv = player.getInventory();

        for (ItemStack stack : inv.items) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        for (ItemStack stack : inv.offhand) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        for (ItemStack stack : inv.armor) {
            if (!stack.isEmpty()) {
                isEmpty = false;
            }
        }
        if (isEmpty) return;

        Map<Integer, ItemStack> itemStackMap = new HashMap<>();

        for (int i = 0; i < inv.items.size(); i++) {
            itemStackMap.put(i, inv.items.get(i));
        }

        itemStackMap.put(100, inv.getArmor(0));
        itemStackMap.put(101, inv.getArmor(1));
        itemStackMap.put(102, inv.getArmor(2));
        itemStackMap.put(103, inv.getArmor(3));

        itemStackMap.put(-106, inv.offhand.get(0));

        var data = InventoryData.encode(itemStackMap);

        if (data.equals(lastInventory.get(player))) {
            return;
        }
        lastInventory.put(player, data);
        data.save(player.getUUID());
    }
}
