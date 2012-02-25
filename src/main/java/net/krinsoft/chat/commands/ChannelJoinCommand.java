package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class ChannelJoinCommand extends ChannelCommand {

    public ChannelJoinCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Channel Join");
        setCommandUsage("/join [channel]");
        setArgRange(1, 1);
        addKey("chatsuite channel join");
        addKey("channel join");
        addKey("ch join");
        addKey("join");
        addKey("chj");
        addKey("j");
        setPermission("chatsuite.join", "Allows this user to join channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args.get(0));
        if (channel != null) {
            channel.join(player);
        } else {
            // channel was null, do nothing
            error(player, "That channel doesn't exist.");
        }
    }

}
