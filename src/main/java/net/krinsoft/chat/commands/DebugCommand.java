package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class DebugCommand extends ChatSuiteCommand {

    public DebugCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Debug");
        setCommandUsage("/chatsuite debug [true|false]");
        setArgRange(0, 1);
        addKey("chatsuite debug");
        addKey("c debug");
        addKey("csd");
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
