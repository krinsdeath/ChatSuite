package net.krinsoft.chat;

/**
 *
 * @author krinsdeath
 */
public class ChatWorld {
    private boolean whisperAllowed;

    public ChatWorld(ChatCore plugin, String world) {
        this.whisperAllowed = true;
    }

    public boolean isWhisperAllowed() {
        return whisperAllowed;
    }

}
