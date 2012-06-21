package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 *
 * @author Krin
 */
public class WhisperCommand extends ChatSuiteCommand {

    public WhisperCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = plugin;
        this.setName("ChatSuite: Whisper");
        this.setCommandUsage("/whisper [player] [message]");
        this.setArgRange(2, 20);
        this.addKey("chatsuite whisper");
        this.addKey("whisper");
        this.addKey("w");
        this.setPermission("chatsuite.whisper", "Whispers a message to another user", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!validateSender(sender)) { return; }
        Player player = plugin.getServer().getPlayer(sender.getName());
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.size(); i++) {
            message.append(args.get(i)).append(" ");
        }
        Player target = plugin.getServer().getPlayer(args.get(0));
        if (target == null) {
            error(player, "That player doesn't exist.");
        } else {
            ChatPlayer whisper = plugin.getPlayerManager().getPlayer(player.getName());
            ChatPlayer whispee = plugin.getPlayerManager().getPlayer(target.getName());
            whisper.whisperTo(whispee, message.toString()); // player sending the whisper
            whispee.whisperFrom(whisper, message.toString()); // player receiving the whisper
            plugin.whisper(whisper, whispee, message.toString()); // log the message
        }
    }

}
