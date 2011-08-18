package net.krinsoft.chat.managers;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.entity.Player;
import org.bukkit.util.config.ConfigurationNode;

/**
 *
 * @author krinsdeath
 */
class ChatPlayer implements Target {
    // normal player stuff
    private String name; // the player's name
    private String group; // the player's group (decided by his root node in groups.*)
    private String world; // the player's current world (or colored world alias, if using Multiverse)
    private String channel; // the player's current channel

    // configurable option strings
    private String prefix; // the player's prefix node (decided by groups.*.prefix)
    private String suffix; // the player's suffix node (decided by groups.*.suffix)
    private String afk; // the player's afk node (decided by groups.*.afk)

    private String format; // the player's root message format (decided by groups.*.format.message)
    private String whisper_send; // the player's whisper send (decided by groups.*.format.whisper_send)
    private String whisper_recv; // the player's whisper recv (decided by groups.*.format.whisper_receive)
    private String global; // the player's channel format (decided by groups.*.format.global)

    private boolean injected; // whether or not this player's format string has been changed

    /**
     * Build a reference to this player's settings
     * @param plugin
     * @param player
     */
    public ChatPlayer(ChatCore plugin, Player player) {
        this.name = player.getName();
        List<String> groups = plugin.getConfigManager().getGroups();
        // start weight at zero, and iterate through available groups
        int weight = 0;
        for (String group : groups) {
            int priority = plugin.getConfigManager().getGroupNode(group).getInt("weight", 1);
            // check if the player has this group, and if the group's weight is higher than his last matched group
            if (player.hasPermission("chatsuite." + group) && priority > weight) {
                // player has this group and its weight is the highest so far, so we set the group to this one
                weight = priority;
                this.group = group;
            }
        }
        // get a node for this player's default settings based on his group
        ConfigurationNode node = plugin.getConfigManager().getGroupNode(this.group);
        this.prefix = node.getString("prefix");
        this.suffix = node.getString("suffix");
        this.afk = node.getString("afk");
        this.format = node.getString("format.message");
        this.global = node.getString("format.global");
        this.whisper_recv = node.getString("format.whisper_receive");
        this.whisper_send = node.getString("format.whisper_send");
        this.injected = false;
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected void updateWorld(String world) {
        this.world = world;
    }

    protected String getWorld() {
        return this.world;
    }

    protected void setChannel(String channel) {
        this.channel = channel;
    }

    protected boolean injected() {
        return this.injected;
    }

    protected void inject(Injection index, String change) {
        switch (index) {
            case PREFIX_BEFORE:
                this.prefix = change + this.prefix;
                break;
            case PREFIX_AFTER:
                this.prefix = this.prefix + change;
                break;
            case SUFFIX_BEFORE:
                this.suffix = change + this.suffix;
                break;
            case SUFFIX_AFTER:
                this.suffix = this.suffix + change;
                break;
            case NAME_BEFORE:
                this.format = this.format.replaceAll("(%n|%dn)", change + "$1");
                this.whisper_recv = this.whisper_recv.replaceAll("(%n|%dn)", change + "$1");
                this.whisper_send = this.whisper_send.replaceAll("(%n|%dn)", change + "$1");
                this.global = this.global.replaceAll("(%n|%dn)", change + "$1");
                break;
            case NAME_AFTER:
                this.format = this.format.replaceAll("(%n|%dn)", "$1" + change);
                this.whisper_recv = this.whisper_recv.replaceAll("(%n|%dn)", "$1" + change);
                this.whisper_send = this.whisper_send.replaceAll("(%n|%dn)", "$1" + change);
                this.global = this.global.replaceAll("(%n|%dn)", "$1" + change);
                break;
            case WORLD_BEFORE:
                this.format = this.format.replaceAll("(%w)", change + "$1");
                this.whisper_recv = this.whisper_recv.replaceAll("(%w)", change + "$1");
                this.whisper_send = this.whisper_send.replaceAll("(%w)", change + "$1");
                this.global = this.global.replaceAll("(%w)", change + "$1");
                break;
            case WORLD_AFTER:
                this.format = this.format.replaceAll("(%w)", "$1" + change);
                this.whisper_recv = this.whisper_recv.replaceAll("(%w)", "$1" + change);
                this.whisper_send = this.whisper_send.replaceAll("(%w)", "$1" + change);
                this.global = this.global.replaceAll("(%w)", "$1" + change);
                break;
            case CHANNEL_BEFORE:
                this.format = this.format.replaceAll("(%c)", change + "$1");
                this.whisper_recv = this.whisper_recv.replaceAll("(%c)", change + "$1");
                this.whisper_send = this.whisper_send.replaceAll("(%c)", change + "$1");
                this.global = this.global.replaceAll("(%c)", change + "$1");
                break;
            case CHANNEL_AFTER:
                this.format = this.format.replaceAll("(%c)", "$1" + change);
                this.whisper_recv = this.whisper_recv.replaceAll("(%c)", "$1" + change);
                this.whisper_send = this.whisper_send.replaceAll("(%c)", "$1" + change);
                this.global = this.global.replaceAll("(%c)", "$1" + change);
                break;
            case AFK_BEFORE:
                this.afk = change + this.afk;
                break;
            case AFK_AFTER:
                this.afk = this.afk + change;
                break;
        }
        this.injected = true;
    }

    protected void revertInections(ChatCore plugin) {
        ConfigurationNode node = plugin.getConfigManager().getGroupNode(this.group);
        this.prefix = node.getString("prefix");
        this.suffix = node.getString("suffix");
        this.afk = node.getString("afk");
        this.format = node.getString("format.message");
        this.global = node.getString("format.global");
        this.whisper_recv = node.getString("format.whisper_receive");
        this.whisper_send = node.getString("format.whisper_send");
        this.injected = false;
    }

}
