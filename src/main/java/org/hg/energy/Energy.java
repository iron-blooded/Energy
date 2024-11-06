package org.hg.energy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
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

import java.sql.SQLException;
import java.util.*;

public final class Energy extends JavaPlugin {
    public List<Mesh> meshes = new ArrayList<>();
    public Map<Player, TextBox> textBoxMap = new HashMap<>();
    public Map<Player, Structure> clone_structures = new HashMap<>();
    public Map<Player, Structure> edit_locations_structure = new HashMap<>();
    public SetupDatabase database;


    @Override
    public void onEnable() {
        database = new SetupDatabase(this);
        this.meshes = database.meshDatabase.getListMesh();
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
                    database.meshDatabase.save();
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
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        database.meshDatabase.save();
        try {
            database.connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Structure> getStructures() {
        Set<Structure> list = new HashSet<>();
        for (Mesh mesh : this.meshes) {
            list.addAll(mesh.getStructures());
        }
        return new ArrayList<>(list);
    }
}
