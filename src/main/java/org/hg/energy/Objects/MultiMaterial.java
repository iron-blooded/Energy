package org.hg.energy.Objects;

public enum MultiMaterial {
    OneThing(0, "Потребит что то одно"),
    All(1, "Потребит все из списка");

    private final int slot;
    private final String name;

    MultiMaterial(int slot, String str) {
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
