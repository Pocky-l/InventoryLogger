package su.gamepoint.pocky.inv.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.data.InventoryData;
import su.gamepoint.pocky.inv.utils.InventoryUtil;

@Mod.EventBusSubscriber
public class PlayerDeadEvent {

    public static boolean deadSaveEnabled = false;

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!deadSaveEnabled) return;
        if (event.getEntity() instanceof Player) {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            saveInventory(player);
        }
    }

    private void saveInventory(ServerPlayer player) {
        Inventory inv = player.getInventory();
        if (InventoryUtil.isEmpty(inv)) return;

        InventoryData.encode(InventoryUtil.collectInventory(inv)).save(player.getUUID());
        InventoryUtil.debugMessageSaveInv(player);
    }
}
