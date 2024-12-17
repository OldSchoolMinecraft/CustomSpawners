package net.oldschoolminecraft.cs;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class PluginConfig extends Configuration
{
    public PluginConfig(File file)
    {
        super(file);
    }

    public void reload()
    {
        load();
        write();
        save();
    }

    public void write()
    {
        generateConfigOption("mobs.chicken.price", 0D);
        generateConfigOption("mobs.chicken.enabled", false);

        generateConfigOption("mobs.cow.price", 0D);
        generateConfigOption("mobs.cow.enabled", false);

        generateConfigOption("mobs.creeper.price", 0D);
        generateConfigOption("mobs.creeper.enabled", false);

        generateConfigOption("mobs.ghast.price", 0D);
        generateConfigOption("mobs.ghast.enabled", false);

        generateConfigOption("mobs.giant.price", 0D);
        generateConfigOption("mobs.giant.enabled", false);

        generateConfigOption("mobs.monster.price", 0D);
        generateConfigOption("mobs.monster.enabled", false);

        generateConfigOption("mobs.pig.price", 0D);
        generateConfigOption("mobs.pig.enabled", false);

        generateConfigOption("mobs.pigzombie.price", 0D);
        generateConfigOption("mobs.pigzombie.enabled", false);

        generateConfigOption("mobs.sheep.price", 0D);
        generateConfigOption("mobs.sheep.enabled", false);

        generateConfigOption("mobs.skeleton.price", 0D);
        generateConfigOption("mobs.skeleton.enabled", false);

        generateConfigOption("mobs.slime.price", 0D);
        generateConfigOption("mobs.slime.enabled", false);

        generateConfigOption("mobs.spider.price", 0D);
        generateConfigOption("mobs.spider.enabled", false);

        generateConfigOption("mobs.squid.price", 0D);
        generateConfigOption("mobs.squid.enabled", false);

        generateConfigOption("mobs.wolf.price", 0D);
        generateConfigOption("mobs.wolf.enabled", false);

        generateConfigOption("mobs.zombie.price", 0D);
        generateConfigOption("mobs.zombie.enabled", false);
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
