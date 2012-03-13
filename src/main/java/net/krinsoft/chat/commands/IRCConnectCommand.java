package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class IRCConnectCommand extends ChatSuiteCommand {

    public IRCConnectCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: IRC Connect");
        setCommandUsage("/irc connect [network]");
        setArgRange(1, 1);
        addKey("chatsuite irc connect");
        addKey("chat irc connect");
        addKey("irc connect");
        setPermission("chatsuite.irc.connect", "Allows users to connect configured IRC Networks.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        try {
            plugin.getIRCBot().initialize(args.get(0));
        } catch (Exception e) {
            error(sender, e.getLocalizedMessage());
            return;
        }
        message(sender, "Connection established.");
    }
}
