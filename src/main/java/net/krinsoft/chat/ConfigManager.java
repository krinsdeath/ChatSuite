/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Krin
 */
public final class ConfigManager {
    private ChatCore plugin;
    private FileConfiguration config = null;

    public ConfigManager(ChatCore aThis) {
        this.plugin = aThis;
        // build the default configuration
        this.config = YamlConfiguration.loadConfiguration(new File(this.plugin.getDataFolder(), "config.yml"));
        Configuration defConf = YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/defaults/config.yml"));
        this.config.setDefaults(defConf);
        this.config.options().copyDefaults(true);
        try {
            this.config.save(new File(this.plugin.getDataFolder(), "config.yml"));
        } catch (IOException ex) {
            plugin.debug("Error saving default configuration");
        }
        
        this.plugin.debug = this.config.getBoolean("plugin.debug", false);
        this.plugin.chatLog = this.config.getBoolean("plugin.logger", false);
        this.plugin.allow_channels = this.config.getBoolean("plugin.allow_channels", true);
        this.plugin.allow_whispers = this.config.getBoolean("plugin.allow_whispers", true);
        this.plugin.allow_afk = this.config.getBoolean("plugin.allow_afk", true);
        registerGroupNodes();
    }

    public File buildDefault(File p, String filename) {
        plugin.debug("Building file references..");
        File file = new File(p, filename);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            plugin.debug("Fetching resource... " + file.getName());
            InputStream in = ChatCore.class.getResourceAsStream("/defaults/" + file.getName());
            if (in != null) {
                plugin.debug("Resource exists as: " + file.getName());
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    byte[] buf = new byte[15];
                    int len = 0;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    plugin.log(file.getName() + " created successfully.");
                } catch (IOException e) {
                    plugin.debug(e.getLocalizedMessage());
                } finally {
                    try {
                        in.close();
                        out.close();
                    } catch (IOException e) {
                        plugin.debug(e.getLocalizedMessage());
                    }
                }
            }
        }
        return file;
    }

    public ConfigurationSection getPluginNode() {
        return this.config.getConfigurationSection("plugin");
    }

    public ConfigurationSection getGroupNode(String key) {
        return this.config.getConfigurationSection("groups." + key);
    }

    public Set<String> getGroups() {
        return this.config.getConfigurationSection("groups").getKeys(false);
    }

    private void registerGroupNodes() {
        int defs = Integer.MAX_VALUE;
        int ops = 0;
        String def = "", op = "";
        for (String key : getGroups()) {
            if (getGroupNode(key).getInt("weight", 0) < defs) {
                defs = getGroupNode(key).getInt("weight", 0);
                def = key;
            }
            if (getGroupNode(key).getInt("weight", 0) > ops) {
                ops = getGroupNode(key).getInt("weight", 0);
                op = key;
            }
            Permission perm = new Permission("chatsuite.groups." + key);
            perm.setDefault(PermissionDefault.FALSE);
            if (plugin.getServer().getPluginManager().getPermission("chatsuite.groups." + key) == null) {
                plugin.getServer().getPluginManager().addPermission(perm);
            }
        }
        plugin.getServer().getPluginManager().getPermission("chatsuite.groups." + def).setDefault(PermissionDefault.TRUE);
        plugin.getServer().getPluginManager().getPermission("chatsuite.groups." + op).setDefault(PermissionDefault.OP);
    }

}
