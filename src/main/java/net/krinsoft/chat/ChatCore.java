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
import net.krinsoft.chat.commands.HelpCommand;
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
    protected boolean debug = true;

    // configuration details and flags
    private Configuration worldConfig;

    private ConfigManager configManager;
    private ChannelManager channelManager;
    private WorldManager worldManager;
    private LocaleManager localeManager;
    private PlayerManager playerManager;
    private CommandHandler commandHandler;
    private CSPermissions permissionHandler;

    @Override
    public void onEnable() {
        pdf = getDescription();
        initConfiguration();
        initEvents();
        log(info() + " enabled.");
    }

    @Override
    public void onDisable() {
        pListener = null;
        eListener = null;
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

    private void initConfiguration() {
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
        commandHandler.registerCommand(new AfkCommand(this));
        commandHandler.registerCommand(new WhisperCommand(this));
        commandHandler.registerCommand(new ChannelCommand(this));
        commandHandler.registerCommand(new ChannelSayCommand(this));
        commandHandler.registerCommand(new ChannelCreateCommand(this));
        commandHandler.registerCommand(new ChannelJoinCommand(this));
        commandHandler.registerCommand(new ChannelLeaveCommand(this));
        commandHandler.registerCommand(new ChannelInviteCommand(this));
        commandHandler.registerCommand(new ChannelListCommand(this));
        //commandHandler.registerCommand(new LocaleCommand(this));
        commandHandler.registerCommand(new HelpCommand(this));
    }

    // logging and information
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
