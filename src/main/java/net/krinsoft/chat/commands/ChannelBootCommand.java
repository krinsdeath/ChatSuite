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
public class ChannelBootCommand extends ChannelCommand {

    public ChannelBootCommand(ChatCore plugin) {
        super(plugin);
        setName("ChatSuite: Channel Boot");
        setCommandUsage("/boot [channel] [user]");
        setPageHeader(0, "Channel Commands", "/boot     ");
        addToPage(0, "global    " + ChatColor.GOLD + "Njodi     " + ChatColor.WHITE + "// Removes 'Njodi' from the 'global' channel.");
        setArgRange(2, 2);
        addKey("channel boot");
        addKey("ch boot");
        addKey("boot");
        addKey("chb");
        addKey("b");
        setPermission("chatsuite.boot", "Allows the forced removal of players from channels.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player booter = (Player) sender;
        Player player = plugin.getServer().getPlayer(args.get(1));
        Channel channel = manager.getChannel(args.get(0));
        if (player == null) {
            error(booter, "That player does not exist.");
            return;
        }
        if (channel == null) {
            error(booter, "That channel does not exist.");
            return;
        }
        channel.boot(booter, player);
    }
}
