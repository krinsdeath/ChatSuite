package net.krinsoft.chat.commands;

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
public class HelpCommand extends ChatSuiteCommand {

    public HelpCommand(ChatCore plugin) {
        super(plugin);
        this.setName("How's about we get you rolling?");
        this.setCommandUsage("/cs help");
        this.setArgRange(0, 1);
        this.addKey("cs help");
        this.addKey("cshelp");
        this.addKey("csh");
        this.addKey("cs");
        this.setPermission("chatsuite.help", "Displays help regarding this plugin", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender cs, List<String> args) {
        ChatPlayer p = plugin.getPlayer((Player) cs);
        ColoredMessage msg = null;
        if (args.size() > 0) {
            if (args.get(0).equalsIgnoreCase("afk")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(p.getLocale(), "afk"));
            } else if (args.get(0).equalsIgnoreCase("chan")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(p.getLocale(), "chan"));
            } else if (args.get(0).equalsIgnoreCase("whisper")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(p.getLocale(), "whisper"));
            } else {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(p.getLocale(), "help"));
            }
        } else {
            msg = new ColoredMessage(plugin.getLocaleManager().getHelp(p.getLocale(), "help"));
        }
        for (String line : msg.getContents()) {
            cs.sendMessage(line);
        }
    }
}
