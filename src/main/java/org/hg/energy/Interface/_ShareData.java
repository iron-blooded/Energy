package org.hg.energy.Interface;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class _ShareData {
    private Mesh mesh;
    private Structure structure;
    private Location location;
    private Energy plugin;
    private InventoryHolder holder;
    private boolean bol = false;
    private String name_item = "";
    private Player player;
    private ItemStack cursor_item = null;
    private ItemStack click_item = null;
    private ClickType clickType;

    public _ShareData(Mesh mesh, Structure structure, Location location, @NotNull Energy plugin) {
        this.mesh = mesh;
        this.structure = structure;
        this.location = location;
        this.plugin = plugin;
    }

    public ClickType getClickType() {
        return clickType;
    }

    public _ShareData setClickType(ClickType clickType) {
        this.clickType = clickType;
        return this;
    }

    public ItemStack getClickItem() {
        return click_item;
    }

    public _ShareData setClickItem(ItemStack click_item) {
        this.click_item = click_item.clone();
        return this;
    }

    public ItemStack getCursorItem() {
        return cursor_item.clone();
    }

    public _ShareData setCursorItem(@Nullable ItemStack click_item) {
        if (click_item != null) {
            this.cursor_item = click_item.clone();
        }
        return this;
    }

    public InventoryHolder getHolder() {
        return this.holder;
    }

    public _ShareData setHolder(InventoryHolder holder) {
        this.holder = holder;
        return this;
    }

    public Energy getPlugin() {
        return plugin;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isBoolean() {
        return bol;
    }

    public void setBoolean(boolean bol) {
        this.bol = bol;
    }

    public String getName_item() {
        return name_item;
    }

    public void setName_item(String name_item) {
        this.name_item = name_item;
    }

    public Player getPlayer() {
        return player;
    }

    public _ShareData setPlayer(Player player) {
        this.player = player;
        return this;
    }
}
