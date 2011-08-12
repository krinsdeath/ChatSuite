package net.krinsoft.chatsuite;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.Arrays;
import java.util.HashMap;
import net.krinsoft.chatsuite.ChatPlayer.Type;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author krinsdeath
 */

public class ChatSuite extends JavaPlugin {
	protected Plugin plugin;
	protected Configuration config;
    protected boolean debug;

	protected HashMap<String, ChatPlayer> players = new HashMap<String, ChatPlayer>();

	private final PlayerListener pListener = new PlayerListener(this);
	private final CommandListener cListener = new CommandListener(this);

    private HashMap<String, String> aliases = new HashMap<String, String>();

    private MultiverseCore multiverse;

	@Override
	public void onEnable() {
		plugin = this;
		config = getConfiguration();
		config.load();
		// make sure the configuration is there
		setup();
        debug = config.getBoolean("plugin.debug", true);

        ChatPlayer.init(this);

		Localization.init(config);
		
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_CHAT, pListener, Event.Priority.Highest, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_JOIN, pListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_KICK, pListener, Event.Priority.Low, this);
		getServer().getPluginManager().registerEvent(Event.Type.PLAYER_QUIT, pListener, Event.Priority.Low, this);

		// make sure players are loaded
		init();

        // set up multiverse aliases
        fetchAliases();

		System.out.println(getDescription().getFullName() + " enabled.");
	}

	@Override
	public void onDisable() {
		System.out.println(getDescription().getFullName() + " disabled.");
	}

	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
		return cListener.onCommand(cs, cmd, label, args);
	}

	public void setup() {
		if (!config.getBoolean("plugin.built", false)) {
			System.out.println("Creating default configuration for " + getDescription().getFullName() + "...");
			// header
			config.setHeader(
				"# Permissions:",
				"#   each group uses the corresponding group name as their permission node, prefixed by 'commandsuite.chat.'",
				"#   'commandsuite.chat.default', 'commandsuite.chat.users', and 'commandsuite.chat.admins' are the defaults",
				"# ---",
				"# Format:",
				"#   %t = target, %td = target display name",
				"#   %w = whisper, %p = prefix, %g = group",
				"#   %s = suffix, %m = message, %n = name, %d = display name",
				"#   %!w = world name",
				"#   %a = afk, %c = channel",
				"# ---",
                "# Each group's 'weight' determines its priority when deciding chat formats.",
                "# The user's chat format will correspond to whichever of his groups has the highest weight",
                "# ---",
				"# Default format:",
				"#   Messages: '[Group] Player@World: Hello!' -> %p%n%s%a&F: %m",
				"#   Whisper sent: '>>[Whisper] Target@World: What's up?' -> %w>> %p%t%s%a&F: %m",
				"#   Whisper receive: '[Whisper]>> Target@World: What's up?' -> >>%w %p%t%s%a&F: %m",
				"#   AFK: '[Group] Player@World [AFK]: I'm away right now.'");
			// plugin information
			config.setProperty("plugin.built", true);
			config.setProperty("plugin.version", getDescription().getVersion());
			
			// error messages
			config.setProperty("error.invalid_target", "&CNo player by that name is online.");
			config.setProperty("error.permission_denied", "&CYou do not have permission to do that.");

			// group stuff
            // default
			config.setProperty("groups.default.formats.message", "%p%n%s%a&F: %m");
			config.setProperty("groups.default.formats.whisper_send", "%w>> %p%t%s%a&F: %m");
			config.setProperty("groups.default.formats.whisper_receive", "&A>>%w %p%t%s%a&F: %m");
			config.setProperty("groups.default.formats.channel", "&A[&B%c&A] %p%t%s%a&F: %m");
            config.setProperty("groups.default.weight", 1);
			config.setProperty("groups.default.whisper", "");
			config.setProperty("groups.default.prefix", "&A[&3d&A] &B");
			config.setProperty("groups.default.suffix", "&A@&B%!w&A");
			config.setProperty("groups.default.group", "users");
			config.setProperty("groups.default.afk", " [&CAFK&A]");
			config.setProperty("groups.default.channels", Arrays.asList());
            // users
			config.setProperty("groups.users.formats.message", "%p%n%s%a&F: %m");
			config.setProperty("groups.users.formats.whisper_send", "%w>> %p%t%s%a&F: %m");
			config.setProperty("groups.users.formats.whisper_receive", "&A>>%w %p%t%s%a&F: %m");
			config.setProperty("groups.users.formats.channel", "&A[&B%c&A] %p%t%s%a&F: %m");
            config.setProperty("groups.users.weight", 2);
			config.setProperty("groups.users.whisper", "&A[&EWhisper&a]");
			config.setProperty("groups.users.prefix", "&A[&B+&A] &B");
			config.setProperty("groups.users.suffix", "&A@&B%!w&A");
			config.setProperty("groups.users.group", "users");
			config.setProperty("groups.users.afk", " [&CAFK&A]");
			config.setProperty("groups.users.channels", Arrays.asList("general"));
            // admins
			config.setProperty("groups.admins.formats.message", "%p%n%s%a&F: %m");
			config.setProperty("groups.admins.formats.whisper_send", "%w>> %p%t%s%a&F: %m");
			config.setProperty("groups.admins.formats.whisper_receive", "&A>>%w %p%t%s%a&F: %m");
			config.setProperty("groups.admins.formats.channel", "&A[&B%c&A] %p%t%s%a&F: %m");
            config.setProperty("groups.admins.weight", 3);
			config.setProperty("groups.admins.whisper", "&A[&EWhisper&a]");
			config.setProperty("groups.admins.prefix", "&A[&C@&A] &B");
			config.setProperty("groups.admins.suffix", "&A@&B%!w&A");
			config.setProperty("groups.admins.group", "admins");
			config.setProperty("groups.admins.afk", " [&CAFK&A]");
			config.setProperty("groups.admins.channels", Arrays.asList("general"));
			// messages
			config.setProperty("status.afk", "You are now marked as &Aaway&F: &B<away>&F");
			config.setProperty("status.back", "You are now &Aback&F.");
			config.setProperty("status.default", "I am away right now.");
			config.save();
			System.out.println("... done!");
		}
		config.load();
	}

	public ChatPlayer getPlayer(Player p) {
		if (players.containsKey(p.getName())) {
			return players.get(p.getName());
		} else {
			return null;
		}
	}

	protected Player getPlayer(String name) {
		return plugin.getServer().getPlayer(name);
	}

	protected void addPlayer(Player p) {
		if (!players.containsKey(p.getName())) {
			players.put(p.getName(), new ChatPlayer(p));
			debug(p.getName() + " created");
		}
	}

	protected void removePlayer(Player player) {
		if (players.containsKey(player.getName())) {
			players.remove(player.getName());
			debug(player.getName() + " removed");
		}
	}
	private void init() {
		for (Player p : getServer().getOnlinePlayers()) {
			addPlayer(p);
		}
	}

	/**
	 * Fetches the raw format string for this player's group.
	 * For example:
	 * <pre>
	 * prefix = [user], name = Player, suffix = [JM], message = "Hello!"
	 *   Raw: %p %n %s: %m
	 *   Parsed: "[user] Player [JM]: Hello!"
	 * </pre>
	 * <pre>
	 * %p evaluates to the group's prefix node
	 * %g evaluates to the group's group node
	 * %s evaluates to the group's suffix node
	 * %a evaluates to the group's afk node
	 * %w evaluates to the group's whisper node (only in whispers)
	 * %n evaluates to the player's name
	 * %d evaluates to the player's display name
	 * %m evaluates to the message sent
	 * %!w evaluates to the player's current world
	 * </pre>
	 * @param player whose group you're fetching
	 * @param key to fetch, any of "message," "whisper_send," or "whisper_receive"
	 * @return
	 * The current format string
	 */
	public String getRawFormat(Player p, Type t) {
		String group = getPlayer(p).getGroup();
		return config.getString("groups." + group + ".format." + t.toString());
	}

	/**
	 * Sets the raw format string for this player's group.
	 * Note: Changes to the raw format are not saved to disk.
	 * <pre>
	 * %p evaluates to the group's prefix node
	 * %g evaluates to the group's group node
	 * %s evaluates to the group's suffix node
	 * %a evaluates to the group's afk node
	 * %w evaluates to the group's whisper node
	 * %n evaluates to the player's name
	 * %d evaluates to the player's display name
	 * </pre>
	 * @param key
	 * The format string to change; "message," "whisper_receive," and "whisper_send"
	 * @param format
	 * The new format for this group's messages
	 */
	public void setRawFormat(Player p, String key, String format) {
		String group = getPlayer(p).getGroup();
		config.setProperty("groups." + group + ".format." + key, format);
	}

	protected String parse(Player p, Type t, String message) {
		String msg = getPlayer(p).getFormat(t, message, p);
		return msg;
	}

    private void fetchAliases() {
        Plugin tmp = getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (tmp != null) {
            debug("Found Multiverse-Core! Fetching aliases...");
            multiverse = (MultiverseCore) tmp;
            for (MVWorld mv : multiverse.getMVWorlds()) {
                aliases.put(mv.getName(), mv.getColoredWorldString());
            }
        } else {
            for (World w : getServer().getWorlds()) {
                aliases.put(w.getName(), w.getName());
            }
        }
    }

    public String getAlias(String w) {
        if (aliases.containsKey(w)) {
            return aliases.get(w);
        } else {
            return w;
        }
    }

    public void debug(String msg) {
        if (debug) {
            msg = "[ChatSuite] [Debug] " + msg;
            plugin.getServer().getLogger().info(msg);
        }
    }
}
