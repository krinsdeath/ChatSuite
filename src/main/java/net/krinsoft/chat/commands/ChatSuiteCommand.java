package net.krinsoft.chat.commands;

import com.pneumaticraft.commandhandler.Command;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public abstract class ChatSuiteCommand extends Command {
    protected ChatCore plugin;

    public ChatSuiteCommand(ChatCore plugin) {
        super(plugin);
        this.plugin = plugin;
    }

}
