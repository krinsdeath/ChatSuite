package net.krinsoft.chat;

import net.krinsoft.chat.targets.Channel;

/**
 *
 * @author krinsdeath
 */
public class ChatWorld {
    private boolean whisperAllowed;

    public ChatWorld(ChatCore plugin, String world) {
        this.whisperAllowed = false;
    }

    public boolean isWhisperAllowed() {
        return whisperAllowed;
    }

}
