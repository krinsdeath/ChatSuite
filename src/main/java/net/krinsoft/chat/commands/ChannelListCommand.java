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
public class ChannelListCommand extends ChannelCommand {

    public ChannelListCommand(ChatCore instance) {
        super(instance);
        plugin = instance;
        setName("ChatSuite: Channel List");
        setCommandUsage("/ch list");
        setArgRange(0, 0);
        addKey("chatsuite channel list");
        addKey("channel list");
        addKey("ch list");
        setPermission("chatsuite.list", "Allows users to list the channels they're in.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        List<Channel> channels = plugin.getChannelManager().getPlayerChannelList(player);
        int i = 1;
        message(player, "=== Channel List ===");
        for (Channel chan : channels) {
            message(player, i + ": " + chan.getName());
            i++;
        }
    }

}
