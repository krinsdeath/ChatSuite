package net.krinsoft.chat.managers;

import java.util.HashMap;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public class PlayerManager {
    private ChatCore plugin;
    private HashMap<String, ChatPlayer> players = new HashMap<String, ChatPlayer>();

    public PlayerManager(ChatCore plugin) {
        this.plugin = plugin;
    }

    /**
     * determines whether the specified player has been registered or not
     * @param player
     * the player's name
     * @return
     * true if the player is registered, false if not
     */
    public boolean isRegistered(String player) {
        return players.containsKey(player);
    }

    /**
     * Gets the ChatPlayer data instance for the specified player
     * @param player
     * the name of the player
     * @return
     * the ChatPlayer instance
     */
    public ChatPlayer getPlayer(String player) {
        if (isRegistered(player)) {
            return players.get(player);
        } else {
            return null;
        }
    }
    
    /**
     * Attempts to inject a change after the specified player's format location
     * this changes the raw format string for this player only
     * no changes to this player's format string are saved
     * @param player
     * the player to inject a change
     * @param location
     * the place to inject a change
     * <pre>
     *   prefix = inject a change after the player's prefix tag
     *   suffix = inject a change after the player's suffix tag
     *   name = inject a change after the player's name or display name
     *   world = inject a change after the player's world
     *   channel = inject a change after the player's channel tag
     *   afk = inject a change after the player's afk tag
     * </pre>
     * @param change
     * the change to affect
     * @return
     * true if the change occurred, false if it didn't
     */
    public boolean injectAfter(String player, String location, String change) {
        return false;
    }

    /**
     * Attempts to inject a change before the specified player's format location
     * this changes the raw format string for this player only
     * changes made to the format string last until reverted
     * @param player
     * the player to inject a change
     * @param location
     * the location to inject a change
     * <pre>
     *   prefix = inject a change before the player's prefix tag
     *   suffix = inject a change before the player's suffix tag
     *   name = inject a change before the player's name or display name
     *   world = inject a change before the player's world
     *   channel = inject a change before the player's channel tag
     *   afk = inject a change before the player's afk tag
     * </pre>
     * @param change
     * @return
     */
    public boolean injectBefore(String player, String location, String change) {
        if (location.equalsIgnoreCase("prefix")) {
        } else if (location.equalsIgnoreCase("suffix")) {
        } else if (location.equalsIgnoreCase("name")) {
        } else if (location.equalsIgnoreCase("world")) {
        } else if (location.equalsIgnoreCase("channel")) {
        } else if (location.equalsIgnoreCase("afk")) {
        }
        return false;
    }

}
