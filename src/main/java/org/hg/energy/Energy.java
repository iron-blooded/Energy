package org.hg.energy;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public final class Energy extends JavaPlugin {
    public List<Mesh> mashes;

    @Override
    public void onEnable() {
        // TODO: Получение мешей из базы данных
        new BukkitRunnable(){
            @Override
            public void run() {
                for (Mesh mesh: mashes){
                    mesh.updateStructures();
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    @Override
    public void onDisable() {
        // TODO: Запись сетей в базу данных
    }
}
