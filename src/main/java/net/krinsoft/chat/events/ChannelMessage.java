package net.krinsoft.chat.events;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.event.Event;

/**
 *
 * @author Krin
 */
public class ChannelMessage extends Event {
    private final Channel channel;
    private final ChatPlayer source;
    private final String message;

    public ChannelMessage(ChatCore plugin, Channel target, String source, String message) {
        super("ChatSuiteChannelMessage");
        this.channel = target;
        this.source = plugin.getPlayer(source);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public ChatPlayer getSource() {
        return this.source;
    }

    public Channel getTarget() {
        return this.channel;
    }
}
