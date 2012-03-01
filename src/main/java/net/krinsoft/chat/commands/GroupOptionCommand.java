package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.PermissionDefault;

import java.util.List;

/**
 * @author krinsdeath
 */
enum GroupOption {
    WEIGHT,
    PREFIX,
    SUFFIX,
    NAME
    ;

    public String toString() {
        return name().toLowerCase();
    }

    public static GroupOption fromName(String name) {
        name = name.toUpperCase();
        for (GroupOption o : values()) {
            if (o.name().equals(name)) {
                return o;
            }
        }
        return null;
    }

}
public class GroupOptionCommand extends ChatSuiteCommand {

    public GroupOptionCommand(ChatCore plugin) {
        super(plugin);
        setName("ChatSuite: Group Option");
        setCommandUsage("/chat group [group] [option] [value]");
        // ---
        // group options
        setPageHeader(0, "Group Commands",   "/chat " + ChatColor.GOLD + "group     ");
        addToPage(0,                ChatColor.RED + "[group]   " + ChatColor.GOLD + "weight    " + ChatColor.YELLOW + "[int]     ");
        addToPage(0,                ChatColor.RED + "[group]   " + ChatColor.GOLD + "prefix    " + ChatColor.YELLOW + "[string]  ");
        addToPage(0,                ChatColor.RED + "[group]   " + ChatColor.GOLD + "suffix    " + ChatColor.YELLOW + "[string]  ");
        addToPage(0,                ChatColor.RED + "[group]   " + ChatColor.GOLD + "name      " + ChatColor.YELLOW + "[string]  ");
        // end examples
        setArgRange(2, 3);
        addKey("chatsuite group");
        addKey("chat group");
        addKey("cgroup");
        addKey("cg");
        setPermission("chatsuite.group.option", "Allows the setting of various group options.", PermissionDefault.OP);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        ConfigurationSection group = plugin.getGroupNode(args.get(0));
        if (group == null) {
            error(sender, "Invalid group.");
            return;
        }
        GroupOption option = GroupOption.fromName(args.get(1));
        if (option == null) {
            error(sender, "Invalid option.");
            return;
        }
        String arg = (args.size() == 3 ? args.get(2) : group.getString(option.toString()));
        try {
            switch (option) {
                case WEIGHT:
                    group.set("weight", (args.size() == 3 ? Integer.parseInt(args.get(2)) : 0));
                    break;
                default:
                    group.set(option.toString(), arg);
                    break;
            }
        } catch (NumberFormatException e) {
            error(sender, "Invalid parameter.");
            return;
        }
        Object val = group.get(option.toString());
        message(sender, "Set '" + ChatColor.AQUA + option.toString() + ChatColor.GREEN + "' for '" + ChatColor.AQUA + args.get(0) + ChatColor.GREEN + "' to '" + ChatColor.GOLD + val.toString() + ChatColor.GREEN + "'");
        plugin.log(">> " + sender.getName() + ": Set " + args.get(0) + "'s " + option.toString() + " to " + val.toString());
        plugin.saveConfig();
    }

}
