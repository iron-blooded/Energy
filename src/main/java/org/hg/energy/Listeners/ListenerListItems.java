package org.hg.energy.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Interface.ListItems;
import org.hg.energy.Interface._Icons;

public class ListenerListItems implements Listener {
    private Energy plugin;

    public ListenerListItems(Energy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory != null && inventory.getHolder() instanceof ListItems holder) {
            ItemStack clicked_item = event.getCurrentItem();
            event.setCancelled(true);
            if (clicked_item != null && _Icons.isSimilar(clicked_item).equals(_Icons.empty)) {
                if (event.getClick().equals(ClickType.MIDDLE)){
                    event.setCancelled(false);
                    return;
                }
                holder.clickItem(clicked_item);
                player.openInventory(holder.getInventory());
            }
        }

    }
}
