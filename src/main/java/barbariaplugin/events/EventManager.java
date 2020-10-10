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
        final ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        final Player player = (Player)event.getWhoClicked();
        switch (event.getView().getTitle().toLowerCase()) {
            case "factions menu":
                event.setCancelled(true);
                FactionsGui tmp = new FactionsGui();
                tmp.handleClick(player, clickedItem);
                break;
            case "armies menu":
                break;
            case "territories menu":
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        event.setCancelled(true);
    }
}