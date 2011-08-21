package net.krinsoft.chat.commands;

import java.util.Arrays;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
public class ChannelCommand extends ChatSuiteCommand {
    private List<String> reserved = Arrays.asList("create", "invite", "join", "leave");

    public ChannelCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite channel");
        this.setCommandUsage("/cs help channel");
        this.setArgRange(1, 3);
        this.addKey("cs channel");
        this.addKey("cs chan");
        this.addKey("cschannel");
        this.addKey("cschan");
        this.addKey("csc");
        this.setPermission("chatsuite.channel", "Allows the user to chat in channels.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender cs, List<String> args) {
        if (!(cs instanceof Player)) { return; }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) cs);
        if (args.get(0).equalsIgnoreCase("join")) {
            // handler for joining channels, or creating new ones
            // check if the required number of arguments were supplied
            if (args.size() < 2) {
                return;
            } else {
                // we have enough arguments to join
                if (plugin.getChannelManager().getChannel(args.get(1)) == null) {
                    // channel specified doesn't exist, let the player know
                } else {
                    // channel exists, let's move the player to it!
                }
            }
        } else if (args.get(0).equalsIgnoreCase("leave")) {
            // handler for leaving your current channel
        } else if (args.get(0).equalsIgnoreCase("invite")) {
            // handler for inviting players to a channel

        } else if (args.get(0).equalsIgnoreCase("create")) {
            // handler for creating new channels
            if (reserved.contains(args.get(1).toLowerCase())) {
                // player tried to create a channel with a reserved keyword for its name
                ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getError(p.getLocale(), "reserved"));
                for (String line : cm.getContents()) {
                    cs.sendMessage(line.replaceAll("%c", args.get(1)));
                }
                return;
            }
        } else {
            // handler for chatting to a different channel than your current one
        }
    }

}
