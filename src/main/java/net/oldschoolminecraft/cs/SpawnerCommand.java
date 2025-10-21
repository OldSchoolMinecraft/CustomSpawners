package net.oldschoolminecraft.cs;

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

public class SpawnerCommand implements CommandExecutor
{
    private CustomSpawners plugin;

    public SpawnerCommand(CustomSpawners plugin)
    {
        this.plugin = plugin;
    }

    public static class PendingPurchase
    {
        public String creatureName;
        public double price;
        public long timestamp;

        public PendingPurchase(String creatureName, double price)
        {
            this.creatureName = creatureName;
            this.price = price;
            this.timestamp = System.currentTimeMillis();
        }

        public boolean isExpired()
        {
            return System.currentTimeMillis() - timestamp > 30000; // 30 seconds
        }
    }

    private String capitalCase(String input)
    {
        if (input == null || input.isEmpty())
        {
            return input;
        }

        // Handle underscores for mob types like IRON_GOLEM
        String[] words = input.toLowerCase().split("_");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < words.length; i++)
        {
            String word = words[i];
            if (word.length() > 0)
            {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1)
                {
                    result.append(word.substring(1));
                }
                if (i < words.length - 1)
                {
                    result.append(" ");
                }
            }
        }

        return result.toString();
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

        if (args.length < 1)
        {
            sender.sendMessage(ChatColor.RED + "Usage: /setspawnermob <mob>");
            return true;
        }

        String creatureName = args[0];
        creatureName = creatureName.equalsIgnoreCase("PigZombie") ? "PigZombie" : capitalCase(creatureName);

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

            if (newCreature == null)
            {
                ply.sendMessage(ChatColor.RED + "Invalid creature type: " + creatureName);
                return true;
            }

            // Store pending purchase and ask for confirmation
            String playerName = ply.getName();
            plugin.setPendingPurchase(playerName, new PendingPurchase(creatureName, price));
            ply.sendMessage(ChatColor.YELLOW + "Are you sure you want to set this spawner to " + creatureName + " for " + priceStr + "?");
            ply.sendMessage(ChatColor.YELLOW + "Type /sp confirm within 30 seconds to complete the purchase.");
        }

        return true;
    }
}
