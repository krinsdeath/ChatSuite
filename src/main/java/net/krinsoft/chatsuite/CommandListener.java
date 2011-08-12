package net.krinsoft.chatsuite;

import java.util.ArrayList;
import net.krinsoft.chatsuite.ChatPlayer.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author krinsdeath
 */

class CommandListener implements CommandExecutor {
	private ChatSuite plugin;

	public CommandListener(ChatSuite inst) {
		plugin = inst;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		Player p = (Player) sender;
		plugin.addPlayer(p);

		// handler for '/whisper'
		if (cmd.getName().equalsIgnoreCase("whisper")) {
			if (sender.hasPermission("commandsuite.chat.commands.whisper") && sender instanceof Player) {
				if (args.length > 1) {
					if (plugin.getServer().getPlayer(args[0]) != null) {
						Player t = plugin.getServer().getPlayer(args[0]);
						StringBuilder out = new StringBuilder();
						for (int i = 1; i < args.length; i++) {
							out.append(args[i]);
							out.append(" ");
						}
						String msg = out.toString().trim();
						plugin.getPlayer(p).whisper(t, Type.SEND, msg);
						if (plugin.getPlayer(t).isAfk()) {
							plugin.getPlayer(p).whisper(t, Type.RECEIVE, plugin.getPlayer(t).getAway());
						}
						plugin.getPlayer(t).whisper(p, Type.RECEIVE, msg);
					} else {

					}
				}
			}
		}
		
		// handler for '/afk'
		if (cmd.getName().equalsIgnoreCase("afk")) {
			if (sender.hasPermission("commandsuite.chat.commands.afk")) {
				if (sender instanceof Player) {
					if (plugin.getPlayer(p) != null) {
						plugin.getPlayer(p).afkToggle();
						String msg = Localization.getString("status.default", "");
						if (args.length > 0) {
							StringBuilder out = new StringBuilder();
							for (int i = 0; i < args.length; i++) {
								out.append(args[i]);
								out.append(" ");
							}
							msg = out.toString().trim();
						}
						if (plugin.getPlayer(p).isAfk()) {
							plugin.getPlayer(p).setAway(msg);
							p.sendMessage(Localization.getString("status.afk", plugin.getPlayer(p).getAway()));
						} else {
							p.sendMessage(Localization.getString("status.back", ""));
						}
					}
				}
			}
		}
		
		// handler for '/global'
		if (cmd.getName().equalsIgnoreCase("global")) {
			if (sender.hasPermission("commandsuite.chat.commands.global")) {
				
			}
		}

		// handler for '/channel'
		if (cmd.getName().equalsIgnoreCase("channel")) {
			if (args.length > 2) {
				if (plugin.config.getStringList("groups." + plugin.getPlayer(p).getGroup() + ".channels", new ArrayList<String>()).contains(args[0])) {
					String msg = "";
					StringBuilder out = new StringBuilder();
					for (int i = 1; i < args.length; i++) {
						out.append(args[i]);
						out.append(" ");
					}
					msg = out.toString().trim();
					plugin.getPlayer(p).channel(args[0], Type.SEND, msg);
				}
			}
		}
		return true;
	}

}
