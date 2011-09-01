package net.krinsoft.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        plugin.getConfigManager().getPluginNode().getBoolean("allow_channels", true);
    }

    public void addPlayerToChannel(Player player, String channel) {
        Channel chan = channels.get(channel.toLowerCase());
        if (chan == null) {
            chan = new Channel(plugin, channel, "public");
            plugin.debug("Channel '" + channel + "' created");
        }
        if (!chan.contains(player.getName())) {
            chan.addPlayer(player.getName());
            plugin.debug(player.getName() + " added to channel '" + channel + "'");
        }
        channels.put(channel.toLowerCase(), chan);
    }

    public void removePlayerFromChannel(Player player, String channel) {
        // get the specified channel
        Channel chan = channels.get(channel.toLowerCase());
        if (chan == null) {
            // no channel by that name existed, so we do nothing
            return;
        } else {
            // remove the player from the channel
            if (chan.contains(player.getName()) && listChannels(player.getName()).size() > 1) {
                chan.removePlayer(player.getName());
                plugin.debug(player.getName() + " removed from channel '" + channel + "'");
                if (chan.getOccupants().size() < 1) {
                    // channel is empty! let's get rid of it
                    channels.remove(channel.toLowerCase());
                    plugin.debug("Channel '" + channel + "' removed.");
                } else {
                    channels.put(channel.toLowerCase(), chan);
                }
            }
        }
    }

    public void playerWorldChange(Player player, String from, String to) {
        if (plugin.getPlayerManager().isPlayerRegistered(player)) {
            removePlayerFromChannel(player, from);
            addPlayerToChannel(player, to);
            if (plugin.getPlayerManager().getPlayer(player).getChannel().equals(from)) {
                plugin.getPlayerManager().getPlayer(player).setChannel(to);
            }
            plugin.getPlayerManager().getPlayer(player).setWorld(plugin.getWorldManager().getAlias(to));
        }
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
        return channels.get(channel.toLowerCase());
    }

    public Channel getGlobalChannel() {
        return channels.get(plugin.getConfiguration().getString("plugin.global_channel_name", "Global").toLowerCase());
    }

    public String getDefaultChannel() {
        return plugin.getConfiguration().getString("plugin.default_channel", "world").toLowerCase();
    }

    public void createChannel(String channel, String player, String type) {
        Channel chan = new Channel(plugin, channel, type);
        chan.addPlayer(player);
        channels.put(channel.toLowerCase(), chan);
    }

    public List<String> listChannels(String player) {
        List<String> list = new ArrayList<String>();
        for (String key : channels.keySet()) {
            if (channels.get(key).contains(player)) {
                list.add(key);
            }
        }
        return list;
    }
}
