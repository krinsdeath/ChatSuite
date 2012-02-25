package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.events.IRCMessageEvent;
import net.krinsoft.chat.events.MinecraftJoinEvent;
import net.krinsoft.chat.events.MinecraftMessageEvent;
import net.krinsoft.chat.events.MinecraftQuitEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class IRCListener implements Listener {
    private ChatCore plugin;

    public IRCListener(ChatCore instance) {
        plugin = instance;
    }

    @EventHandler
    void ircMessage(IRCMessageEvent event) {
        String format = plugin.getConfig().getString("format.message", "[%t] %p %n&F: %m");
        String target = plugin.getChannelManager().getGlobalChannel().getName();
        format = format.replaceAll("%t", plugin.getChannelManager().getGlobalChannel().getColoredName());
        format = format.replaceAll("%p", event.getTag());
        format = format.replaceAll("%n|%dn|%fn", event.getNickname());
        format = format.replaceAll("%m", event.getMessage());
        format = format.replaceAll("&([a-fA-F0-9])", "\u00A7$1");
        plugin.getChannelManager().getGlobalChannel().sendMessage(format);
        plugin.getChannelManager().log(target, ChatColor.stripColor(format));
    }

    @EventHandler
    void mcMessage(MinecraftMessageEvent event) {
        plugin.getIRCBot().msg(event.getMessage());
        plugin.log(event.getMessage());
    }

    @EventHandler
    void mcJoin(MinecraftJoinEvent event) {
        plugin.getIRCBot().join(event.getNickname());
    }

    @EventHandler
    void mcQuit(MinecraftQuitEvent event) {
        plugin.getIRCBot().quit(event.getNickname());
    }

}
