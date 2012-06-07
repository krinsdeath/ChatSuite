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
public class ChannelCreateCommand extends ChannelCommand {

    public ChannelCreateCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Create");
        setCommandUsage("/create [channel]");
        setPageHeader(0, "Channel Commands", "/create   ");
        addToPage(0, "test      " + ChatColor.WHITE + "// Create a channel called 'test'");
        setArgRange(1, 1);
        addKey("chatsuite channel create");
        addKey("channel create");
        addKey("ch create");
        addKey("create");
        addKey("chc");
        addKey("c");
        setPermission("chatsuite.create", "Allows this user to create channels.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        Channel channel = manager.getChannel(args.get(0));
        if (channel == null) {
            // channel is free to create
            channel = manager.createChannel(player, args.get(0));
            plugin.getPlayerManager().getPlayer(player.getName()).setTarget(channel);
        } else {
            error(sender, "That channel already exists.");
        }
    }

}
