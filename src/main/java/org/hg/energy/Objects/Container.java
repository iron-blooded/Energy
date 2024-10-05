package org.hg.energy.Objects;

import org.bukkit.Location;

import java.util.List;

public class Container extends Structure {
    /**
     * Представляет собой структуру контейнера
     *
     * @param name      имя контейнера
     * @param locations каким блокам соответствует контейнер
     */
    public Container(String name, List<Location> locations) {
        super(name, locations);
        super.setPriority(0);
    }

}
