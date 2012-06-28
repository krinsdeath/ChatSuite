package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
public class ChannelSetCommand extends ChannelCommand {

    private enum Option {
        PUBLIC(false),
        PERMANENT(false),
        ENABLED(false),
        COLOR(""),
        NETWORK(""),
        CHANNEL(""),
        OWNER("")
        ;
        Object value;
        Option(Object val) {
            value = val;
        }

        public Object getValue() {
            return value;
        }

        public static Option forName(String name) throws TypeNotPresentException {
            for (Option o : values()) {
                if (o.name().equalsIgnoreCase(name)) {
                    return o;
                }
            }
            throw new TypeNotPresentException(name, new Throwable("No such option: " + name));
        }
    }

    public ChannelSetCommand(ChatCore instance) {
        super(instance);
        setName("ChatSuite: Channel Set");
        setCommandUsage("/ch set [channel] [option] [value]");
        setPageHeader(0, "Channel Set [Basic]", "/ch set   ");
        addToPage(0, "staff     " + ChatColor.GOLD + "color     " + ChatColor.YELLOW + "WHITE");
        addToPage(0, "staff     " + ChatColor.GOLD + "permanent " + ChatColor.YELLOW + "true");
        addToPage(0, "staff     " + ChatColor.GOLD + "owner     " + ChatColor.YELLOW + "Njodi");
        addToPage(0, "staff     " + ChatColor.GOLD + "public    " + ChatColor.YELLOW + "true");
        setPageHeader(1, "Channel Set [IRC]", "/ch set   ");
        addToPage(1, "staff     " + ChatColor.GOLD + "network   " + ChatColor.YELLOW + "esper");
        addToPage(1, "staff     " + ChatColor.GOLD + "channel   " + ChatColor.YELLOW + "#chatsuite");
        addToPage(1, "staff     " + ChatColor.GOLD + "enabled   " + ChatColor.YELLOW + "true");
        addToPage(1, "staff     " + ChatColor.GOLD + "key       " + ChatColor.YELLOW + "password");
        setArgRange(1, 3);
        addKey("chatsuite channel set");
        addKey("chat channel set");
        addKey("channel set");
        addKey("ch set");
        addKey("chs");
        setPermission("chatsuite.channel.set", "Allows this user to set channel options.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (args.get(0).equals("?") || args.get(0).equals("help")) {
            showPage(sender, (args.size() > 1 ? Integer.parseInt(args.get(1)) : 0));
            return;
        }
        if (args.size() < 2) {
            error(sender, "Invalid parameter count.");
            return;
        }
        Option o;
        try {
            o = Option.forName(args.get(1));
        } catch (TypeNotPresentException e) {
            error(sender, e.getLocalizedMessage());
            return;
        }
        Object value = null;
        if (o.getValue() instanceof Boolean) {
            try {
                value = Boolean.parseBoolean(args.get(2));
            } catch (NumberFormatException e) {
                value = false;
            }
        } else if (o.getValue() instanceof String) {
            value = args.get(2);
        }
        Channel chan = manager.getChannel(args.get(0));
        if (chan != null && chan.canEdit(sender) && chan.set(o.name().toLowerCase(), value)) {
            message(sender, "Setting saved.");
        } else {
            error(sender, "Setting failed.");
        }
    }

}
