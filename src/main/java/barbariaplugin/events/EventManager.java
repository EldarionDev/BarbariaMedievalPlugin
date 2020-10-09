package barbariaplugin.events;

import barbariaplugin.gui.FactionsGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class EventManager implements Listener {
    public EventManager() {

    }
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.getView().getTitle().equalsIgnoreCase("Factions Menu")) return;
        event.setCancelled(true);
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player player = (Player)event.getWhoClicked();
        FactionsGui tmp = new FactionsGui();
        tmp.handleClick(player, clickedItem);
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
