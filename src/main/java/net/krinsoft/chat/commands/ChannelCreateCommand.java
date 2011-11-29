package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class ChannelCreateCommand extends ChatSuiteCommand {

    public ChannelCreateCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite create");
        this.setCommandUsage("/chatsuite channel create [channel] [type]");
        this.addCommandExample("/csc create \"My Channel\" public -- Create a channel called 'My Channel' that anyone can join");
        this.addCommandExample("/csc create aChannel private -- Create a private channel called aChannel");
        this.setArgRange(2, 2);
        this.addKey("chatsuite channel create");
        this.addKey("cs channel create");
        this.addKey("c channel create");
        this.addKey("csc create");
        this.addKey("cscc");
        this.setPermission("chatsuite.create", "Allows this user to create public and private channels.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            return;
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) sender);
        if (plugin.getChannelManager().getChannel(args.get(0)) == null) {
            // channel is free to create
            plugin.getChannelManager().createChannel(args.get(0), p.getName(), args.get(1));
            ColoredMessage cm = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_switch"));
            for (String line : cm.getContents()) {
                sender.sendMessage(line.replaceAll("%c", args.get(0)));
            }
            p.setChannel(args.get(0));
        } else {
            // channel exists
            ColoredMessage msg = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "channel_exists"));
            for (String line : msg.getContents()) {
                sender.sendMessage(line);
            }
        }
    }

}
