/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.listeners;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.targets.ChatPlayer;
import net.krinsoft.chat.events.ChannelMessage;
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
        if (e instanceof ChannelMessage) {
            ChannelMessage event = (ChannelMessage) e;
            onChannelMessage(event);
        } else if (e instanceof WhisperMessage) {
            WhisperMessage event = (WhisperMessage) e;
            onWhisperMessage(event);
        }
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
        ChatPlayer source = event.getPlayer();
        ChatPlayer target = event.getTarget();
        String message = event.getMessage();
        if (event.isWhisperAllowed()) {
            source.whisper("whisper_send", target.getName(), message);
            target.whisper("whisper_receive", source.getName(), message);
        }
    }

}
