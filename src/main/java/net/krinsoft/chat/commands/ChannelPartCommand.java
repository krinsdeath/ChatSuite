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
public class ChannelPartCommand extends ChannelCommand {

    public ChannelPartCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Part");
        setCommandUsage("/part [channel]");
        setPageHeader(0, "Channel Commands", "/part     ");
        addToPage(0, "admin     " + ChatColor.WHITE + "// Leaves the 'admin' channel.");
        setArgRange(1, 1);
        addKey("chatsuite channel part");
        addKey("chat channel part");
        addKey("channel part");
        addKey("ch part");
        addKey("part");
        addKey("chp");
        addKey("p");
        setPermission("chatsuite.channel.part", "Allows this user to leave channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        Channel channel = manager.getChannel(args.get(0));
        if (channel != null) {
            if (channel.part(player.getPlayer())) {
                player.part(channel);
                player.setTarget(manager.getChannel(manager.getDefaultChannel()));
            }
        } else {
            // channel was null, do nothing
            error(sender, "That channel doesn't exist.");
        }
    }

}
