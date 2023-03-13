package su.gamepoint.pocky.inv.events;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.data.InventoryData;
import su.gamepoint.pocky.inv.utils.InventoryUtil;

import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber
public class PlayerTickEvent {

    /**
     * ServerPlayer - игрок. Long - количество тиков.
     */
    Map<ServerPlayerEntity, Long> map = new HashMap<>();
    Map<ServerPlayerEntity, InventoryData> lastInventory = new HashMap<>();

    private static final Long PERIOD = InventoryConfig.general.preservationPeriod.get();

    /**
     * Раз в какое-то время даёт сигнал сохранить инвентарь в файл
     * @param event
     */
    @SubscribeEvent
    public void onTickPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!InventoryConfig.general.tickSaveEnabled.get()) return;
        if (event.side.isServer()) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.player;

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

    private void saveInventory(ServerPlayerEntity player) {

        PlayerInventory inv = player.inventory;

        if (InventoryUtil.isEmpty(inv)) return;

        Map<Integer, ItemStack> itemStackMap = InventoryUtil.collectInventory(inv);

        var data = InventoryData.encode(itemStackMap);

        if (data.equals(lastInventory.get(player))) {
            return;
        }
        lastInventory.put(player, data);
        data.save(player.getUUID());
        InventoryUtil.debugMessageSaveInv(player);
    }
}
