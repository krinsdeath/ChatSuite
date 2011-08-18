package net.krinsoft.chat.managers;

import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public class ChatWorld implements Target {
    private String name;
    private boolean whisperAllowed;
    private boolean chatAllowed;

    public ChatWorld(ChatCore plugin, String name) {
        this.name = name;
        this.whisperAllowed = plugin.getWorldNode(name).getBoolean("whisper_allowed", true);
        this.chatAllowed = plugin.getWorldNode(name).getBoolean("chat_allowed", true);
    }

    @Override
    public String getName() {
        return this.name;
    }

}
