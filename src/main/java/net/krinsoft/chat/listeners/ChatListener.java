/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import net.krinsoft.chat.events.ChannelMessage;
import net.krinsoft.chat.events.ChatMessage;
import net.krinsoft.chat.events.WhisperMessage;
import net.krinsoft.chat.targets.Channel;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

/**
 *
 * @author krinsdeath
 */
public class ChatListener extends CustomEventListener {
    private ChatCore plugin;

    public ChatListener(ChatCore aThis) {
        plugin = aThis;
    }

    @Override
    public void onCustomEvent(Event e) {
        if (e instanceof ChatMessage) {
            ChatMessage event = (ChatMessage) e;
            onChatMessage(event);
        } else if (e instanceof ChannelMessage) {
            ChannelMessage event = (ChannelMessage) e;
            onChannelMessage(event);
        } else if (e instanceof WhisperMessage) {
            WhisperMessage event = (WhisperMessage) e;
            onWhisperMessage(event);
        }
    }

    public void onChatMessage(ChatMessage event) {
    }

    public void onChannelMessage(ChannelMessage event) {
        ChatPlayer player = event.getSource();
        Channel c = event.getTarget();
        String msg = player.message(c, event.getMessage());
        if (msg != null) {
            for (String p : c.getOccupants()) {
                plugin.getServer().getPlayer(p).sendMessage(msg);
            }
        }
    }

    public void onWhisperMessage(WhisperMessage event) {
    }

}
