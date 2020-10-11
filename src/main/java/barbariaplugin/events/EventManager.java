package barbariaplugin.events;

import barbariaplugin.factions.Faction;
import barbariaplugin.factions.Factions;
import barbariaplugin.gui.FactionsGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
            case "requests":
                event.setCancelled(true);
                FactionsGui tmp2 = new FactionsGui();
                tmp2.handleRequestClick(player, clickedItem);
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
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (FactionsGui.factionCreator != null) {
            FactionsGui.createFaction(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.factionJoiner != null) {
            FactionsGui.joinFaction(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.acceptRequest != null) {
            FactionsGui.acceptRequest(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.declineRequest != null) {
            FactionsGui.declineRequest(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.proposalSender != null) {
            FactionsGui.applyProposal(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.proposalDeletor != null) {
            FactionsGui.deleteProposal(event.getPlayer(), event.getMessage(), event);
        }
        else if (FactionsGui.ownerChanger != null) {
            FactionsGui.changeOwner(event.getPlayer(), event.getMessage(), event);
        }
    }

    @EventHandler
    public void OnPlayerAttack(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            if (Factions.checkFriendlyFire((Player) event.getEntity(), (Player) event.getDamager())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent event) {
        event.setCancelled(true);
    }
}
