/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author Krin
 */
public final class ConfigManager {
    private ChatCore plugin;
    private Configuration config;

    public ConfigManager(ChatCore aThis) {
        this.plugin = aThis;
        this.config = new Configuration(buildDefault(this.plugin.getDataFolder(), "config.yml"));
        this.config.load();
        this.plugin.debug = this.config.getBoolean("plugin.debug", false);
        this.plugin.allow_channels = this.config.getBoolean("plugin.allow_channels", true);
        this.plugin.allow_whispers = this.config.getBoolean("plugin.allow_whispers", true);
        this.plugin.allow_afk = this.config.getBoolean("plugin.allow_afk", true);
        this.config.save();
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

    public ConfigurationNode getPluginNode() {
        return this.config.getNode("plugin");
    }

    public ConfigurationNode getGroupNode(String key) {
        return this.config.getNode("groups." + key);
    }

    public List<String> getGroups() {
        return this.config.getKeys("groups");
    }

    private void registerGroupNodes() {
        int priority = Integer.MAX_VALUE;
        String def = "";
        for (String key : getGroups()) {
            if (getGroupNode(key).getInt("weight", 0) < priority) {
                priority = getGroupNode(key).getInt("weight", 0);
                def = key;
            }
            Permission perm = new Permission("chatsuite.groups." + key);
            perm.setDefault(PermissionDefault.OP);
            if (plugin.getServer().getPluginManager().getPermission("chatsuite.groups." + key) == null) {
                plugin.getServer().getPluginManager().addPermission(perm);
            }
        }
        plugin.getServer().getPluginManager().getPermission("chatsuite.groups." + def).setDefault(PermissionDefault.TRUE);
    }

}
