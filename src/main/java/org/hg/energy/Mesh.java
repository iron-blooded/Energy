package org.hg.energy;

import org.bukkit.Location;
import org.hg.energy.Objects.Structure;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Mesh {
    private final UUID uuid;
    private final Set<Structure> structures = new HashSet<>();
    private String display_name;
    private String energy_name;
    private double energy_count = 0;
    private boolean enabled = false;

    /**
     * Представляет объект сети, к которой подключаются блоки для обмена энергией
     *
     * @param display_name отображаемое имя сети
     * @param energy_name  имя энергии, которая используется в этой сети
     */
    public Mesh(String display_name, String energy_name) {
        this.uuid = UUID.randomUUID();
        this.display_name = display_name;
        this.energy_name = energy_name;
    }

    public void updateStructures() {
        if (enabled) {
            for (Structure structure : this.structures) {
                structure.update();
            }
        }
    }

    /**
     * Присоединить к сети структуру
     */
    public void addStructure(Structure structure) {
        if (!structure.getMesh().equals(this)) {
            structure.connectToMesh(this);
        }
        this.structures.add(structure);
    }

    /**
     * Отсоединить от сети структуру
     */
    public void removeStructure(Structure structure) {
        this.structures.remove(structure);
        if (getEnergyCount() > getEnergyLimit() && !structure.getLocations().isEmpty()) {
            Location location = structure.getLocations().get(0);
            location.getWorld().createExplosion(location, 1);
        }
    }

    /**
     * Получить количество хранимой сейчас энергии
     */
    public double getEnergyCount() {
        return energy_count;
    }

    /**
     * Задать количество энергии, которое сейчас хранит сеть
     */
    public void setEnergyCount(double energy_count) {
        this.energy_count = Math.min(getEnergyLimit(), Math.max(0, energy_count));
    }

    /**
     * Вычесть хранимую энергию
     *
     * @param count количество энергии, которое нужно вычесть из хранилища
     * @return true - если изъятие прошло успешно
     */
    public boolean removeEnergy(double count) {
        if (count > getEnergyCount()) {
            return false;
        }
        setEnergyCount(getEnergyCount() - count);
        return true;
    }

    /**
     * Добавить энергию в сеть
     *
     * @param count количество энергии, которую нужно добавить в сеть
     * @return true - если добавление прошло успешно
     */
    public boolean addEnergy(double count) {
        if (count > getEnergyLimit()) {
            return false;
        } else {
            setEnergyCount(getEnergyCount() + count);
            return true;
        }
    }

    /**
     * Получить лимит энергии, который вмещает сеть
     */
    public double getEnergyLimit() {
        if (this.structures.isEmpty()) {
            return 0D;
        } else {
            return structures.stream().mapToDouble(Structure::getVolume).sum();
        }
    }

    /**
     * Задать отображаемое имя сети
     */
    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    /**
     * Получить имя сети
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * Получить имя энергии в этой сети
     */
    public String getEnergyName() {
        return energy_name;
    }

    /**
     * Задать имя энергии в сети
     *
     * @param energy_name имя
     */
    public void setEnergyName(String energy_name) {
        this.energy_name = energy_name;
    }

}
