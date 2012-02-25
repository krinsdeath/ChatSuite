package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class NickCommand extends ChatSuiteCommand {

    public NickCommand(ChatCore plugin) {
        super(plugin);
        setName("ChatSuite: Nick");
        setCommandUsage("nick [player] [newnick]");
        setArgRange(0, 2);
        addKey("chatsuite nickname");
        addKey("nickname");
        addKey("nick");
        setPermission("chatsuite.nick", "Allows this user to change their display name.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.isEmpty() && sender instanceof Player) {
            ((Player)sender).setDisplayName(sender.getName());
            sender.sendMessage("Your nickname has been reset to default.");
            return;
        }
        Player target = plugin.getServer().getPlayer(args.get(0));
        String nick = args.get(0);
        if (sender instanceof Player && args.size() < 2) {
            target = (Player) sender;
        } else {
            if (sender instanceof ConsoleCommandSender && args.size() < 2) {
                sender.sendMessage("You must supply a target and a new name for him.");
                return;
            }
            if (target == null) {
                sender.sendMessage("The specified target must be online and a valid player.");
                return;
            }
        }
        if (args.size() == 2 && plugin.getServer().getPlayer(args.get(0)) != null) {
            target = plugin.getServer().getPlayer(args.get(0));
            nick = args.get(1);
        }
        target.setDisplayName(nick);
        target.sendMessage("You are now identified as " + nick);
    }

}
