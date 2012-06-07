package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.events.MinecraftJoinEvent;
import net.krinsoft.chat.events.MinecraftQuitEvent;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private ChatCore plugin;

    public PlayerListener(ChatCore instance) {
        plugin = instance;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().registerPlayer(event.getPlayer().getName());
        if (plugin.getIRCBot() != null) {
            MinecraftJoinEvent evt = new MinecraftJoinEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerKick(PlayerKickEvent event) {
        if (event.isCancelled()) { return; }
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
        if (plugin.getIRCBot() != null) {
            MinecraftQuitEvent evt = new MinecraftQuitEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerQuit(PlayerQuitEvent event) {
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
        if (plugin.getIRCBot() != null) {
            MinecraftQuitEvent evt = new MinecraftQuitEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerChatLowest(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("chatsuite.colorize")) {
            event.setMessage(event.getMessage().replaceAll("&([0-9a-fA-F])", "\u00A7$1"));
        }
        ChatPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getName());
        if (player == null) { return; } // player object was null
        Target target = player.getTarget();
        if (target == null) { return; } // target object was null
        String format = player.getFormattedMessage();
        plugin.debug("Player: " + player.getName() + " / Target: " + target.getName());
        Set<Player> players = new HashSet<Player>();
        if (target instanceof Channel) {
            for (Player occ : ((Channel)target).getOccupants()) {
                if (occ != null) {
                    players.add(occ);
                } else {
                    ((Channel)target).part(occ);
                }
            }
        } else {
            player.whisperTo(target, event.getMessage());
            ((ChatPlayer)target).whisperFrom(player, event.getMessage());
            event.setCancelled(true);
            return;
        }
        event.getRecipients().clear();
        event.getRecipients().addAll(players);
        event.setFormat(format);
        event.setMessage(event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    void playerChatMonitor(PlayerChatEvent event) {
        if (event.isCancelled()) { return; }
        if (plugin.getIRCBot() == null) { return; }
        String message = event.getFormat();
        message = message.replaceAll("%2\\$s", event.getMessage().replaceAll("\\$", "\\\\$"));
        message = ChatColor.stripColor(message);
        ChatPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getName());
        if (player.getTarget() instanceof Channel) {
            ((Channel)player.getTarget()).sendToIRC(message);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerChangedWorld(PlayerChangedWorldEvent event) {
        if (!plugin.getPlayerManager().isPlayerRegistered(event.getPlayer().getName())) {
            return;
        }
        plugin.getChannelManager().playerWorldChange(event.getPlayer(), event.getFrom().getName(), event.getPlayer().getWorld().getName());
    }

}
