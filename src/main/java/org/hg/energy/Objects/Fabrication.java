package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Fabrication extends Structure {
    private final Map<ItemStack, Double> products;
    private final Random random;
    private final List<ItemStack> materials;
    private int distance_material;
    private boolean isMultiProduct = false;

    /**
     * Представляет собой производство физических предметов
     *
     * @param name      имя фабрикатора
     * @param locations каким блокам соответствует структура
     */
    public Fabrication(String name, List<Location> locations) {
        super(name, locations);
        products = new HashMap<>();
        materials = new ArrayList<>();
        distance_material = 2;
        random = new Random();
    }

    /**
     * Вызывает работу фабрикатора
     * <br>
     */
    @Override
    public void update() {
        // Смотрим кулдаун и прокнулся ли шанс на работу
        if (useCooldown() && castChanceWork()) {
            if (consumeResources()) {
                //TODO: вычитание энергии из сети
                ItemStack product = getRandomProduct();
                for (Inventory inventory: getNearInventories()){
                    HashMap<Integer, ItemStack> notAdded = inventory.addItem(product);
                    if (notAdded.isEmpty()) {
                        product = null;
                        break;
                    }
                    product = notAdded.values().iterator().next();
                }
                if (product != null){
                    Location upper_location = Collections.max(super.getLocations(), Comparator.comparingDouble(Location::getY)).add(0, 1, 0);
                    upper_location.getWorld().dropItem(upper_location, product);
                }
            } else {
                //TODO: Сделать тут визуальный какой нибудь пух типа не хватило в холостую сработал
            }

        }
    }

    private List<Inventory> getNearInventories(){
        int radius = getDistanceMaterial();
        // Ищем все хранилища в радиусе от структуры
        List<Inventory> inventories = new ArrayList<>();
        for (Location location : super.getLocations()) {
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
     * @return то, хватило ли ресурсов для производства. Если не хватило (false) - ресурсы не потратятся, но и
     * произвестись ничего не должно
     */
    private boolean consumeResources() {
        // Создаем таблицу для хранения подходящих ItemStack, для их последующего удаления
        Map<ItemStack, Integer> foundItems = new HashMap<>();
        // Инициализируем таблицу запрашиваемых материалов
        Map<ItemStack, Integer> requestsMaterials = new HashMap<>();
        for (ItemStack material : materials) {
            requestsMaterials.merge(material, material.getAmount(), Integer::sum);
        }
        // Проверяем все хранилища и собираем подходящие ItemStack
        for (Inventory inventory : getNearInventories()) {
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
        if (Collections.max(requestsMaterials.values()) > 0) { //Есть ли требуемые материалы в полной мере
            return false;
        }
        for (ItemStack item : foundItems.keySet()) {
            item.setAmount(item.getAmount() - foundItems.get(item));
        }
        return true;
    }


    /**
     * Получить случайный предмет из списка продуктов
     *
     * @return случайный предмет из списка продуктов
     */
    private ItemStack getRandomProduct() {
        double sum = 0;
        for (double chance : products.values()) {
            sum += chance;
        }

        double randomValue = random.nextDouble() * sum;

        double cumulativeSum = 0;
        for (Map.Entry<ItemStack, Double> entry : products.entrySet()) {
            cumulativeSum += entry.getValue();
            if (randomValue <= cumulativeSum) {
                return entry.getKey();
            }
        }
        return new ItemStack(Material.AIR);
//        throw new RuntimeException("Сумма шансов не равна 1");
    }

    /**
     * Узнать, производит ли фабрикатор несколько предметов или только один согласно таблице продуктов.
     *
     * @return True - если за раз производит несколько. False - если за раз производит только один
     */
    public boolean isMultiProduct() {
        return isMultiProduct;
    }

    /**
     * Устанавливает, должен ли фабрикатор производить несколько предметов, или гарантировано только один из таблицы
     * продуктов.
     *
     * @param multiProduct нужно ли производить несколько предметов
     */
    public void setMultiProduct(boolean multiProduct) {
        isMultiProduct = multiProduct;
    }

    /**
     * Радиус, в котором нужно искать материалы для производства
     *
     * @return дистанция, больше нуля, и меньше 16. По умолчанию - 2
     */
    public int getDistanceMaterial() {
        return distance_material;
    }

    /**
     * Задать радиус, в котором нужно искать материалы для производства
     *
     * @param distance_material число больше нуля, и меньше 16
     */
    public void setDistanceMaterial(int distance_material) {
        this.distance_material = Math.max(0, Math.min(16, distance_material));
    }

    /**
     * Добавить продукт, который может производить фабрикатор
     *
     * @param itemStack предмет
     * @param chance    шанс того, что будет произведен данный предмет
     * @throws RuntimeException если была попытка добавить воздух
     */
    public void addProduct(@NotNull ItemStack itemStack, double chance) {
        if (itemStack.getType().isAir()) {
            throw new RuntimeException("Не надо пихать в продукты фабикатора воздух!");
        }
        this.products.put(itemStack, Math.max(0, Math.min(100, chance)));
    }

    /**
     * Забрать возможность фабрикатора производить данный предмет
     *
     * @param itemStack предмет, который нужно удалить из продуктовой линейки
     */
    public void removeProduct(ItemStack itemStack) {
        this.products.remove(itemStack);
    }

    /**
     * Список продуктов, которые производит фабрикатор с шансами их получения
     *
     * @return таблица предметов и шансов
     */
    public HashMap<ItemStack, Double> getProducts() {
        return new HashMap<>(products);
    }

    /**
     * Добавить материал, который требуется для производства
     *
     * @param itemStack предмет
     * @throws RuntimeException если была попытка добавить воздух
     */
    public void addMaterial(@NotNull ItemStack itemStack) {
        if (itemStack.getType().isAir()) {
            throw new RuntimeException("Не надо пихать в материалы фабикатора воздух!");
        }
        this.materials.add(itemStack);
    }

    /**
     * Убрать материал из списка необходимых
     *
     * @param itemStack предмет, который нужно убрать
     */
    public void removeMaterial(ItemStack itemStack) {
        this.products.remove(itemStack);
    }

    /**
     * Список продуктов, которые требует фабрикатор
     *
     * @return список предметов
     */
    public List<ItemStack> getMaterials() {
        return new ArrayList<>(materials);
    }
}
