package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class BaseCommand extends ChatSuiteCommand {

    public BaseCommand(ChatCore plugin) {
        super(plugin);
        setName("ChatSuite: Main");
        setCommandUsage("/chat");
        setArgRange(0, 1);
        // headers!

        // default page
        setPageHeader(0, "Explanation",      "/chat ");
        addToPage(0, "?         " + ChatColor.GRAY + "// display this message.");
        addToPage(0, "[page]    " + ChatColor.GRAY + "// display the specified page of help.");
        // ---
        // basic commands           / page 1
        setPageHeader(1, "Main Commands",    "/chat ");
        addToPage(1, "reload  ");
        addToPage(1, "debug   ");
        addToPage(1, "version ");
        // ---
        // group editing commands   / page 2
        setPageHeader(2, "Group Commands",   "/chat " + ChatColor.GOLD + "group     ");
        addToPage(2,              ChatColor.RED + "[group]   " + ChatColor.GOLD + "weight    " + ChatColor.YELLOW + "[int]     ");
        addToPage(2,              ChatColor.RED + "[group]   " + ChatColor.GOLD + "prefix    " + ChatColor.YELLOW + "[string]  ");
        addToPage(2,              ChatColor.RED + "[group]   " + ChatColor.GOLD + "suffix    " + ChatColor.YELLOW + "[string]  ");
        addToPage(2,              ChatColor.RED + "[group]   " + ChatColor.GOLD + "name      " + ChatColor.YELLOW + "[string]  ");
        // ---
        // user editing commands    / page 3
        setPageHeader(3, "User Commands",    "/chat " + ChatColor.GOLD + "user      ");
        addToPage(3,              ChatColor.RED + "[user]    " + ChatColor.GOLD + "nick      " + ChatColor.YELLOW + "[string]  ");
        addToPage(3,              ChatColor.RED + "[user]    " + ChatColor.GOLD + "lang      " + ChatColor.YELLOW + "[string]  ");
        addToPage(3,              ChatColor.RED + "[user]    " + ChatColor.GOLD + "prefix    " + ChatColor.YELLOW + "[string]  " + ChatColor.GRAY + "// overrides group prefix");
        addToPage(3,              ChatColor.RED + "[user]    " + ChatColor.GOLD + "suffix    " + ChatColor.YELLOW + "[string]  " + ChatColor.GRAY + "// overrides group suffix");
        // ---
        // channel commands         / page 4
        setPageHeader(4, "Channel Commands", "/ch   ");
        addToPage(4, "create    " + ChatColor.RED + "[channel] ");
        addToPage(4, "join      " + ChatColor.RED + "[channel] ");
        addToPage(4, "part      " + ChatColor.RED + "[channel] ");
        addToPage(4, "invite    " + ChatColor.RED + "[channel] " + ChatColor.GOLD + "[user]    ");
        addToPage(4, "boot      " + ChatColor.RED + "[channel] " + ChatColor.GOLD + "[user]    ");
        addToPage(4, "msg       " + ChatColor.RED + "[channel] " + ChatColor.GOLD + "[string]  ");
        // ---
        // irc commands 1           / page 5
        setPageHeader(5, "IRC Commands 1",   "/irc  ");
        addToPage(5, "list      ");
        addToPage(5, "create    " + ChatColor.RED + "[network] " + ChatColor.GOLD + "[host]    [port]    [channel]");
        addToPage(5, "connect   " + ChatColor.RED + "[network] ");
        addToPage(5, "quit      " + ChatColor.RED + "[network] ");
        addToPage(5, "save      ");
        // ---
        // irc commands 2           / page 6
        setPageHeader(6, "IRC Commands 2",   "/irc  ");
        addToPage(6, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "host      " + ChatColor.YELLOW + "[string]");
        addToPage(6, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "port      " + ChatColor.YELLOW + "[int]");
        addToPage(6, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "nick      " + ChatColor.YELLOW + "[string]");
        addToPage(6, "set       " + ChatColor.RED + "[network] " + ChatColor.GOLD + "channel   " + ChatColor.YELLOW + "[string]");
        addKey("chatsuite");
        addKey("chat");
        setPermission("chatsuite.help", "Basic ChatSuite help!", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (sender instanceof ConsoleCommandSender) {
            super.showHelp(sender);
            return;
        }
        if (args.size() == 0) {
            showHelp(sender, 0);
        } else {
            try {
                showHelp(sender, Integer.parseInt(args.get(0)));
            } catch (Exception e) {
                showHelp(sender, 0);
            }
        }
    }
}
