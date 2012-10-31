package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ReplyCommand extends ChatSuiteCommand {

    public ReplyCommand(ChatCore plugin) {
        super(plugin);
        setName("ChatSuite: Reply");
        setCommandUsage("/r [message]");
        setPageHeader(0, "User Commands", "/reply");
        addToPage(0, "sup       " + ChatColor.WHITE + "// Say 'sup' to the last person who whispered you.");
        setArgRange(1, 20);
        addKey("chatsuite reply");
        addKey("chat reply");
        addKey("reply");
        addKey("r");
        setPermission("chatsuite.reply", "Replies to the last user who sent you a whisper.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        ChatPlayer player = plugin.getPlayerManager().getPlayer(sender.getName());
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        player.reply(message.toString().trim());
    }
}
