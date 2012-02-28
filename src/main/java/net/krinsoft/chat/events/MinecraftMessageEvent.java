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

    private final String network;
    private final String channel;
    private final String message;

    public MinecraftMessageEvent(String net, String chn, String msg) {
        network = net;
        channel = chn;
        message = msg;
    }

    /**
     * The name of the network that this message is being sent to
     * @return The name of the network
     */
    public String getNetwork() {
        return network;
    }

    /**
     * The name of the channel this message is being sent to
     * @return The channel name
     */
    public String getChannel() {
        return channel;
    }

    /**
     * The full, parsed message that is being sent
     * @return The message
     */
    public String getMessage() {
        return message;
    }

}
