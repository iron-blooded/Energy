package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.hg.energy.Objects._InteractInventories.consumeResources;
import static org.hg.energy.Objects._InteractInventories.getNearInventories;

public class Generator extends Structure {
    private double amount_energy_produced = 0;
    private double chance_use = 0;
    private final Set<ItemStack> materials = new HashSet<>();
     private int distance_material;

    /**
     * Представляет собой структуру генератора, который предоставляет возможность производить энергию в сеть
     *
     * @param name      имя генератора, которое будет отображаться людям
     * @param locations список локаций, которым соответствует данная структура
     */
    public Generator(@NotNull String name, @NotNull List<Location> locations) {
        super(name, locations);
        distance_material = 2;
        super.setPriority(1);
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
     * Добавить материал, который требуется для работы генератора
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
     * Список продуктов, которые требует генератор
     *
     * @return список предметов
     */
    public List<ItemStack> getMaterials() {
        return new ArrayList<>(materials);
    }

    /**
     * @return Число, соответствующее шансу срабатывания генератора от 0 до 100%
     */
    public double getChanceUse() {
        return chance_use;
    }

    /**
     * Метод, устанавливающий шанс срабатывания генератора от 0 до 100%
     *
     * @param chance_use параметр, отвечающий за шанс срабатывания генератора
     */
    public void setChanceUse(double chance_use) {
        this.chance_use = Math.max(Math.min(100, chance_use), 0);
    }

    /**
     * При вызове увеличивает счетчик периода до срабатывания на единицу.
     * <br>
     * Как только счетчик доходит до значения, которое требуется для срабатывания, бросается шанс на то, что энергия
     * будет произведена.
     * <br>
     * Переполнение обнуляет счетчик
     */
    @Override
    public void update() {
        if (super.useCooldown() && super.castChanceWork()) {
            List<Inventory> inventories = getNearInventories(this.getDistanceMaterial(), super.getLocations());
            if (consumeResources(inventories, this.getMaterials(), super.getLocations())) {
                super.getMesh().addEnergy(getAmountEnergyProduced());
            }
        }
    }


    /**
     * @return Возвращает количество генерируемой энергии за период
     */
    public double getAmountEnergyProduced() {
        return amount_energy_produced;
    }

    /**
     * @param amount Устанавливает количество генерируемой энергии за период
     */
    public void setAmountEnergyProduced(double amount) {
        amount_energy_produced = Math.max(amount, 0);
    }
}
