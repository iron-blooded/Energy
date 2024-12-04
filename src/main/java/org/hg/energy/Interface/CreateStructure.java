package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import static org.hg.energy.Interface._Icons.*;

public class CreateStructure implements InventoryHolder, Window {
    Location location;
    _ShareData data;

    public CreateStructure(_ShareData data) {
        this.location = data.getLocation();
        this.data = data;
    }

    @Override
    public _ShareData getObject() {
        return new _ShareData(null, null, location, data.getPlugin());
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                this, InventoryType.HOPPER, ChatColor.GOLD + "Выбор типа структуры");
        inventory.setItem(calculate(1, 1), ХранилищеЭнергии.getItem("", ""));
        inventory.setItem(calculate(1, 2), Генератор.getItem("", ""));
        inventory.setItem(calculate(1, 4), Конвертер.getItem("", ""));
        inventory.setItem(calculate(1, 5), Фабрикатор.getItem("",""));
        return inventory;
    }

    @Override
    public boolean needUpdate() {
        return true;
    }
}
