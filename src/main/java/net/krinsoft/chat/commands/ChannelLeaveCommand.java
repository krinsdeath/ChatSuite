package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class ChannelLeaveCommand extends ChatSuiteCommand {

    public ChannelLeaveCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite leave");
        this.setCommandUsage("/cs help channel");
        this.setArgRange(1, 1);
        this.addKey("cs chan leave");
        this.addKey("csc leave");
        this.addKey("cscleave");
        this.addKey("cscl");
        this.setPermission("chatsuite.leave", "Allows this user to leave channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            return;
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) sender);
        Channel c = plugin.getChannelManager().getChannel(args.get(0));
        if (c != null) {
            if (c.getOccupants().contains(((Player)sender).getName())) {
                plugin.getChannelManager().removePlayerFromChannel((Player)sender, args.get(0));
                p.setChannel(plugin.getChannelManager().listChannels(p.getName()).get(0));
                ColoredMessage msg = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_switch"));
                for (String line : msg.getContents()) {
                    sender.sendMessage(line.replaceAll("%c", p.getChannel()));
                }
            } else {
                // you can't leave a channel you aren't in!
            }
        } else {
            // you can't leave a channel that doesn't exist!
        }
    }

}
