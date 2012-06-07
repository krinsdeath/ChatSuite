package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Target;
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
public class ChannelMessageCommand extends ChatSuiteCommand {

    public ChannelMessageCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Message");
        setCommandUsage("/msg [channel] [message]");
        setPageHeader(0, "Channel Commands", "/msg      ");
        addToPage(0, "admin     " + ChatColor.GOLD + "sup guys, spammer in global");
        setArgRange(2, 20);
        addKey("chatsuite channel message");
        addKey("chat channel message");
        addKey("channel message");
        addKey("ch message");
        addKey("message");
        addKey("msg");
        addKey("chm");
        setPermission("chatsuite.message", "Allows the user to say messages to channels.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        Channel channel = plugin.getChannelManager().getChannel(args.get(0));
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            message.append(args.get(i)).append(" ");
        }
        if (channel != null) {
            Target target = plugin.getPlayerManager().getPlayer(player.getName()).getTarget();
            plugin.getPlayerManager().getPlayer(player.getName()).setTarget(channel);
            player.chat(message.toString());
            plugin.getPlayerManager().getPlayer(player.getName()).setTarget(target);
        } else {
            error(player, "That channel doesn't exist.");
        }
    }

}
