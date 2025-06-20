package org.hg.energy;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hg.energy.Database.SetupDatabase;
import org.hg.energy.Interface.TextBox;
import org.hg.energy.Interface.Window;
import org.hg.energy.Listeners.ListenerChat;
import org.hg.energy.Listeners.ListenerClickBlock;
import org.hg.energy.Listeners.ListenerIcons;
import org.hg.energy.Listeners.ListenerListItems;
import org.hg.energy.Objects.Structure;
import org.hg.ironChest.IronChest;
import org.hg.scorchingsun.ScorchingSun;

import java.sql.SQLException;
import java.util.*;

public final class Energy extends JavaPlugin {
    static Energy instance;
    public Map<Player, TextBox> textBoxMap = new HashMap<>();
    public Map<Player, Structure> clone_structures = new HashMap<>();
    public Map<Player, Structure> edit_locations_structure = new HashMap<>();
    public SetupDatabase database;
    private List<Mesh> meshes;

    public static Energy getInstance() {
        return instance;
    }

    public static ScorchingSun getScorchingSun() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("ScorchingSun");
        if (plugin instanceof ScorchingSun) {
            return (ScorchingSun) plugin;
        }
        return null;
    }

    public static IronChest getIronChest() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("IronChest");
        if (plugin instanceof IronChest) {
            return (IronChest) plugin;
        }
        return null;
    }

    @Override
    public void onLoad() {
        Energy.instance = this;
    }

    @Override
    public void onEnable() {
        database = new SetupDatabase(this);
        this.meshes = database.meshDatabase.getAllMeshes();
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerIcons(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerClickBlock(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerChat(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerListItems(this), this);
        final int[] count = {0};
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Mesh mesh : meshes) {
                    for (Structure structure : mesh.getStructures()) {
                        if (structure.getMesh() == null) {
                            structure.connectToMesh(mesh);
                        } else if (!structure.getMesh().equals(mesh)) {
                            mesh.removeStructure(structure);
                        }
                        structure.checkLocationsMatching();
                    }
                    mesh.updateStructures();
                }
                if (count[0] > 60) {
                    for (Mesh mesh : getMeshes()) {
                        database.meshDatabase.setMesh(mesh);
                    }
                    count[0] = 0;
                }
                count[0]++;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
                    if (holder instanceof Window window && window.needUpdate()) {
                        player.getOpenInventory().getTopInventory().setContents(holder.getInventory().getContents());
                        player.updateInventory();
                    }
                }
                Random random = new Random();
                for (Player player : edit_locations_structure.keySet()) {
                    if (!player.isOnline()) continue;
                    Structure structure = edit_locations_structure.get(player);
                    for (Location location : structure.getLocations()) {
                        double radius = 0.6;
                        for (double phi = 0; phi < Math.PI; phi += Math.PI / 10) {
                            for (double theta = 0; theta < 2 * Math.PI; theta += Math.PI / 10) {
                                double x = radius * Math.cos(theta) * Math.sin(phi);
                                double y = radius * Math.cos(phi);
                                double z = radius * Math.sin(theta) * Math.sin(phi);
                                Particle.DustOptions dustOptions = new Particle.DustOptions(
                                        Color.fromBGR(
                                                (int) (random.nextDouble() * 255), (int) (random.nextDouble() * 255),
                                                (int) (random.nextDouble() * 255)
                                                     ), 1.0f
                                );
                                player.spawnParticle(
                                        Particle.REDSTONE, location.clone().add(x, y, z), 0
                                        , 0, 0, 0, dustOptions
                                                    );
                            }
                        }
//                        player.spawnParticle(Particle.COMPOSTER, location, 50, 0.25, 0.25, 0.25, 1);
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        if (meshes != null) {
            for (Mesh mesh : getMeshes()) {
                database.meshDatabase.setMesh(mesh);
            }
        }
        try {
            database.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Mesh> getMeshes() {
        return meshes;
    }

    public void addMesh(Mesh mesh) {
        database.meshDatabase.setMesh(mesh);
        meshes.add(mesh);
    }

    public void deleteMesh(Mesh mesh) {
        database.meshDatabase.deleteMesh(mesh.getUuid());
        meshes.remove(mesh);
    }

    public List<Structure> getStructures() {
        Set<Structure> list = new HashSet<>();
        for (Mesh mesh : this.meshes) {
            list.addAll(mesh.getStructures());
        }
        return new ArrayList<>(list);
    }
}
