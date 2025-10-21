package net.oldschoolminecraft.cs;

import org.bukkit.util.config.Configuration;

import java.io.File;

public class MessageConfig extends Configuration
{
    public MessageConfig(File file)
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
        // Messages with color codes
        generateConfigOption("messages.header", "&6=== Spawner Prices ===");
        generateConfigOption("messages.search_header", "&6=== Spawner Prices (Search: %search%) ===");
        generateConfigOption("messages.no_results", "&cNo spawner types found matching '%search%'");
        generateConfigOption("messages.price_format", "&e%mob% Spawner: %price%");
        generateConfigOption("messages.disabled_text", "&cDISABLED");
    }

    private void generateConfigOption(String key, Object defaultValue)
    {
        if (this.getProperty(key) == null) this.setProperty(key, defaultValue);
        final Object value = this.getProperty(key);
        this.removeProperty(key);
        this.setProperty(key, value);
    }

    public String getMessage(String key, String defaultValue)
    {
        Object value = this.getProperty("messages." + key);
        if (value == null) return defaultValue;
        String message = value.toString();
        // Convert & color codes to § (section symbol)
        return message.replace('&', '\u00A7');
    }
}
