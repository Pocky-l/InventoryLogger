package com.pocky.invbackups.data;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.util.JsonUtils;
import com.pocky.invbackups.io.JsonFileHandler;
import com.pocky.invbackups.utils.CuriosHelper;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventoryData implements Serializable {

    /**
     * Хранит id слота и предмет в формате строки
     */
    List<ItemData> data = new ArrayList<>();

    public void save(UUID playerUUID, boolean isPlayerDead) {
        save(playerUUID, isPlayerDead ? "death" : null);
    }

    public void save(UUID playerUUID, String suffix) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        String fileName;
        if (suffix != null && !suffix.isEmpty()) {
            fileName = formattedDateTime + "-" + suffix;
        } else {
            fileName = formattedDateTime;
        }

        String path = "inventory/" + playerUUID.toString() + "/";

        try {
            new JsonFileHandler<>(this).save(path, fileName);
        } catch (Exception e) {
        }
    }

    public Map<Integer, ItemStack> decode(HolderLookup.Provider registryAccess) {

        Map<Integer, ItemStack> map = new HashMap<>();
        data.forEach(e -> map.put(e.getIndex(), ItemStack.parseOptional(registryAccess, getTag(e.getNbt()))));

        return map;
    }

    public Inventory getInventory(Player player) {
        Inventory inv = new Inventory(player);
        HolderLookup.Provider registryAccess = player.level().registryAccess();
        Map<Integer, ItemStack> curiosItems = new HashMap<>();

        data.forEach(e -> {
            int index = e.getIndex();
            ItemStack stack = ItemStack.parseOptional(registryAccess, getTag(e.getNbt()));

            if (index == 100) {
                inv.armor.set(0, stack);
            } else if (index == 101) {
                inv.armor.set(1, stack);
            } else if (index == 102) {
                inv.armor.set(2, stack);
            } else if (index == 103) {
                inv.armor.set(3, stack);
            } else if (index == -106) {
                inv.offhand.set(0, stack);
            } else if (CuriosHelper.isCuriosSlot(index)) {
                // Collect Curios items separately
                curiosItems.put(index, stack);
            } else {
                inv.add(index, stack);
            }
        });

        // Restore Curios items if this is a ServerPlayer and Curios is loaded
        if (player instanceof ServerPlayer serverPlayer && !curiosItems.isEmpty()) {
            CuriosHelper.restoreCuriosItems(serverPlayer, curiosItems);
        }

        return inv;
    }

    public static InventoryData encode(HolderLookup.Provider registryAccess, Map<Integer, ItemStack> map) {
        List<ItemData> result = new ArrayList<>();

        map.forEach((i, s) -> {
            // Skip empty ItemStacks to avoid IllegalStateException
            if (!s.isEmpty()) {
                CompoundTag tag = (CompoundTag) s.save(registryAccess);
                result.add(new ItemData(i, tag.toString()));
            }
        });

        InventoryData data = new InventoryData();
        data.setData(result);

        return data;
    }

    private CompoundTag getTag(String nbt) {
        var jsonObject = new JsonObject();
        jsonObject.addProperty("nbt", nbt);

        return JsonUtils.readNBT(jsonObject, "nbt");
    }

    public List<ItemData> getData() {
        return data;
    }

    public void setData(List<ItemData> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryData that = (InventoryData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
