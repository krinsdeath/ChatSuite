package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class UserIgnoreCommand extends UserCommand {

    public UserIgnoreCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: User Ignore");
        setCommandUsage("/chat user ignore [target] [-remove]");
        setPageHeader(0, "User Commands", "/ignore   ");
        addToPage(0, "[user]    " + ChatColor.WHITE + "// Adds the specified user to your ignore list.");
        addToPage(0, "[user]    " + ChatColor.GOLD + "-remove   " + ChatColor.WHITE + "// Removes the specified user from your ignore list.");
        addCommandExample("/ignore Njodi -- Ignores messages sent from Njodi.");
        addCommandExample("/ignore Njodi -remove -- Stops ignoring Njodi.");
        setArgRange(1, 2);
        addKey("chatsuite user ignore");
        addKey("chat user ignore");
        addKey("cuser ignore");
        addKey("ignore");
        setPermission("chatsuite.user.ignore", "Allows a user to ignore other users.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        if (player == null) {
            sender.sendMessage("You can't ignore people!");
            return;
        }
        if (args.size() == 1 && (args.get(0).equalsIgnoreCase("-l") || args.get(0).equalsIgnoreCase("-list"))) {
            player.sendMessage("=== Ignore List ===");
            for (String name : player.getIgnoreList()) {
                player.sendMessage(name);
            }
            return;
        }
        ChatPlayer target = plugin.getPlayerManager().getPlayer(args.get(0));
        if (target == null || player.getName().equals(target.getName())) {
            sender.sendMessage(ChatColor.RED + "Invalid target.");
            return;
        }
        if (args.size() == 2 && (args.get(1).equalsIgnoreCase("-r") || args.get(1).equalsIgnoreCase("-remove"))) {
            if (player.removeIgnore(target.getName())) {
                sender.sendMessage(ChatColor.GREEN + "You are no longer ignoring " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + ".");
                return;
            }
            sender.sendMessage(ChatColor.RED + "You were not ignoring " + ChatColor.AQUA + target.getName() + ChatColor.RED + ".");
            return;
        }
        if (player.addIgnore(target.getName())) {
            sender.sendMessage(ChatColor.GREEN + "You are now ignoring " + ChatColor.AQUA + target.getName() + ChatColor.GREEN + ".");
            return;
        }
        sender.sendMessage(ChatColor.RED + "You are already ignoring " + ChatColor.AQUA + target.getName() + ChatColor.RED + ".");
    }
}
