package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Converter;
import org.hg.energy.Objects.Fabrication;
import org.hg.energy.Objects.Generator;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;

import static org.hg.energy.Interface._Icons.*;

public class SettingsStructure implements InventoryHolder, Window {
    private final Structure structure;
    private _ShareData data;

    /**
     * Представляет интерфейс для настройки структуры
     */
    public SettingsStructure(_ShareData data) {
        this.structure = data.getStructure();
        this.data = data;
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
                String.valueOf(structure.getMaxCooldown()),
                String.valueOf(structure.getMaxCooldown() - structure.getCooldown())
                                                                         ));
        inventory.setItem(calculate(1, 4), ЗадатьБлокиСтруктуры.getItem(""));
        inventory.setItem(calculate(1, 5), ПрисоеденитьСетькКСтруктуре.getItem(
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
        inventory.setItem(calculate(1, 8), ЗадатьШансПоломки.getItem(String.valueOf(structure.getChanceBreak())));
        inventory.setItem(calculate(2, 1), ВызватьРаботуСтруктуры.getItem(""));
        inventory.setItem(calculate(2, 3), ЗадатьКулдаун.getItem(""));
        if (structure.isEnabled()) {
            inventory.setItem(calculate(2, 7), ВыключитьСтруктуру.getItem(""));
        } else {
            inventory.setItem(calculate(2, 7), ВключитьСтруктуру.getItem(""));
        }
        if (structure.isCanPlayerEdit()) {
            inventory.setItem(calculate(2, 8), ЗапретитьРедактироватьИгрокам.getItem(""));
        } else {
            inventory.setItem(calculate(2, 8), РазрешитьРедактироватьИгрокам.getItem(""));
        }
        inventory.setItem(calculate(2, 9), УдалитьСтруктуру.getItem(""));
        if (structure instanceof Converter converter) {
            Mesh outputMesh = new Mesh("empty", "empty_energy");
            if (converter.getOutputMesh() != null) {
                outputMesh = converter.getOutputMesh();
            }
            inventory.setItem(calculate(3, 1), ЗадатьРасход.getItem(String.valueOf(converter.getAmount())));
            inventory.setItem(calculate(3, 2), ВыходнаяСеть.getItem(
                    outputMesh.getDisplayName(),
                    outputMesh.getEnergyName()
                                                                   ));
            inventory.setItem(
                    calculate(3, 3),
                    КоофициентКонвертации.getItem(String.valueOf(converter.getCoefficient()))
                             );
        } else if (structure instanceof Generator generator) {
            inventory.setItem(
                    calculate(3, 1),
                    ДистаницяМатериал.getItem(String.valueOf(generator.getDistanceMaterial()))
                             );
            inventory.setItem(calculate(3, 2), СписокПотребляемыхРесурсов.getItem(""));
            inventory.setItem(
                    calculate(3, 3),
                    КоличествоЭнергииНаВыходе.getItem(String.valueOf(generator.getAmountEnergyProduced()))
                             );
        } else if (structure instanceof Fabrication fabricator) {
            inventory.setItem(
                    calculate(3, 1),
                    ДистаницяМатериал.getItem(String.valueOf(fabricator.getDistanceMaterial()))
                             );
            inventory.setItem(calculate(3, 2), СписокПотребляемыхРесурсов.getItem(""));
            inventory.setItem(calculate(3, 3), СписокПроизводимыхПредметов.getItem(""));
            inventory.setItem(calculate(3, 4), ЦенаПроизводства.getItem(String.valueOf(fabricator.getPrice())));
            inventory.setItem(
                    calculate(3, 5),
                    ЛогикаПроизводства.getItem(String.valueOf(fabricator.getMultiProduct().getName()))
                             );
        }
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return new _ShareData(null, structure, structure.getLocations().get(0), data.getPlugin());
    }
}
