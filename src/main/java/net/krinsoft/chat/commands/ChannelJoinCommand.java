package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class ChannelJoinCommand extends ChannelCommand {

    public ChannelJoinCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Join");
        setCommandUsage("/join [channel]");
        setPageHeader(0, "Channel Commands", "/join     ");
        addToPage(0, "admin     " + ChatColor.WHITE + "// Join the 'admin' channel.");
        setArgRange(1, 1);
        addKey("chatsuite channel join");
        addKey("chat channel join");
        addKey("channel join");
        addKey("ch join");
        addKey("join");
        addKey("chj");
        addKey("j");
        setPermission("chatsuite.channel.join", "Allows this user to join channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args.get(0));
        if (channel != null) {
            channel.join(player.getPlayer());
            player.join(channel);
            player.setTarget(channel, false);
        } else {
            // channel was null, do nothing
            error(sender, "That channel doesn't exist.");
        }
    }

}
