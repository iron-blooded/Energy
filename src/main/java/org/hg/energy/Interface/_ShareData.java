package org.hg.energy.Interface;

import org.bukkit.Location;
import org.hg.energy.Mesh;
import org.hg.energy.Objects.Structure;

public class _ShareData {
    private Mesh mesh;
    private Structure structure;
    private Location location;

    public _ShareData(Mesh mesh, Structure structure, Location location) {
        this.mesh = mesh;
        this.structure = structure;
        this.location = location;
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
}
