package net.oldschoolminecraft.cs;

import com.earth2me.essentials.Essentials;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import static org.bukkit.Bukkit.getLogger;

public class CustomSpawners extends JavaPlugin
{
    private final PlayerHandler handler = new PlayerHandler();
    private Essentials essentials;
    private PluginConfig config;

    public void onEnable()
    {
        essentials = (Essentials) getServer().getPluginManager().getPlugin("Essentials");
        config = new PluginConfig(new File(getDataFolder(), "config.yml"));

        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_INTERACT, handler, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvent(Event.Type.PLAYER_MOVE, handler, Event.Priority.Normal, this);
        getCommand("setspawnermob").setExecutor(new SpawnerCommand(this));
        getLogger().info("CustomSpawners enabled");
    }

    public PlayerHandler getHandler()
    {
        return handler;
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

    public void onDisable()
    {
        getLogger().info("CustomSpawners disabled");
    }
}
