package net.krinsoft.chat.listeners;

import java.util.HashSet;
import java.util.Set;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        plugin.getPlayerManager().registerPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        String msg = event.getMessage().replaceAll("\\$", "\\\\\\$");
        if (!event.getPlayer().hasPermission("chatsuite.colorize")) {
            msg = msg.replaceAll("&([0-9a-fA-F])", "");
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer(event.getPlayer().getName());
        if (p == null) { return; }
        Channel c = plugin.getChannelManager().getChannel(p.getChannel());
        if (c == null) { return; }
        Set<Player> players = new HashSet<Player>();
        for (String occ : c.getOccupants()) {
            if (plugin.getServer().getPlayer(occ) != null) {
                players.add(plugin.getServer().getPlayer(occ));
            }
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(players);
        // logger method; no longer necessary
        //plugin.chat(event.getPlayer().getName(), msg);
        msg = p.message(c, msg);
        event.setFormat(msg);
        event.setMessage("");
        // custom event methods; no longer necessary
        //ChannelMessage e = new ChannelMessage(plugin, c, event.getPlayer().getName(), msg);
        //plugin.getServer().getPluginManager().callEvent(e);
        //event.setCancelled(true);
    }

    @Override
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        if (!plugin.getPlayerManager().isPlayerRegistered(event.getPlayer())) {
            return;
        }
        plugin.getChannelManager().playerWorldChange(event.getPlayer(), event.getFrom().getName(), event.getPlayer().getWorld().getName());
    }

}
