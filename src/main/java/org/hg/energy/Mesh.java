package org.hg.energy;

import org.hg.energy.Objects.Container;
import org.hg.energy.Objects.Converter;
import org.hg.energy.Objects.Fabrication;
import org.hg.energy.Objects.Generator;

import java.util.ArrayList;
import java.util.List;

public class Mesh {
    private final String display_name;
    private final String energy_name;
    private final List<Generator> generators = new ArrayList<>();
    private final List<Converter> converters = new ArrayList<>();
    private final List<Fabrication> fabrications = new ArrayList<>();
    private final List<Container> containers = new ArrayList<>();
    private final double production = 0;
    private final double max_contain = 0;
    private final double contain = 0;


    public Mesh(String display_name, String energy_name) {
        this.display_name = display_name;
        this.energy_name = energy_name;
    }

}
