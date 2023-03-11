package su.gamepoint.pocky.inv.data;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.JsonUtils;
import su.gamepoint.pocky.inv.io.JsonFileHandler;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InventoryData implements Serializable {

    /**
     * Хранит id слота и предмет в формате строки
     */
    List<ItemData> data = new ArrayList<>();

    public void save(UUID playerUUID) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDateTime = now.format(formatter);
        new JsonFileHandler<>(this).save("inventory/" + playerUUID.toString() + "/", formattedDateTime);
    }

    public Map<Integer, ItemStack> decode() {

        Map<Integer, ItemStack> map = new HashMap<>();
        data.forEach(e -> map.put(e.getIndex(), ItemStack.of(getTag(e.getNbt()))));

        return map;
    }

    public Inventory getInventory(Player player) {
        Inventory inv = new Inventory(player);
        data.forEach(e -> {
            if (e.getIndex() == 100) {
                inv.armor.set(0, ItemStack.of(getTag(e.getNbt())));
            } else if (e.getIndex() == 101) {
                inv.armor.set(1, ItemStack.of(getTag(e.getNbt())));
            } else if (e.getIndex() == 102) {
                inv.armor.set(2, ItemStack.of(getTag(e.getNbt())));
            } else if (e.getIndex() == 103) {
                inv.armor.set(3, ItemStack.of(getTag(e.getNbt())));
            } else if (e.getIndex() == -106) {
                inv.offhand.set(0, ItemStack.of(getTag(e.getNbt())));
            } else {
                inv.add(e.getIndex(), ItemStack.of(getTag(e.getNbt())));
            }
        });

        return inv;
    }

    public static InventoryData encode(Map<Integer, ItemStack> map) {
        List<ItemData> result = new ArrayList<>();

        map.forEach((i, s) -> {
            CompoundTag tag = new CompoundTag();
            s.save(tag);
            result.add(new ItemData(i, tag.toString()));
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
