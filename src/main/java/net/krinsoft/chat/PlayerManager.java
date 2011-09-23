package net.krinsoft.chat;

import java.io.File;
import net.krinsoft.chat.targets.ChatPlayer;
import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author krinsdeath
 */
public class PlayerManager {
    private ChatCore plugin;

    private HashMap<String, ChatPlayer> players = new HashMap<String, ChatPlayer>();
    private Configuration user;
    private boolean persist = false;

    public PlayerManager(ChatCore plugin) {
        clear();
        this.plugin = plugin;
        ChatPlayer.init(plugin);
        persist = this.plugin.getConfigManager().getPluginNode().getBoolean("persist_user_settings", false);
        user = new Configuration(new File(plugin.getDataFolder(), "users.yml"));
        user.load();
        buildPlayerList();
    }

    private void clear() {
        players.clear();
    }

    private void buildPlayerList() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            registerPlayer(p);
        }
    }

    /**
     * Gets the ChatPlayer instance for the player specified
     * @param p
     * the player to fetch
     * @return
     * the player's ChatPlayer instance, or null
     */
    public ChatPlayer getPlayer(Player p) {
        if (players.get(p.getName()) == null) {
            registerPlayer(p);
        }
        groupUpdate(p);
        return players.get(p.getName());
    }

    /**
     * Update the player's group.
     * @param p
     */
    public void groupUpdate(Player p) {
        ConfigManager config = plugin.getConfigManager();
        String group = "";
        int weight = 0;
        for (String key : config.getGroups()) {
            int i = config.getGroupNode(key).getInt("weight", 1);
            if ((p.hasPermission("chatsuite.groups." + key) || p.hasPermission("group." + key)) && i > weight) {
                weight = i;
                group = key;
            }
        }
        players.get(p.getName()).setGroup((group == null ? "default" : group));
    }

    /**
     * Gets the ChatPlayer instance for the player specified
     * @param p
     * the player to fetch
     * @return
     * the player's ChatPlayer instance, or null
     */
    public ChatPlayer getPlayer(String p) {
        if (players.get(p) == null) {
            registerPlayer(plugin.getServer().getPlayer(p));
        }
        return players.get(p);
    }

    public boolean isPlayerRegistered(Player p) {
        return (players.get(p.getName()) != null);
    }

    public void registerPlayer(Player player) {
        if (players.containsKey(player.getName())) {
            return;
        }
        players.put(player.getName(), new ChatPlayer(player, getLocale(player.getName())));
        plugin.getChannelManager().addPlayerToChannel(player, player.getWorld().getName());
        plugin.getChannelManager().addPlayerToChannel(player, plugin.getConfigManager().getPluginNode().getString("global_channel_name", "Global"));
        plugin.debug("Player '" + player.getName() + "' registered");
        if (persist) {
            user.save();
        }
    }

    public void unregisterPlayer(Player player) {
        if (!players.containsKey(player.getName())) {
            return;
        }
        plugin.getChannelManager().removePlayerFromAllChannels(player);
        players.remove(player.getName());
        plugin.debug("Player '" + player.getName() + "' unregistered");
        if (persist) {
            user.save();
        }
    }

    public String getLocale(String player) {
        return user.getString(player + ".locale", plugin.getLocaleManager().getLocaleKey());
    }

}
