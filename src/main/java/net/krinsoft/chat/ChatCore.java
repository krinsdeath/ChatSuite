package net.krinsoft.chat;

import com.pneumaticraft.commandhandler.CommandHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import net.krinsoft.chat.commands.AfkCommand;
import net.krinsoft.chat.commands.ChannelCommand;
import net.krinsoft.chat.commands.ChannelCreateCommand;
import net.krinsoft.chat.commands.ChannelInviteCommand;
import net.krinsoft.chat.commands.ChannelJoinCommand;
import net.krinsoft.chat.commands.ChannelLeaveCommand;
import net.krinsoft.chat.commands.ChannelListCommand;
import net.krinsoft.chat.commands.ChannelSayCommand;
import net.krinsoft.chat.commands.DebugCommand;
import net.krinsoft.chat.commands.HelpCommand;
import net.krinsoft.chat.commands.LocaleCommand;
import net.krinsoft.chat.commands.ReloadCommand;
import net.krinsoft.chat.commands.VersionCommand;
import net.krinsoft.chat.commands.WhisperCommand;
import net.krinsoft.chat.listeners.ChatListener;
import net.krinsoft.chat.listeners.EntityListener;
import net.krinsoft.chat.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 * @author krinsdeath (Jeff Wardian)
 * @copyright 2011-2012 All Rights Reserved
 * @license MIT
 * @version 1.0
 */
public class ChatCore extends JavaPlugin {

    public enum Info {
        AUTHORS,
        NAME,
        FULLNAME,
        VERSION;
    }

    // logger
    private final static Logger LOGGER = Logger.getLogger("ChatSuite");

    // listeners
    private PlayerListener pListener;
    private EntityListener eListener;
    private ChatListener chatListener;

    // plugin info and managers
    private PluginDescriptionFile pdf;
    private PluginManager pm;
    public boolean debug = true;
    public boolean chatLog = true;

    // configuration details and flags
    private Configuration worldConfig;

    private ConfigManager configManager;
    private ChannelManager channelManager;
    private WorldManager worldManager;
    private LocaleManager localeManager;
    private PlayerManager playerManager;
    private CommandHandler commandHandler;
    private CSPermissions permissionHandler;
    private double chVersion = 1;

    public boolean allow_channels = true;
    public boolean allow_whispers = true;
    public boolean allow_afk = true;

    @Override
    public void onEnable() {
        pdf = getDescription();
        if (!validateCommandHandler()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        initConfiguration();
        initEvents();
        log(info() + " enabled.");
    }

    @Override
    public void onDisable() {
        pListener = null;
        eListener = null;
        chatListener = null;
        commandHandler = null;
        playerManager = null;
        localeManager = null;
        channelManager = null;
        worldManager = null;
        configManager = null;
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

    private boolean validateCommandHandler() {
        try {
            commandHandler = new CommandHandler(this, null);
            if (this.commandHandler.getVersion() >= chVersion) {
                return true;
            } else {
                LOGGER.warning("A plugin with an outdated version of CommandHandler initialized before " + this + ".");
                LOGGER.warning(this + " needs CommandHandler v" + chVersion + " or higher, but CommandHandler v" + commandHandler.getVersion() + " was detected.");
                return false;
            }
        } catch (Throwable t) {
        }
        LOGGER.warning("A plugin with an outdated version of CommandHandler initialized before " + this + ".");
        LOGGER.warning(this + " needs CommandHandler v" + chVersion + " or higher, but CommandHandler v" + commandHandler.getVersion() + " was detected.");
        return false;
    }

    public void initConfiguration() {
        configManager = new ConfigManager(this);
        channelManager = new ChannelManager(this);
        worldManager = new WorldManager(this);
        localeManager = new LocaleManager(this);
        playerManager = new PlayerManager(this);
    }

    private void initEvents() {
        // set up the listeners
        initListeners();
        pm = getServer().getPluginManager();

        // register events
        // ---
        // player events
        pm.registerEvent(Type.PLAYER_CHAT, pListener, Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_JOIN, pListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_QUIT, pListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_KICK, pListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_TELEPORT, pListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLAYER_PORTAL, pListener, Priority.Monitor, this);
        // ---
        // chat event
        pm.registerEvent(Type.CUSTOM_EVENT, chatListener, Priority.Highest, this);
    }

    private void initListeners() {
        pListener = new PlayerListener(this);
        eListener = new EntityListener(this);
        chatListener = new ChatListener(this);
        initCommandHelper();
    }

    private void initCommandHelper() {
        // set up the handlers
        permissionHandler = new CSPermissions(this);
        commandHandler = new CommandHandler(this, permissionHandler);
        // register the commands
        if (allow_afk) {
            commandHandler.registerCommand(new AfkCommand(this));
        }
        if (allow_whispers) {
            commandHandler.registerCommand(new WhisperCommand(this));
        }
        if (allow_channels) {
            commandHandler.registerCommand(new ChannelCommand(this));
            commandHandler.registerCommand(new ChannelSayCommand(this));
            commandHandler.registerCommand(new ChannelCreateCommand(this));
            commandHandler.registerCommand(new ChannelJoinCommand(this));
            commandHandler.registerCommand(new ChannelLeaveCommand(this));
            commandHandler.registerCommand(new ChannelInviteCommand(this));
            commandHandler.registerCommand(new ChannelListCommand(this));
        }
        commandHandler.registerCommand(new LocaleCommand(this));
        commandHandler.registerCommand(new HelpCommand(this));
        commandHandler.registerCommand(new ReloadCommand(this));
        commandHandler.registerCommand(new VersionCommand(this));
        commandHandler.registerCommand(new DebugCommand(this));
    }

    // logging and information
    public void chat(String player, String message) {
        if (chatLog) {
            message = "[" + player + "] " + message;
            LOGGER.info(message.replaceAll("[^a-zA-Z0-9,\\.\\)\\(\\[\\]\\s]", ""));
        }
    }

    public void debug(String message) {
        if (debug) {
            message = "[" + info(Info.NAME) + "] [Debug] " + message;
            LOGGER.info(message);
        }
    }

    public void log(String message) {
        message = "[" + info(Info.NAME) + "] " + message;
        LOGGER.info(message);
    }

    public String info() {
        return info(Info.FULLNAME) + " by " + info(Info.AUTHORS);
    }

    public String info(Info i) {
        switch (i) {
            case AUTHORS: return pdf.getAuthors().toString().replaceAll("[\\[\\]]", "");
            case NAME: return pdf.getName();
            case FULLNAME: return pdf.getFullName();
            case VERSION: return pdf.getVersion();
            default: return pdf.getFullName();
        }
    }

    // CONFIGURATIONS
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    // WORLDS
    public WorldManager getWorldManager() {
        return this.worldManager;
    }
    
    // CHANNELS
    public ChannelManager getChannelManager() {
        return this.channelManager;
    }

    // LOCALIZATIONS
    public LocaleManager getLocaleManager() {
        return this.localeManager;
    }

    // COMMANDS
    public CommandHandler getCommandHandler() {
        return this.commandHandler;
    }

    // PLAYERS
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

}
