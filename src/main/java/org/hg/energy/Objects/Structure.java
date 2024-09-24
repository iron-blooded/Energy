package org.hg.energy.Objects;

import org.bukkit.Location;
import org.bukkit.Material;
import org.hg.energy.Mesh;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class Structure {
    private final UUID uuid;
    private String name;
    private HashMap<Location, Material> locations;
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
    public double getChanceWork() {
        return chance;
    }

    /**
     * Устанавливает шанс, с которым структура успешно сработает
     *
     * @param chance число от 0 до 100%
     */
    public void setChanceWork(double chance) {
        this.chance = Math.max(Math.min(chance, 100), 0);
    }

    /**
     * Позволяет сделать проверку на то, сработала ли структура
     *
     * @return True - если сработала, False - если нет
     */
    public boolean castChanceWork() {
        return Math.random() * 100 > getChanceWork();
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
        return new ArrayList<>(locations.keySet());
    }

    /**
     * @param locations Задает список локаций, которым соответствует структура данного генератора
     * @throws NullPointerException Выкидывается, когда на запись подается пустой массив локаций
     */
    public void setLocations(@NotNull List<Location> locations) {
        if (locations.isEmpty()) {
            throw new NullPointerException();
        }
        this.locations = new HashMap<>();
        for (Location location : locations) {
            this.locations.put(location, location.getBlock().getType());
        }
    }

    /**
     * Вызывается сетью для проверки соответствия блоков структуры заданным
     */
    public void checkLocationsMatching() {
        for (Location location : locations.keySet()) {
            if (location.getBlock().getType().equals(locations.get(location))) {
                location.createExplosion(0);
                this.disconnectToMesh();
                return;
            }
        }
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
     * Отсоединить структуру от сети
     */
    public void disconnectToMesh() {
        this.mesh = null;
        //TODO: отключение от сети вызовом внутреннего метода сети
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
        if (useCooldown() && castChanceWork()) {
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
