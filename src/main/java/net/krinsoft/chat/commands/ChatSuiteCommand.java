package net.krinsoft.chat.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author krinsdeath
 */
public abstract class ChatSuiteCommand extends Command {
    protected ChatCore plugin;

    public ChatSuiteCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    public boolean validateSender(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage(ChatColor.RED + "[ChatSuite] This command cannot be used from the console.");
            return false;
        }
        return true;
    }

    public void message(Player player, String message) {
        player.sendMessage(ChatColor.GREEN + "[ChatSuite] " + message);
    }

    public void error(Player player, String message) {
        player.sendMessage(ChatColor.RED + "[ChatSuite] " + message);
    }

}
