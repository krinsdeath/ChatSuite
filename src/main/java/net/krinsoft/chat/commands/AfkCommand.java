package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
public class AfkCommand extends ChatSuiteCommand {

    public AfkCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: AFK");
        setCommandUsage("/afk [message]");
        setArgRange(0, 16);
        addKey("chatsuite afk");
        addKey("chat afk");
        addKey("afk");
        setPermission("chatsuite.afk", "Allows you to set your AFK status.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        if (player == null) { return; }
        if (args.isEmpty()) {
            player.toggleAfk("I'm away.");
        } else {
            StringBuilder message = new StringBuilder();
            for (String arg : args) {
                message.append(arg).append(" ");
            }
            player.toggleAfk(message.toString().trim());
        }
    }

}
