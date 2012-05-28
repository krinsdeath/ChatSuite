package net.krinsoft.chat;

import net.krinsoft.chat.api.Manager;
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
        saveConfig();
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
        if (!persist) { return; }
        try {
            for (ChatPlayer p : players.values()) {
                p.persist();
            }
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
            registerPlayer(p);
        }
    }

    /**
     * Gets the ChatPlayer instance for the player specified
     * @param p The player we're fetching.
     * @return The player's ChatPlayer instance if it already exists, or creates a new instance for them
     */
    public ChatPlayer getPlayer(Player p) {
        if (!isPlayerRegistered(p)) {
            registerPlayer(p);
        }
        ChatPlayer player = players.get(p.getName());
        player.getGroup();
        return player;
    }

    /**
     * Checks whether the specified player is registered with ChatSuite or not
     * @param p The player we're checking
     * @return true if the player is registered in ChatSuite, otherwise false
     */
    public boolean isPlayerRegistered(Player p) {
        return (players.get(p.getName()) != null);
    }

    public void registerPlayer(Player player) {
        if (players.containsKey(player.getName())) {
            return;
        }
        players.put(player.getName(), new ChatPlayer(this, player));
        plugin.getChannelManager().getGlobalChannel().join(player);
        plugin.debug("Player '" + player.getName() + "' registered");
    }

    public void unregisterPlayer(Player player) {
        if (!players.containsKey(player.getName())) {
            return;
        }
        plugin.getChannelManager().removePlayerFromAllChannels(player);
        players.remove(player.getName());
        plugin.debug("Player '" + player.getName() + "' unregistered");
    }

}
