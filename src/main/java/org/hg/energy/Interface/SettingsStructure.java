package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;

import static org.hg.energy.Interface._Icons.*;

public class SettingsStructure implements InventoryHolder, Window {
    private final Structure structure;

    /**
     * Представляет интерфейс для настройки структуры
     */
    public SettingsStructure(Structure structure) {
        this.structure = structure;
    }

    @Override
    public @NotNull Inventory getInventory() {
        Inventory inventory = Bukkit.createInventory(
                this, InventoryType.BARREL, ChatColor.GOLD + "Настройка структуры");
        Mesh mesh = structure.getMesh();
        if (mesh == null) {
            mesh = new Mesh("no_mesh", "no_mesh");
        }
        // настройка содержимого
        inventory.setItem(calculate(1, 1), ИзменитьИмяСтруктуры.getItem(structure.getName()));
        inventory.setItem(calculate(1, 2), ШансРаботыСтруктуры.getItem(String.valueOf(structure.getChanceWork())));
        inventory.setItem(calculate(1, 3), КулдаунРаботыСтруктуры.getItem(
                String.valueOf(structure.getCooldown()),
                String.valueOf(structure.getMaxCooldown())
                                                                         ));
        inventory.setItem(calculate(1, 4), ЗадатьБлокиСтруктуры.getItem());
        inventory.setItem(calculate(1, 5), ПрисоеденитьСтруктуру.getItem(
                mesh.getDisplayName(),
                mesh.getEnergyName()
                                                                        ));
        inventory.setItem(
                calculate(1, 6),
                ЗадатьКоличествоЭнергии.getItem(
                        String.valueOf(structure.getVolume()),
                        mesh.getEnergyName()
                                               )
                         );
        inventory.setItem(calculate(2, 1), ВызватьРаботуСтруктуры.getItem());
        inventory.setItem(calculate(2, 2), ИспользоатьШансРаботыСтруктуры.getItem());
        inventory.setItem(calculate(2, 3), ЗадатьКулдаун.getItem());
        inventory.setItem(calculate(2, 9), УдалитьСтруктуру.getItem());
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return new _ShareData(null, structure, structure.getLocations().get(0));
    }
}
