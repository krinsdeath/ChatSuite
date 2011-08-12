package net.krinsoft.chatsuite;

import net.krinsoft.chatsuite.ChatPlayer.Type;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author krinsdeath
 */

class PlayerListener extends org.bukkit.event.player.PlayerListener {
    private ChatSuite plugin;

	public PlayerListener(ChatSuite aThis) {
        plugin = aThis;
	}

	@Override
	public void onPlayerChat(PlayerChatEvent event) {
		if (event.isCancelled()) { return; }
		plugin.addPlayer(event.getPlayer());
		Player p = event.getPlayer();
		event.setFormat(plugin.parse(p, Type.NORMAL, event.getMessage()));
	}

	@Override
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.addPlayer(event.getPlayer());
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event) {
        if (event.isCancelled()) { return; }
		plugin.removePlayer(event.getPlayer());
	}

	@Override
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.removePlayer(event.getPlayer());
	}
}
