package net.krinsoft.chat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class MinecraftMessageEvent extends Event {

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    private final static HandlerList HANDLERS = new HandlerList();

    private final String target;
    private final String message;

    public MinecraftMessageEvent(String trg, String msg) {
        target = trg;
        message = msg;
    }

    /**
     * The name of the target for this message on IRC, channel or player
     * @return The name of the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * The full, parsed message that is being sent
     * @return The message
     */
    public String getMessage() {
        return message;
    }

}
