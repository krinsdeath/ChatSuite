package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Target;
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
        plugin = instance;
        setName("ChatSuite: Target");
        setCommandUsage("/target [c|p]:[target]");
        addCommandExample("/target c:world");
        addCommandExample("/target p:Njodi");
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
            if (args.get(0).startsWith("c:")) {
                target = plugin.getChannelManager().getChannel(args.get(0).split(":")[1]);
            } else {
                Player t = plugin.getServer().getPlayer(args.get(0).split(":")[1]);
                if (t == null) {
                    error(player, "That player doesn't exist.");
                    return;
                }
                target = plugin.getPlayerManager().getPlayer(t);
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
