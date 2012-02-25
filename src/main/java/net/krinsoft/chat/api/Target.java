package net.krinsoft.chat.api;

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

}
