package net.krinsoft.chat.commands;

import com.pneumaticraft.commandhandler.Command;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
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
        this.setName("chatsuite help");
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
        String loc = null;
        if (cs instanceof Player) {
            loc = plugin.getPlayerManager().getPlayer((Player) cs).getLocale();
        } else {
            loc = plugin.getConfigManager().getPluginNode().getString("default_locale", "en");
        }
        String aliases = null;
        ColoredMessage msg = null;
        if (args.size() > 0) {
            if (args.get(0).equalsIgnoreCase("afk")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(loc, "afk"));
                Command c = null;
                for (Command cmd : plugin.getCommandHandler().getAllCommands()) {
                    if (cmd.getCommandName().equalsIgnoreCase("chatsuite afk")) {
                        c = cmd;
                         break;
                    }
                }
                aliases = buildAliases(c);
            } else if (args.get(0).equalsIgnoreCase("channel")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(loc, "channel"));
                Command c = null;
                for (Command cmd : plugin.getCommandHandler().getAllCommands()) {
                    if (cmd.getCommandName().equalsIgnoreCase("chatsuite channel")) {
                        c = cmd;
                        break;
                    }
                }
                aliases = buildAliases(c);
            } else if (args.get(0).equalsIgnoreCase("whisper")) {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(loc, "whisper"));
                Command c = null;
                for (Command cmd : plugin.getCommandHandler().getAllCommands()) {
                    if (cmd.getCommandName().equalsIgnoreCase("chatsuite whisper")) {
                        c = cmd;
                        break;
                    }
                }
                aliases = buildAliases(c);
            } else {
                msg = new ColoredMessage(plugin.getLocaleManager().getHelp(loc, "help"));
                aliases = buildAliases(this);
            }
        } else {
            msg = new ColoredMessage(plugin.getLocaleManager().getHelp(loc, "help"));
            aliases = buildAliases(this);
        }
        for (String line : msg.getContents()) {
            cs.sendMessage(line.replaceAll("%aliases", aliases));
        }
    }

    public String buildAliases(Command c) {
        StringBuilder aliases = new StringBuilder();
        aliases.append("/").append(c.getKeyStrings().get(0));
        for (int i = 1; i < c.getKeyStrings().size(); i++) {
            aliases.append(", /").append(c.getKeyStrings().get(i));
        }
        return aliases.toString().trim();
    }
}
