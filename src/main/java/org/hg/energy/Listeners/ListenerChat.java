package org.hg.energy.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.hg.energy.Energy;
import org.hg.energy.Interface.SettingsMesh;
import org.hg.energy.Interface.SettingsStructure;
import org.hg.energy.Interface.TextBox;
import org.hg.energy.Interface._ShareData;


public class ListenerChat implements Listener {
    private Energy plugin;

    public ListenerChat(Energy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.textBoxMap.remove(player);
    }

    @EventHandler
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (plugin.textBoxMap.containsKey(player)) {
            event.setCancelled(true);
            TextBox textBox = plugin.textBoxMap.get(player);
            String value = event.getMessage();
            event.setMessage("");
            if (textBox.useFunction(value)) {
                player.sendMessage(ChatColor.GREEN + "Значение принято!");
                plugin.textBoxMap.remove(player);
                _ShareData shareData = textBox.getData();
                if (shareData.getMesh() != null) {
                    Bukkit.getScheduler().runTask(plugin,
                                                  () -> player.openInventory(new SettingsMesh(shareData).getInventory()));
                } else if (shareData.getStructure() != null) {
                    Bukkit.getScheduler().runTask(plugin,
                                                  () -> player.openInventory(new SettingsStructure(shareData).getInventory()));
                }
            } else {
                player.sendMessage(
                        ChatColor.RED + "Значение не принято! Пожалуйста, еще раз укажите валидное значение.");
            }
        }
    }
}
