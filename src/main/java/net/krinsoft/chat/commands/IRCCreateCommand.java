package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.irc.InvalidNetworkException;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class IRCCreateCommand extends ChatSuiteCommand {

    public IRCCreateCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: IRC Create");
        setCommandUsage("/irc create [network] [host] [port] [channel] [key]");
        addCommandExample("/irc create esper irc.esper.net 6667 #chatsuite");
        setArgRange(4, 5);
        addKey("chatsuite irc create");
        addKey("chat irc create");
        addKey("irc create");
        setPermission("chatsuite.irc.create", "Creates an IRC Network connection.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        try {
            plugin.getIRCBot().create(args.get(0), args.get(1), args.get(2), args.get(3), (args.size() == 5 ? args.get(4) : null));
        } catch (InvalidNetworkException e) {
            error(sender, e.getLocalizedMessage());
            return;
        }
        message(sender, "Network created.");
    }
}
