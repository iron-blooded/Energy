package org.hg.energy.tests;

import org.bukkit.*;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Container;
import org.hg.energy.Objects.Converter;
import org.hg.energy.Objects.Fabrication;
import org.hg.energy.Objects.Generator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class StructureTest {
    @Mock
    private World world;
    @Mock
    private Server server;
    private Container structure;
    private Mesh mesh;

    @BeforeEach
    void setUp() {
        List<Location> locations = new ArrayList<>();
        locations.add(new Location(world, 0, 0, 0));
        this.structure = new Container("test", locations);
        this.mesh = new Mesh("test1", "test2");
        try {
            Bukkit.setServer(server);
        } catch (Exception ignored) {
        }
    }

    @Test
    void testGetUuid() {
        UUID uuid = structure.getUuid();
        assertNotNull(uuid, "UUID не должен быть null.");
    }


    @Test
    void testName() {
        structure.setName("NewName");
        assertEquals("NewName", structure.getName());
    }

    @Test
    void testGetAndSetChanceWork() {
        structure.setChanceWork(50);
        assertEquals(50, structure.getChanceWork(), 0.01);

        structure.setChanceWork(150);  // Должно быть ограничено 100
        assertEquals(100, structure.getChanceWork(), 0.01);

        structure.setChanceWork(-10);  // Должно быть ограничено 0
        assertEquals(0, structure.getChanceWork(), 0.01);
    }

    @Test
    void testCastChanceWork() {
        structure.setChanceWork(100);  // 100% шанс
        assertTrue(structure.castChanceWork());

        structure.setChanceWork(0);  // 0% шанс
        assertFalse(structure.castChanceWork());
    }

    @Test
    void testGetAndSetCooldown() {
        structure.setCooldown(10);
        assertEquals(10, structure.getCooldown());

        structure.setCooldown(-1);  // Должно быть установлено в 0
        assertEquals(0, structure.getCooldown());
    }

    @Test
    void testUseCooldown() {
        structure.setCooldown(3);
        assertFalse(structure.useCooldown());
        assertFalse(structure.useCooldown());
        assertTrue(structure.useCooldown());  // Должно сработать на третьем вызове
    }

//    @Test
//    void testGetLocations() {
//        List<Location> locations = structure.getLocations();
//        assertEquals(2, locations.size());
//    }

    @Test
    void testSetLocationsThrowsExceptionForEmptyList() {
        List<Location> emptyList = new ArrayList<>();
        assertThrows(NullPointerException.class, () -> structure.setLocations(emptyList));
    }


    @Test
    void testToMesh() {
        structure.connectToMesh(mesh);
        assertEquals(mesh, structure.getMesh());
        structure.disconnectToMesh();
        assertNull(structure.getMesh());
        assertTrue(mesh.getStructures().isEmpty());
//        verify(mesh, times(1)).removeStructure(structure);  // Проверка вызова метода удаления
    }

    @Test
    void testGetAndSetVolume() {
        structure.setVolume(50);
        assertEquals(50, structure.getVolume(), 0.01);

        structure.setVolume(-10);  // Должно быть установлено в 0
        assertEquals(0, structure.getVolume(), 0.01);
    }

    @Test
    void DefaultStructure() {
        structure.setCooldown(0);
        assertTrue(structure.useCooldown());
        structure.setCooldown(2);
        assertFalse(structure.useCooldown());
        assertNull(structure.getMesh());
        structure.setChanceWork(100);
        assertTrue(structure.castChanceWork());
        structure.setChanceWork(0);
        assertFalse(structure.castChanceWork());
    }
}
