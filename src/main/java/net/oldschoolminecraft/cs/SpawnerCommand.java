package net.oldschoolminecraft.cs;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.block.CraftCreatureSpawner;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

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

        String mob = CustomSpawners.capitalCase(args[0]);
        Player ply = (Player) sender;

        if (!plugin.getHandler().hasBlockSelected(ply))
        {
            ply.sendMessage(ChatColor.RED + "You don't have a spawner selected!");
            return true;
        }

        Block block = plugin.getHandler().getSelectedBlock(ply);
        BlockState rawState = block.getState();
        if (rawState instanceof CraftCreatureSpawner)
        {
            boolean enabled = plugin.getConfig().getBoolean("mob." + mob.toLowerCase() + ".enabled", false);

            if (!enabled)
            {
                ply.sendMessage(ChatColor.RED + "Sorry, this mob option has been disabled by the system administrator.");
                return true;
            }

            double price = plugin.getConfig().getDouble("mob." + mob.toLowerCase() + ".price", -1D);
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

            plugin.takeMoney(ply, price);
            ((CraftCreatureSpawner)rawState).setCreatureType(CreatureType.fromName(mob));
            ply.sendMessage(ChatColor.GREEN + "The spawner type has been successfully changed for: " + priceStr + "!");
        }

        return true;
    }
}
