package net.krinsoft.chat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.Configuration;

/**
 *
 * @author krinsdeath
 */
public class LocaleManager {
    private ChatCore plugin;
    private HashMap<String, Configuration> locales = new HashMap<String, Configuration>();
    private final String LOCALE;

    LocaleManager(ChatCore aThis) {
        plugin = aThis;
        ConfigManager config = plugin.getConfigManager();
        LOCALE = config.getPluginNode().getString("default_locale", "en");
        List<String> list = config.getPluginNode().getStringList("locales");
        plugin.debug("Locales: " + list.toString());
        File langDir = new File(plugin.getDataFolder() + File.separator + "languages");
        if (!langDir.exists()) langDir.mkdirs();
        for (String key : list) {
            File l = config.buildDefault(langDir, key + ".yml");
            FileConfiguration c = YamlConfiguration.loadConfiguration(l);
            c.setDefaults(YamlConfiguration.loadConfiguration(ChatCore.class.getResourceAsStream("/defaults/" + key + ".yml")));
            c.options().copyDefaults(true);
            locales.put(key, c);
            plugin.debug("Localization '" + key + "' added.");
        }
    }

    public String getLocaleKey() {
        return LOCALE;
    }

    public Object getAfk(String loc, String key) {
        String l = verifyLocale(loc);
        return locales.get(l).get("afk." + key);
    }

    public Object getHelp(String loc, String key) {
        String l = verifyLocale(loc);
        return locales.get(l).get("help." + key);
    }

    public Object getMessage(String loc, String key) {
        String l = verifyLocale(loc);
        return locales.get(l).get("messages." + key);
    }

    public Object getError(String loc, String key) {
        String l = verifyLocale(loc);
        return locales.get(l).get("error." + key);
    }

    private String verifyLocale(String loc) {
        String l = loc;
        if (locales.get(l) == null) {
            l = LOCALE;
        }
        return l;
    }

}
