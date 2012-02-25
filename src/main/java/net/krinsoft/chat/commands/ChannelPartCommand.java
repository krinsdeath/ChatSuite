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
public class ChannelPartCommand extends ChannelCommand {

    public ChannelPartCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Channel Part");
        setCommandUsage("/part [channel]");
        setArgRange(1, 1);
        addKey("chatsuite channel part");
        addKey("channel part");
        addKey("ch part");
        addKey("part");
        addKey("chp");
        addKey("p");
        setPermission("chatsuite.part", "Allows this user to leave channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args.get(0));
        if (channel != null) {
            channel.part(player);
        } else {
            error(player, "That channel doesn't exist.");
        }
    }

}
