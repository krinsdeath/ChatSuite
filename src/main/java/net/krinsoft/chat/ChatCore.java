package net.krinsoft.chat;

import com.pneumaticraft.commandhandler.CommandHandler;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.commands.*;
import net.krinsoft.chat.listeners.IRCListener;
import net.krinsoft.chat.listeners.PlayerListener;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.irc.IRCBot;
import net.krinsoft.irc.InvalidIRCBotException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

/**
 * @author krinsdeath (Jeff Wardian)
 */
public class ChatCore extends JavaPlugin {

    // listeners
    private PlayerListener pListener;
    private IRCListener ircListener;

    private ChannelManager channelManager;
    private WorldManager worldManager;
    private PlayerManager playerManager;
    private CommandHandler commandHandler;

    private boolean debug = true;
    private boolean allow_channels = true;
    private boolean allow_whispers = true;
    private boolean allow_afk = true;

    @Override
    public void onEnable() {
        initConfiguration();
        initEvents();
        log("v" + getDescription().getVersion() + " enabled.");
    }

    @Override
    public void onDisable() {
        pListener = null;
        playerManager.clean();
        playerManager.saveConfig();
        channelManager.clean();
        channelManager.saveConfig();
        worldManager.clean();
        if (irc_bot != null) { irc_bot.clean(); }
        log("v" + getDescription().getVersion() + " disabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if (!isEnabled()) {
            cs.sendMessage(ChatColor.RED + "no.");
            return true;
        }
        ArrayList<String> allArgs = new ArrayList<String>(Arrays.asList(args));
        allArgs.add(0, label);
        return this.commandHandler.locateAndRunCommand(cs, allArgs);
    }

    public void initConfiguration() {
        configuration = null;
        registerConfiguration();
        registerGroups();
        channelManager = new ChannelManager(this);
        worldManager = new WorldManager(this);
        playerManager = new PlayerManager(this);
        connectIRCBot(allow_irc);
    }

    private void initEvents() {
        // set up the listeners
        initListeners();
        getServer().getPluginManager().registerEvents(pListener, this);
        getServer().getPluginManager().registerEvents(ircListener, this);
    }

    private void initListeners() {
        pListener       = new PlayerListener(this);
        ircListener     = new IRCListener(this);
        initCommands();
    }

    private void initCommands() {
        // set up the handlers
        CSPermissions permissionHandler = new CSPermissions();
        commandHandler = new CommandHandler(this, permissionHandler);
        // register the commands
        if (allow_afk) {
            commandHandler.registerCommand(new AfkCommand(this));
        }
        if (allow_whispers) {
            commandHandler.registerCommand(new WhisperCommand(this));
            commandHandler.registerCommand(new ReplyCommand(this));
        }
        if (allow_channels) {
            commandHandler.registerCommand(new ChannelAdminCommand(this));
            commandHandler.registerCommand(new ChannelCreateCommand(this));
            commandHandler.registerCommand(new ChannelInfoCommand(this));
            commandHandler.registerCommand(new ChannelInviteCommand(this));
            commandHandler.registerCommand(new ChannelJoinCommand(this));
            commandHandler.registerCommand(new ChannelListCommand(this));
            commandHandler.registerCommand(new ChannelMessageCommand(this));
            commandHandler.registerCommand(new ChannelPartCommand(this));
            commandHandler.registerCommand(new ChannelSetCommand(this));
        }
        if (allow_irc) {
            commandHandler.registerCommand(new IRCCommand(this));
            commandHandler.registerCommand(new IRCConnectCommand(this));
            commandHandler.registerCommand(new IRCCreateCommand(this));
            commandHandler.registerCommand(new IRCListCommand(this));
            commandHandler.registerCommand(new IRCQuitCommand(this));
        }
        commandHandler.registerCommand(new BaseCommand(this));
        commandHandler.registerCommand(new DebugCommand(this));
        commandHandler.registerCommand(new GroupOptionCommand(this));
        commandHandler.registerCommand(new NickCommand(this));
        commandHandler.registerCommand(new ReloadCommand(this));
        commandHandler.registerCommand(new TargetCommand(this));
        commandHandler.registerCommand(new UserInfoCommand(this));
        commandHandler.registerCommand(new VersionCommand(this));

        registerDynamicPermissions();
    }

    private void registerDynamicPermissions() {
        Permission root = getServer().getPluginManager().getPermission("chatsuite.*");
        Permission chan = getServer().getPluginManager().getPermission("chatsuite.channel.*");
        if (root == null) {
            root = new Permission("chatsuite.*");
            getServer().getPluginManager().addPermission(root);
        }
        if (chan == null) {
            chan = new Permission("chatsuite.channel.*");
            getServer().getPluginManager().addPermission(chan);
        }
        chan.getChildren().put("chatsuite.channel.list.all", true);
        chan.recalculatePermissibles();
        root.getChildren().put("chatsuite.bypass.*", true);
        root.getChildren().put("chatsuite.channel.*", true);
        root.getChildren().put("chatsuite.commands", true);
        root.getChildren().put("chatsuite.commands.admin", true);
        root.recalculatePermissibles();
    }

    public void debug(String message) {
        if (debug) {
            message = "[Debug] " + message;
            getLogger().info(message);
        }
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void warn(String message) {
        getLogger().warning(message);
    }

    // WORLDS
    public WorldManager getWorldManager() {
        return this.worldManager;
    }
    
    // CHANNELS
    public ChannelManager getChannelManager() {
        return this.channelManager;
    }

    // PLAYERS
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }


    ////////////////////
    // PLUGIN OPTIONS //
    ////////////////////

    public void setDebug(boolean val) {
        debug = val;
        log("Debug mode: " + (val ? "enabled" : "disabled"));
    }

    public void setAllowChannels(boolean val) {
        allow_channels = val;
        debug("Channels: " + (val ? "allowed" : "denied"));
    }

    public void setAllowWhispers(boolean val) {
        allow_whispers = val;
        debug("Whispers: " + (val ? "allowed" : "denied"));
    }

    public void setAllowAfk(boolean val) {
        allow_afk = val;
        debug("Afk: " + (val ? "allowed" : "denied"));
    }

    public void setAllowIRC(boolean val) {
        allow_irc = val;
        debug("IRC: " + (val ? "allowed" : "denied"));
    }

    ///////////////////////////
    // IRC BOT CONFIGURATION //
    ///////////////////////////

    private boolean allow_irc = false;
    private IRCBot irc_bot    = null;

    public IRCBot getIRCBot() {
        return irc_bot;
    }

    public void connectIRCBot(boolean val) {
        try {
            allow_irc = val;
            if (allow_irc) {
                irc_bot = new IRCBot(this);
                channelManager.connect();
            } else {
                if (irc_bot != null) {
                    irc_bot.clean();
                }
            }
        } catch (IOException e) {
            warn("An error occurred while initializing the IRC Bot.");
            e.printStackTrace();
            irc_bot = null;
        } catch (InvalidIRCBotException e) {
            warn(e.getLocalizedMessage());
            irc_bot = null;
        }
    }


    ///////////////////////////
    // CONFIGURATION METHODS //
    ///////////////////////////

    private FileConfiguration configuration;
    private File config;

    private boolean OP_FALLBACK;
    private String DEFAULT_GROUP;
    private String OP_GROUP;

    private void registerConfiguration() {
        String header = "Any group can have a format section, which overrides the 'global' format.\n" +
                "Variables, denoted by the use of a % (percent sign), can be specified in any order you wish.\n" +
                "%n = player name\n" +
                "%dn = player display name\n" +
                "%fn = player full name (includes prefix/suffix from players.yml)\n" +
                "%t = target (player or channel)\n" +
                "%p = prefix (from group section)\n" +
                "%s = suffix (from group section)\n" +
                "%g = group name\n" +
                "%afk = afk message (if afk is allowed)\n" +
                "%m = chat message\n" +
                "%w = world name (or multiverse alias + color if applicable)\n" +
                "%h = heroes class\n" +
                "Plugins which insert or parse their own chat variables can be inserted as well, such as {FACTION}";
        config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(this.getClass().getResourceAsStream("/defaults/config.yml")));
            getConfig().options().copyDefaults(true);
            getConfig().options().header(header);
            saveConfig();
        }
        if (getConfig().get("plugin.prefixOnJoin") == null) {
            getConfig().set("plugin.prefixOnJoin", false);
            getConfig().set("plugin.prefixOnQuit", false);
            saveConfig();
        }
        setDebug(getConfig().getBoolean("plugin.debug"));
        setAllowChannels(getConfig().getBoolean("plugin.allow_channels"));
        setAllowWhispers(getConfig().getBoolean("plugin.allow_whispers"));
        setAllowAfk(getConfig().getBoolean("plugin.allow_afk"));
        setAllowIRC(getConfig().getBoolean("plugin.allow_irc"));
        OP_FALLBACK = getConfig().getBoolean("plugin.op_fallback", false);
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
            warn("An error occurred while saving the file 'config.yml'");
        }
    }

    public ConfigurationSection getGroupNode(String key) {
        ConfigurationSection group = getConfig().getConfigurationSection("groups." + key);
        if (group == null) {
            getConfig().set("groups." + key + ".prefix", "[" + key + "]");
            getConfig().set("groups." + key + ".group", "");
            getConfig().set("groups." + key + ".suffix", "");
            getConfig().set("groups." + key + ".weight", 0);
        }
        return getConfig().getConfigurationSection("groups." + key);
    }

    public Set<String> getGroups() {
        return getConfig().getConfigurationSection("groups").getKeys(false);
    }

    private void registerGroups() {
        int defs = Integer.MAX_VALUE;
        int ops = 0;
        for (String key : getGroups()) {
            int weight = getGroupNode(key).getInt("weight");
            if (weight < defs) {
                defs = weight;
                DEFAULT_GROUP = key;
            }
            if (weight > ops) {
                ops = weight;
                OP_GROUP = key;
            }
            debug("Registering group node: 'chatsuite.groups." + key + "' with weight '" + weight + "'");
            Permission perm = new Permission("chatsuite.groups." + key);
            perm.setDescription("The attached player belongs to the ChatSuite group: " + key);
            perm.setDefault(PermissionDefault.FALSE);
            if (getServer().getPluginManager().getPermission("chatsuite.groups." + key) == null) {
                getServer().getPluginManager().addPermission(perm);
            }
        }
        debug("DEFAULT GROUP: " + DEFAULT_GROUP);
        getServer().getPluginManager().getPermission("chatsuite.groups." + DEFAULT_GROUP).setDefault(PermissionDefault.TRUE);
        if (OP_FALLBACK) {
            debug("OP GROUP: " + OP_GROUP);
            getServer().getPluginManager().getPermission("chatsuite.groups." + OP_GROUP).setDefault(PermissionDefault.OP);
        }
    }

    public String getDefaultGroup() {
        return DEFAULT_GROUP;
    }

    public String getOpGroup() {
        if (!OP_FALLBACK) { return DEFAULT_GROUP; }
        return OP_GROUP;
    }

    public Target getTarget(String t) {
        Target target;
        if (t.startsWith("p:")) {
            target = playerManager.getPlayer(t.split(":")[1]);
        } else if (t.startsWith("c:")) {
            target = channelManager.getChannel(t.split(":")[1]);
        } else {
            target = channelManager.getChannel(channelManager.getDefaultChannel());
        }
        return target;
    }

    public void whisper(ChatPlayer whisper, ChatPlayer whispee, String msg) {
        if (whisper != null && whispee != null && msg != null) {
            log("[Whisper] " + whisper.getName() + "->" + whispee.getName() + ": " + msg);
        }
    }
}
