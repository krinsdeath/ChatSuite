package net.krinsoft.chat.api;

import org.bukkit.entity.Player;

import java.util.Set;

/**
 *
 * @author Krin
 */
public interface Target {

    /**
     * Gets the name of this target
     * @return The name of the target
     */
    public String getName();

    /**
     * Attempts to saves all of this target's settings to disk
     */
    public void persist();

    /**
     * Sends a message to this target
     * @param message The message we're sending
     */
    public void sendMessage(String message);

    /**
     * Checks whether this target is muted or not.
     * @return true if the target is muted, otherwise false
     */
    public boolean isMuted();

    /**
     * Toggles whether or not this target can send or receive messages.
     */
    public void toggleMute();

    /**
     * Retrieves a set of player objects to which messages to this object should be sent
     * @return The list of players
     */
    public Set<Player> getRecipients();

}
