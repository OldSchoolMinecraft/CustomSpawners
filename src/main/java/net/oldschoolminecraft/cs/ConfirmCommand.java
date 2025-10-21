package net.oldschoolminecraft.cs;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;

public class ConfirmCommand implements CommandExecutor
{
    private CustomSpawners plugin;

    public ConfirmCommand(CustomSpawners plugin)
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

        Player ply = (Player) sender;
        String playerName = ply.getName();
        SpawnerCommand.PendingPurchase pending = plugin.getPendingPurchase(playerName);

        if (pending == null)
        {
            ply.sendMessage(ChatColor.RED + "You don't have any pending purchases to confirm.");
            return true;
        }

        if (pending.isExpired())
        {
            plugin.removePendingPurchase(playerName);
            ply.sendMessage(ChatColor.RED + "Your pending purchase has expired. Please try again.");
            return true;
        }

        if (!plugin.getHandler().hasBlockSelected(ply))
        {
            ply.sendMessage(ChatColor.RED + "You don't have a spawner selected!");
            plugin.removePendingPurchase(playerName);
            return true;
        }

        Block block = plugin.getHandler().getSelectedBlock(ply);
        BlockState rawState = block.getState();

        if (!(rawState instanceof CreatureSpawner))
        {
            ply.sendMessage(ChatColor.RED + "You must be looking at a spawner!");
            plugin.removePendingPurchase(playerName);
            return true;
        }

        // Re-check if player can still afford it
        if (!plugin.canAfford(ply, pending.price))
        {
            String priceStr = (pending.price == 0D) ? "FREE" : "$" + pending.price;
            ply.sendMessage(ChatColor.RED + "You can no longer afford this! It costs " + priceStr + ", but you only have $" + plugin.getBalance(ply) + ".");
            plugin.removePendingPurchase(playerName);
            return true;
        }

        CreatureType newCreature = CreatureType.fromName(pending.creatureName);

        if (newCreature == null)
        {
            ply.sendMessage(ChatColor.RED + "Invalid creature type: " + pending.creatureName);
            plugin.removePendingPurchase(playerName);
            return true;
        }

        // Complete the purchase
        ((CreatureSpawner)rawState).setCreatureType(newCreature);
        plugin.takeMoney(ply, pending.price);
        String priceStr = (pending.price == 0D) ? "FREE" : "$" + pending.price;
        ply.sendMessage(ChatColor.GREEN + "The spawner type has been set! Cost: " + priceStr);
        plugin.getHandler().deselectBlock(ply);
        plugin.removePendingPurchase(playerName);

        return true;
    }
}

