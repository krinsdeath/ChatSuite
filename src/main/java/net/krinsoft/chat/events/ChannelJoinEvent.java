package net.krinsoft.chat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
public class ChannelJoinEvent extends Event {

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    private final static HandlerList HANDLERS = new HandlerList();

    private final String joined;
    private final String network;
    private final String channel;
    private final String nickname;

    public ChannelJoinEvent(String name, String net, String chan, String nick) {
        joined   = name;
        network  = net;
        channel  = chan;
        nickname = nick;
    }

    public String getJoined() {
        return joined;
    }

    public String getNetwork() {
        return network;
    }

    public String getChannel() {
        return channel;
    }

    public String getNickname() {
        return nickname;
    }
}
