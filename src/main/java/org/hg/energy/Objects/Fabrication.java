package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static org.hg.energy.Objects._InteractInventories.consumeResources;
import static org.hg.energy.Objects._InteractInventories.getNearInventories;

public class Fabrication extends Structure {
    private final Map<ItemStack, Double> products;
    private final Random random;
    private final List<ItemStack> materials;
    private int distance_material;
    private MultiProduct isMultiProduct = MultiProduct.MaybeNothing;
    private double price = 0;

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
        super.setPriority(3);
    }

    /**
     * Вызывает работу фабрикатора
     * <br>
     */
    @Override
    public void update() {
        // Смотрим кулдаун, прокнулся ли шанс на работу и хватает ли энергии в сети
        if (super.getMesh().getEnergyCount() - getPrice() >= 0 && useCooldown() && castChanceWork()) {
            work();
        }
    }

    /**
     * Представляет собой метод, который отвечает за работу структуры
     * <br>
     * При этом не должны учитываться такие параметры как шанс работы и кулдаун
     */
    @Override
    public void work() {
        List<Inventory> inventories = getNearInventories(this.getDistanceMaterial(), super.getLocations());
        if (consumeResources(inventories, this.getMaterials(), super.getLocations())) {
            super.getMesh().removeEnergy(getPrice());
            List<ItemStack> products = getRandomProducts();
            for (Inventory inventory : inventories) {
                for (ItemStack product : products) {
                    if (product.getAmount() > 0) {
                        HashMap<Integer, ItemStack> notAdded = inventory.addItem(product);
                        if (notAdded.isEmpty()) {
                            product.setAmount(0);
                        } else {
                            product.setAmount(notAdded.values().iterator().next().getAmount());
                        }
                    }
                }
            }
            if (!products.isEmpty()) {
                Location upper_location = Collections.max(
                        super.getLocations(),
                        Comparator.comparingDouble(Location::getY)
                                                         ).add(0, 1, 0);
                for (ItemStack product : products) {
                    if (product.getAmount() > 0) {
                        upper_location.getWorld().dropItem(upper_location, product);
                    }
                }
            }
        } else {
            //TODO: Сделать тут визуальный какой нибудь пух типа не хватило в холостую сработал
        }
    }

    /**
     * Получить цену, за которую работает фабрикатор
     */
    public double getPrice() {
        return price;
    }

    /**
     * Задать цену, за которую будет работать фабрикатор
     */
    public void setPrice(double price) {
        this.price = Math.max(0, price);
    }


    /**
     * Получить случайный предмет из списка продуктов
     *
     * @return случайный предмет из списка продуктов
     */
    private List<ItemStack> getRandomProducts() {
        List<ItemStack> result = new ArrayList<>();
        result.add(new ItemStack(Material.AIR));
        switch (this.getMultiProduct()) {
            case OneThing -> { //Выпадает гарантировано один
                double sum = 0;
                for (double chance : products.values()) {
                    sum += chance;
                }
                double randomValue = random.nextDouble() * sum;
                double cumulativeSum = 0;
                for (Map.Entry<ItemStack, Double> entry : products.entrySet()) {
                    cumulativeSum += entry.getValue();
                    if (randomValue <= cumulativeSum) {
                        result.add(entry.getKey());
                        return result;
                    }
                }
            }
            case Lot -> {  // Выпадет один или более
                for (ItemStack product : products.keySet()) {
                    if (random.nextDouble() * 100 <= products.get(product)) {
                        result.add(product);
                    }
                }
                return result;
            }
            case MaybeNothing -> { //Может не выпасть ничего
                for (ItemStack product : products.keySet()) {
                    if (random.nextDouble() * 100 <= products.get(product)) {
                        result.add(product);
                        return result;

                    }
                }
            }
            default -> {
            }
        }

        return result;
    }

    /**
     * Узнать, по какой логике фабрикатор производит предметы
     */
    public MultiProduct getMultiProduct() {
        return isMultiProduct;
    }

    /**
     * Устанавливает, по какой логике фабрикатор должен производить предметы
     */
    public void setMultiProduct(MultiProduct multiProduct) {
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
        this.materials.remove(itemStack);
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
