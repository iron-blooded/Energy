package org.hg.energy.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Interface.CreateStructure;
import org.hg.energy.Interface.ListMeshes;
import org.hg.energy.Interface.SettingsStructure;
import org.hg.energy.Interface._ShareData;

public class ListenerClickBlock implements Listener {
    Energy plugin;

    public ListenerClickBlock(Energy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand().equals(EquipmentSlot.OFF_HAND) || event.getAction().isLeftClick()) {
            return;
        }
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        ItemStack item = player.getItemInHand();
        if (item != null && item.getItemMeta() != null && !item.getType().isAir() &&
                block != null && block.isSolid()) {
            if (item.getItemMeta().getDisplayName().contains("Настроечный ключ")) { // Настройка структур
                event.setCancelled(true);
                plugin.getStructures().stream()
                        .filter(str -> {
                            for (Location location : str.getLocations()) {
                                Material type1 = location.getBlock().getType();
                                Material type2 = block.getType();
                                if (type1.equals(type2)) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .findFirst()
                        .ifPresentOrElse(
                                structure -> player.openInventory(new SettingsStructure(
                                        new _ShareData(null, structure, null, plugin)
                                ).getInventory()),
                                () -> player.openInventory(new CreateStructure(
                                        new _ShareData(null, null, block.getLocation(), plugin)
                                ).getInventory())
                                        );

            } else if (item.getItemMeta().getDisplayName().contains("поволжье")) {//Настройка сети
                player.openInventory(new ListMeshes(new _ShareData(null, null, null, plugin), 0).getInventory());
                event.setCancelled(true);
            }
        }
    }
}
