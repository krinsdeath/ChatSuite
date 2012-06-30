package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class UserInfoCommand extends UserCommand {

    public UserInfoCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: User Info");
        setCommandUsage("/chat user info [target]");
        setArgRange(0, 1);
        addKey("chatsuite user info");
        addKey("chat user info");
        addKey("cuser info");
        setPermission("chatsuite.user.info", "Allows a user to view their current settings.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.size() == 1 && !(sender.hasPermission("chatsuite.user.info.others"))) {
            error(sender, "You don't have permission to do that.");
            return;
        }
        if (args.size() == 0 && sender instanceof ConsoleCommandSender) {
            error(sender, "Target is required from the Console.");
            return;
        }
        String target = (args.size() == 1 ? args.get(0) : sender.getName());
        ChatPlayer player = manager.getPlayer(target);
        if (player == null) {
            error(sender, "That player does not exist.");
            return;
        }
        sender.sendMessage(ChatColor.GREEN + "=== " + ChatColor.GOLD + player.getName() + ChatColor.GREEN + " ===");
        sender.sendMessage(ChatColor.GOLD + "Display Name: " + player.getPlayer().getDisplayName());
        sender.sendMessage(ChatColor.GOLD + "Group: " + ChatColor.GREEN + player.getGroup());
        sender.sendMessage(ChatColor.GOLD + "Auto-Join: " + player.getAutoJoinChannelString());
    }

}
