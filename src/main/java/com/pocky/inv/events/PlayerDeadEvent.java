package com.pocky.inv.events;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.pocky.inv.data.InventoryData;
import com.pocky.inv.utils.InventoryUtil;

@Mod.EventBusSubscriber
public class PlayerDeadEvent {

    public static boolean deadSaveEnabled = false;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
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
