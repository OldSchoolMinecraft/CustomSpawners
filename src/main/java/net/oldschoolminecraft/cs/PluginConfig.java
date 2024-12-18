package net.oldschoolminecraft.cs;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class PluginConfig extends Configuration
{
    public PluginConfig(File file)
    {
        super(file);
        reload();
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    public void write()
    {
        generateConfigOption("mobs.chicken.price", 7000D);
        generateConfigOption("mobs.chicken.enabled", true);

        generateConfigOption("mobs.cow.price", 4500D);
        generateConfigOption("mobs.cow.enabled", true);

        generateConfigOption("mobs.creeper.price", 15000D);
        generateConfigOption("mobs.creeper.enabled", true);

        generateConfigOption("mobs.ghast.price", 0D);
        generateConfigOption("mobs.ghast.enabled", false);

        generateConfigOption("mobs.giant.price", 0D);
        generateConfigOption("mobs.giant.enabled", false);

        generateConfigOption("mobs.monster.price", 69420D);
        generateConfigOption("mobs.monster.enabled", true);

        generateConfigOption("mobs.pig.price", 5000D);
        generateConfigOption("mobs.pig.enabled", true);

        generateConfigOption("mobs.pigzombie.price", 15000D);
        generateConfigOption("mobs.pigzombie.enabled", true);

        generateConfigOption("mobs.sheep.price", 8000D);
        generateConfigOption("mobs.sheep.enabled", true);

        generateConfigOption("mobs.skeleton.price", 10000D);
        generateConfigOption("mobs.skeleton.enabled", true);

        generateConfigOption("mobs.slime.price", 7500D);
        generateConfigOption("mobs.slime.enabled", true);

        generateConfigOption("mobs.spider.price", 6000D);
        generateConfigOption("mobs.spider.enabled", true);

        generateConfigOption("mobs.squid.price", 4000D);
        generateConfigOption("mobs.squid.enabled", true);

        generateConfigOption("mobs.wolf.price", 0D);
        generateConfigOption("mobs.wolf.enabled", false);

        generateConfigOption("mobs.zombie.price", 6000D);
        generateConfigOption("mobs.zombie.enabled", true);
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public Object getConfigOption(String key)
    {
        return this.getProperty(key);
    }

    public Object getConfigOption(String key, Object defaultValue)
    {
        Object value = getConfigOption(key);
        if (value == null) value = defaultValue;
        return value;
    }
}
