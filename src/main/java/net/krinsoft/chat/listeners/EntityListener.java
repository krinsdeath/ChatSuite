package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
public class EntityListener extends org.bukkit.event.entity.EntityListener {
    private ChatCore plugin;

    public EntityListener(ChatCore aThis) {
        plugin = aThis;
    }

}
