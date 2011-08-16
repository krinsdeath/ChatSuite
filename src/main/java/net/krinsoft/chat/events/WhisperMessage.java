package net.krinsoft.chat.events;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import org.bukkit.event.Event;

/**
 *
 * @author Krin
 */
public class WhisperMessage extends Event {
    private ChatPlayer source;
    private ChatPlayer target;
    private String message;
    private boolean whisperAllowed;

    public WhisperMessage(ChatCore plugin, String source, String target, String message) {
        super("ChatSuiteWhisperMessage");
        this.source = plugin.getPlayerManager().getPlayer(source);
        this.target = plugin.getPlayerManager().getPlayer(target);
        this.message = message;
        this.whisperAllowed = plugin.getWorldManager().getWorld(this.target.getWorld()).isWhisperAllowed();
    }

    public ChatPlayer getPlayer() {
        return this.source;
    }

    public ChatPlayer getTarget() {
        return this.target;
    }

    public String getMessage() {
        return this.message;
    }

    public boolean isWhisperAllowed() {
        return this.whisperAllowed;
    }
}
