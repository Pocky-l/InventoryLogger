package su.gamepoint.pocky.inv.data;

import java.util.Objects;

public class ItemData {

    private int index;

    private String nbt;

    public ItemData(int index, String nbt) {
        this.index = index;
        this.nbt = nbt;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getNbt() {
        return nbt;
    }

    public void setNbt(String nbt) {
        this.nbt = nbt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemData itemData = (ItemData) o;
        return index == itemData.index && Objects.equals(nbt, itemData.nbt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, nbt);
    }
}
