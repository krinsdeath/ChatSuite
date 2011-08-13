package net.krinsoft.chat.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author krinsdeath
 */
public class AfkCommand extends ChatSuiteCommand {

    public AfkCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("Sets your AFK status.");
        this.setCommandUsage("/cs afk \"[message]\"");
        this.addKey("csa");
        this.addKey("csafk");
        this.addKey("afk");
        this.setPermission("chatsuite.afk", "Allows you to set your AFK status.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        ChatPlayer p = plugin.getPlayer((Player) sender);
        if (args.isEmpty()) {
            p.toggleAfk(plugin.getLocaleManager().getAfkDefault(p.getLocale()));
        } else {
            if (args.get(0).equals("afk")) {
                if (args.size() > 1) {
                    p.toggleAfk(args.get(1));
                }
            } else {
                p.toggleAfk(args.get(0));
            }
        }
        if (p.isAfk()) {
            ColoredMessage message = buildMessage(plugin.getLocaleManager().getAfkLines(p.getLocale(), true));
            for (String line : message.getContents()) {
                line = line.replaceAll("%msg", p.getAwayMessage());
                sender.sendMessage(line);
            }
        } else {
            ColoredMessage message = buildMessage(plugin.getLocaleManager().getAfkLines(p.getLocale(), false));
            for (String line : message.getContents()) {
                line = line.replaceAll("%msg", p.getAwayMessage());
                sender.sendMessage(line);
            }
        }
    }

    protected ColoredMessage buildMessage(Object obj) {
        if (obj instanceof List) {
            return new ColoredMessage((List<String>) obj);
        } else if (obj instanceof String) {
            return new ColoredMessage(Arrays.asList(obj.toString()));
        } else {
            return new ColoredMessage(new ArrayList<String>());
        }
    }

}
