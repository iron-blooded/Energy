package org.hg.energy.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.hg.energy.Energy;
import org.hg.energy.Interface.Window;
import org.hg.energy.Interface._Icons;
import org.hg.energy.Interface._ShareData;

import java.util.UUID;

import static org.hg.energy.Interface._Icons.getUUID;
import static org.hg.energy.Interface._Icons.isSimilar;

public class ListenerIcons implements Listener {
    Energy plugin;

    public ListenerIcons(Energy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getClickedInventory();
        Player player = (Player) event.getWhoClicked();
        if (inventory.getHolder() instanceof Window holder) {
            ItemStack itemStack = event.getCurrentItem();
            if (itemStack != null && itemStack.getItemMeta() != null) {
                event.setCancelled(true);
                _Icons icon = isSimilar(itemStack);
                _ShareData data = holder.getObject();
                UUID uuid = getUUID(itemStack);
                if (uuid != null) {
                    plugin.meshes.stream()
                            .filter(mesh -> mesh.getUuid().equals(uuid))
                            .findFirst()
                            .ifPresent(data::setMesh);

                    plugin.getStructures().stream()
                            .filter(structure -> structure.getUuid().equals(uuid))
                            .findFirst()
                            .ifPresent(data::setStructure);
                }
                icon.use(player, data);
            }
        }

    }
}
