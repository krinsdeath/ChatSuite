package net.krinsoft.chat;

import java.util.HashMap;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author krinsdeath
 */
public class LocaleManager {
    private ChatCore plugin;
    private HashMap<String, Configuration> locales = new HashMap<String, Configuration>();

    LocaleManager(ChatCore aThis) {
        plugin = aThis;
    }

    public Object getAfkLines(String loc, boolean flag) {
        if (flag) {
            return locales.get(loc).getProperty("afk.away");
        } else {
            return locales.get(loc).getProperty("afk.back");
        }
    }

    public String getAfkDefault(String loc) {
        return locales.get(loc).getString("afk.default");
    }
    
}
