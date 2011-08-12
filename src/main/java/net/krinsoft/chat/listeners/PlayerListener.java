package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import net.krinsoft.chat.events.ChannelMessage;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 *
 * @author krinsdeath
 */
public class PlayerListener extends org.bukkit.event.player.PlayerListener {
    private ChatCore plugin;

    public PlayerListener(ChatCore aThis) {
        plugin = aThis;
    }

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.registerPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) { return; }
        plugin.unregisterPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.unregisterPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) { return; }
        String msg = event.getMessage();
        if (msg.contains("%")) {
            msg = msg.replaceAll("%", "");
        }
        ChatPlayer p = plugin.getPlayer(event.getPlayer().getName());
        if (p != null) {
            Channel c = null;
            if (plugin.getChannelManager().getDefaultChannel().equalsIgnoreCase("global")) {
                c = plugin.getChannelManager().getGlobalChannel();
            } else {
                c = plugin.getChannelManager().getChannel(event.getPlayer().getWorld().getName());
            }
            ChannelMessage e = new ChannelMessage(plugin, c, event.getPlayer().getName(), msg);
            plugin.getServer().getPluginManager().callEvent(e);
            event.setCancelled(true);
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.isCancelled()) { return; }
    }

    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getChannelManager().playerWorldChange(event.getPlayer(), event.getFrom().getWorld().getName(), event.getTo().getWorld().getName());
    }
}
