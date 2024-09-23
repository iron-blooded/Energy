package org.hg.energy.Objects;

import org.bukkit.Location;
import org.hg.energy.Mesh;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class Structure {
    private final UUID uuid;
    private String name;
    private Set<Location> locations;
    private Mesh mesh;
    private int cooldown_required;
    private int cooldown;
    private double chance;

    /**
     * Представляет собой структуру
     *
     * @param name      имя структуры
     * @param locations каким блокам соответствует структура
     */
    public Structure(String name, List<Location> locations) {
        this.name = name;
        setLocations(locations);
        this.uuid = UUID.randomUUID();
        this.cooldown_required = 0;
        this.cooldown = 0;
        this.chance = 100;
    }

    /**
     * Возвращает шанс, с которым структура будет успешно выполнять свои действия
     * <br>
     * Работает, только если разработчик учел это свойство
     *
     * @return число от 0 до 100%
     */
    public double getChance() {
        return chance;
    }

    /**
     * Устанавливает шанс, с которым структура успешно сработает
     *
     * @param chance число от 0 до 100%
     */
    public void setChance(double chance) {
        this.chance = Math.max(Math.min(chance, 100), 0);
    }

    /**
     * Позволяет сделать проверку на то, сработала ли структура
     * @return True - если сработала, False - если нет
     */
    public boolean castChance() {
        return Math.random() * 100 > getChance();
    }

    /**
     * Получить время, требуемое для перезарядки структуры
     * <br>
     * По умолчанию равняется нулю, если не было переопределено
     *
     * @return время перезарядки (в секундах)
     */
    public int getCooldown() {
        return cooldown_required;
    }

    /**
     * Установить требуемое время перезарядки структуры
     *
     * @param cooldown_required время перезарядки (в секундах)
     */
    public void setCooldown(int cooldown_required) {
        this.cooldown_required = Math.max(0, cooldown_required);
    }

    /**
     * Используется для проверки, кончилась ли перезарядка у структуры
     * <br>
     * В то же время отнимает счетчик перезарядки, так что данная структура должна использоваться в функции update()
     *
     * @return True - если перезарядка кончилась, False - если структура еще на перезарядке
     */
    public boolean useCooldown() {
        cooldown++;
        if (cooldown >= cooldown_required) {
            cooldown = 0;
            return true;
        }
        return false;
    }

    /**
     * Возвращает уникальный номер структуры
     *
     * @return уникальный номер структуры
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * @return Возвращает имя структуры
     */
    public String getName() {
        return this.name;
    }

    /**
     * Устанавливает имя структуры
     *
     * @param name новое имя
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param location Добавляет блок, которому соответствует структура данного генератора
     */
    public void addLocation(@NotNull Location location) {
        location.add(location);
    }

    /**
     * @return Возвращает список локаций, которым соответствует структура данного генератора
     */
    public List<Location> getLocations() {
        return new ArrayList<>(locations);
    }

    /**
     * @param locations Задает список локаций, которым соответствует структура данного генератора
     * @throws NullPointerException Выкидывается, когда на запись подается пустой массив локаций
     */
    public void setLocations(@NotNull List<Location> locations) {
        if (locations.isEmpty()) {
            throw new NullPointerException();
        }
        this.locations = new HashSet<>(locations);
    }


    /**
     * Присоединяет структуру к сети
     *
     * @param mesh сеть, к которой нужно присоединить структуру
     */
    public void connectToMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    /**
     * Получить сеть, к которой присоединена структура
     *
     * @return ссылка на сеть
     */
    public Mesh getMesh() {
        return mesh;
    }

    /**
     * Вызывает работу структуры
     * <br>
     * Тем, кому нужна механика работы структуры, необходимо переопределить логику работы самостоятельно
     */
    public void update() {
        if (useCooldown() && castChance()) {
            // Ваша реализация обновления структуры
        }
    }

    /**
     * Получение объема, который может хранить структура
     *
     * @return по умолчанию (сейчас) возвращает ноль. Кому надо - переопределит
     */
    public double getVolume() {
        return 0;
    }
}
