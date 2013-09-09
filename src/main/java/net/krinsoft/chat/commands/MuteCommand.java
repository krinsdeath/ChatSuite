package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Target;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class MuteCommand extends ChatSuiteCommand {

    public MuteCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: User Mute");
        setCommandUsage("/mute [target]");
        setArgRange(1, 1);
        addKey("chatsuite mute");
        addKey("chat mute");
        addKey("mute");
        setPermission("chatsuite.mute", "Prevents the specified target from sending messages.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        Target target = plugin.getChannelManager().getChannel(args.get(0));
        if (target == null) {
            target = plugin.getPlayerManager().getPlayer(args.get(0));
            if (target == null) {
                if (args.get(0).startsWith("c:")) {
                    target = plugin.getChannelManager().getChannel(args.get(0).split(":")[1]);
                } else if (args.get(0).startsWith("p:")) {
                    target = plugin.getPlayerManager().getPlayer(args.get(0).split(":")[1]);
                } else {
                    target = null;
                }
            }
        }
        if (target != null) {
            target.toggleMute();
            target.persist();
            plugin.getPlayerManager().saveConfig();
            sender.sendMessage(ChatColor.GRAY + "You " + (target.isMuted() ? "" : "un") + "muted the " + (target instanceof Channel ? "channel " : "player ") + ChatColor.RED + target.getName() + ChatColor.GRAY + ".");
        } else {
            sender.sendMessage(ChatColor.RED + "No such player or channel.");
        }

    }
}
