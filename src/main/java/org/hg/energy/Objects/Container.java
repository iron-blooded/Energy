package org.hg.energy.Objects;

import org.bukkit.Location;

import java.util.List;

public class Container extends Structure {
    private double volume = 0;


    /**
     * Представляет собой структуру контейнера
     *
     * @param name      имя контейнера
     * @param locations каким блокам соответствует контейнер
     */
    public Container(String name, List<Location> locations) {
        super(name, locations);
    }

    /**
     * @return число, означающее объем контейнера
     */
    @Override
    public double getVolume() {
        return volume;
    }

    /**
     * Установка объема
     *
     * @param volume новый объем
     */
    public void setVolume(double volume) {
        this.volume = Math.max(volume, 0);
    }
}
