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
        this.setName("Nick Command");
        this.setCommandUsage("cs nick [player] [newnick]");
        this.addCommandExample("cs nick Player Player2 -- Makes 'Player' display as 'Player2'");
        this.addCommandExample("cs nick Player2 -- Sets your own display name to 'Player2'");
        this.setArgRange(0, 2);
        this.addKey("chatsuite nick");
        this.addKey("cs nick");
        this.addKey("c nick");
        this.addKey("csn");
        this.addKey("nick");
        this.setPermission("chatsuite.nick", "Allows this user to change their display name.", PermissionDefault.OP);
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
