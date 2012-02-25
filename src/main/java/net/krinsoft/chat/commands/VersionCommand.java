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
public class VersionCommand extends ChatSuiteCommand {

    public VersionCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Version");
        setCommandUsage("/chatsuite version");
        setArgRange(0, 0);
        addKey("chatsuite version");
        addKey("c -v");
        addKey("csv");
        setPermission("chatsuite.version", "Allows the user to check ChatSuite's version", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage(ChatColor.GREEN + "[ChatSuite] Version: " + ChatColor.AQUA + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.GREEN + "[ChatSuite] Website: " + ChatColor.AQUA + plugin.getDescription().getWebsite());
        sender.sendMessage(ChatColor.GREEN + "[ChatSuite] By " + ChatColor.AQUA + "krinsdeath");
    }

}
