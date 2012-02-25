package net.krinsoft.chat.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author krinsdeath
 */
public class MinecraftQuitEvent extends Event {

    @Override
    public HandlerList getHandlers() { return HANDLERS; }
    public static HandlerList getHandlerList() { return HANDLERS; }
    private final static HandlerList HANDLERS = new HandlerList();

    private final String nickname;

    public MinecraftQuitEvent(String nick) {
        nickname = nick;
    }

    /**
     * Gets the nickname of the player that just joined Minecraft
     * @return The player's nickname
     */
    public String getNickname() {
        return nickname;
    }
}
