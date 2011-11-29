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
public class ChannelListCommand extends ChatSuiteCommand {

    public ChannelListCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite list");
        this.setCommandUsage("/chatsuite list");
        this.setArgRange(0, 0);
        this.addKey("chatsuite list");
        this.addKey("cs list");
        this.addKey("c list");
        this.addKey("cslist");
        this.addKey("csl");
        this.setPermission("chatsuite.list", "Allows users to list the channels they're in.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            return;
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player) sender);
        List<String> channels = plugin.getChannelManager().listChannels(p.getName());
        int i = 0;
        ColoredMessage msg = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "list_channels"));
        for (String chan : channels) {
            for (String line : msg.getContents()) {
                sender.sendMessage(line.replaceAll("%c", chan).replaceAll("%num", ""+i));
            }
            i++;
        }
    }

}
