package net.krinsoft.chat.targets;

import net.krinsoft.chat.ChannelManager;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.events.ChannelBootEvent;
import net.krinsoft.chat.events.ChannelJoinEvent;
import net.krinsoft.chat.events.ChannelPartEvent;
import net.krinsoft.chat.events.MinecraftMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
enum TextColor {
    AQUA(       "AQUA",         ChatColor.AQUA),
    BLACK(      "BLACK",        ChatColor.BLACK),
    BLUE(       "BLUE",         ChatColor.BLUE),
    DARKAQUA(   "DARKAQUA",     ChatColor.DARK_AQUA),
    DARKBLUE(   "DARKBLUE",     ChatColor.DARK_BLUE),
    DARKGRAY(   "DARKGRAY",     ChatColor.DARK_GRAY),
    DARKGREEN(  "DARKGREEN",    ChatColor.DARK_GREEN),
    DARKPURPLE( "DARKPURPLE",   ChatColor.DARK_PURPLE),
    DARKRED(    "DARKRED",      ChatColor.DARK_RED),
    GOLD(       "GOLD",         ChatColor.GOLD),
    GRAY(       "GRAY",         ChatColor.GRAY),
    GREEN(      "GREEN",        ChatColor.GREEN),
    LIGHTPURPLE("LIGHTPURPLE",  ChatColor.LIGHT_PURPLE),
    RED(        "RED",          ChatColor.RED),
    YELLOW(     "YELLOW",       ChatColor.YELLOW),
    WHITE(      "WHITE",        ChatColor.WHITE);

    private String name;
    private ChatColor color;
    TextColor(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return this.name;
    }

    public ChatColor getColor() {
        return this.color;
    }

    public static TextColor get(String name) {
        if (name != null) {
            name = name.toUpperCase().replaceAll("[^A-Z]", "");
            for (TextColor c : values()) {
                if (c.getName().equals(name)) {
                    return c;
                }
            }
        }
        return null;
    }
}

@SuppressWarnings("unused")
public class Channel implements Target {

    private List<Player> occupants  = new ArrayList<Player>();
    private ChannelManager manager  = null;
    private String name             = null;
    private boolean is_public       = true;
    private boolean is_irc          = false;
    private String target           = null;
    private TextColor color         = TextColor.WHITE;
    private boolean permanent       = false;
    private String owner            = null;
    private List<String> admins     = new ArrayList<String>();
    private List<String> members    = new ArrayList<String>();

    private String IRC_CHANNEL;
    private String IRC_NETWORK;
    private String IRC_KEY;

    /**
     * Creates a new channel with the specified variables as options
     * @param instance The ChannelManager instance, so we can fetch channel options
     * @param channel The name of the channel we've just instantiated
     * @param player The name of the creator of the channel; can be empty.
     */
    public Channel(ChannelManager instance, String channel, Player player) {
        manager   = instance;
        name      = channel;
        owner     = (player != null ? player.getName() : "");
        if (manager.getConfig().getConfigurationSection("channels." + name) != null) {
            // we found a permanent channel! Let's set it up.
            manager.log(name, "Detected Permanent channel!");
            owner     = manager.getConfig().getString(              "channels." + name + ".owner");
            admins    = manager.getConfig().getStringList(          "channels." + name + ".admins");
            members   = manager.getConfig().getStringList(          "channels." + name + ".members");
            is_public = manager.getConfig().getBoolean(             "channels." + name + ".public");
            color     = TextColor.get(manager.getConfig().getString("channels." + name + ".color"));
            if (color == null) {
                color = TextColor.WHITE;
            }
            is_irc      = manager.getConfig().getBoolean(           "channels." + name + ".irc.enabled");
            if (is_irc) {
                IRC_CHANNEL = manager.getConfig().getString(        "channels." + name + ".irc.channel");
                IRC_KEY     = manager.getConfig().getString(        "channels." + name + ".irc.key");
                IRC_NETWORK = manager.getConfig().getString(        "channels." + name + ".irc.network");
                connect();
            }
            permanent   = true;
        }
    }

    /**
     * Get the name of this channel
     * @return The channel's name
     */
    public String getName() {
        return name;
    }

    /**
     * Fetches a colored string with the channel's name
     * @return The colored name of the channel
     */
    public String getColoredName() {
        return color.getColor() + name + ChatColor.WHITE;
    }

    /**
     * Sets the color of this channel when parsed in chat
     * @param c The new color of the channel
     * @return true if the color was set successfully, otherwise false
     */
    public boolean setColor(String c) {
        color = TextColor.get(c);
        if (color == null) {
            color = TextColor.WHITE;
            manager.log(name, "Color set to: " + color.getName());
            return false;
        }
        manager.log(name, "Color set to: " + color.getName());
        return true;
    }

    public void persist() {
        if (permanent) {
            manager.getConfig().set("channels." + name + ".color", "WHITE");
            manager.getConfig().set("channels." + name + ".public", is_public);
            manager.getConfig().set("channels." + name + ".owner", owner);
            manager.getConfig().set("channels." + name + ".admins", admins);
            manager.getConfig().set("channels." + name + ".members", members);
            manager.getConfig().set("channels." + name + ".color", color.getName());
            manager.getConfig().set("channels." + name + ".irc.enabled", is_irc);
            manager.getConfig().set("channels." + name + ".irc.channel", IRC_CHANNEL);
            manager.getConfig().set("channels." + name + ".irc.network", IRC_NETWORK);
        }
    }

    /**
     * Get a cloned list of the occupants of this channel
     * @return The list of players on this channel
     */
    public List<Player> getOccupants() {
        return new ArrayList<Player>(occupants);
    }

    public void setPermanent(boolean val) {
        permanent = val;
        if (permanent) {
            manager.log(name, "Now being persisted.");
        } else {
            manager.log(name, "No longer being saved.");
            manager.getConfig().set("channels." + name, null);
        }
    }

    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Adds the specified player to the occupants list
     * @param player The player to add to the list
     */
    public void join(Player player) {
        if (isAllowed(player)) {
            occupants.add(player);
            player.sendMessage(ChatColor.GREEN + "[ChatSuite] You have joined: " + name);
            manager.log(name, player.getName() + " joined the channel.");
            if (validIRC()) {
                ChannelJoinEvent event = new ChannelJoinEvent(name, IRC_NETWORK, IRC_CHANNEL, player.getName());
                manager.getPlugin().getServer().getPluginManager().callEvent(event);
            }
        } else {
            player.sendMessage(ChatColor.RED + "[ChatSuite] Failed to join: " + name);
            manager.log(name, player.getName() + " was denied entry.");
        }
    }

    /**
     * Removes the specified player from the occupants list
     * @param player The player to remove from the list
     */
    public void part(Player player) {
        occupants.remove(player);
        player.sendMessage(ChatColor.GRAY + "[ChatSuite] You have left: " + name);
        manager.log(name, player.getName() + " left the channel.");
        if (validIRC()) {
            ChannelPartEvent event = new ChannelPartEvent(name, IRC_NETWORK, IRC_CHANNEL, player.getName());
            manager.getPlugin().getServer().getPluginManager().callEvent(event);
        }
    }

    /**
     * Forcibly removes the specified player from the channel
     * @param sender If the target is an admin, the sender must be the owner of the channel
     * @param player The player to remove from the channel
     */
    public void boot(Player sender, Player player) {
        if (sender.equals(player)) {
            sender.sendMessage(ChatColor.RED + "[ChatSuite] Stop kicking yourself.");
            return;
        }
        if ((isAdmin(sender) && !isAdmin(player)) || (isOwner(sender) && !isOwner(player)) || sender.hasPermission("chatsuite.bypass.boot")) {
            occupants.remove(player);
            members.remove(player.getName());
            player.sendMessage(ChatColor.RED + "[ChatSuite] You have been kicked: " + name);
            manager.log(name, player.getName() + " was kicked from the channel.");
            if (validIRC()) {
                ChannelBootEvent event = new ChannelBootEvent(name, IRC_NETWORK, IRC_CHANNEL, player.getName());
                manager.getPlugin().getServer().getPluginManager().callEvent(event);
            }
        }
    }

    /**
     * Checks whether the specified player is allowed into this channel
     * @param player The player whose access we're checking
     * @return True if the player was allowed, otherwise false
     */
    public boolean isAllowed(Player player) {
        return isPublic() || isMember(player) || player.hasPermission("chatsuite.bypass.join");
    }

    /**
     * Checks if the player's nickname matches the owner's
     * @param player The player we're authenticating
     * @return true if the player's name matches, otherwise false
     */
    public boolean isOwner(Player player) {
        return owner != null && owner.equals(player.getName());
    }

    /**
     * Grants the specified player admin status on the channel
     * @param owner This player must be the owner of the channel
     * @param player The player we're adding to the admin list
     */
    public void addAdmin(Player owner, Player player) {
        if (isOwner(player) || owner.hasPermission("chatsuite.bypass.admin")) {
            admins.add(player.getName());
            player.sendMessage(ChatColor.GREEN + "[ChatSuite] You are now an admin: " + name);
        }
    }

    /**
     * Removes the admin powers from the specified player
     * @param owner This player must be the owner of the channel
     * @param player The player we're removing from the admin list
     */
    public void remAdmin(Player owner, Player player) {
        if (isOwner(player) || owner.hasPermission("chatsuite.bypass.admin")) {
            admins.remove(player.getName());
            player.sendMessage(ChatColor.RED + "[ChatSuite] You are no longer an admin: " + name);
        }
    }

    /**
     * Checks if the player is authenticated as an admin on this channel
     * @param player The player we're authenticating
     * @return true if the player is on the admin list (or the owner), otherwise false
     */
    public boolean isAdmin(Player player) {
        return isOwner(player) || admins.contains(player.getName());
    }

    /**
     * Checks whether the specified player is on the channel's access list
     * @param player The player we're authenticating
     * @return true if the player is on the member list (or an admin), otherwise false
     */
    public boolean isMember(Player player) {
        return isAdmin(player) || members.contains(player.getName());
    }

    /**
     * Checks whether the channel's occupants list contains the specified player
     * @param player The player we're checking occupancy thereof
     * @return true if the list contains the player, otherwise false
     */
    public boolean contains(Player player) {
        return occupants.contains(player);
    }

    /**
     * Checks whether the channel is public or private
     * @return true if the channel is public, otherwise false
     */
    public boolean isPublic() {
        return is_public;
    }

    /**
     * Invites the specified player to the channel by adding them to the members list
     * @param inviter The sender of the invite; must be an admin
     * @param player  The player we're inviting to the channel
     * @return true if the invite succeeded, otherwise false
     */
    public boolean invite(Player inviter, Player player) {
        return isAdmin(inviter) && !contains(player) && members.add(player.getName());
    }

    public void sendMessage(String message) {
        for (Player player : occupants) {
            player.sendMessage(message);
        }
    }

    private boolean validIRC() {
        return is_irc && IRC_NETWORK != null && IRC_CHANNEL != null;
    }

    public void sendToIRC(String message) {
        if (validIRC()) {
            MinecraftMessageEvent event = new MinecraftMessageEvent(IRC_NETWORK, IRC_CHANNEL, message);
            manager.getPlugin().getServer().getPluginManager().callEvent(event);
        }
    }

    public void connect() {
        if (is_irc && manager.getPlugin().getIRCBot() != null) {
            if (!manager.getPlugin().getIRCBot().connect(IRC_NETWORK, IRC_CHANNEL, IRC_KEY)) {
                manager.log(name, "Connection to IRC failed.");
                is_irc = false;
            }
        }
    }

}
