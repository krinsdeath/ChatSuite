package net.krinsoft.irc;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Manager;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author krinsdeath
 */
public class IRCBot implements Manager {
    private final static Pattern    NICK                    = Pattern.compile("\\[nick\\]");

    private ChatCore                plugin;
    private FileConfiguration       configuration;
    private File                    config;

    public final String             NICKNAME;
    public final String             IDENTITY;
    public final String             REALNAME;
    public final String             DEFAULT_NETWORK;

    // messages
    public final String             STARTUP;
    public final String             IRC_TAG;
    public final String             PLAYER_JOIN_IRC;
    public final String             PLAYER_JOIN_MINECRAFT;
    public final String             PLAYER_QUIT_IRC;
    public final String             PLAYER_QUIT_MINECRAFT;

    private Map<String, Connection> connections             = new HashMap<String, Connection>();

    private List<String>            online                  = new ArrayList<String>();

    public IRCBot(ChatCore instance) throws IOException {
        plugin = instance;

        registerConfiguration();

        // bot information
        NICKNAME                = getConfig().getString("nickname");
        IDENTITY                = getConfig().getString("ident");
        REALNAME                = getConfig().getString("realname");
        boolean DEV             = getConfig().getBoolean("developer.run");
        if ((NICKNAME.equals("ChatSuite") || IDENTITY.equals("ChatSuite") || (REALNAME.equals("ChatSuite"))) && !DEV) {
            throw new InvalidIRCBotException("Please customize your IRC settings in 'irc.yml'.");
        }

        DEFAULT_NETWORK         = getConfig().getString("default");

        // messages
        STARTUP                 = getConfig().getString("messages.startup", "ChatSuite IRC Bot Initialized.");
        IRC_TAG                 = getConfig().getString("messages.tag", "[IRC]");
        PLAYER_JOIN_IRC         = getConfig().getString("messages.irc.join", "[nick] is now on IRC.");
        PLAYER_QUIT_IRC         = getConfig().getString("messages.irc.quit", "[nick] has quit IRC.");
        PLAYER_JOIN_MINECRAFT   = getConfig().getString("messages.mc.join", "[nick] has logged in.");
        PLAYER_QUIT_MINECRAFT   = getConfig().getString("messages.mc.quit", "[nick] has logged out.");

        initializeConnections();
    }

    public void clean() {
        for (Connection conn : connections.values()) {
            conn.stop();
        }
    }

    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(config);
            configuration.setDefaults(YamlConfiguration.loadConfiguration(config));
        }
        return configuration;
    }

    public void saveConfig() {
        try {
            getConfig().save(config);
        } catch (Exception e) {
            plugin.warn("An error occurred while saving 'irc.yml'");
        }
    }

    public ChatCore getPlugin() {
        return plugin;
    }

    public void registerConfiguration() {
        config = new File(plugin.getDataFolder(), "irc.yml");
        if (!config.exists() || getConfig().getConfigurationSection("networks") == null) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(plugin.getClass().getResourceAsStream("/defaults/irc.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void initializeConnections() throws IOException {
        ConfigurationSection networks = getConfig().getConfigurationSection("networks");
        for (String network : networks.getKeys(false)) {
            String host     = networks.getString(   network + ".host");
            int port        = networks.getInt(      network + ".port");
            String channel  = networks.getString(   network + ".channel");
            Connection conn = new Connection(this, network, host, port, channel);
            connections.put(network, conn);
        }
    }

    public void msg(String network, String chan, String message) {
        if (network == null) {
            network = DEFAULT_NETWORK;
        }
        if (chan == null) {
            chan = getConfig().getString("networks." + network + ".channel");
            if (chan == null) {
                return;
            }
        }
        Connection conn = connections.get(network);
        conn.chanMsg(chan, message);
    }

    /**
     * Creates a connection to the specified IRC network, and joins the given channel
     * @param network The network connection to connect to
     * @param channel The channel to connect to
     * @param key A channel key, if applicable (for private IRC channels)
     * @return true if the command succeeds, false otherwise
     */
    public boolean connect(String network, String channel, String key) {
        if (connections.get(network) != null) {
            Connection conn = connections.get(network);
            conn.writeLine("JOIN " + channel + " " + key);
            return true;
        } else {
        }
        return false;
    }

    /**
     * Announces that a player has joined the minecraft server
     * @param network The IRC Network to send the message to
     * @param channel The IRC Channel to send the message to
     * @param nickname The nickname of the player joining the minecraft server
     */
    public void join(String network, String channel, String nickname) {
        msg(network, channel, NICK.matcher(PLAYER_JOIN_MINECRAFT).replaceAll(nickname));
    }

    /**
     * Announces that a player has quit minecraft
     * @param network The IRC Network to send the message to
     * @param channel The IRC Channel to send the message to
     * @param nickname The nickname of the quitting player
     */
    public void quit(String network, String channel, String nickname) {
        msg(network, channel, NICK.matcher(PLAYER_QUIT_MINECRAFT).replaceAll(nickname));
    }

    public boolean isOnline(String nickname) {
        return online.contains(nickname);
    }

    public void setOnline(String nickname, boolean val) {
        if (val) {
            online.add(nickname);
        } else {
            online.remove(nickname);
        }
    }
}
