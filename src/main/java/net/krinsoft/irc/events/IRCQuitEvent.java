package net.krinsoft.irc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
public class IRCQuitEvent extends Event {

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    private final static HandlerList HANDLERS = new HandlerList();

    private final String message;

    public IRCQuitEvent(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }

}
