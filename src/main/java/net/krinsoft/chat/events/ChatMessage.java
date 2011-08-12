/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.events;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.Chat;
import org.bukkit.event.Event;

/**
 *
 * @author Krin
 */
public class ChatMessage extends Event {
    private final Chat target;
    private final String message;

    public ChatMessage(ChatCore plugin, Chat target, String message) {
        super("ChatSuiteChatMessage");
        this.target = target;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public Chat getTarget() {
        return this.target;
    }

}
