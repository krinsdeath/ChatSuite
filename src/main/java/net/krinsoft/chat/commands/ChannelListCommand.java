package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class ChannelListCommand extends ChannelCommand {

    public ChannelListCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel List");
        setCommandUsage("/ch list [-all]");
        setPageHeader(0, "Channel Commands", "/ch list  ");
        addToPage(0, "          " + ChatColor.WHITE + "// List your current channels.");
        addToPage(0, "-all      " + ChatColor.WHITE + "// List all channels.");
        setArgRange(0, 1);
        addKey("chatsuite channel list");
        addKey("chat channel list");
        addKey("channel list");
        addKey("ch list");
        setPermission("chatsuite.channel.list", "Allows users to list the channels they're in.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        List<Channel> channels;
        if (sender instanceof ConsoleCommandSender) {
            channels = manager.getChannels();
        } else {
            Player player = plugin.getServer().getPlayer(sender.getName());
            if (args.size() > 0 && args.get(0).startsWith("-all") && sender.hasPermission("chatsuite.channel.list.all")) {
                channels = manager.getChannels();
            } else {
                channels = manager.getPlayerChannelList(player);
            }
        }
        int i = 1;
        if (channels.size() > 0) {
            message(sender, "=== Channel List ===");
            for (Channel chan : channels) {
                message(sender, i + ": " + chan.getName());
                i++;
            }
        } else {
            message(sender, "No channels available.");
        }
    }

}
