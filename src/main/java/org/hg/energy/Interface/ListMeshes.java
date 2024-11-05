package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Converter;
import org.hg.energy.Objects.Fabrication;
import org.hg.energy.Objects.Generator;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static org.hg.energy.Interface._Icons.calculate;

public class ListMeshes implements Window, InventoryHolder, Pagination {
    private _ShareData data;
    private int page;
    private UUID uuid_structure = null;

    public ListMeshes(_ShareData data) {
        this(data, 0);
    }

    public ListMeshes(_ShareData data, int page) {
        if (data.getStructure() != null) {
            this.uuid_structure = data.getStructure().getUuid();
        }
        this.data = data;
        this.page = page;
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public @NotNull Inventory getInventory() {
        List<Mesh> meshList = data.getPlugin().meshes;
        int max_elements = 9 * 6 - 9;
        if (this.page < 0 || this.page > meshList.size() / max_elements) {
            this.page = 0;
        }
        Inventory inventory = Bukkit.createInventory(
                this, 9 * 6, ChatColor.GOLD + "Список сетей (" + (this.page + 1) + ")");
        if (!meshList.isEmpty()) {
            meshList = meshList.subList(Math.min(page * max_elements, meshList.size() - 1), Math.min(
                    (page + 1) * max_elements, meshList.size()));
        }
        for (int slot = 0; slot < max_elements && slot < meshList.size(); slot++) {
            Mesh mesh = meshList.get(slot);
            // вычисление, сколько энергии структура тратит и производит в минуту
            double product_energy = 0;
            double consumed_energy = 0;
            for (Structure structure : data.getPlugin().getStructures()) {
                double coefficient = 1;
                if (structure.getMaxCooldown() >= 0) {
                    coefficient *= 1 - (((structure.getChanceWork() / (structure.getMaxCooldown() + 1)) / 100));
                }
                if (structure.getCooldownForPlayer() > 0) {
                    coefficient *= 1 - (((structure.getChanceWork() / structure.getCooldownForPlayer()) / 100));
                }
                coefficient = 1 - coefficient;
                if (structure.isEnabled()) {
                    if (structure instanceof Generator generator && generator.getMesh().equals(mesh)) {
                        product_energy += coefficient * generator.getAmountEnergyProduced();
                    } else if (structure instanceof Converter converter) {
                        if (converter.getMesh().equals(mesh)) {
                            consumed_energy += coefficient * converter.getAmount();
                        }
                        if (mesh.equals(converter.getOutputMesh())) {
                            product_energy += coefficient * (converter.getCoefficient() * converter.getAmount());
                        }
                    } else if (structure instanceof Fabrication fabrication && fabrication.getMesh().equals(mesh)) {
                        consumed_energy += coefficient * fabrication.getPrice();
                    }
                }
            }
            inventory.setItem(slot, _Icons.ИконкаСети.getItem(
                                      mesh.getDisplayName(),
                                      mesh.getEnergyName() + "\n" +
                                              ChatColor.GREEN + "Производит: " + String.format("%.4f ", product_energy)
                                              + mesh.getEnergyName() + "/сек.\n" +
                                              ChatColor.RED + "Потребляет: " + String.format("%.4f ", consumed_energy)
                                              + mesh.getEnergyName() + "/сек.\n" +
                                              ChatColor.GOLD + "Хранит: " + String.format(
                                              "%.3f/%.3f %s",
                                              mesh.getEnergyCount(),
                                              mesh.getEnergyLimit(),
                                              mesh.getEnergyName()
                                                                                         )
                                      ,
                                      mesh.getUuid()
                                                             )
                             );
        }
        inventory.setItem(calculate(6, 6), _Icons.СтраницаВперед.getItem("", ""));
        inventory.setItem(calculate(6, 5), _Icons.СоздатьСеть.getItem("", "", uuid_structure));
        inventory.setItem(calculate(6, 4), _Icons.СтраницаНазад.getItem("", ""));
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return data;
    }

    @Override
    public boolean needUpdate() {
        return true;
    }
}
