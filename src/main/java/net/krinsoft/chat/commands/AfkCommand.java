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
 * @author krinsdeath (Jeff Wardian)
 */
public class AfkCommand extends ChatSuiteCommand {

    public AfkCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("Sets your AFK status.");
        this.setCommandUsage("/cs afk \"[message]\"");
        this.setArgRange(0, 1);
        this.addKey("cs afk");
        this.addKey("csa");
        this.addKey("csafk");
        this.setPermission("chatsuite.afk", "Allows you to set your AFK status.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        plugin.debug("Got to afk handler");
        ChatPlayer p = plugin.getPlayer((Player) sender);
        if (p == null) { return; }
        if (args.isEmpty()) {
            p.toggleAfk(plugin.getLocaleManager().getAfk(p.getLocale(), "generic").toString());
        } else {
            p.toggleAfk(args.get(0));
        }
        if (p.isAfk()) {
            ColoredMessage message = buildMessage(plugin.getLocaleManager().getAfk(p.getLocale(), "away"));
            for (String line : message.getContents()) {
                line = line.replaceAll("%msg", p.getAwayMessage());
                sender.sendMessage(line);
            }
        } else {
            ColoredMessage message = buildMessage(plugin.getLocaleManager().getAfk(p.getLocale(), "back"));
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
