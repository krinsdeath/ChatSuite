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
public class ChannelJoinCommand extends ChatSuiteCommand {

    public ChannelJoinCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite join");
        this.setCommandUsage("/chatsuite channel join [channel]");
        this.addCommandExample("/csc join [channel] -- Joins the specified channel, provided you have access");
        this.addCommandExample("/csc join aChannel");
        this.setArgRange(1, 1);
        this.addKey("chatsuite channel join");
        this.addKey("cs channel join");
        this.addKey("csc join");
        this.addKey("cscj");
        this.setPermission("chatsuite.join", "Allows this user to join channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            return;
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) sender);
        Channel c = plugin.getChannelManager().getChannel(args.get(0));
        if (c != null) {
            boolean r = false;
            if (c.isPrivate() && c.hasInvite(((Player)sender).getName())) {
                plugin.getChannelManager().addPlayerToChannel((Player)sender, c.getName());
                r = true;
            } else if (!c.isPrivate()) {
                plugin.getChannelManager().addPlayerToChannel((Player)sender, c.getName());
                r = true;
            }
            if (r) {
                ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_switch"));
                for (String line : cm.getContents()) {
                    sender.sendMessage(line.replaceAll("%c", args.get(0)));
                }
                p.setChannel(args.get(0));
            }
        } else {
            // channel was null, do nothing
            ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_does_not_exist"));
            for (String line : cm.getContents()) {
                sender.sendMessage(line.replaceAll("%c", args.get(0)));
            }
        }
    }

}
