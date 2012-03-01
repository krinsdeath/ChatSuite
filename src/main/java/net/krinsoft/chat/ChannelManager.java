package net.krinsoft.chat;

import net.krinsoft.chat.api.Manager;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.*;

/**
 *
 * @author krinsdeath
 */
public class ChannelManager implements Manager {
    private ChatCore plugin;
    private HashMap<String, Channel> channels = new HashMap<String, Channel>();

    private FileConfiguration configuration;
    private File config;

    public ChannelManager(ChatCore instance) {
        clean();
        plugin = instance;
        registerConfiguration();
        registerChannels();
    }

    public void clean() {
        channels.clear();
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
            plugin.warn("An error occurred while trying to save 'channels.yml'");
        }
    }

    @Override
    public ChatCore getPlugin() {
        return plugin;
    }

    public void registerConfiguration() {
        config = new File(plugin.getDataFolder(), "channels.yml");
        if (!config.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(plugin.getClass().getResourceAsStream("/defaults/channels.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void registerChannels() {
        Set<String> channels = getConfig().getConfigurationSection("channels").getKeys(false);
        for (String channel : channels) {
            createChannel(null, channel);
        }
        plugin.debug("Default Channel: " + getDefaultChannel());
    }

    public void log(String channel, String message) {
        plugin.log("[" + channel + "] " + message);
    }

    /**
     * Adds the specified player to the given channel
     * @param player The player to add to the channel
     * @param channel The name of the channel we're adding the player to
     * @return The handle of the channel the player was added to
     */
    public Channel addPlayerToChannel(Player player, String channel) {
        Channel chan = channels.get(channel.toLowerCase());
        if (chan == null) {
            chan = new Channel(this, channel, player);
            plugin.debug("Channel '" + channel + "' created");
        }
        if (!chan.contains(player)) {
            chan.join(player);
        }
        return channels.put(channel.toLowerCase(), chan);
    }

    /**
     * Removes the specified player from the given channel
     * @param player The player we're removing from the channel
     * @param channel The channel we're removing the player from
     * @return The handle of the channel the player was removed from
     */
    public Channel removePlayerFromChannel(Player player, String channel) {
        // get the specified channel
        Channel chan = channels.get(channel.toLowerCase());
        if (chan == null) {
            // no channel by that name existed, so we do nothing
            return null;
        } else {
            // remove the player from the channel
            if (chan.contains(player) && getPlayerChannelList(player).size() > 1) {
                chan.part(player);
                if (chan.getOccupants().size() < 1 && !chan.isPermanent()) {
                    // channel is empty! let's get rid of it
                    chan = channels.remove(channel.toLowerCase());
                    plugin.debug("Channel '" + channel + "' removed.");
                }
            }
            return chan;
        }
    }

    public void playerWorldChange(Player p, String from, String to) {
        if (plugin.getPlayerManager().isPlayerRegistered(p)) {
            ChatPlayer player = plugin.getPlayerManager().getPlayer(p);
            removePlayerFromChannel(p, from);
            Target target = addPlayerToChannel(p, to);
            if (player.getTarget().getName().equals(from)) {
                player.setTarget(target);
            }
            player.setWorld(plugin.getWorldManager().getAlias(to));
        }
    }

    void removePlayerFromAllChannels(Player player) {
        Set<String> keys = new HashSet<String>(channels.keySet());
        for (String c : keys) {
            if (channels.get(c).contains(player)) {
                removePlayerFromChannel(player, c);
            }
        }
    }

    public Channel getChannel(String channel) {
        return channels.get(channel.toLowerCase());
    }

    public Channel getGlobalChannel() {
        return channels.get(getDefaultChannel());
    }

    public String getDefaultChannel() {
        return getConfig().getString("default");
    }

    public Channel createChannel(Player player, String channel) {
        Channel chan = new Channel(this, channel, player);
        if (player != null) {
            chan.join(player);
        }
        channels.put(channel.toLowerCase(), chan);
        return channels.get(channel.toLowerCase());
    }

    public List<Channel> getChannels() {
        List<Channel> chans = new ArrayList<Channel>();
        for (Channel chan : channels.values()) {
            chans.add(chan);
        }
        return chans;
    }

    public List<Channel> getPlayerChannelList(Player player) {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel chan : channels.values()) {
            if (chan.contains(player)) {
                list.add(chan);
            }
        }
        return list;
    }

}
