package net.krinsoft.chat.managers;

import java.util.HashMap;
import java.util.Set;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public class ChannelManager {
    private ChatCore plugin;

    private HashMap<String, Channel> channels = new HashMap<String, Channel>();

    public ChannelManager(ChatCore plugin) {
        this.plugin = plugin;
    }

    public void addPlayerToChannel(String channel, String player) {
        Channel c = channels.get(channel);
        if (c == null) {
            c = new Channel(plugin, channel, player);
            plugin.debug("Channel '" + channel + "' created. (Initial occupants: " + c.getOccupants().toString() + ")");
        } else {
            c.addPlayer(player);
        }
        channels.put(channel, c);
    }

    public void removePlayerFromChannel(String channel, String player) {
        Channel c = channels.get(channel);
        if (c == null) {
            // channel doesn't exist, so we return
            return;
        } else {
            c.removePlayer(player);
            if (c.getOccupants().size() < 1) {
                channels.remove(channel);
                plugin.debug("Channel '" + channel + "' removed.");
            } else {
                channels.put(channel, c);
            }
        }
    }

    public void removePlayerFromAllChannels(String player) {
        Set<Channel> chanSet = (Set<Channel>) ((HashMap<String, Channel>)channels.clone()).values();
        for (Channel channel : chanSet) {
            removePlayerFromChannel(channel.getName(), player);
        }
    }

    public void playerWorldChange(String player, String from, String to) {
        if (plugin.getPlayerManager().isRegistered(player)) {
            if (!from.equalsIgnoreCase(to) || !plugin.getPlayerManager().getPlayer(player).getWorld().equalsIgnoreCase(to)) {
                plugin.debug("Player " + player + " moving from '" + from + "' to '" + to + "'.");
                addPlayerToChannel(to, player);
                removePlayerFromChannel(from, player);
                plugin.getPlayerManager().getPlayer(player).updateWorld(plugin.getWorldManager().getAlias(to));
            }
        }
    }
}
