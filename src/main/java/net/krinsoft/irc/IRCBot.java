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
        connections.clear();
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
            String key      = networks.getString(   network + ".key");
            Connection conn = new Connection(this, network, host, port, channel, key);
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

    public void initialize(String net) throws IOException {
        ConfigurationSection network = getConfig().getConfigurationSection("networks." + net);
        if (network == null) {
            throw new InvalidNetworkException("Unknown network.");
        }
        if (connections.get(net) != null) {
            throw new InvalidNetworkException("Network already initialized.");
        }
        String host     = network.getString("host");
        int port        = network.getInt("port");
        String channel  = network.getString("channel");
        String key      = network.getString("key");
        try {
            Connection conn = new Connection(this, net, host, port, channel, key);
            connections.put(net, conn);
        } catch (IOException e) {
            plugin.warn("An exception occurred while initializing the network '" + net + "': " + e.getLocalizedMessage());
        } catch (InvalidNetworkException e) {
            plugin.warn("InvalidNetworkException: " + e.getLocalizedMessage());
        }
    }

    /**
     * Creates a connection to the specified IRC network, and joins the given channel
     * @param network The network connection to connect to
     * @param c The channel to connect to
     * @param k A channel key, if applicable (for private IRC channels)
     * @return true if the command succeeds, false otherwise
     */
    public boolean connect(String network, String c, String k) {
        String channel = (c != null ? c : getConfig().getString("networks." + network + ".channel"));
        String key = (k != null ? k : getConfig().getString("networks." + network + ".key", ""));
        if (connections.get(network) != null && channel != null) {
            Connection conn = connections.get(network);
            conn.writeLine("JOIN " + channel + " " + key);
            return true;
        } else {
        }
        return false;
    }

    /**
     * Kills the specified network connection
     * @param network The network we're attempting to disconnect
     * @return true if the disconnection is successful, otherwise false
     */
    public void disconnect(String network) {
        if (network == null) {
            throw new InvalidNetworkException("Invalid network.");
        }
        if (connections.get(network) == null) {
            throw new InvalidNetworkException("Invalid network.");
        }
        connections.get(network).stop();
        connections.remove(network);
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

    public void create(String network, String host, String p, String channel, String key) {
        if (connections.get(network) != null) {
            throw new InvalidNetworkException("Network already exists.");
        }
        int port;
        try {
            port = Integer.parseInt(p);
        } catch (NumberFormatException e) {
            throw new InvalidNetworkException("Invalid port (try 6667).");
        }
        if (!channel.startsWith("#")) {
            throw new InvalidNetworkException("Channels must be prefixed with '#'!");
        }
        if (key == null) {
            key = "";
        }
        getConfig().set("networks." + network + ".host", host);
        getConfig().set("networks." + network + ".port", port);
        getConfig().set("networks." + network + ".channel", channel);
        getConfig().set("networks." + network + ".key", key);
        getConfig().set("networks." + network + ".auth", "");
        saveConfig();
    }

    /**
     * Returns a list of active connections
     * @return The connections active on this bot
     */
    public List<Connection> getConnections() {
        List<Connection> list = new ArrayList<Connection>();
        for (Connection conn : connections.values()) {
            list.add(conn);
        }
        return list;
    }

}
