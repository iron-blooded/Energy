package org.hg.energy;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.hg.energy.Interface.TextBox;
import org.hg.energy.Listeners.ListenerChat;
import org.hg.energy.Listeners.ListenerClickBlock;
import org.hg.energy.Listeners.ListenerIcons;
import org.hg.energy.Listeners.ListenerListItems;
import org.hg.energy.Objects.Structure;

import java.util.*;

public final class Energy extends JavaPlugin {
    public List<Mesh> meshes = new ArrayList<>();
    public Map<Player, TextBox> textBoxMap = new HashMap<>();


    @Override
    public void onEnable() {
        // TODO: Получение мешей из базы данных
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerIcons(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerClickBlock(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerChat(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ListenerListItems(this), this);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Mesh mesh : meshes) {
                    mesh.updateStructures();
                    for (Structure structure: mesh.getStructures()){
                        structure.checkLocationsMatching();
                    }
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // TODO: Запись сетей в базу данных
        //TODO: телепорт к структурам
    }

    public List<Structure> getStructures() {
        Set<Structure> list = new HashSet<>();
        for (Mesh mesh : this.meshes) {
            list.addAll(mesh.getStructures());
        }
        return new ArrayList<>(list);
    }
}
