/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

/**
 *
 * @author krinsdeath
 */
public class CommandHandler {
    private ChatCore plugin;

    public CommandHandler(ChatCore aThis) {
        plugin = aThis;
    }

    public boolean handle(CommandSender cs, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("chatsuite")) {

        }
        return true;
    }

}
