package net.krinsoft.chat;

import net.krinsoft.chat.targets.ChatPlayer;
import java.util.HashMap;
import org.bukkit.entity.Player;

/**
 *
 * @author krinsdeath
 */
public class PlayerManager {
    private ChatCore plugin;

    private HashMap<String, ChatPlayer> players = new HashMap<String, ChatPlayer>();

    public PlayerManager(ChatCore plugin) {
        this.plugin = plugin;
        ChatPlayer.init(plugin);
        buildPlayerList();
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
        return players.get(p.getName());
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
        players.put(player.getName(), new ChatPlayer(player));
        plugin.getChannelManager().addPlayerToChannel(player, player.getWorld().getName());
        plugin.getChannelManager().addPlayerToChannel(player, plugin.getConfigManager().getPluginNode().getString("global_channel_name", "Global"));
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
