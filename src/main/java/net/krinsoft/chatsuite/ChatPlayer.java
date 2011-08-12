package net.krinsoft.chatsuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

/**
 * Creates an instance of ChatPlayer containing information relevant to the player.
 * For example:
 * <pre>
 * String getFormat(Player player) {
 *   ChatPlayer cPlayer = ChatSuite.getPlayer(player);
 *   return cPlayer.getFormat();
 * }
 * </pre>
 *
 * @author krinsdeath
 * @version 1.0.0
 * @see net.krinsoft.chatsuite.ChatSuite
 * @see #getFormat(String message)
 * @see #getRawMessageFormat()
 */

public class ChatPlayer {

	public enum Type {
		NORMAL,
		RECEIVE,
		SEND;
	}

	protected static HashMap<String, LinkedList<String>> channels = new HashMap<String, LinkedList<String>>();
    private static ChatSuite plugin;
    private static Configuration config;

    protected static void init(ChatSuite p) {
        plugin = p;
        config = plugin.getConfiguration();
    }

	private String name;
	private String group;
	private boolean afk = false;
	private boolean whisper = false;
	private String away;

	public ChatPlayer(Player player) {
		this.name = player.getName();
		List<String> list = config.getKeys("groups");
        int weight = 0;
		for (String g : list) {
			if (player.hasPermission("commandsuite.chat." + g)) {
                int w = config.getInt("groups." + g + ".weight", 1);
                if (w > weight) {
                    weight = w;
                    this.group = g;
                }
			}
		}
        plugin.debug("Weight " + weight + " determined " + this.name + "'s group to be " + this.group);
	}

	/**
	 * Returns a formatted string using the values for this player's group, based
	 * on the raw format string
	 * @see #getRawMessageFormat()
	 * @param type
	 * The message type, any of Type.NORMAL, Type.RECEIVE, Type.SEND
	 * @param message
	 * The message to inject for the %m field
	 * @return
	 * The formatted string including prefix/group/name/suffix/afk/message
	 */
	public String getFormat(Type type, String message, Player target) {
		String msg = "";
        ConfigurationNode node = config.getNode("groups." + this.group);
		String pref = node.getString("prefix");
		String suff = node.getString("suffix");
		String group = node.getString("group");
		String afk = node.getString("afk");
		String whisper = node.getString("whisper");
		if (this.whisper) {
			switch (type) {
				case RECEIVE:
					msg = getRawWhisperReceiveFormat();
					break;
				case SEND:
					msg = getRawWhisperSendFormat();
					break;
				default:
					msg = getRawWhisperSendFormat();
					break;
			}
		} else {
			msg = getRawMessageFormat();
		}
		msg = msg.replaceAll("%p", pref);
		msg = msg.replaceAll("%s", suff);
		msg = msg.replaceAll("%g", group);
		msg = msg.replaceAll("(%n)", name);
        msg = msg.replaceAll("(%t)", target.getName());
		if (this.afk) {
			msg = msg.replaceAll("%a", afk);
		} else {
			msg = msg.replaceAll("%a", "");
		}
		if (this.whisper) {
			msg = msg.replaceAll("%w", whisper);
		} else {
			msg = msg.replaceAll("%w", "");
		}
		msg = msg.replaceAll("(%d)", plugin.getPlayer(name).getDisplayName());
        msg = msg.replaceAll("(%td)", target.getDisplayName());
		msg = msg.replaceAll("(%!w)", plugin.getAlias(plugin.getPlayer(name).getWorld().getName()));
		msg = msg.replaceAll("%m", message);
		msg = msg.replaceAll("&([a-fA-F0-9])", "\u00A7$1");
		return msg;
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
	 * @return
	 * The current format string
	 */
	public String getRawMessageFormat() {
		return config.getString("groups." + this.group + ".formats.message");
	}

	/**
	 * @see #getRawMessageFormat()
	 * @return
	 */
	public String getRawWhisperSendFormat() {
		return config.getString("groups." + this.group + ".formats.whisper_send");
	}

	/**
	 * @see #getRawMessageFormat()
	 * @return
	 */
	public String getRawWhisperReceiveFormat() {
		return config.getString("groups." + this.group + ".formats.whisper_receive");
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
	public void setRawFormat(String key, String format) {
		config.setProperty("groups." + this.group + ".formats." + key, format);
	}

	public void setAway(String msg) {
		if (msg == null) {
			this.away = Localization.getString("status.default", "");
		} else {
			this.away = msg;
		}
	}

	public String getAway() {
		return this.away;
	}

	public boolean isAfk() {
		return this.afk;
	}

	public void afkToggle() {
		afk = !afk;
	}

	public void whisper(Player player, Type type, String msg) {
		this.toggleWhisper(true);
		plugin.getPlayer(player).toggleWhisper(true);
		plugin.getPlayer(name).sendMessage(plugin.parse(player, type, msg));
		toggleWhisper(false);
		plugin.getPlayer(player).toggleWhisper(false);
	}

	public void toggleWhisper(boolean f) {
		whisper = f;
	}
	
	protected boolean isWhispering() {
		return whisper;
	}

	public String getGroup() {
		return this.group;
	}

	public void channel(String tag, Type type, String msg) {
		for (String user : channels.get(tag)) {
			String f = getFormat(type, msg, plugin.getPlayer(user));
			f = f.replaceAll("(%c)", tag);
			plugin.getPlayer(user).sendMessage(f);
		}
	}

	public void join(String tag) {
		List<String> list = config.getStringList("groups." + this.group + ".channels", new ArrayList<String>());
		if (list.contains(tag)) {
			channels.get(tag).add(name);
			plugin.getPlayer(name).sendMessage("You joined the channel " + tag + ".");
		}
	}

	public void leave(String tag) {
		List<String> list = config.getStringList("groups." + this.group + ".channels", new ArrayList<String>());
		if (list.contains(tag) && channels.get(tag).contains(name)) {
			channels.remove(tag);
			plugin.getPlayer(name).sendMessage("You left the channel " + tag + ".");
		}
	}

    @Override
    public String toString() {
        return "ChatPlayer{name=" + this.name + ",group=" + this.group + "}@" + this.hashCode();
    }

    @Override
    public int hashCode() {
        int hash = 11;
        hash = 37 * hash + (this.group.length() * 37 < 155 ? hash * 37 : hash / 37);
        hash = 37 * hash - (this.name.length() * 11 > 121 ? hash / 11 : hash * 11);
        return hash;
    }

    @Override
    public boolean equals(Object aThat) {
        if (this == aThat) {
            return true;
        }
        if (aThat == null || aThat.getClass() != this.getClass()) {
            return false;
        }
        ChatPlayer that = (ChatPlayer) aThat;
        if (that.hashCode() == this.hashCode() && that.toString().equals(this.toString())) {
            return true;
        }
        return false;
    }
}
