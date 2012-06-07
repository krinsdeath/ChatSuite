package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ChannelInfoCommand extends ChannelCommand {

    public ChannelInfoCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Info");
        setCommandUsage("/ch info [channel]");
        setArgRange(1, 1);
        addKey("chatsuite channel info");
        addKey("chat channel info");
        addKey("channel info");
        addKey("ch info");
        setPermission("chatsuite.channel.info", "Allows viewing channel information.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Channel chan = manager.getChannel(args.get(0));
        if (chan == null) {
            error(sender, "'" + args.get(0) + "' cannot be matched to a channel.");
            return;
        }
        StringBuilder occs = new StringBuilder();
        for (Player p : chan.getOccupants()) {
            occs.append(ChatColor.AQUA).append(p.getName());
            if (!p.getName().equals(p.getDisplayName())) {
                occs.append(" (").append(ChatColor.GOLD).append(p.getDisplayName()).append(ChatColor.AQUA).append(")");
            }
            occs.append(ChatColor.WHITE).append(", ");
        }
        sender.sendMessage(ChatColor.GOLD + "=== " + chan.getColoredName() + ChatColor.GOLD + " ===");
        sender.sendMessage(ChatColor.GREEN + "Occupants: " + ChatColor.AQUA + occs.toString().substring(0, occs.toString().length()-2));
        sender.sendMessage(ChatColor.GREEN + "Public:    " + ChatColor.AQUA + chan.isPublic());
        sender.sendMessage(ChatColor.GREEN + "Permanent: " + ChatColor.AQUA + chan.isPermanent());
        sender.sendMessage(ChatColor.GREEN + "IRC:       " + ChatColor.AQUA + chan.validIRC());
        if (sender instanceof Player) {
            String state = "a guest of";
            if (chan.isMember((Player)sender)) { state = "a member of"; }
            if (chan.isAdmin((Player)sender)) { state = "an admin on"; }
            if (chan.isOwner((Player)sender)) { state = "the owner of"; }
            sender.sendMessage(ChatColor.GREEN + "I'm " + ChatColor.GOLD + state + ChatColor.GREEN + " this channel.");
            sender.sendMessage(ChatColor.GREEN + "Can I join? " + (chan.isAllowed((Player)sender) ? "Yes" : "No"));
        }
    }
}
