package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.events.ChannelMessage;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class ChannelSayCommand extends ChatSuiteCommand {

    public ChannelSayCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite say");
        this.setCommandUsage("/chatsuite channel say [channel] \"[message here]\"");
        this.addCommandExample("/csc say aChannel \"Hello there, how are you all?\"");
        this.setArgRange(2, 2);
        this.addKey("chatsuite channel say");
        this.addKey("cs channel say");
        this.addKey("csc say");
        this.addKey("cscs");
        this.setPermission("chatsuite.say", "Allows the user to say messages to channels.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender cs, List<String> args) {
        if (cs instanceof Player) {
            // sender is a player, check if he's in the channel
            ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) cs);
            if (plugin.getChannelManager().getChannel(args.get(0)) != null) {
                // channel isn't null
                if (plugin.getChannelManager().getChannel(args.get(0)).contains(p.getName())) {
                    // player is in the channel
                    if (!p.getChannel().equals(args.get(0).toLowerCase())) { // player's in the channel, but it's not his active channel
                        ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_switch"));
                        for (String line : cm.getContents()) {
                            cs.sendMessage(line.replaceAll("%c", args.get(0)));
                        }
                        p.setChannel(args.get(0));
                    }
                    ChannelMessage msg = new ChannelMessage(plugin, plugin.getChannelManager().getChannel(args.get(0)), p.getName(), args.get(1));
                    plugin.getServer().getPluginManager().callEvent(msg);
                } else { // player isn't in the channel, so warn him he can't chat there
                    ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "not_in_channel"));
                    for (String line : cm.getContents()) {
                        cs.sendMessage(line.replaceAll("%c", args.get(0)));
                    }
                }
            } else { // channel was null
                ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_does_not_exist"));
                for (String line : cm.getContents()) {
                    cs.sendMessage(line.replaceAll("%c", args.get(0)));
                }
            }
        }// sender wasn't a player; do nothing for now
    }

}
