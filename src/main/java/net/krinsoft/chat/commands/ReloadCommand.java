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
        addKey("chat reload");
        setPermission("chatsuite.reload", "Allows this user to reload ChatSuite.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        long time = System.currentTimeMillis();
        plugin.initConfiguration();
        message(sender, "ChatSuite configuration reloaded. (" + (System.currentTimeMillis() - time) + "ms)");
    }

}
