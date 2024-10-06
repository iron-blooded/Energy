package org.hg.energy.Listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Interface.CreateStructure;
import org.hg.energy.Interface.SettingsStructure;

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
            if (item.getItemMeta().getDisplayName().contains("Настроечный ключ")) {
                event.setCancelled(true);
                plugin.getStructures().stream()
                        .filter(str -> str.getLocations().contains(block.getLocation()))
                        .findFirst()
                        .ifPresentOrElse(
                                structure -> player.openInventory(new SettingsStructure(structure).getInventory()),
                                () -> player.openInventory(new CreateStructure(block.getLocation()).getInventory())
                                        );

            }
        }
    }
}
