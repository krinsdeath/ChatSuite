package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class VersionCommand extends ChatSuiteCommand {

    public VersionCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite version");
        this.setCommandUsage("/chatsuite version");
        this.setArgRange(0, 0);
        this.addKey("chatsuite version");
        this.addKey("cs version");
        this.addKey("c version");
        this.addKey("c -v");
        this.addKey("csv");
        this.setPermission("chatsuite.version", "Allows the user to check ChatSuite's version", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        sender.sendMessage("Running " + plugin.getDescription().getFullName());
    }

}
