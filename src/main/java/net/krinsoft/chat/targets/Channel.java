package net.krinsoft.chat.targets;

import net.krinsoft.chat.ChannelManager;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.events.ChannelBootEvent;
import net.krinsoft.chat.events.ChannelJoinEvent;
import net.krinsoft.chat.events.ChannelPartEvent;
import net.krinsoft.chat.events.MinecraftMessageEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

    private Set<String> occupants  = new HashSet<String>();
    private ChannelManager manager  = null;
    private String name             = null;
    private boolean is_public       = true;
    private boolean is_irc          = false;
    private String target           = null;
    private TextColor color         = TextColor.WHITE;
    private boolean permanent       = false;
    private boolean muted           = false;
    private String owner            = null;
    private Set<String> admins     = new HashSet<String>();
    private Set<String> members    = new HashSet<String>();

    private String IRC_CHANNEL;
    private String IRC_NETWORK;
    private String IRC_KEY;
    private boolean connected;

    /**
     * Creates a new channel with the specified variables as options
     * @param instance The ChannelManager instance, so we can fetch channel options
     * @param channel The name of the channel we've just instantiated
     * @param player The name of the creator of the channel; can be empty.
     */
    public Channel(ChannelManager instance, String channel, Player player) {
        long time = System.nanoTime();
        manager   = instance;
        name      = channel;
        owner     = (player != null ? player.getName() : "");
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "Creating channel " + name + "...");
        }
        if (manager.getConfig().getConfigurationSection("channels." + name) != null) {
            // we found a permanent channel! Let's set it up.
            manager.log(name, "Detected Permanent channel!");
            owner     = manager.getConfig().getString(              "channels." + name + ".owner");
            admins.addAll(manager.getConfig().getStringList(        "channels." + name + ".admins"));
            members.addAll(manager.getConfig().getStringList(       "channels." + name + ".members"));
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
            muted       = manager.getConfig().getBoolean("channels." + name + ".muted", false);
        }
        time = System.nanoTime() - time;
        manager.getPlugin().debug(name + " registered in " + (time / 1000000L) + "ms. (" + time + "ns)");
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

    /**
     * Checks whether the specified sender can edit this channel's settings
     * @param sender The entity whose access we're checking
     * @return true if access is allowed, otherwise false
     */
    public boolean canEdit(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || isOwner((Player) sender) || sender.hasPermission("chatsuite.bypass.set");
    }

    /**
     * Attempts to set the specified option to the given value
     * @param option The option we're trying to set
     * @param value The value we're trying to set
     * @return true if the value is set successfully, otherwise false
     */
    public boolean set(String option, Object value) {
        if (option.equals("public")) {
            try {
                is_public = Boolean.parseBoolean(value.toString());
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (option.equals("permanent")) {
            try {
                permanent = Boolean.parseBoolean(value.toString());
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (option.equals("owner")) {
            owner = value.toString();
        } else if (option.equals("color")) {
            return setColor(value.toString());
            /////////////////
            // IRC OPTIONS
        } else if (option.equals("enabled")) {
            try {
                is_irc = Boolean.parseBoolean(value.toString());
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (option.equals("network")) {
            IRC_NETWORK = value.toString();
        } else if (option.equals("channel")) {
            IRC_CHANNEL = value.toString();
        } else if (option.equals("key")) {
            IRC_KEY = value.toString();
        } else {
            return false;
        }
        return true;
    }

    public void persist() {
        if (permanent) {
            manager.getConfig().set("channels." + name + ".public", is_public);
            manager.getConfig().set("channels." + name + ".owner", owner);
            List<String> adm = new ArrayList<String>();
            adm.addAll(admins);
            List<String> mem = new ArrayList<String>();
            mem.addAll(members);
            manager.getConfig().set("channels." + name + ".admins", adm);
            manager.getConfig().set("channels." + name + ".members", mem);
            manager.getConfig().set("channels." + name + ".color", color.getName());
            manager.getConfig().set("channels." + name + ".muted", muted);
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
        List<Player> players = new ArrayList<Player>();
        Iterator<String> iter = occupants.iterator();
        while (iter.hasNext()) {
            String p = iter.next();
            Player ply = manager.getPlugin().getServer().getPlayer(p);
            if (ply != null) {
                players.add(ply);
            } else {
                iter.remove();
            }
        }
        return players;
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
            if (occupants.contains(player.getName())) {
                player.sendMessage(ChatColor.RED + "[ChatSuite] You are already on this channel!");
                manager.log(name, player.getName() + " was already on " + name + ".");
                return;
            }
            occupants.add(player.getName());
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
        occupants.remove(player.getName());
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
            manager.removePlayerFromChannel(player, getName());
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
        if (isOwner(player) || owner.hasPermission("chatsuite.bypass.admin") && !isAdmin(player)) {
            admins.add(player.getName());
            owner.sendMessage(ChatColor.GREEN + "[ChatSuite] " + player.getName() + " is now an admin on '" + name + "'");
            player.sendMessage(ChatColor.GREEN + "[ChatSuite] You are now an admin on '" + name + "'");
        } else {
            owner.sendMessage(ChatColor.RED + "[ChatSuite] Failed to add admin.");
        }
    }

    /**
     * Removes the admin powers from the specified player
     * @param owner This player must be the owner of the channel
     * @param player The player we're removing from the admin list
     */
    public void remAdmin(Player owner, Player player) {
        if (isOwner(player) || owner.hasPermission("chatsuite.bypass.admin") && isAdmin(player)) {
            admins.remove(player.getName());
            player.sendMessage(ChatColor.RED + "[ChatSuite] You are no longer an admin: " + name);
        } else {
            owner.sendMessage(ChatColor.RED + "[ChatSuite] Failed to remove admin.");
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
        return occupants.contains(player.getName());
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
        if (isMember(player)) {
            inviter.sendMessage(player.getName() + " is already a member of " + name + ".");
            return false;
        }
        return isAdmin(inviter) && !contains(player) && members.add(player.getName());
    }

    @Override
    public boolean isMuted() {
        return muted;
    }

    @Override
    public void toggleMute() {
        muted = !muted;
        if (muted) {
            sendMessage(ChatColor.RED + "[" + getName() + "] Channel has been muted.");
        } else {
            sendMessage(ChatColor.GREEN + "[" + getName() + "] Channel has been unmuted.");
        }
    }

    public void sendMessage(String message) {
        for (Player player : getOccupants()) {
            player.sendMessage(message);
        }
    }

    public boolean validIRC() {
        return is_irc && IRC_NETWORK != null && IRC_CHANNEL != null && connect();
    }

    public void sendToIRC(String message) {
        if (validIRC()) {
            MinecraftMessageEvent event = new MinecraftMessageEvent(IRC_NETWORK, IRC_CHANNEL, message);
            manager.getPlugin().getServer().getPluginManager().callEvent(event);
        }
    }

    public boolean connect() {
        if (connected) { return true; }
        if (is_irc && manager.getPlugin().getIRCBot() != null) {
            if (!manager.getPlugin().getIRCBot().connect(IRC_NETWORK, IRC_CHANNEL, IRC_KEY)) {
                manager.log(name, "Connection to IRC failed.");
                connected = false;
                is_irc = false;
                return false;
            }
            connected = true;
            return true;
        }
        return false;
    }

}
