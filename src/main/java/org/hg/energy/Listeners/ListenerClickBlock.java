package org.hg.energy.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Interface.*;
import org.hg.energy.Objects.Structure;

public class ListenerClickBlock implements Listener {
    Energy plugin;

    public ListenerClickBlock(Energy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (EquipmentSlot.OFF_HAND.equals(event.getHand()) || event.getAction().isLeftClick()) {
            if (event.getAction().isLeftClick() && plugin.clone_structures.containsKey(player)) {
                player.sendMessage(ChatColor.GOLD + "Вы отменили копирование структуры.");
                plugin.clone_structures.remove(player);
            }
            return;
        }
        Block block = event.getClickedBlock();
        ItemStack item = player.getItemInHand();
        if (item != null && item.getItemMeta() != null && !item.getType().isAir() &&
                block != null && block.isSolid()) {
            if (item.getItemMeta().getDisplayName().contains("Отвёртка")
                    && item.getItemMeta().getDisplayName().contains("" + ChatColor.COLOR_CHAR)) { // Настройка структур
                event.setCancelled(true);
                Structure structure = null;
                for (Structure str : plugin.getStructures()) {
                    for (Location location : str.getLocations()) {
                        if (block.equals(location.getBlock())) {
                            structure = str;
                            break;
                        }
                    }
                    if (structure != null) {
                        break;
                    }
                }
                if (plugin.clone_structures.containsKey(player) && structure == null) {
                    structure = plugin.clone_structures.get(player).cloneWithNewUUID(block.getLocation());
                    plugin.clone_structures.remove(player);
                    player.sendMessage(ChatColor.GREEN + "Вы успешно скопировали структуру!");
                }
                if (player.hasPermission("energy.settings.structure")) {
                    if (structure != null) {
                        player.openInventory(new SettingsStructure(
                                new _ShareData(null, structure, block.getLocation(), plugin)
                        ).getInventory());
                    } else {
                        player.openInventory(new CreateStructure(new _ShareData(null, null, block.getLocation(),
                                                                                plugin
                        )).getInventory());
                    }
                } else {
                    player.openInventory(new PlayerSettingStructure(new _ShareData(null,
                                                                                   structure, null, plugin
                    )).getInventory());
                }

            } else if (item.getItemMeta().getDisplayName().contains("Паяльник")
                    && item.getItemMeta().getDisplayName().contains("" + ChatColor.COLOR_CHAR)
                    && player.hasPermission("energy.settings.mesh")) {//Настройка сети
                player.openInventory(new ListMeshes(new _ShareData(null, null, null, plugin), 0).getInventory());
                event.setCancelled(true);
            }
        }
    }
}
