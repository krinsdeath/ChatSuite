package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class IRCCommand extends ChatSuiteCommand {

    public IRCCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: IRC");
        setCommandUsage("/irc");
        setArgRange(0, 1);
        setPageHeader(0, "Basic IRC Commands", "/irc  ");
        addToPage(0, "list      ");
        addToPage(0, "create    " + ChatColor.RED + "[network] " + ChatColor.GOLD + "[host]    [port]    [channel]");
        addToPage(0, "connect   " + ChatColor.RED + "[network] ");
        addToPage(0, "quit      " + ChatColor.RED + "[network] ");
        addToPage(0, "save      ");
        // ---
        // irc commands 2           / page 6
        setPageHeader(1, "IRC Set Commands",   "/irc  ");
        addToPage(1, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "host      " + ChatColor.YELLOW + "[string]");
        addToPage(1, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "port      " + ChatColor.YELLOW + "[int]");
        addToPage(1, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "nick      " + ChatColor.YELLOW + "[string]");
        addToPage(1, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "channel   " + ChatColor.YELLOW + "[string]");
        addKey("chatsuite irc");
        addKey("chat irc");
        addKey("irc");
        setPermission("chatsuite.help", "Basic ChatSuite help!", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        int page = 0;
        if (args.size() == 1) {
            try {
                page = Integer.parseInt(args.get(0));
            } catch (NumberFormatException e) {
                page = 0;
            }
        }
        showHelp(sender, page);
    }
}
