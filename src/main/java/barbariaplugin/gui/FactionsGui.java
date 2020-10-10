package barbariaplugin.gui;

import barbariaplugin.factions.Faction;
import barbariaplugin.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FactionsGui implements Listener {
    public FactionsGui() {
        inventory = null;
    }

    public FactionsGui(Player caller) {
        player = caller;
        inventory = Bukkit.createInventory(caller, 18, "Factions Menu");
        this.createUserGui();
        this.createCloseButton();
        caller.openInventory(inventory);
    }

    public void handleClick(Player player, ItemStack clickedItem) {
        switch (clickedItem.getType()) {
            case RED_WOOL:
                player.closeInventory();
                break;
            case NETHER_STAR:
                this.createFactionMessage(player);
                break;
            case OAK_DOOR:
                this.joinFactionMessage(player);
                break;
        }
    }

    public static void joinFaction(Player player, String name, AsyncPlayerChatEvent event) {
        if (player != factionJoiner) return;
        event.setCancelled(true);
        player.sendMessage("You decided to join: " + name);
        factionJoiner = null;
    }

    public static void createFaction(Player player, String name, AsyncPlayerChatEvent event) {
        if (player != factionCreator) return;
        event.setCancelled(true);
        player.sendMessage("You decided to create: " + name);
        factionCreator = null;
        if (!Factions.checkFactionCreate(name, player.getUniqueId())) {
            player.sendMessage("The faction could not be created.");
            return;
        }
        Factions.addFaction(name, player.getUniqueId());
        player.sendMessage("Successfully created faction.");
    }

    private void joinFactionMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Congratulations, you have decided to join a faction!");
        player.closeInventory();
        if (factionCreator != null || factionJoiner != null) {
            player.sendMessage("Somebody else is attempting to interact with a faction at the moment. Please retry again soon.");
            return;
        }
        factionJoiner = player;
        player.sendMessage("Please enter the name of the faction you want to join");
    }

    private void createFactionMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Congratulations, you have decided to create a faction!");
        player.closeInventory();
        if (factionCreator != null || factionJoiner != null) {
            player.sendMessage("Somebody else is attempting to interact with a faction at the moment. Please retry again soon.");
        }
        factionCreator = player;
        player.sendMessage("Please enter the name of the faction you want to create.");
    }

    private void createUserGui() {
        final ItemStack entranceItem = new ItemStack(Material.OAK_DOOR, 1);
        final ItemStack createItem = new ItemStack(Material.NETHER_STAR, 1);

        final ItemMeta entranceItemMeta = entranceItem.getItemMeta();
        final ItemMeta createItemMeta = createItem.getItemMeta();

        entranceItemMeta.setDisplayName("Enter a faction!");
        createItemMeta.setDisplayName("Create a faction!");

        entranceItem.setItemMeta(entranceItemMeta);
        createItem.setItemMeta(createItemMeta);

        inventory.setItem(0, entranceItem);
        inventory.setItem(8, createItem);
    }

    private void createMemberGui() {

    }

    private void createLeaderGui() {

    }

    private void createCloseButton() {
        final ItemStack closeButton = new ItemStack(Material.RED_WOOL, 1);
        final ItemMeta closeButtonMeta = closeButton.getItemMeta();
        closeButtonMeta.setDisplayName("Close this GUI.");
        closeButton.setItemMeta(closeButtonMeta);
        inventory.setItem(17, closeButton);

    }


    Player player;
    public static Player factionCreator = null;
    public static Player factionJoiner = null;
    private final Inventory inventory;
}
