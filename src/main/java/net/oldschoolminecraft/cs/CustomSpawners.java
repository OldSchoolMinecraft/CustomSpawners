package net.oldschoolminecraft.cs;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getLogger;

public class CustomSpawners extends JavaPlugin
{
    private final PlayerHandler handler = new PlayerHandler();
    private Essentials essentials;
    private PluginConfig config;
    private MessageConfig messageConfig;
    private UpdateManager updateManager;
    private Map<String, SpawnerCommand.PendingPurchase> pendingPurchases = new HashMap<String, SpawnerCommand.PendingPurchase>();

    public void onEnable()
    {
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        config = new PluginConfig(new File(getDataFolder(), "config.yml"));
        messageConfig = new MessageConfig(new File(getDataFolder(), "messages.yml"));

        updateManager = new UpdateManager(this, "https://micro.os-mc.net/plugin_ci/CustomSpawners/latest");

        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, handler, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, handler, Event.Priority.Normal, this);
        getCommand("setspawnermob").setExecutor(new SpawnerCommand(this));
        getCommand("spawnerprice").setExecutor(new PriceCommand(this));
        getLogger().info("CustomSpawners enabled");
    }

    public PlayerHandler getHandler()
    {
        return handler;
    }

    public File getPluginFile()
    {
        return getFile();
    }

    public boolean canAfford(Player ply, double required)
    {
        return essentials.getUser(ply).canAfford(required);
    }

    public void takeMoney(Player ply, double amount)
    {
        essentials.getUser(ply).takeMoney(amount);
    }

    public double getBalance(Player ply)
    {
        return essentials.getUser(ply).getMoney();
    }

    public static String capitalCase(String input)
    {
        if (input == null || input.isEmpty())
            return input;
        char firstChar = input.charAt(0);
        String restOfString = input.substring(1).toLowerCase();
        return firstChar + restOfString;
    }

    public PluginConfig getConfig()
    {
        return config;
    }

    public MessageConfig getMessageConfig()
    {
        return messageConfig;
    }

    public SpawnerCommand.PendingPurchase getPendingPurchase(String playerName)
    {
        return pendingPurchases.get(playerName);
    }

    public void setPendingPurchase(String playerName, SpawnerCommand.PendingPurchase purchase)
    {
        pendingPurchases.put(playerName, purchase);
    }

    public void removePendingPurchase(String playerName)
    {
        pendingPurchases.remove(playerName);
    }

    public void onDisable()
    {
        updateManager.checkForUpdates();
        getLogger().info("CustomSpawners disabled");
    }
}
