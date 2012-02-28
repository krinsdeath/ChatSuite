package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class DebugCommand extends ChatSuiteCommand {

    public DebugCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Debug");
        setCommandUsage("/chatsuite debug [true|false]");
        setPageHeader(0, "Debug Command", "chat debug");
        addToPage(0, "true      " + ChatColor.WHITE + "// Turns debug mode on.");
        addToPage(0, "false     " + ChatColor.WHITE + "// Turns debug mode off.");
        setArgRange(0, 1);
        addKey("chatsuite debug");
        addKey("chat debug");
        setPermission("chatsuite.debug", "Toggles debug messages on or off.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() > 0) {
            try {
                plugin.setDebug(Boolean.parseBoolean(args.get(0)));
            } catch (Exception e) {
                plugin.warn("An error occurred while executing the command.");
                sender.sendMessage(ChatColor.RED + "Invalid parameters: argument must be true or false (boolean)");
            }
        } else {
            plugin.setDebug(true);
        }
    }

}
