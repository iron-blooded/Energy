package org.hg.energy.Interface;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static org.hg.energy.Interface._Icons.calculate;

public class ListMeshes implements Window, InventoryHolder, Pagination {
    private Structure structure;
    private _ShareData data;
    private int page;

    public ListMeshes(_ShareData data) {
        this(data, 0);
    }

    public ListMeshes(_ShareData data, int page) {
        this.structure = data.getStructure();
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
            inventory.setItem(slot, _Icons.ИконкаСети.getItem(
                                      mesh.getDisplayName(),
                                      mesh.getEnergyName(),
                                      mesh.getUuid()
                                                             )
                             );
        }
        inventory.setItem(calculate(6, 6), _Icons.СтраницаВперед.getItem("", "", structure.getUuid()));
        inventory.setItem(calculate(6, 5), _Icons.СоздатьСеть.getItem("", "", structure.getUuid()));
        inventory.setItem(calculate(6, 4), _Icons.СтраницаНазад.getItem("", "", structure.getUuid()));
        return inventory;
    }

    @Override
    public _ShareData getObject() {
        return data;
    }
}
