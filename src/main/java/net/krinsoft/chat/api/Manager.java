package net.krinsoft.chat.api;

import net.krinsoft.chat.ChatCore;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * @author krinsdeath
 */
public interface Manager {

    public FileConfiguration getConfig();

    public void saveConfig();

    public ChatCore getPlugin();

}
