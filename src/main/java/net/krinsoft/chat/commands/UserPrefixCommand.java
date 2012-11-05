package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class UserPrefixCommand extends UserCommand {

    public UserPrefixCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: User Prefix");
        setCommandUsage("/chat user prefix [player] [prefix]");
        setArgRange(1, 20);
        addKey("chatsuite user prefix");
        addKey("chat user prefix");
        addKey("cuser prefix");
        addKey("prefix");
        setPermission("chatsuite.user.prefix", "Allows a user to change their personal prefix.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        String name = args.remove(0);
        ChatPlayer player = plugin.getPlayerManager().getPlayer(name);
        if (player == null) {
            sender.sendMessage("The player '" + name + "' does not exist.");
            return;
        }
        if (args.size() == 0) {
            player.setPrefix(null);
            sender.sendMessage(player.getName() + "'s prefix has been reset.");
            return;
        }
        StringBuilder prefix = new StringBuilder(args.remove(0));
        for (String arg : args) {
            prefix.append(" ").append(arg);
        }
        player.setPrefix(prefix.toString());
        sender.sendMessage(player.getName() + "'s prefix has been set to '" + prefix + "'");
    }
}
