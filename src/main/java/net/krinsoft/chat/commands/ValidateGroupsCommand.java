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
public class ValidateGroupsCommand extends ChatSuiteCommand {

    public ValidateGroupsCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Group Update");
        setCommandUsage("/chatsuite validate groups");
        setArgRange(0, 0);
        addKey("chatsuite validate groups");
        addKey("chat validate groups");
        setPermission("chatsuite.validate.groups", "Validates the groups for all currently registered users.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        long time = System.currentTimeMillis();
        try {
            for (ChatPlayer p : plugin.getPlayerManager().getPlayers()) {
                if (p != null) {
                    p.getGroup();
                }
            }
            sender.sendMessage(ChatColor.GREEN + "All groups validated successfully. (" + (System.currentTimeMillis() - time) + "ms)");
        } catch (NullPointerException e) {
            sender.sendMessage(ChatColor.RED + "An error occurred while validating groups!");
            e.printStackTrace();
        }
    }

}
