package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class _InteractInventories {
    /**
     * Получить список ближайших инвентарей
     *
     * @param radius    радиус, в котором искать инвентари
     * @param locations локации, вокруг которых искать
     * @return список найденных инвентарей
     */
    public static List<Inventory> getNearInventories(int radius, List<Location> locations) {
        // Ищем все хранилища в радиусе от структуры
        List<Inventory> inventories = new ArrayList<>();
        for (Location location : locations) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = location.clone().add(x, y, z).getBlock();
                        if (block.getState() instanceof InventoryHolder) {
                            inventories.add(((InventoryHolder) block.getState()).getInventory());
                        }
                    }
                }
            }
        }
        return inventories;
    }

    /**
     * Потратить ресурсы для производства
     *
     * @param inventories список инвентарей, в которых нужно потратить ресурсы
     * @param materials   список материалов
     * @param locations   список локаций, на которых расположена структура
     * @return то, хватило ли ресурсов для производства. Если не хватило (false) - ресурсы не потратятся, но и
     * произвестись ничего не должно
     */
    public static boolean consumeResources(List<Inventory> inventories, List<ItemStack> materials,
                                           List<Location> locations) {
        // Создаем таблицу для хранения подходящих ItemStack, для их последующего удаления
        Map<ItemStack, Integer> foundItems = new HashMap<>();
        // Инициализируем таблицу запрашиваемых материалов
        Map<ItemStack, Integer> requestsMaterials = new HashMap<>();
        for (ItemStack material : materials) {
            requestsMaterials.merge(material, material.getAmount(), Integer::sum);
        }
        // Проверяем все хранилища и собираем подходящие ItemStack
        for (Inventory inventory : inventories) {
            for (ItemStack itemStack : inventory.getContents()) {
                if (itemStack != null) {
                    for (ItemStack material : requestsMaterials.keySet()) {
                        if (requestsMaterials.get(material) > 0 && material.isSimilar(itemStack)) {
                            int minus = Math.min(requestsMaterials.get(material), itemStack.getAmount());
                            requestsMaterials.put(material, requestsMaterials.get(material) - minus);
                            foundItems.put(itemStack, minus); //Сколько нужно будет вычесть
                            break;
                        }
                    }
                }
            }
        }
        if (!requestsMaterials.isEmpty() && Collections.max(requestsMaterials.values()) > 0) { //Есть ли требуемые материалы в полной мере
            return false;
        }
        for (ItemStack item : foundItems.keySet()) {
            item.setAmount(item.getAmount() - foundItems.get(item));
        }
        return true;
    }
}