package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChannelManager;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
public abstract class ChannelCommand extends ChatSuiteCommand {
    protected ChannelManager manager;

    public ChannelCommand(ChatCore instance) {
        super(instance);
        plugin  = instance;
        manager = plugin.getChannelManager();
    }

}
