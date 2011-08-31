/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.commands;

import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.util.ColoredMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;

/**
 *
 * @author Krin
 */
public class LocaleCommand extends ChatSuiteCommand {

    public LocaleCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = (ChatCore) plugin;
        this.setName("chatsuite locale");
        this.setCommandUsage("/chatsuite locale [locale]");
        this.setArgRange(1, 1);
        this.addKey("chatsuite locale");
        this.addKey("cs locale");
        this.addKey("cs loc");
        this.setPermission("chatsuite.locale", "Allows the user to set his localization settings.", PermissionDefault.TRUE);
    }

    @Override
    public void runCommand(CommandSender sender, List<String> args) {
        if (!(sender instanceof Player)) {
            return;
        }
        ChatPlayer p = plugin.getPlayerManager().getPlayer((Player)sender);
        if (plugin.getConfigManager().getPluginNode().getStringList("locales", null).contains(args.get(0).toLowerCase())) {
            p.setLocale(args.get(0).toLowerCase());
            ColoredMessage msg = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "locale_changed"));
            for (String line : msg.getContents()) {
                sender.sendMessage(line);
            }
        } else {
            ColoredMessage msg = new ColoredMessage(plugin.getLocaleManager().getMessage(p.getLocale(), "no_such_locale"));
            for (String line : msg.getContents()) {
                sender.sendMessage(line);
            }
        }
    }

}
