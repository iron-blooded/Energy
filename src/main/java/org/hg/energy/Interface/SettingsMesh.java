package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Mesh;
import org.jetbrains.annotations.NotNull;

import static org.hg.energy.Interface._Icons.*;

public class SettingsMesh implements InventoryHolder, Window {
    private _ShareData shareData;
    private Mesh mesh;

    public SettingsMesh(_ShareData shareData) {
        this.shareData = shareData;
        this.mesh = shareData.getMesh();
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(this, InventoryType.BARREL, ChatColor.GREEN + "Настройка сети");
        inventory.setItem(calculate(1, 1), ИмяСети.getItem(mesh.getDisplayName()));
        inventory.setItem(calculate(2, 1), ИмяЭнергии.getItem(mesh.getEnergyName()));
        inventory.setItem(calculate(1, 2), СписокСтруктур.getItem(""));
        inventory.setItem(calculate(1, 4),
                          КоличествоХранимойЭнергии.getItem(String.valueOf(mesh.getEnergyCount()),
                                                                   mesh.getEnergyName()));
        inventory.setItem(calculate(1, 5),
                          МаксимальноеКоличествоЭнергии.getItem(String.valueOf(mesh.getEnergyLimit()),
                                                                       mesh.getEnergyName()));
        inventory.setItem(calculate(2, 4), ДобавитьЭнергию.getItem(""));
        if (mesh.isEnabled()) {
            inventory.setItem(calculate(2, 9), ВыключитьСеть.getItem(""));
        } else {
            inventory.setItem(calculate(2, 9), ВключитьСеть.getItem(""));
        }
        inventory.setItem(calculate(2, 7), УдалитьСеть.getItem(""));
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return shareData;
    }
}
