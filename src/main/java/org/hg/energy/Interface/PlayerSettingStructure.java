package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;

import static org.hg.energy.Interface._Icons.calculate;
import static org.hg.energy.Interface._Icons.ВызыватьРаботуСтруктурыИгроку;

public class PlayerSettingStructure implements Window, InventoryHolder {
    private _ShareData data;
    private Structure structure;

    public PlayerSettingStructure(_ShareData data) {
        this.data = data;
        structure = data.getStructure();
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                this, InventoryType.HOPPER, ChatColor.GOLD + "Настройка структуры");
        if (structure == null) return inventory;
        if (structure.getCooldownForPlayer() != 0) {
            inventory.setItem(
                    calculate(1, 1),
                    ВызыватьРаботуСтруктурыИгроку.getItem(String.valueOf(structure.getStayCooldownForPlayer()))
                             );
        }
        if (structure.isCanPlayerEdit()) {
            if (structure.isEnabled()) {
                inventory.setItem(calculate(1, 3), _Icons.ВыключитьСтруктуру.getItem(""));
            } else {
                inventory.setItem(calculate(1, 3), _Icons.ВключитьСтруктуру.getItem(""));
            }
        }
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return data;
    }
}
