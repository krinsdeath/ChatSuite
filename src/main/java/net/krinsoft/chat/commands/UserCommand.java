package net.krinsoft.chat.commands;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.PlayerManager;

/**
 * @author krinsdeath
 */
public abstract class UserCommand extends ChatSuiteCommand {
    protected PlayerManager manager;

    public UserCommand(ChatCore instance) {
        super(instance);
        manager = instance.getPlayerManager();
    }

}
