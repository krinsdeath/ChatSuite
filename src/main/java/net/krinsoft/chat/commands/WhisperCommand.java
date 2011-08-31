/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.events.WhisperMessage;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Krin
 */
public class WhisperCommand extends ChatSuiteCommand {

    public WhisperCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite whisper");
        this.setCommandUsage("/cs whisper [player] \"[message]\"");
        this.setArgRange(2, 2);
        this.addKey("chatsuite whisper");
        this.addKey("cs whisper");
        this.addKey("csw");
        this.setPermission("chatsuite.whisper", "Whispers a message to another user", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender cs, List<String> args) {
        Player target = plugin.getServer().getPlayer(args.get(0));
        if (target == null) {
            String l = plugin.getLocaleManager().getLocaleKey();
            if (cs instanceof Player) {
                l = plugin.getPlayerManager().getPlayer((Player)cs).getLocale();
            }
            ColoredMessage messages = buildMessage(plugin.getLocaleManager().getError(l, "invalid_target"));
            for (String line : messages.getContents()) {
                cs.sendMessage(line.replaceAll("%target", args.get(0)));
            }
            return;
        } else {
            WhisperMessage msg = new WhisperMessage(plugin, ((Player)cs).getName(), target.getName(), args.get(1));
            plugin.getServer().getPluginManager().callEvent(msg);
        }
    }

    private ColoredMessage buildMessage(Object obj) {
        if (obj instanceof List) {
            return new ColoredMessage((List<String>) obj);
        } else if (obj instanceof String) {
            return new ColoredMessage(new ArrayList<String>(Arrays.asList((String)obj)));
        } else {
            return new ColoredMessage(new ArrayList<String>());
        }
    }
}
