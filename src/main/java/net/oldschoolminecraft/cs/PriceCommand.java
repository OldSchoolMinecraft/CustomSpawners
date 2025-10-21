package net.oldschoolminecraft.cs;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.CreatureType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PriceCommand implements CommandExecutor
{
    private CustomSpawners plugin;

    // List of all possible mob types
    private static final String[] MOB_TYPES = {
        "pig", "sheep", "cow", "chicken", "squid", "wolf", "spider",
        "pigzombie", "zombie", "skeleton", "creeper", "slime", "ghast",
        "monster", "giant"
    };

    public PriceCommand(CustomSpawners plugin)
    {
        this.plugin = plugin;
    }

    private String capitalCase(String input)
    {
        if (input == null || input.isEmpty())
        {
            return input;
        }

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
        // If an argument is provided
        if (args.length > 0)
        {
            // Check if it's a help command
            if (args[0].equalsIgnoreCase("help"))
            {
                sender.sendMessage(ChatColor.GOLD + "=== Spawner Commands ===");
                sender.sendMessage(ChatColor.YELLOW + "/sp" + ChatColor.WHITE + " - View all spawner prices");
                sender.sendMessage(ChatColor.YELLOW + "/sp <mob>" + ChatColor.WHITE + " - Search for a specific mob price");
                sender.sendMessage(ChatColor.YELLOW + "/sp confirm" + ChatColor.WHITE + " - Confirm a pending spawner purchase");
                sender.sendMessage(ChatColor.YELLOW + "/setspawnermob <mob>" + ChatColor.WHITE + " - Set a spawner's mob type");
                return true;
            }

            // Check if it's a confirm command
            if (args[0].equalsIgnoreCase("confirm"))
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

            // Otherwise, it's a search for mob prices
            String searchTerm = args[0].toLowerCase();
            boolean foundAny = false;

            String searchHeader = plugin.getMessageConfig().getMessage("search_header", "&6=== Spawner Prices (Search: %search%) ===");
            searchHeader = searchHeader.replace("%search%", searchTerm);
            sender.sendMessage(searchHeader);

            for (String mobType : MOB_TYPES)
            {
                if (mobType.contains(searchTerm))
                {
                    displayMobPrice(sender, mobType);
                    foundAny = true;
                }
            }

            if (!foundAny)
            {
                String noResults = plugin.getMessageConfig().getMessage("no_results", "&cNo spawner types found matching '%search%'");
                noResults = noResults.replace("%search%", searchTerm);
                sender.sendMessage(noResults);
            }
        }
        else
        {
            // Display all enabled spawners
            String header = plugin.getMessageConfig().getMessage("header", "&6=== Spawner Prices ===");
            sender.sendMessage(header);

            // Create a list to sort by price
            List<MobPrice> mobPrices = new ArrayList<MobPrice>();

            for (String mobType : MOB_TYPES)
            {
                String keyBase = "mobs." + mobType.toLowerCase();
                String keyEnabled = keyBase + ".enabled";
                String keyPrice = keyBase + ".price";

                boolean enabled = plugin.getConfig().getBoolean(keyEnabled, false);

                if (enabled)
                {
                    double price = plugin.getConfig().getDouble(keyPrice, -1D);
                    mobPrices.add(new MobPrice(mobType, price));
                }
            }

            // Sort by price
            Collections.sort(mobPrices, new Comparator<MobPrice>()
            {
                public int compare(MobPrice a, MobPrice b)
                {
                    return Double.compare(a.price, b.price);
                }
            });

            // Display sorted list
            String priceFormat = plugin.getMessageConfig().getMessage("price_format", "&e%mob% Spawner: %price%");

            for (MobPrice mp : mobPrices)
            {
                String displayName = capitalCase(mp.mobType);
                String priceStr = "$" + mp.price;
                String message = priceFormat.replace("%mob%", displayName).replace("%price%", priceStr);
                sender.sendMessage(message);
            }
        }

        return true;
    }

    private void displayMobPrice(CommandSender sender, String mobType)
    {
        String keyBase = "mobs." + mobType.toLowerCase();
        String keyEnabled = keyBase + ".enabled";
        String keyPrice = keyBase + ".price";

        boolean enabled = plugin.getConfig().getBoolean(keyEnabled, false);
        double price = plugin.getConfig().getDouble(keyPrice, -1D);

        String displayName = capitalCase(mobType);

        String priceFormat = plugin.getMessageConfig().getMessage("price_format", "&e%mob% Spawner: %price%");
        String disabledText = plugin.getMessageConfig().getMessage("disabled_text", "&cDISABLED");

        String message;
        if (!enabled)
        {
            message = priceFormat.replace("%mob%", displayName).replace("%price%", disabledText);
            sender.sendMessage(message);
        }
        else if (price < 0D)
        {
            // Skip misconfigured spawners
            return;
        }
        else
        {
            String priceStr = "$" + price;
            message = priceFormat.replace("%mob%", displayName).replace("%price%", priceStr);
            sender.sendMessage(message);
        }
    }

    // Helper class to store mob type and price together
    private static class MobPrice
    {
        String mobType;
        double price;

        MobPrice(String mobType, double price)
        {
            this.mobType = mobType;
            this.price = price;
        }
    }
}
