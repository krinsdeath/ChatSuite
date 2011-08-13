package net.krinsoft.chat;

import java.util.HashMap;
import java.util.Set;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.entity.Player;

/**
 *
 * @author krinsdeath
 */
public class ChannelManager {
    private ChatCore plugin;
    private HashMap<String, Channel> channels = new HashMap<String, Channel>();

    public ChannelManager(ChatCore aThis) {
        plugin = aThis;
    }

    public void addPlayerToChannel(Player player, String channel) {
        if (plugin.getPlayer(player) != null) {
            Channel chan = channels.get(channel);
            if (chan == null) {
                chan = new Channel(plugin, channel);
                plugin.debug("Channel '" + channel + "' created");
            }
            if (!chan.contains(player.getName())) {
                chan.addPlayer(player.getName());
                plugin.debug(player.getName() + " added to channel '" + channel + "'");
            }
            channels.put(channel, chan);
        }
    }

    public void removePlayerFromChannel(Player player, String channel) {
        // get the specified channel
        Channel chan = channels.get(channel);
        if (chan == null) {
            // no channel by that name existed, so we do nothing
            return;
        } else {
            // remove the player from the channel
            if (chan.contains(player.getName())) {
                chan.removePlayer(player.getName());
                plugin.debug(player.getName() + " removed from channel '" + channel + "'");
                if (chan.getOccupants().size() < 1) {
                    // channel is empty! let's get rid of it
                    channels.remove(channel);
                    plugin.debug("Channel '" + channel + "' removed.");
                } else {
                    channels.put(channel, chan);
                }
            }
        }
    }

    public void playerWorldChange(Player player, String from, String to) {
        if (!plugin.isPlayerRegistered(player)) { return; }
        removePlayerFromChannel(player, from);
        addPlayerToChannel(player, to);
    }

    void removePlayerFromAllChannels(Player player) {
        Set<String> keys = ((HashMap<String, Channel>) channels.clone()).keySet();
        for (String c : keys) {
            if (channels.get(c).contains(player.getName())) {
                removePlayerFromChannel(player, c);
            }
        }
    }

    public Channel getChannel(String channel) {
        return channels.get(channel);
    }

    public Channel getGlobalChannel() {
        return channels.get(plugin.getConfiguration().getString("plugin.global_channel_name", "Global"));
    }

    public String getDefaultChannel() {
        return plugin.getConfiguration().getString("plugin.default_channel", "world");
    }

}
