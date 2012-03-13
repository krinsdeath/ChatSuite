package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.irc.Connection;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class IRCListCommand extends ChatSuiteCommand {

    public IRCListCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: IRC List");
        setCommandUsage("/irc list");
        setArgRange(0, 0);
        addKey("chatsuite irc list");
        addKey("chat irc list");
        addKey("irc list");
        setPermission("chatsuite.irc.list", "Lists active IRC Connections.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        message(sender, ChatColor.GOLD + "=== " + ChatColor.BLUE + "Active Connections" + ChatColor.GOLD + " ===");
        for (Connection conn : plugin.getIRCBot().getConnections()) {
            message(sender, ChatColor.GREEN + conn.getName() + ChatColor.WHITE + ": " + ChatColor.AQUA + conn.getInfo());
        }
        message(sender, "End of list.");
    }
}
