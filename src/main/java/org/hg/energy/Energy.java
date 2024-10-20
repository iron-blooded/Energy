package org.hg.energy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hg.energy.Database.SetupDatabase;
import org.hg.energy.Interface.TextBox;
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
                    mesh.updateStructures();
                    for (Structure structure : mesh.getStructures()) {
                        structure.checkLocationsMatching();
                    }
                }
                if (count[0] > 60) {
                    database.meshDatabase.save();
                    count[0] = 0;
                }
                count[0]++;
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
