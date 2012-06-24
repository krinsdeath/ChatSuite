package net.krinsoft.chat.groups;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Group;

/**
 * @author krinsdeath
 */
public class ChatGroup implements Group {
    private ChatCore plugin;
    private String name;
    private String prefix;
    private String suffix;
    private int channels;
    private String format_message;
    private String format_to;
    private String format_from;

    public ChatGroup(ChatCore plugin, String group) {
        this.plugin         = plugin;
        this.name           = group;
        this.prefix         = plugin.getConfig().getString("groups." + group + ".prefix", "");
        this.suffix         = plugin.getConfig().getString("groups." + group + ".suffix", "");
        this.channels       = plugin.getConfig().getInt("groups." + group + ".settings.max_channels", 1);
        this.format_message = plugin.getConfig().getString("groups." + group + ".format.message", "[%t] %p %n&F: %m");
        this.format_to      = plugin.getConfig().getString("groups." + group + ".format.to", "");
        this.format_from    = plugin.getConfig().getString("groups." + group + ".format.from", "");
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getPrefix() {
        return this.prefix;
    }

    @Override
    public String getSuffix() {
        return this.suffix;
    }

    @Override
    public int maxChannels() {
        return this.channels;
    }
}
