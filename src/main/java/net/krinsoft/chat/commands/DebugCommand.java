package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class DebugCommand extends ChatSuiteCommand {

    public DebugCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite debug");
        this.setCommandUsage("/chatsuite debug [true|false]");
        this.setArgRange(0, 1);
        this.addKey("chatsuite debug");
        this.addKey("cs debug");
        this.addKey("csd");
        this.setPermission("chatsuite.debug", "Toggles debug messages on or off.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() > 0) {
            try {
                plugin.debug = Boolean.parseBoolean(args.get(0));
            } catch (NumberFormatException e) {
                plugin.debug(e.getLocalizedMessage());
            }
        } else {
            plugin.debug = !plugin.debug;
        }
        if (plugin.debug) {
            sender.sendMessage("Debug mode enabled.");
        } else {
            sender.sendMessage("Debug mode disabled.");
        }
    }

}
