package com.eldarion.barbariaplugin;

import com.eldarion.barbariaplugin.events.EventManager;
import com.eldarion.barbariaplugin.factions.Factions;
import com.eldarion.barbariaplugin.gui.ArmiesGui;
import com.eldarion.barbariaplugin.gui.FactionsGui;
import com.eldarion.barbariaplugin.gui.TerritoriesGui;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class Barbariaplugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Launching Barbaria Plugin.");
        getServer().getPluginManager().registerEvents(new EventManager(), this);
        Factions.load();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player)sender;
        if (command.getName().equalsIgnoreCase("factions")) {
            sender.sendMessage(ChatColor.AQUA + "Entering the factions main menu.");
            FactionsGui newGui = new FactionsGui(player);
        }
        if (command.getName().equalsIgnoreCase("armies")) {
            sender.sendMessage(ChatColor.DARK_GREEN + "Entering the armies main menu.");
            ArmiesGui newGui = new ArmiesGui(player);
        }
        if (command.getName().equalsIgnoreCase("territories")) {
            sender.sendMessage(ChatColor.DARK_RED + "Entering the territories main menu.");
            TerritoriesGui newGui = new TerritoriesGui(player);
        }
        return true;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Factions.save();
    }
}
