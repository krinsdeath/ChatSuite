package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ChannelAdminCommand extends ChannelCommand {

    private enum Option {
        ADD,
        REMOVE
    }

    public ChannelAdminCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Admin");
        setCommandUsage("/adm [channel] [user]");
        setPageHeader(0, "Channel Commands", "/adm      ");
        addToPage(0, "[channel] " + ChatColor.GOLD + "add       " + ChatColor.YELLOW + "[user]");
        addToPage(0, "[channel] " + ChatColor.GOLD + "remove    " + ChatColor.YELLOW + "[user]");
        setArgRange(3, 3);
        addKey("chatsuite channel admin");
        addKey("channel admin");
        addKey("ch admin");
        addKey("admin");
        addKey("adm");
        addKey("cha");
        setPermission("chatsuite.admin", "Allows the user to add admins to their channels", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = (Player) sender;
        Channel channel = manager.getChannel(args.get(0));
        Option o = validateOption(args.get(1));
        Player target = plugin.getServer().getPlayer(args.get(2));
        if (target == null) {
            error(sender, "Player " + ChatColor.DARK_RED + args.get(2) + ChatColor.RED + " does not exist.");
            return;
        }
        if (channel != null) {
            switch (o) {
                case ADD:
                    channel.addAdmin(player, target);
                    break;
                case REMOVE:
                    channel.remAdmin(player, target);
                    break;
                default:
            }
        }
    }

    private Option validateOption(String val) {
        for (Option o : Option.values()) {
            if (o.name().toLowerCase().startsWith(val.toLowerCase())) {
                return o;
            }
        }
        return null;
    }

}
