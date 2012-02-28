package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Target;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class TargetCommand extends ChatSuiteCommand {

    public TargetCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Target");
        setCommandUsage("/target [c|p]:[target]");
        setPageHeader(0, "Target Commands", "/target");
        addToPage(0, "c:world   " + ChatColor.WHITE + "// Sets your current target to the channel 'world'");
        addToPage(0, "p:Njodi   " + ChatColor.WHITE + "// Sets your current target to the player 'Njodi'");
        setArgRange(1, 1);
        addKey("chatsuite target");
        addKey("target");
        addKey("t");
        setPermission("chatsuite.target", "Sets the sender's current chat target.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        Target target;
        try {
            target = plugin.getChannelManager().getChannel(args.get(0));
            Player t = plugin.getServer().getPlayer(args.get(0));
            if (target == null && t != null) {
                target = plugin.getPlayerManager().getPlayer(t);
            }
            if (target == null) {
                if (args.get(0).startsWith("c:")) {
                    target = plugin.getChannelManager().getChannel(args.get(0).split(":")[1]);
                } else if (args.get(0).startsWith("p:")) {
                    t = plugin.getServer().getPlayer(args.get(0).split(":")[1]);
                    if (t == null) {
                        error(player, "That player doesn't exist.");
                        return;
                    }
                    target = plugin.getPlayerManager().getPlayer(t);
                }
            }
        } catch (Exception e) {
            error(player, "Invalid parameter(s).");
            return;
        }
        if (target == null) {
            error(player, "The target couldn't be found.");
            return;
        }
        plugin.getPlayerManager().getPlayer(player).setTarget(target);
    }
}
