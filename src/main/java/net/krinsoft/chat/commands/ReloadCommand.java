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

    public ReloadCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Reload");
        setCommandUsage("/chatsuite reload");
        setArgRange(0, 0);
        addKey("chatsuite reload");
        addKey("chatsuite -r");
        setPermission("chatsuite.reload", "Allows this user to reload ChatSuite.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.initConfiguration();
        sender.sendMessage("ChatSuite configuration reloaded.");
    }

}
