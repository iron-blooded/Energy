package org.hg.energy.tests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GeneratorTest {
    private Generator generator;
    private Mesh mesh;
    private ItemStack mockItemStack;
    @Mock
    private World world;
    @Mock
    private Server server;

    @BeforeEach
    void setUp() {
        List<Location> mockLocations = new ArrayList<>();
        mockLocations.add(new Location(world, 0, 0, 0));

        mesh = new Mesh("test1", "test2");
        generator = new Generator("TestGenerator", mockLocations);
        generator.connectToMesh(mesh);

        mockItemStack = new ItemStack(Material.DIRT);
    }

    @Test
    void testGetDistanceMaterial() {
        assertEquals(2, generator.getDistanceMaterial());
    }

    @Test
    void testSetDistanceMaterial() {
        generator.setDistanceMaterial(5);
        assertEquals(5, generator.getDistanceMaterial());

        generator.setDistanceMaterial(20);  // Должно быть ограничено 16
        assertEquals(16, generator.getDistanceMaterial());

        generator.setDistanceMaterial(-1);  // Должно быть ограничено 0
        assertEquals(0, generator.getDistanceMaterial());
    }


    @Test
    void testGetAndSetChanceUse() {
        generator.setChanceWork(50);
        assertEquals(50, generator.getChanceWork(), 0.01);

        generator.setChanceWork(150);  // Должно быть ограничено 100
        assertEquals(100, generator.getChanceWork(), 0.01);

        generator.setChanceWork(-10);  // Должно быть ограничено 0
        assertEquals(0, generator.getChanceWork(), 0.01);
    }

    @Test
    void testGetAndSetAmountEnergyProduced() {
        generator.setAmountEnergyProduced(500);
        assertEquals(500, generator.getAmountEnergyProduced(), 0.01);

        generator.setAmountEnergyProduced(-50);  // Должно быть установлено в 0
        assertEquals(0, generator.getAmountEnergyProduced(), 0.01);
    }

}
