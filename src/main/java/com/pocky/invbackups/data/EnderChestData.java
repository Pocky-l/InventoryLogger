package com.pocky.invbackups.data;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.neoforged.neoforge.common.util.JsonUtils;
import com.pocky.invbackups.io.JsonFileHandler;
import net.minecraft.world.SimpleContainer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Data class for storing ender chest inventory backups
 */
public class EnderChestData implements Serializable {

    /**
     * Stores slot id and item in string format
     * Ender chest has 27 slots (3 rows of 9 slots)
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

        String path = "enderchest/" + playerUUID.toString() + "/";

        try {
            new JsonFileHandler<>(this).save(path, fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Decode stored data to a map of slot index to ItemStack
     */
    public Map<Integer, ItemStack> decode(HolderLookup.Provider registryAccess) {
        Map<Integer, ItemStack> map = new HashMap<>();
        data.forEach(e -> map.put(e.getIndex(), ItemStack.parseOptional(registryAccess, getTag(e.getNbt()))));
        return map;
    }

    /**
     * Convert to SimpleContainer for GUI display
     */
    public SimpleContainer toContainer(HolderLookup.Provider registryAccess) {
        SimpleContainer container = new SimpleContainer(27);

        data.forEach(e -> {
            int index = e.getIndex();
            if (index >= 0 && index < 27) {
                ItemStack stack = ItemStack.parseOptional(registryAccess, getTag(e.getNbt()));
                container.setItem(index, stack);
            }
        });

        return container;
    }

    /**
     * Encode a map of items to EnderChestData
     */
    public static EnderChestData encode(HolderLookup.Provider registryAccess, Map<Integer, ItemStack> map) {
        List<ItemData> result = new ArrayList<>();

        map.forEach((i, s) -> {
            // Skip empty ItemStacks to avoid IllegalStateException
            if (!s.isEmpty()) {
                CompoundTag tag = (CompoundTag) s.save(registryAccess);
                result.add(new ItemData(i, tag.toString()));
            }
        });

        EnderChestData data = new EnderChestData();
        data.setData(result);

        return data;
    }

    /**
     * Encode a SimpleContainer to EnderChestData
     */
    public static EnderChestData fromContainer(HolderLookup.Provider registryAccess, SimpleContainer container) {
        Map<Integer, ItemStack> map = new HashMap<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (!stack.isEmpty()) {
                map.put(i, stack);
            }
        }
        return encode(registryAccess, map);
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
        EnderChestData that = (EnderChestData) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
