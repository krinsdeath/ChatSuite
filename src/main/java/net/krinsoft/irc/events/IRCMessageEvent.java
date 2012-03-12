package net.krinsoft.irc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
@SuppressWarnings("unused")
public class IRCMessageEvent extends Event {

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    private final static HandlerList HANDLERS = new HandlerList();

    private final String nickname;
    private final String irc_tag;
    private final String message;

    /**
     * Constructs a new IRC Message Event, to send to the players in Minecraft,
     * with the specified values
     * @param nick The nickname from which the message originated (on IRC)
     * @param tag The tag to use in the place of a player prefix ("[IRC]" by default)
     * @param msg The message body to send to the
     */
    public IRCMessageEvent(String nick, String tag, String msg) {
        nickname = nick;
        message  = msg;
        irc_tag  = tag;
    }

    public String getNickname() {
        return nickname;
    }

    public String getTag() {
        return irc_tag;
    }

    public String getMessage() {
        return message;
    }

}
