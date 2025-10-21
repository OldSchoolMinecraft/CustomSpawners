package net.oldschoolminecraft.cs;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class ColorConfig extends Configuration
{
    public ColorConfig(File file)
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
        generateConfigOption("header", "GOLD");
        generateConfigOption("mob_name", "YELLOW");
        generateConfigOption("price", "GREEN");
        generateConfigOption("free", "GREEN");
        generateConfigOption("disabled", "RED");
        generateConfigOption("error", "RED");
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public String getColor(String key, String defaultValue)
    {
        Object value = this.getProperty(key);
        if (value == null) return defaultValue;
        return value.toString();
    }
}

