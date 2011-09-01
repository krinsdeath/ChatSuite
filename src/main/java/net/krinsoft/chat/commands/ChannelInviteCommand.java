package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class ChannelInviteCommand extends ChatSuiteCommand {

    public ChannelInviteCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite invite");
        this.setCommandUsage("/chatsuite channel invite [player] [channel]");
        this.addCommandExample("/csc invite [player] [channel] -- Invite the specified player to the specified channel");
        this.addCommandExample("/csci Player aChannel -- Invites 'Player' to 'aChannel'");
        this.setArgRange(1, 2);
        this.addKey("chatsuite channel invite");
        this.addKey("cs channel invite");
        this.addKey("csc invite");
        this.addKey("csci");
        this.setPermission("chatsuite.invite", "Allows users to invite others into their channels.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) { return; }
        Player p = plugin.getServer().getPlayer(args.get(0));
        ChatPlayer player = plugin.getPlayerManager().getPlayer((Player)sender);
        Channel c = null;
        if (args.size() > 1) {
            c = plugin.getChannelManager().getChannel(args.get(1));
        } else {
            c = plugin.getChannelManager().getChannel(player.getChannel());
        }
        if (p == null || c == null) { return; }
        c.invite(player.getName(), p.getName());
    }

}
