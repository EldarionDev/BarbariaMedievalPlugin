package barbariaplugin.gui;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
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

    public void handleClick(Player player, ItemStack clickedItem) {
        switch (clickedItem.getType()) {
            case RED_WOOL:
                player.closeInventory();
                break;
            case NETHER_STAR:
                player.sendMessage("Creating new faction.");
                break;
            case OAK_DOOR:
                player.sendMessage("Joining a faction.");
                break;
        }
    }

    Player player;
    private final Inventory inventory;
}
