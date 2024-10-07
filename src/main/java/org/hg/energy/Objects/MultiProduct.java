package org.hg.energy.Objects;

public enum MultiProduct {
    MaybeNothing(0, "Может быть не выпадет ничего"),
    OneThing(1, "С гарантией выпадет что то одно"),
    Lot(2, "Гарантировано выпадет 1 или больше");

    private final int slot;
    private final String name;

    MultiProduct(int slot, String str) {
        this.slot = slot;
        this.name = str;
    }

    public String getName() {
        return name;
    }

    public int getSlot() {
        return slot;
    }
}
