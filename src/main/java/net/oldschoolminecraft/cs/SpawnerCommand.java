package net.oldschoolminecraft.cs;

import com.earth2me.essentials.Util;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

import java.io.File;

public class SpawnerCommand implements CommandExecutor
{
    private CustomSpawners plugin;

    public SpawnerCommand(CustomSpawners plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.RED + "Usage: /setspawnermob <mob>");
            return true;
        }

        String creatureName = args[0];
        creatureName = creatureName.equalsIgnoreCase("PigZombie") ? "PigZombie" : Util.capitalCase(creatureName);
        Player ply = (Player) sender;

        if (!plugin.getHandler().hasBlockSelected(ply))
        {
            ply.sendMessage(ChatColor.RED + "You don't have a spawner selected!");
            return true;
        }

        Block block = plugin.getHandler().getSelectedBlock(ply);
        BlockState rawState = block.getState();
        if (rawState instanceof CreatureSpawner)
        {
            String keyBase = "mobs." + creatureName.toLowerCase();
            String keyEnabled = keyBase + ".enabled";
            String keyPrice = keyBase + ".price";
            boolean enabled = plugin.getConfig().getBoolean(keyEnabled, false);

            if (!enabled)
            {
                ply.sendMessage(ChatColor.RED + "Sorry, this mob option is not allowed.");
                return true;
            }

            double price = plugin.getConfig().getDouble(keyPrice, -1D);
            String priceStr = (price == 0D) ? "FREE" : "$" + price;

            if (price < 0D)
            {
                ply.sendMessage(ChatColor.RED + "The price for this mob has not been configured properly. Contact the system administrator.");
                return true;
            }

            if (!(plugin.canAfford(ply, price)))
            {
                ply.sendMessage(ChatColor.RED + "You cannot afford this spawner type! It costs " + priceStr + ", but you only have $" + plugin.getBalance(ply) + ".");
                return true;
            }

            System.out.println("trying to set creature from input: " + creatureName);
            CreatureType newCreature = CreatureType.fromName(creatureName);
            ((CreatureSpawner)rawState).setCreatureType(newCreature);
            plugin.takeMoney(ply, price);
            ply.sendMessage(ChatColor.GREEN + "The spawner type has been set! Cost: " + priceStr);
            plugin.getHandler().deselectBlock(ply); // deselect on success
        }

        return true;
    }
}
