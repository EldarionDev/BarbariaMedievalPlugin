package com.eldarion.barbariaplugin.gui;

import com.eldarion.barbariaplugin.factions.Contract;
import com.eldarion.barbariaplugin.factions.Faction;
import com.eldarion.barbariaplugin.factions.Factions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FactionsGui implements Listener {
    public FactionsGui() {
        inventory = null;
    }

    public FactionsGui(Player caller) {
        player = caller;
        UUID playerUUID = player.getUniqueId();
        inventory = Bukkit.createInventory(caller, 18, "Factions Menu");

        if (Factions.checkPlayerInFaction(playerUUID)) {
            String factionName = Factions.getMemberFactionName(playerUUID);
            if (factionName.equals("")) {
                player.sendMessage("Could not get your faction.");
                return;
            }
            if (Factions.checkPlayerFactionLeader(player.getUniqueId(), factionName)) {
                this.createLeaderGui();
            } else {
                this.createMemberGui();
            }
        } else {
            this.createUserGui();
        }
        this.createCloseButton();
        this.createStatsButton();
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
            case ZOMBIE_HEAD:
                this.openRequestGui(player);
                break;
            case PLAYER_HEAD:
                this.sendPlayerList(player);
                break;
            case BARRIER:
                Factions.deleteFaction(Factions.getMemberFactionName(player.getUniqueId()), player.getUniqueId());
                break;
            case IRON_DOOR:
                Factions.removeMember(player.getUniqueId());
                Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId())).addLoyalty(-5.0f);
                break;
            case BOOK:
                player.sendMessage("Your faction proposals:");
                this.listProposalsMessage(player);
                break;
            case PAPER:
                player.sendMessage("Please enter your proposal.");
                proposalSender = player;
                break;
            case LAVA_BUCKET:
                player.sendMessage("Please enter the name of the player whose proposal you want to delete");
                this.listProposalsMessage(player);
                proposalDeletor = player;
                break;
            case DRAGON_HEAD:
                ownerChanger = player;
                player.sendMessage("Please enter the name of the player you want to be faction leader.");
                break;
            case SPRUCE_SIGN:
                printStatsMessage(player);
                break;
            case SHIELD:
                ceaseFirePlayer = player;
                ceaseFireMessage(player);
                break;
            case DIAMOND_SWORD:
                warDeclarer = player;
                declareWarMessage(player);
                break;
            case GOLD_INGOT:
                tradeOffer = player;
                offerTradeMessage(player);
                break;
        }
    }

    public void offerTradeMessage (Player player) {
        player.sendMessage("This are the things you can offer for trade: ");
        for (String s : Contract.getOffers()) {
            player.sendMessage(s);
        }
        player.sendMessage("Please enter one and afterwards the amplifier(time or amount) to continue.");
        player.sendMessage("When you have finished enter exit.");
        player.sendMessage("Now enter the name of the trade agreement.");
    }

    public void offerTrade (Player player, String name, AsyncPlayerChatEvent event) {
        if (!player.equals(tradeOffer)) return;
        if (tradeNameDone != 1) {
            tradeNameDone = 1;
            Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId())).addTrade(name);
            tradeName = name;
        }
        else if (name.equalsIgnoreCase("exit")) {
            player.sendMessage("Created trade.");
            tradeNameDone = 0;
            tradeOffer = null;
            tradeName = "";
            return;
        }
        else {
            String[] arrOfStr = name.split(" ");
            Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId())).getTrade(name).addCondition(Contract.Conditions.valueOf(arrOfStr[0]), Double.parseDouble(arrOfStr[1]));
        }
        player.sendMessage("Please enter another trade item or exit to finish the agreement.");
    }

    public void declareWarMessage (Player player) {
        player.sendMessage("Please enter the name of the faction you want to declare war to!");
    }

    public static void declareWar (Player player, String name, AsyncPlayerChatEvent event) {
        if (!player.equals(warDeclarer)) return;
        warDeclarer = null;
        Faction f = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        f.addFactionAtWar(name);
        Factions.getFaction(name).addFactionAtWar(f.getFactionName());
        f.addAggression(1000);
        player.sendMessage("Your faction is now at war with: " + name);
    }

    public void ceaseFireMessage (Player player) {
        player.sendMessage("You are at war with following factions:");
        Faction f = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        for (String s : f.getFactionsAtWar()) {
            player.sendMessage(s);
        }
        player.sendMessage("Please enter a faction you want to cease fire with!");
    }

    public static void ceaseFire (Player player, String name, AsyncPlayerChatEvent event) {
        if (!player.equals(ceaseFirePlayer)) return;
        ceaseFirePlayer = null;
        Faction f = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        f.stopWar(name);
        Factions.getFaction(name).stopWar(f.getFactionName());
        player.sendMessage("You have now ceased fire with: " + name);
    }

    public static void printStatsMessage (Player player) {
        List <Faction> factions = Factions.getFactions();
        player.closeInventory();
        for (Faction f : factions) {
            player.sendMessage(ChatColor.BLACK + "Faction name: " + f.getFactionName());
            player.sendMessage("Trustworthy: " + f.getTrustworthy());
            player.sendMessage("Loyalty: " + f.getLoyalty());
            player.sendMessage("Aggression: " + f.getAggression());
        }
    }

    public static void changeOwner (Player player, String name, AsyncPlayerChatEvent event) {
        if (player != ownerChanger) return;
        ownerChanger = null;
        event.setCancelled(true);
        UUID newFactionLeader = Objects.requireNonNull(Bukkit.getPlayer(name)).getUniqueId();
        Factions.setFactionLeader(Factions.getMemberFactionName(player.getUniqueId()), newFactionLeader);
    }
    
    public static void deleteProposal (Player player, String name, AsyncPlayerChatEvent event) {
        if (player != proposalDeletor) return;
        event.setCancelled(true);
        Faction f = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        f.removeProposal(Bukkit.getPlayer(name).getUniqueId());
        proposalDeletor = null;
    }

    public static void applyProposal (Player player, String name, AsyncPlayerChatEvent event) {
        if (player != proposalSender) return;
        event.setCancelled(true);
        Faction f = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        f.addProposal(name, player.getUniqueId());
        proposalSender = null;
    }
    
    public void listProposalsMessage (Player player) {
        List<String> proposals = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId())).getProposals();
        if (proposals == null) {
            player.sendMessage("There are no open proposals.");
            return;
        }
        for (String proposal: proposals) {
            player.sendMessage(proposal);
        }
        player.closeInventory();
    }

    private void sendPlayerList(Player player) {
        player.sendMessage("Player list of your faction: ");
        String playerFactionName = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId())).factionName;
        for (String member : Factions.getMembers(playerFactionName)) {
            player.sendMessage(member);
        }
    }

    public void handleRequestClick(Player player, ItemStack clickedItem) {
        switch (clickedItem.getType()) {
            case GREEN_WOOL:
                this.acceptRequestMessage(player);
                break;
            case RED_WOOL:
                this.declineRequestMessage(player);
                break;
        }
    }

    public static void joinFaction(Player player, String name, AsyncPlayerChatEvent event) {
        if (player != factionJoiner) return;
        event.setCancelled(true);
        List<String> available_factions = Factions.getFactionNames();
        for (String current : available_factions) {
            if (current.equalsIgnoreCase(name)) {
                player.sendMessage("Sent request to faction: " + current);
                Factions.getFaction(current).addRequest(player);
                factionJoiner = null;
                return;
            }
        }
        player.sendMessage("This faction is not available. Did you have a Typo?");
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

    private void openRequestGui (Player player) {
        respondRequest = player;
        player.closeInventory();

        Inventory requestInventory = Bukkit.createInventory(player, 9, "Requests");
        player.openInventory(requestInventory);

        final ItemStack acceptItem = new ItemStack(Material.GREEN_WOOL, 1);
        final ItemStack declineItem = new ItemStack(Material.RED_WOOL, 1);

        final ItemMeta acceptItemMeta = acceptItem.getItemMeta();
        final ItemMeta declineItemMeta = declineItem.getItemMeta();

        acceptItemMeta.setDisplayName("Accept requests.");
        declineItemMeta.setDisplayName("Decline Requests.");

        acceptItem.setItemMeta(acceptItemMeta);
        declineItem.setItemMeta(declineItemMeta);

        requestInventory.setItem(0, acceptItem);
        requestInventory.setItem(8, declineItem);
    }

    public void acceptRequestMessage (Player player) {
        player.closeInventory();
        respondRequest = null;
        acceptRequest = player;
        player.sendMessage("Please enter one of the following names to accept: ");
        Faction playerFaction = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        List<String> names = playerFaction.getRequests();
        if (names == null) {
            player.sendMessage("There are no pending requests.");
            acceptRequest = null;
            return;
        }
        for (String name : names) {
            player.sendMessage(name);
        }
    }

    public void declineRequestMessage (Player player) {
        player.closeInventory();
        respondRequest = null;
        declineRequest = player;
        player.sendMessage("Please enter one of the following names to decline: ");
        Faction playerFaction = Factions.getFaction(Factions.getMemberFactionName(player.getUniqueId()));
        List<String> names = playerFaction.getRequests();
        if (names == null) {
            player.sendMessage("There are no pending requests.");
            declineRequest = null;
            return;
        }
        for (String name : names) {
            player.sendMessage(name);
        }
    }

    public static void acceptRequest (Player caller, String name, AsyncPlayerChatEvent event) {
        if (caller != acceptRequest) return;
        acceptRequest = null;
        event.setCancelled(true);
        Faction playerFaction = Factions.getFaction(Factions.getMemberFactionName(caller.getUniqueId()));
        for (String a : playerFaction.getRequests()) {
            if (a.equalsIgnoreCase(name)) {
                playerFaction.acceptRequest(name);
                Factions.addMember(Factions.getMemberFactionName(caller.getUniqueId()), Bukkit.getPlayer(name).getUniqueId());
                caller.sendMessage("Successfully added player: " + name);
                return;
            }
        }
        caller.sendMessage("Player could not be found!");
    }

    public static void declineRequest (Player caller, String name, AsyncPlayerChatEvent event) {
        if (caller != declineRequest) return;
        declineRequest = null;
        Faction playerFaction = Factions.getFaction(Factions.getMemberFactionName(caller.getUniqueId()));
        for (String a : playerFaction.getRequests()) {
            if (a.equalsIgnoreCase(name)) {
                playerFaction.declineRequest(caller.getDisplayName());
            }
        }
    }

    private void joinFactionMessage(Player player) {
        player.sendMessage(ChatColor.GOLD + "Congratulations, you have decided to join a faction!");
        player.closeInventory();
        if (factionCreator != null || factionJoiner != null) {
            player.sendMessage("Somebody else is attempting to interact with a faction at the moment. Please retry again soon.");
            return;
        }
        factionJoiner = player;
        player.sendMessage("A list of factions: ");
        List<String> faction_choice = Factions.getFactionNames();
        for (String currentFaction : faction_choice)  {
            player.sendMessage(currentFaction);
        }
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
        final ItemStack seeProposalItem = new ItemStack(Material.BOOK, 1);
        final ItemStack writeProposalItem = new ItemStack(Material.PAPER, 1);
        final ItemStack leaveItem = new ItemStack(Material.IRON_DOOR, 1);
        final ItemStack playerListItem = new ItemStack(Material.PLAYER_HEAD, 1);

        final ItemMeta seeProposalItemMeta = seeProposalItem.getItemMeta();
        final ItemMeta writeProposalItemMeta = writeProposalItem.getItemMeta();
        final ItemMeta leaveItemMeta = leaveItem.getItemMeta();
        final ItemMeta playerListItemMeta = playerListItem.getItemMeta();

        seeProposalItemMeta.setDisplayName("See faction proposals!");
        writeProposalItemMeta.setDisplayName("Write a faction proposal!");
        leaveItemMeta.setDisplayName("Leave this faction!");
        playerListItemMeta.setDisplayName("Send the player list.");

        seeProposalItem.setItemMeta(seeProposalItemMeta);
        writeProposalItem.setItemMeta(writeProposalItemMeta);
        leaveItem.setItemMeta(leaveItemMeta);
        playerListItem.setItemMeta(playerListItemMeta);

        inventory.setItem(0, playerListItem);
        inventory.setItem(7, writeProposalItem);
        inventory.setItem(8, seeProposalItem);
        inventory.setItem(9, leaveItem);
    }

    private void createLeaderGui() {
        final ItemStack deleteFactionItem = new ItemStack(Material.BARRIER, 1);
        final ItemStack deleteProposalItem = new ItemStack(Material.LAVA_BUCKET, 1);
        final ItemStack playerListItem = new ItemStack(Material.PLAYER_HEAD, 1);
        final ItemStack seeProposalsItem = new ItemStack(Material.BOOK, 1);
        final ItemStack seeTradesItem = new ItemStack(Material.ENCHANTED_BOOK, 1);
        final ItemStack makeProposalItem = new ItemStack(Material.PAPER, 1);
        final ItemStack offerTradeItem = new ItemStack(Material.GOLD_INGOT, 1);
        final ItemStack changeOwnerItem = new ItemStack(Material.DRAGON_HEAD, 1);
        final ItemStack ceaseFireItem = new ItemStack(Material.SHIELD, 1);
        final ItemStack declareWarItem = new ItemStack(Material.DIAMOND_SWORD, 1);
        final ItemStack joinRequestsItem = new ItemStack(Material.ZOMBIE_HEAD, 1);

        final ItemMeta deleteFactionItemMeta = deleteFactionItem.getItemMeta();
        final ItemMeta deleteProposalItemMeta = deleteProposalItem.getItemMeta();
        final ItemMeta playerListItemMeta = playerListItem.getItemMeta();
        final ItemMeta seeProposalsItemMeta = seeProposalsItem.getItemMeta();
        final ItemMeta seeTradesItemMeta = seeTradesItem.getItemMeta();
        final ItemMeta makeProposalItemMeta = makeProposalItem.getItemMeta();
        final ItemMeta offerTradeItemMeta = offerTradeItem.getItemMeta();
        final ItemMeta changeOwnerItemMeta = changeOwnerItem.getItemMeta();
        final ItemMeta ceaseFireItemMeta = ceaseFireItem.getItemMeta();
        final ItemMeta declareWarItemMeta = declareWarItem.getItemMeta();
        final ItemMeta joinRequestsItemMeta = joinRequestsItem.getItemMeta();
        
        deleteFactionItemMeta.setDisplayName("Delete your faction!");
        deleteProposalItemMeta.setDisplayName("Delete a proposal to your faction!");
        playerListItemMeta.setDisplayName("See a list of players of this faction!");
        seeProposalsItemMeta.setDisplayName("See faction proposals!");
        seeTradesItemMeta.setDisplayName("See faction trade agreements!");
        makeProposalItemMeta.setDisplayName("Make a faction proposal!");
        offerTradeItemMeta.setDisplayName("Offer a trade agreement.");
        changeOwnerItemMeta.setDisplayName("Change the faction owner!");
        ceaseFireItemMeta.setDisplayName("Cease fire with a faction!");
        declareWarItemMeta.setDisplayName("Declare war to a faction!");
        joinRequestsItemMeta.setDisplayName("See join requests!");

        deleteFactionItem.setItemMeta(deleteFactionItemMeta);
        deleteProposalItem.setItemMeta(deleteProposalItemMeta);
        playerListItem.setItemMeta(playerListItemMeta);
        seeProposalsItem.setItemMeta(seeProposalsItemMeta);
        seeTradesItem.setItemMeta(seeTradesItemMeta);
        makeProposalItem.setItemMeta(makeProposalItemMeta);
        offerTradeItem.setItemMeta(offerTradeItemMeta);
        changeOwnerItem.setItemMeta(changeOwnerItemMeta);
        ceaseFireItem.setItemMeta(ceaseFireItemMeta);
        declareWarItem.setItemMeta(declareWarItemMeta);
        joinRequestsItem.setItemMeta(joinRequestsItemMeta);

        inventory.setItem(16, deleteFactionItem);
        inventory.setItem(15, deleteProposalItem);
        inventory.setItem(0, playerListItem);
        inventory.setItem(7, makeProposalItem);
        inventory.setItem(8, seeProposalsItem);
        inventory.setItem(3, offerTradeItem);
        inventory.setItem(4, seeTradesItem);
        inventory.setItem(9, declareWarItem);
        inventory.setItem(10, ceaseFireItem);
        inventory.setItem(12, changeOwnerItem);
        inventory.setItem(13, joinRequestsItem);
    }

    private void createCloseButton () {
        final ItemStack closeButton = new ItemStack(Material.RED_WOOL, 1);
        final ItemMeta closeButtonMeta = closeButton.getItemMeta();
        closeButtonMeta.setDisplayName("Close this GUI.");
        closeButton.setItemMeta(closeButtonMeta);
        inventory.setItem(17, closeButton);
    }

    public void createStatsButton () {
        final ItemStack statsButton = new ItemStack(Material.SPRUCE_SIGN, 1);
        final ItemMeta statsButtonMeta = statsButton.getItemMeta();
        statsButtonMeta.setDisplayName("Get the stats of all factions.");
        statsButton.setItemMeta(statsButtonMeta);
        inventory.setItem(1, statsButton);
    }

    Player player;
    public static Player factionCreator = null;
    public static Player factionJoiner = null;
    public static Player respondRequest = null;
    public static Player acceptRequest = null;
    public static Player declineRequest = null;
    public static Player proposalSender = null;
    public static Player proposalDeletor = null;
    public static Player ownerChanger = null;
    public static Player warDeclarer = null;
    public static Player ceaseFirePlayer = null;
    public static Player tradeOffer = null;
    public static int tradeNameDone = 0;
    public static String tradeName;
    private final Inventory inventory;
}
