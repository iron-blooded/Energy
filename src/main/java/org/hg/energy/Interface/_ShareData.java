package org.hg.energy.Interface;

import org.bukkit.Location;
import org.bukkit.inventory.InventoryHolder;
import org.hg.energy.Energy;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;

public class _ShareData {
    private Mesh mesh;
    private Structure structure;
    private Location location;
    private Energy plugin;
    private InventoryHolder holder;
    private boolean bol = false;

    public _ShareData(Mesh mesh, Structure structure, Location location, Energy plugin) {
        this.mesh = mesh;
        this.structure = structure;
        this.location = location;
        this.plugin = plugin;
    }

    public InventoryHolder getHolder() {
        return this.holder;
    }

    public _ShareData setHolder(InventoryHolder holder) {
        this.holder = holder;
        return this;
    }

    public Energy getPlugin() {
        return plugin;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isBoolean() {
        return bol;
    }

    public void setBoolean(boolean bol) {
        this.bol = bol;
    }
}
