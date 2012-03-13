package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class IRCQuitCommand extends ChatSuiteCommand {

    public IRCQuitCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: IRC Quit");
        setCommandUsage("/irc quit [network]");
        setArgRange(1, 1);
        addKey("chatsuite irc quit");
        addKey("chat irc quit");
        addKey("irc quit");
        setPermission("chatsuite.irc.quit", "Allows users to disconnect from IRC networks.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        try {
            plugin.getIRCBot().disconnect(args.get(0));
        } catch (Exception e) {
            error(sender, e.getLocalizedMessage());
            return;
        }
        message(sender, "Connection terminated.");
    }
}
