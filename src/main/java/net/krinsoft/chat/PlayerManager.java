package net.krinsoft.chat;

import net.krinsoft.chat.api.Manager;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;

/**
 *
 * @author krinsdeath
 */
public class PlayerManager implements Manager {
    private ChatCore plugin;

    private HashMap<String, ChatPlayer> players = new HashMap<String, ChatPlayer>();
    private FileConfiguration configuration;
    private File config;
    private boolean persist = false;

    public PlayerManager(ChatCore instance) {
        clean();
        plugin = instance;
        persist = plugin.getConfig().getBoolean("plugin.persist_user_settings");
        config = new File(plugin.getDataFolder(), "players.yml");
        if (!config.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(plugin.getClass().getResourceAsStream("/defaults/players.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        saveConfig();
        buildPlayerList();
    }

    public void clean() {
        if (persist) {
            for (ChatPlayer player : players.values()) {
                player.persist();
            }
        }
        players.clear();
    }

    @Override
    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(config);
            configuration.setDefaults(YamlConfiguration.loadConfiguration(config));
        }
        return configuration;
    }

    @Override
    public void saveConfig() {
        try {
            getConfig().save(config);
        } catch (Exception e) {
            plugin.warn("An error occurred while saving 'players.yml'");
        }
    }

    @Override
    public ChatCore getPlugin() {
        return plugin;
    }

    private void buildPlayerList() {
        for (Player p : plugin.getServer().getOnlinePlayers()) {
            registerPlayer(p.getName());
        }
    }

    /**
     * Gets the ChatPlayer instance for the player specified
     * @param p The player we're fetching.
     * @return The player's ChatPlayer instance if it already exists, or creates a new instance for them
     */
    public ChatPlayer getPlayer(String p) {
        if (!isPlayerRegistered(p)) {
            registerPlayer(p);
        }
        ChatPlayer player = players.get(p);
        player.getGroup();
        return player;
    }

    /**
     * Checks whether the specified player is registered with ChatSuite or not
     * @param p The player we're checking
     * @return true if the player is registered in ChatSuite, otherwise false
     */
    public boolean isPlayerRegistered(String p) {
        return (players.get(p) != null);
    }

    public void registerPlayer(String player) {
        if (players.containsKey(player)) {
            return;
        }
        ChatPlayer cplayer = new ChatPlayer(this, plugin.getServer().getPlayer(player));
        players.put(player, cplayer);
        plugin.getChannelManager().getGlobalChannel().join(plugin.getServer().getPlayer(player));
        for (String channel : cplayer.getAutoJoinChannels()) {
            Channel chan = plugin.getChannelManager().getChannel(channel);
            if (chan != null && !chan.getOccupants().contains(cplayer.getPlayer())) {
                chan.join(cplayer.getPlayer());
            }
        }
        plugin.getTarget(getConfig().getString(cplayer.getName() + ".target", "c:" + plugin.getChannelManager().getDefaultChannel()));
        plugin.debug("Player '" + player + "' registered");
    }

    public void unregisterPlayer(Player player) {
        if (!players.containsKey(player.getName())) {
            return;
        }
        plugin.getChannelManager().removePlayerFromAllChannels(player);
        players.get(player.getName()).persist();
        players.remove(player.getName());
        plugin.debug("Player '" + player.getName() + "' unregistered");
    }

}
