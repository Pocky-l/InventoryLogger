package su.gamepoint.pocky.inv.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import su.gamepoint.pocky.inv.config.InventoryConfig;
import su.gamepoint.pocky.inv.data.InventoryData;
import su.gamepoint.pocky.inv.utils.InventoryUtil;

@Mod.EventBusSubscriber
public class PlayerDeadEvent {

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (!InventoryConfig.general.deadSaveEnabled.get()) return;
        if (event.getEntity() instanceof PlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) event.getEntity();
            saveInventory(player);
        }
    }

    private void saveInventory(ServerPlayerEntity player) {
        PlayerInventory inv = player.inventory;
        if (InventoryUtil.isEmpty(inv)) return;

        InventoryData.encode(InventoryUtil.collectInventory(inv)).save(player.getUUID());
        InventoryUtil.debugMessageSaveInv(player);
    }
}
