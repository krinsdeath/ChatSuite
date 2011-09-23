package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class ReloadCommand extends ChatSuiteCommand {

    public ReloadCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite reload");
        this.setCommandUsage("/chatsuite reload");
        this.setArgRange(0, 0);
        this.addKey("chatsuite reload");
        this.addKey("cs reload");
        this.setPermission("chatsuite.reload", "Allows this user to reload ChatSuite.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.initConfiguration();
        sender.sendMessage("ChatSuite configuration reloaded.");
    }

}
