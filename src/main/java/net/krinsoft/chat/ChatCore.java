package net.krinsoft.chat;

import com.pneumaticraft.commandhandler.CommandHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import net.krinsoft.chat.managers.ChannelManager;
import net.krinsoft.chat.managers.ConfigManager;
import net.krinsoft.chat.managers.PlayerManager;
import net.krinsoft.chat.managers.WorldManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author krinsdeath
 */
public class ChatCore extends JavaPlugin {
    // debug/logger
    private final static Logger LOGGER = Logger.getLogger("ChatSuite");
    private boolean debug = true;

    // listener instances

    // manager instances
    private CommandHandler commandHandler;
    private ChatPermissions permissionHandler;
    private ConfigManager configManager;
    private PlayerManager playerManager;
    private ChannelManager channelManager;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        initManagers();
        initListeners();
    }

    @Override
    public void onDisable() {
        clean();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        List<String> full = new ArrayList<String>();
        full.addAll(Arrays.asList(args));
        full.add(0, label);
        return this.commandHandler.locateAndRunCommand(sender, full);
    }

    // populate the managers
    // init players
    // create channels
    // set up worlds
    private void initManagers() {
        this.configManager = new ConfigManager(this);
        this.worldManager = new WorldManager(this);
        this.channelManager = new ChannelManager(this);
        this.playerManager = new PlayerManager(this);
        this.permissionHandler = new ChatPermissions(this);
        this.commandHandler = new CommandHandler(this, this.permissionHandler);
    }

    // initialize the listener instances
    private void initListeners() {

    }

    private void clean() {
        this.commandHandler = null;
        this.permissionHandler = null;
        this.worldManager = null;
        this.channelManager = null;
        this.playerManager = null;
    }

    /**
     * Logs standard information, normal messages
     * @param message
     */
    public void log(String message) {
        message = "[" + getDescription().getName() + "] " + message;
        LOGGER.info(message);
    }

    /**
     * Logs warnings, error stuff
     * @param message
     */
    public void warn(String message) {
        message = "[" + getDescription().getName() + "] " + message;
        LOGGER.warning(message);
    }

    /**
     * Logs debug messages, developer stuff
     * @param message
     */
    public void debug(String message) {
        if (debug) {
            message = "[" + getDescription().getName() + "] [Debug] " + message;
            LOGGER.info(message);
        }
    }

    /**
     * Get the world manager, for per-world chat settings
     * @return
     * the world manager instance
     */
    public WorldManager getWorldManager() {
        return this.worldManager;
    }

    /**
     * Gets a configuration node relevant to the world specified
     * @param world
     * the world whose node we're fetching
     * @return
     * a node for the world, or null
     */
    public ConfigurationNode getWorldNode(String world) {
        if (this.getConfigManager().getWorldNode(world) == null) {
            this.getConfigManager().newWorld(world);
        }
        return this.getConfigManager().getWorldNode(world);
    }

    /**
     * Get the channel manager, for handling channels, their settings and occupants
     * @return
     * the channel manager instance
     */
    public ChannelManager getChannelManager() {
        return this.channelManager;
    }

    /**
     * Get the player manager, for creating new instances of players and changing
     * settings for those instances
     * @return
     * the player manager instance
     */
    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    /**
     * Get the configuration manager, for reading the configuration files
     * @return
     * the config manager instance
     */
    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public Configuration getConfiguration() {
        return null;
    }
}
