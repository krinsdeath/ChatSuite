package net.krinsoft.chat;

import com.pneumaticraft.commandhandler.PermissionsInterface;
import java.util.List;
import org.bukkit.command.CommandSender;

/**
 *
 * @author krinsdeath
 */
public class ChatPermissions implements PermissionsInterface {
    private ChatCore plugin;

    public ChatPermissions(ChatCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(CommandSender cs, String node, boolean opRequired) {
        if (node == null) { return false; }
        if (opRequired && !cs.isOp()) { return false; }
        if (cs.hasPermission(node)) { return true; }
        if (cs.isPermissionSet(node) && !cs.hasPermission(node)) { return false; }
        if (!cs.isPermissionSet(node) && cs.hasPermission("chatsuite.*")) { return true; }
        return false;
    }

    @Override
    public boolean hasAnyPermission(CommandSender cs, List<String> list, boolean opRequired) {
        if (opRequired && !cs.isOp()) { return false; }
        for (String node : list) {
            if (node == null) { continue; }
            if (cs.hasPermission(node)) { return true; }
            if (!cs.isPermissionSet(node) && cs.hasPermission("chatsuite.*")) { return true; }
        }
        return false;
    }

    @Override
    public boolean hasAllPermission(CommandSender cs, List<String> list, boolean opRequired) {
        if (opRequired && !cs.isOp()) { return false; }
        for (String node : list) {
            if (node == null) { return false; }
            if (cs.isPermissionSet(node) && !cs.hasPermission(node)) { return false; }
        }
        return true;
    }

}
