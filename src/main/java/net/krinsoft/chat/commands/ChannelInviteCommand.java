package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class ChannelInviteCommand extends ChannelCommand {

    public ChannelInviteCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Invite");
        setCommandUsage("/invite [channel] [player]");
        setPageHeader(0, "Channel Commands", "/invite   ");
        addToPage(0, "admin     " + ChatColor.GOLD + "Njodi     " + ChatColor.WHITE + "// Invite 'Njodi' to the 'admin' channel.");
        setArgRange(2, 2);
        addKey("chatsuite channel invite");
        addKey("chat channel invite");
        addKey("channel invite");
        addKey("ch invite");
        addKey("invite");
        addKey("chi");
        addKey("inv");
        setPermission("chatsuite.channel.invite", "Allows users to invite others into their channels.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(args.get(1));
        Player inviter = plugin.getServer().getPlayer(sender.getName());
        Channel channel = manager.getChannel(args.get(0));
        if (player == null) {
            error(inviter, "That player doesn't exist.");
            return;
        }
        if (channel == null) {
            error(inviter, "That channel doesn't exist.");
            return;
        }
        if (channel.invite(inviter, player)) {
            message(inviter, "You invited " + player.getName() + " to " + channel.getName() + ".");
            message(player, "You were invited to join " + channel.getName() + ". (" + ChatColor.AQUA + "/join " + channel.getName() + ChatColor.GREEN + ")");
        } else {
            error(inviter, "Invite failed.");
        }
    }

}
