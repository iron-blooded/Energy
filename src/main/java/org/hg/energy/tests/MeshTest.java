package org.hg.energy.tests;

import org.bukkit.Location;
import org.bukkit.World;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Container;
import org.hg.energy.Objects.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MeshTest {
    Mesh mesh;
    @Mock
    private World world;

    @BeforeEach
    void setUp() {
        this.mesh = new Mesh("test_mesh", "test_energy_name");
    }

    @Test
    void energyProduce() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(world, 0, 0, 0));
        Generator structure = new Generator("test", locations);
        structure.setAmountEnergyProduced(94);
        mesh.addStructure(structure);
        assertEquals(mesh.getEnergyCount(), 0);
        mesh.setEnabled(true);
        mesh.updateStructures();
        assertEquals(mesh.getEnergyCount(), 0);
        structure.setVolume(99999);
        mesh.updateStructures();
        assertEquals(mesh.getEnergyCount(), 94);

    }

    @Test
    void operandStructure() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(world, 0, 0, 0));
        Container structure = new Container("test", locations);
        assertTrue(mesh.getStructures().isEmpty());
        mesh.addStructure(structure);
        assertFalse(mesh.getStructures().isEmpty());
    }

    @Test
    void EnergyLimit() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(world, 0, 0, 0));
        Container structure = new Container("test", locations);
        structure.setVolume(9494);
        assertEquals(mesh.getEnergyLimit(), 0);
        mesh.addStructure(structure);
        assertEquals(mesh.getEnergyLimit(), 9494);
    }

    @Test
    void setNonlegalMeshName() {
        assertThrows(RuntimeException.class, () -> mesh.setDisplayName("123 123"));
        assertThrows(RuntimeException.class, () -> mesh.setDisplayName("11111111111111111"));
    }

    @Test
    void setLegalMeshName() {
        String name = "Gfdg^$&%$^!!";
        mesh.setDisplayName(name);
        assertEquals(mesh.getDisplayName(), name);
    }


    @Test
    void setNonlegalEnergyName() {
        assertThrows(RuntimeException.class, () -> mesh.setEnergyName("123 123"));
        assertThrows(RuntimeException.class, () -> mesh.setEnergyName("11111111111111111"));
    }

    @Test
    void setLegalEnergyName() {
        String name = "GfdgGdsf^$&%$^!!";
        mesh.setEnergyName(name);
        assertEquals(mesh.getEnergyName(), name);
    }


}