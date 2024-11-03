package org.hg.energy;

import org.bukkit.Location;
import org.hg.energy.Objects.Structure;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class Mesh implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private UUID uuid;
    private Set<Structure> structures = new TreeSet<>(Comparator
                                                              .comparingInt(Structure::getPriority)
                                                              .thenComparing(Structure::getUuid)
    );
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
        setDisplayName(display_name);
        setEnergyName(energy_name);
    }

    /**
     * Серелизация
     */
    @Serial
    private void writeObject(java.io.ObjectOutputStream stream)
    throws IOException {
        stream.writeObject(uuid);
        stream.writeUTF(display_name);
        stream.writeUTF(energy_name);
        stream.writeDouble(energy_count);
        stream.writeBoolean(enabled);
        stream.writeInt(structures.size());
        for (Structure structure : structures) {
            stream.writeObject(structure);
        }
    }

    /**
     * Десерелизация
     */
    @Serial
    private void readObject(java.io.ObjectInputStream stream)
    throws IOException, ClassNotFoundException {
        uuid = (UUID) stream.readObject();
        display_name = stream.readUTF();
        energy_name = stream.readUTF();
        energy_count = stream.readDouble();
        enabled = stream.readBoolean();
        int size = stream.readInt();
        structures = new TreeSet<>(Comparator.comparingInt(Structure::getPriority).thenComparing(Structure::getUuid));
        for (int i = 0; i < size; i++) {
            structures.add((Structure) stream.readObject());
        }
        for (Structure structure : structures) {
            structure.connectToMesh(this);
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public void updateStructures() {
        if (enabled) {
            for (Structure structure : this.structures) {
                structure.update();
            }
        }
    }

    /**
     * Включена ли сеть
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Задать, включена сеть или нет
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Получить список структур в сети
     */
    public List<Structure> getStructures() {
        return new ArrayList<>(this.structures);
    }

    /**
     * Присоединить к сети структуру
     */
    public void addStructure(Structure structure) {
        if (structure.getMesh() == null || !structure.getMesh().equals(this)) {
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
            return 0;
        } else {
            return structures.stream().mapToDouble(Structure::getVolume).sum();
        }
    }

    /**
     * Получить имя сети
     */
    public String getDisplayName() {
        return display_name;
    }

    /**
     * Задать отображаемое имя сети
     *
     * @throws RuntimeException если имя сети длиннее 16 символов или содержит пробел
     */
    public void setDisplayName(String display_name) {
        if (display_name.length() > 32 || display_name.contains(" ")) {
            throw new RuntimeException("Имя сети не прошло валидацию!");
        }
        this.display_name = display_name;
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
     * @throws RuntimeException если имя энергии длиннее 16 символов или содержит пробел
     */
    public void setEnergyName(String energy_name) {
        if (energy_name.length() > 16 || energy_name.contains(" ")) {
            throw new RuntimeException("Имя энергии не прошло валидацию!");
        }
        this.energy_name = energy_name;
    }

}
