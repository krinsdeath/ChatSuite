package net.krinsoft.chat.events;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import org.bukkit.event.Event;

/**
 *
 * @author Krin
 */
public class WhisperMessage extends Event {
    private ChatPlayer source;
    private ChatPlayer target;
    private boolean whisperAllowed;

    public WhisperMessage(ChatCore plugin, String source, String target) {
        super("ChatSuiteWhisperMessage");
        this.source = plugin.getPlayer(source);
        this.target = plugin.getPlayer(target);
        this.whisperAllowed = plugin.getWorldManager().getWorld(this.target.getWorld()).isWhisperAllowed();
    }

    public ChatPlayer getPlayer() {
        return this.source;
    }

    public ChatPlayer getTarget() {
        return this.target;
    }

    public boolean isWhisperAllowed() {
        return this.whisperAllowed;
    }
}
