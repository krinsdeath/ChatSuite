package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class UserSuffixCommand extends UserCommand {

    public UserSuffixCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: User Suffix");
        setCommandUsage("/chat user suffix [player] [suffix]");
        setArgRange(1, 20);
        addKey("chatsuite user suffix");
        addKey("chat user suffix");
        addKey("cuser suffix");
        addKey("suffix");
        setPermission("chatsuite.user.suffix", "Allows a user to change their personal suffix.", PermissionDefault.OP);
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
            player.setSuffix(null);
            sender.sendMessage(player.getName() + "'s suffix has been reset.");
            return;
        }
        StringBuilder suffix = new StringBuilder(args.remove(0));
        for (String arg : args) {
            suffix.append(" ").append(arg);
        }
        player.setSuffix(suffix.toString());
        sender.sendMessage(player.getName() + "'s suffix has been set to '" + suffix + "'");
    }
}
