package com.foxycraft.invbackup.backup;

import com.foxycraft.invbackup.utils.LogUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerBackup {
    private final File file;
    private final CompoundTag data;

    public PlayerBackup(File file, CompoundTag data) {
        this.file = file;
        this.data = data;
    }

    public UUID getPlayerUUID() {
        return UUID.fromString(data.getString("uuid"));
    }

    public String getPlayerName() {
        return data.getString("name");
    }

    public String getEventType() {
        return data.getString("reason");
    }

    public String getTimestamp() {
        return data.getString("timestamp");
    }

    public Instant getBackupTime() {
        try {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss")
                    .parse(getTimestamp(), Instant::from);
        } catch (Exception e) {
            return Instant.ofEpochMilli(file.lastModified());
        }
    }

    public CompoundTag getData() {
        return data;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return "[" + getTimestamp() + "] " + getPlayerName() + " - " + getEventType();
    }
    public List<ItemStack> getInventoryItems() {
        List<ItemStack> items = new ArrayList<>();
        if (!data.contains("Inventory")) return items;

        ListTag list = data.getList("Inventory", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            items.add(ItemStack.of(list.getCompound(i)));
        }
        return items;
    }

    public List<ItemStack> getEnderChestItems() {
        List<ItemStack> items = new ArrayList<>();
        if (!data.contains("EnderChest")) return items;

        ListTag list = data.getList("EnderChest", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            int slot = tag.getByte("Slot") & 255;
            while (items.size() <= slot) items.add(ItemStack.EMPTY); // Fill empty slots
            items.set(slot, ItemStack.of(tag));
        }
        return items;
    }
    public List<ItemStack> getCuriosItems() {
        List<ItemStack> curios = new ArrayList<>();
        if (!data.contains("ForgeCaps")) {
            LogUtil.warn("No ForgeCaps in NBT");
            return curios;
        }

        CompoundTag forgeCaps = data.getCompound("ForgeCaps");

        if (forgeCaps.contains("curios:inventory")) {
            CompoundTag curiosInventory = forgeCaps.getCompound("curios:inventory");

            if (curiosInventory.contains("Curios", Tag.TAG_LIST)) {
                ListTag curiosList = curiosInventory.getList("Curios", Tag.TAG_COMPOUND);

                for (int i = 0; i < curiosList.size(); i++) {
                    CompoundTag entry = curiosList.getCompound(i);

                    if (entry.contains("StacksHandler", Tag.TAG_COMPOUND)) {
                        CompoundTag handler = entry.getCompound("StacksHandler");

                        if (handler.contains("Stacks", Tag.TAG_LIST)) {
                            ListTag stacks = handler.getList("Stacks", Tag.TAG_COMPOUND);

                            for (int j = 0; j < stacks.size(); j++) {
                                CompoundTag stackWrapper = stacks.getCompound(j);

                                if (stackWrapper.contains("Items", Tag.TAG_LIST)) {
                                    ListTag items = stackWrapper.getList("Items", Tag.TAG_COMPOUND);

                                    for (int k = 0; k < items.size(); k++) {
                                        CompoundTag itemTag = items.getCompound(k);
                                        ItemStack stack = ItemStack.of(itemTag);
                                        if (!stack.isEmpty()) {
                                            curios.add(stack);
                                            LogUtil.debug("Curio [{}][{}][{}] = {} x{}", i, j, k, stack.getItem(), stack.getCount());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        LogUtil.info("Found {} Curios items", curios.size());
        return curios;
    }

}
