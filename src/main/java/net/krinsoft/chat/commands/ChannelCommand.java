package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
public class ChannelCommand extends ChatSuiteCommand {

    public ChannelCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite channel");
        this.setCommandUsage("/cs help channel");
        this.setArgRange(1, 2);
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
            if (args.size() < 2) {
                return;
            } else {
                if (plugin.getChannelManager().getChannel(args.get(1)) == null) {
                    if (cs.hasPermission("chatsuite.create")) {

                    } else {

                    }
                }
            }
        } else if (args.get(0).equalsIgnoreCase("leave")) {

        } else if (args.get(0).equalsIgnoreCase("invite")) {

        } else if (args.get(0).equalsIgnoreCase("kick")) {
            
        }
    }

}
