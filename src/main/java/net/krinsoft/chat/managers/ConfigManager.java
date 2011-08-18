package net.krinsoft.chat.managers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author krinsdeath
 */
public final class ConfigManager {
    private ChatCore plugin;
    private Configuration config;

    public ConfigManager(ChatCore plugin) {
        this.plugin = plugin;
        config = new Configuration(buildDefault(plugin.getDataFolder(), "config.yml"));
        config.load();
    }

    public File buildDefault(File parent, String filename) {
        File file = new File(parent, filename);
        if (!file.exists()) {
            InputStream in = ChatCore.class.getResourceAsStream("/defaults/" + file.getName());
            FileOutputStream out = null;
            if (in != null) {
                try {
                    out = new FileOutputStream(file);
                    byte[] buf = new byte[5];
                    int len = 0;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    plugin.log("File " + file.getName() + " created successfully.");
                } catch (IOException e) {
                    plugin.warn("Error opening stream: " + e.getLocalizedMessage());
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        plugin.warn("Error closing stream: " + e.getLocalizedMessage());
                    }
                }

            }
        }
        return file;
    }

    /**
     * Returns the "plugins" node from the config.yml
     * @return
     * the node
     */
    public ConfigurationNode getPluginNode() {
        return this.config.getNode("plugin");
    }

    /**
     * Returns a specific group node from the "groups" key in config.yml
     * @param group
     * the group to fetch
     * @return
     * the node
     */
    public ConfigurationNode getGroupNode(String group) {
        return this.config.getNode("groups." + group);
    }

    /**
     * Gets a list of all groups listed in config.yml
     * @return
     * the list of groups
     */
    public List<String> getGroups() {
        return this.config.getKeys("groups");
    }

    /**
     * Gets a specific world node from "worlds" key in config.yml
     * @param world
     * @return
     */
    public ConfigurationNode getWorldNode(String world) {
        return this.config.getNode("worlds." + world);
    }

    public void newWorld(String world) {
        if (this.config.getNode("worlds." + world) != null) {
            this.config.setProperty("worlds." + world + ".channel", world);
            this.config.setProperty("worlds." + world + ".chat_allowed", true);
            this.config.setProperty("worlds." + world + ".whisper_allowed", true);
            this.config.setProperty("worlds." + world + ".flood_limit", "5:5");
            this.config.setProperty("worlds." + world + ".flood_punish", "mute:5s");
            this.config.save();
        }
    }
}
