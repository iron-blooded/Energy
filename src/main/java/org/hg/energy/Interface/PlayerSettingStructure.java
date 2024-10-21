package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Objects.Structure;

import static org.hg.energy.Interface._Icons.calculate;

public class PlayerSettingStructure implements Window, InventoryHolder {
    private _ShareData data;
    private Structure structure;

    public PlayerSettingStructure(_ShareData data) {
        this.data = data;
        structure = data.getStructure();
    }

    @Override
    public Inventory getInventory() {
        if (structure == null) return null;
        Inventory inventory = Bukkit.createInventory(
                this, InventoryType.HOPPER, ChatColor.GOLD + "Настройка структуры");
        if (structure.isCanPlayerEdit()) {
            if (structure.isEnabled()) {
                inventory.setItem(calculate(1, 3), _Icons.ВыключитьСтруктуру.getItem(""));
            } else {
                inventory.setItem(calculate(1, 3), _Icons.ВключитьСтруктуру.getItem(""));
            }
        } else {
            inventory = null;
        }
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return data;
    }
}
