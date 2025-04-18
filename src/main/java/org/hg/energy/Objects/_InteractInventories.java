package org.hg.energy.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Interface._Icons;
import org.hg.ironChest.Chest;
import org.hg.ironChest.IronChest;

import java.util.*;

import static org.hg.energy.Energy.getIronChest;
import static org.hg.ironChest.ChestOperationsNamespacedKey.getUUID;

public class _InteractInventories {
    /**
     * Получить список инвентарь ближайшего к локациям игрока
     */
    private static Inventory getInventoryNearestPlayer(List<Location> locations) {
        return locations.stream()
                .flatMap(location -> location.getNearbyPlayers(3).stream()
                        .map(player -> new AbstractMap.SimpleEntry<>(player, location.distance(player.getLocation()))))
                .min(Comparator.comparingDouble(AbstractMap.SimpleEntry::getValue))
                .map(entry -> (Inventory) entry.getKey().getInventory())
                .orElseGet(_InteractInventories::createLockedInventory);
    }

    private static Inventory createLockedInventory() {
        Inventory inventory = Bukkit.createInventory(null, InventoryType.HOPPER);
        for (int i = 0; i < 5; i++) {
            inventory.setItem(i, _Icons.СтраницаВперед.getItem(""));
        }
        return inventory;
    }

    /**
     * Получить список ближайших инвентарей
     *
     * @param radius    радиус, в котором искать инвентари
     * @param locations локации, вокруг которых искать
     * @return список найденных инвентарей
     */
    public static List<Inventory> getNearInventories(int radius, List<Location> locations,
                                                     boolean need_nearest_player) {
        // Ищем все хранилища в радиусе от структуры
        List<Inventory> inventories = new ArrayList<>();
        if (need_nearest_player) inventories.add(getInventoryNearestPlayer(locations));
        for (Location location : locations) {
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        Block block = location.clone().add(x, y, z).getBlock();
                        if (block.getState() instanceof InventoryHolder
                                && !block.getType().equals(Material.TRAPPED_CHEST)) {
                            IronChest ironChest = getIronChest();
                            if (ironChest != null) {
                                Chest chest = ironChest.getChests().get(getUUID(block));
                                if (chest != null) {
                                    inventories.addAll(chest.getWindows());
                                    continue;
                                }
                            }
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
            material = material.clone();
            int amount = material.getAmount();
            material.setAmount(1);
            requestsMaterials.merge(material, amount, Integer::sum);
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
        if (!requestsMaterials.isEmpty()
                && Collections.max(requestsMaterials.values()) > 0) { //Есть ли требуемые материалы в полной мере
            return false;
        }
        for (ItemStack item : foundItems.keySet()) {
            item.setAmount(item.getAmount() - foundItems.get(item));
        }
        return true;
    }
}
