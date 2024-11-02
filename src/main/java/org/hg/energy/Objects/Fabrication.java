package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Serial;
import java.util.*;

import static org.hg.energy.Objects._InteractInventories.consumeResources;
import static org.hg.energy.Objects._InteractInventories.getNearInventories;

public class Fabrication extends Structure {
    @Serial
    private static final long serialVersionUID = 7269679853745084560L;
    private List<ItemStack> materials;
    private Map<ItemStack, Double> products;
    private Random random;
    private int distance_material;
    private double price = 0;
    private MultiProduct isMultiProduct = MultiProduct.MaybeNothing;

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
     * Серелизация
     */
    @Serial
    private void writeObject(java.io.ObjectOutputStream stream)
    throws IOException {
        this.defaultSerialize(stream);
        stream.writeInt(distance_material);
        stream.writeDouble(price);
        stream.writeObject(isMultiProduct);
        stream.writeInt(materials.size());
        for (ItemStack item : materials) {
            stream.writeObject(item);
        }
        stream.writeInt(products.size());
        for (Map.Entry<ItemStack, Double> entry : products.entrySet()) {
            stream.writeObject(entry.getKey());
            stream.writeDouble(entry.getValue());
        }
    }

    /**
     * Десерелизация
     */
    @Serial
    private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException {
        this.defaultSerialize(stream);
        random = new Random();
        distance_material = stream.readInt();
        price = stream.readDouble();
        isMultiProduct = (MultiProduct) stream.readObject();
        materials = new ArrayList<>();
        int size = stream.readInt();
        for (int i = 0; i < size; i++) {
            materials.add((ItemStack) stream.readObject());
        }
        products = new HashMap<>();
        size = stream.readInt();
        for (int i = 0; i < size; i++) {
            products.put((ItemStack) stream.readObject(), stream.readDouble());
        }
    }

//    /**
//     * Вызывает работу фабрикатора
//     * <br>
//     */
//    @Override
//    public void update() {
//        // Смотрим кулдаун, прокнулся ли шанс на работу и хватает ли энергии в сети
//        if (isEnabled()
//                && useCooldown() && castChanceWork()
//                && useChanceBreak()) {
//            work();
//        }
//    }

    /**
     * Представляет собой метод, который отвечает за работу структуры
     * <br>
     * При этом не должны учитываться такие параметры как шанс работы и кулдаун
     */
    @Override
    public boolean work() {
        if (super.getMesh().getEnergyCount() - getPrice() < 0) return false;
        List<Inventory> inventories = getNearInventories(
                this.getDistanceMaterial(), super.getLocations(), super.getCooldownForPlayer() != 0);
        if (consumeResources(inventories, this.getMaterials(), super.getLocations())) {
            super.getMesh().removeEnergy(getPrice());
            List<ItemStack> products = getRandomProducts();
            products = unpackingItems(products);
            for (Inventory inventory : inventories) {
                for (ItemStack product : products) {
                    if (product != null && product.getAmount() > 0) {
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
                    if (product != null && product.getAmount() > 0 && !product.getType().equals(Material.AIR)) {
                        upper_location.getWorld().dropItem(upper_location, product);
                    }
                }
            }
        } else {
            return false;
        }
        return true;
    }

    private List<ItemStack> unpackingItems(List<ItemStack> list) {
        for (ItemStack item : list.stream().toList()) {
            if (item != null && Material.TRAPPED_CHEST.equals(item.getType())) {
                ItemStack item_clone = item.clone();
                item_clone.setType(Material.SHULKER_BOX);
                if (item_clone.getItemMeta() instanceof BlockStateMeta blockStateMeta) {
                    if (blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBox) {
                        list.addAll(Arrays.asList(shulkerBox.getInventory().getContents()));
                        list.remove(item);
                    }
                }
            }
        }
        return list;
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
                for (double chance : getProducts().values()) {
                    sum += chance;
                }
                double randomValue = random.nextDouble() * sum;
                double cumulativeSum = 0;
                for (Map.Entry<ItemStack, Double> entry : getProducts().entrySet()) {
                    cumulativeSum += entry.getValue();
                    if (randomValue <= cumulativeSum) {
                        result.add(entry.getKey());
                        return result;
                    }
                }
            }
            case Lot -> {  // Выпадет один или более
                for (ItemStack product : getProducts().keySet()) {
                    if (random.nextDouble() * 100 <= getProducts().get(product)) {
                        result.add(product);
                    }
                }
                return result;
            }
            case MaybeNothing -> { //Может не выпасть ничего
                for (ItemStack product : getProducts().keySet()) {
                    if (!getProducts().isEmpty()
                            && !product.getType().equals(Material.AIR)
                            && random.nextDouble() * 100 <= getProducts().get(product)) {
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
        HashMap<ItemStack, Double> map = new HashMap<>();
        for (Map.Entry<ItemStack, Double> entry : this.products.entrySet()) {
            map.put(entry.getKey().clone(), entry.getValue());
        }
        return map;
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
        List<ItemStack> list = new ArrayList<>();
        for (ItemStack item : this.materials) {
            list.add(item.clone());
        }
        return list;
    }
}
