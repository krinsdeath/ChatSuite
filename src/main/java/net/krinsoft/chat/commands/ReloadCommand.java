package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class ReloadCommand extends ChatSuiteCommand {

    public ReloadCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Reload");
        setCommandUsage("/chatsuite reload");
        setArgRange(0, 0);
        addKey("chatsuite reload");
        addKey("chatsuite -r");
        addKey("chat reload");
        addKey("chat -r");
        setPermission("chatsuite.reload", "Allows this user to reload ChatSuite.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.initConfiguration();
        sender.sendMessage("ChatSuite configuration reloaded.");
    }

}
