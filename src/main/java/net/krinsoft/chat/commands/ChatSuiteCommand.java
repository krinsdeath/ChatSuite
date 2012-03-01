package net.krinsoft.chat.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author krinsdeath
 */
public abstract class ChatSuiteCommand extends Command {
    private Map<Integer, String>        headers     = new HashMap<Integer, String>();
    private Map<Integer, String>        root        = new HashMap<Integer, String>();
    private Map<Integer, List<String>>  pages       = new HashMap<Integer, List<String>>();

    protected ChatCore plugin;

    public ChatSuiteCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public abstract void runCommand(CommandSender sender, List<String> args);

    public boolean validateSender(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "[ChatSuite] This command cannot be used from the console.");
            return false;
        }
        return true;
    }

    public void message(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.GREEN + "[ChatSuite] " + message);
    }

    public void error(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.RED + "[ChatSuite] " + message);
    }

    @Override
    public void showHelp(CommandSender sender) {
        showHeader(     sender);
        showUsage(      sender);
        showDescription(sender);
        showAliases(    sender);
        showExamples(sender);
    }

    public void showHeader(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.GOLD + getCommandName() + ChatColor.GREEN + " ===");
    }

    public void showUsage(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Usage:       " + ChatColor.GOLD + getCommandUsage());
    }

    public void showDescription(CommandSender sender) {
        sender.sendMessage(ChatColor.GREEN + "Description: " + ChatColor.GOLD + getCommandDesc());
        sender.sendMessage(ChatColor.GREEN + "Permission:  " + ChatColor.GOLD + getPermissionString());
    }

    public void showAliases(CommandSender sender) {
        String theKeys = "";
        for (String key : getKeyStrings()) {
            theKeys += ChatColor.AQUA + key + ChatColor.WHITE + ", ";
        }
        theKeys = theKeys.substring(0, theKeys.length() - 2);
        sender.sendMessage(ChatColor.GREEN + "Aliases:     " + theKeys);
    }

    public void showExamples(CommandSender sender) {
        if (this.getCommandExamples().size() > 0) {
            sender.sendMessage(ChatColor.GREEN + "Examples:    ");
            if (sender instanceof Player) {
                showPage(sender, 0);
            } else {
                for (int i = 0; i < pages.size(); i++) {
                    showPage(sender, i);
                }
            }
        }
    }

    public void setPageHeader(int page, String text, String cmd) {
        text = ChatColor.GREEN + "### " + ChatColor.AQUA + text + ChatColor.GREEN + " ###";
        headers.put(page, text);
        root.put(page, ChatColor.GREEN + cmd + ChatColor.GOLD);
    }

    public void addToPage(int page, String line) {
        addCommandExample(line);
        List<String> lines = pages.get(page);
        if (lines == null) {
            lines = new ArrayList<String>();
        }
        lines.add(root.get(page) + line);
        pages.put(page, lines);
    }

    public void showHelp(CommandSender sender, int page) {
        if (page >= pages.size()) { page = 0; }
        showHeader(sender, page);
        showPage(sender, page);
    }

    public void showHeader(CommandSender sender, int page) {
        String thePage = ChatColor.GOLD + "Page " + page + "/" + (pages.size()-1) + ChatColor.GREEN;
        sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.AQUA + getCommandName() + ChatColor.GREEN + " [" + thePage + "] ===");
    }

    public void showPage(CommandSender sender, int page) {
        List<String> lines = new ArrayList<String>();
        lines.addAll(pages.get(page));
        sender.sendMessage(headers.get(page));
        for (String line : lines) {
            sender.sendMessage(line);
        }
    }

}
