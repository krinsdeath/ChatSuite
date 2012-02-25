package net.krinsoft.chat;

import com.pneumaticraft.commandhandler.PermissionsInterface;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import java.util.List;

/**
 *
 * @author krinsdeath
 */
public class CSPermissions implements PermissionsInterface {

    public boolean hasPermission(CommandSender cs, String node, boolean bln) {
        return cs instanceof ConsoleCommandSender || (node != null && cs.hasPermission(node));
    }

    public boolean hasAnyPermission(CommandSender cs, List<String> list, boolean bln) {
        for (String node : list) {
            if (cs.hasPermission(node)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAllPermission(CommandSender cs, List<String> list, boolean bln) {
        for (String node : list) {
            if (!cs.hasPermission(node)) {
                return false;
            }
        }
        return true;
    }

}
