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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class PlayerListener implements Listener {
    private ChatCore plugin;
    private boolean prefixOnJoin;
    private boolean prefixOnQuit;

    public PlayerListener(ChatCore instance) {
        plugin = instance;
        prefixOnJoin = plugin.getConfig().getBoolean("plugin.prefixOnJoin", false);
        prefixOnQuit = plugin.getConfig().getBoolean("plugin.prefixOnQuit", false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerJoin(PlayerJoinEvent event) {
        plugin.getPlayerManager().registerPlayer(event.getPlayer().getName());
        if (plugin.getIRCBot() != null) {
            MinecraftJoinEvent evt = new MinecraftJoinEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
        if (prefixOnJoin) {
            event.setJoinMessage("[" + plugin.getPlayerManager().getPlayer(event.getPlayer().getName()).getGroup() + "] " + event.getJoinMessage());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void playerKick(PlayerKickEvent event) {
        if (prefixOnQuit) {
            event.setLeaveMessage("[" + plugin.getPlayerManager().getPlayer(event.getPlayer().getName()).getGroup() + "] "+ event.getLeaveMessage());
        }
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
        if (plugin.getIRCBot() != null) {
            MinecraftQuitEvent evt = new MinecraftQuitEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerQuit(PlayerQuitEvent event) {
        if (prefixOnQuit) {
            event.setQuitMessage("[" + plugin.getPlayerManager().getPlayer(event.getPlayer().getName()).getGroup() + "] " + event.getQuitMessage());
        }
        plugin.getPlayerManager().unregisterPlayer(event.getPlayer());
        if (plugin.getIRCBot() != null) {
            MinecraftQuitEvent evt = new MinecraftQuitEvent(event.getPlayer().getName());
            plugin.getServer().getPluginManager().callEvent(evt);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    void playerChatLowest(AsyncPlayerChatEvent event) {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getName());
        if (player == null) { return; } // player object was null
        if (player.isMuted()) {
            player.sendMessage(ChatColor.RED + "You are muted.");
            event.setCancelled(true);
            return;
        }
        Target target = player.getTarget();
        if (target == null) { return; } // target object was null
        if (target.isMuted()) {
            player.sendMessage(ChatColor.RED + "Target is muted.");
            event.setCancelled(true);
            return;
        }
        if (player.colorfulChat()) {
            event.setMessage(ChatColor.translateAlternateColorCodes('&', event.getMessage()));
        }
        String format = player.getFormattedMessage();
        Set<Player> players = new HashSet<Player>();
        if (target instanceof Channel) {
            if (!((Channel)target).getOccupants().contains(event.getPlayer())) {
                event.getPlayer().sendMessage(ChatColor.RED + "You aren't on that channel.");
                event.setCancelled(true);
                return;
            }
            players.addAll(((Channel) target).getOccupants());
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void playerChatMonitor(AsyncPlayerChatEvent event) {
        if (plugin.getIRCBot() == null) { return; }
        final String message = ChatColor.stripColor(replaceAllLiteral(event.getFormat(), "%2$s", replaceAll(event.getMessage(), '$', "\\\\$")));
        ChatPlayer player = plugin.getPlayerManager().getPlayer(event.getPlayer().getName());
        if (player.getTarget() instanceof Channel) {
            ((Channel)player.getTarget()).sendToIRC(message);
        }
    }

    private static String replaceAll(final String string, final char c, final CharSequence replacement) {
    	StringBuilder out = new StringBuilder(replacement.length() > 1 ? string.length() + (string.length() >> 3) : string.length());
        int p, i = -1;
        while ((i = string.indexOf(c, p = i + 1)) != -1) {
        	// Copy pre-sequence and then replacement
    		out.append(string, p, i).append(replacement);
        }
    	// Final sequence
    	return out.append(string, p, string.length()).toString();
    }

    private static String replaceAllLiteral(final String string, final String value, final CharSequence replacement) {
    	StringBuilder out = new StringBuilder(replacement.length() > value.length() ? string.length() + (string.length() >> 3) : string.length());
        int valueLength = value.length(), p, i = -valueLength;
        while ((i = string.indexOf(value, p = i + valueLength)) != -1) {
        	// Copy pre-sequence and then replacement
        	out.append(string, p, i).append(replacement);
        }
        // Final sequence
        return out.append(string, p, string.length()).toString();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    void playerChangedWorld(PlayerChangedWorldEvent event) {
        if (!plugin.getPlayerManager().isPlayerRegistered(event.getPlayer().getName())) {
            return;
        }
        plugin.getChannelManager().playerWorldChange(event.getPlayer(), event.getFrom().getName(), event.getPlayer().getWorld().getName());
    }

}
