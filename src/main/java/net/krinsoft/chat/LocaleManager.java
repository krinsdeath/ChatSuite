package net.krinsoft.chat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.krinsoft.chat.util.ChatConfiguration;
import org.bukkit.util.config.Configuration;

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
        LOCALE = plugin.getConfiguration().getString("plugin.default_locale", "en");
        List<String> list = plugin.getConfiguration().getStringList("plugin.locales", new ArrayList<String>());
        File langDir = new File(plugin.getDataFolder() + File.separator + "languages");
        if (!langDir.exists()) langDir.mkdirs();
        for (String key : list) {
            File t = ChatConfiguration.buildDefault(langDir, key + ".yml");
            Configuration c = new Configuration(t);
            c.load();
            locales.put(key, c);
            plugin.debug("Localization '" + key + "' added.");
        }
    }

    public Object getAfk(String loc, String key) {
        return locales.get(loc).getProperty("afk." + key);
    }

    public Object getHelp(String loc, String key) {
        return locales.get(loc).getProperty("help." + key);
    }

}
