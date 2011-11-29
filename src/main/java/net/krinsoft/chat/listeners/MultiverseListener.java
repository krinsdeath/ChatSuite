package net.krinsoft.chat.listeners;

import com.onarandombox.MultiverseCore.event.MVWorldPropertyChangeEvent;
import net.krinsoft.chat.ChatCore;
import org.bukkit.event.CustomEventListener;
import org.bukkit.event.Event;

/**
 *
 * @author krinsdeath
 */
public class MultiverseListener extends CustomEventListener {
    private ChatCore plugin;

    public MultiverseListener(ChatCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onCustomEvent(Event e) {
        if (e instanceof MVWorldPropertyChangeEvent) {
            MVWorldPropertyChangeEvent event = (MVWorldPropertyChangeEvent) e;
            onWorldPropertyChange(event);
        }
    }

    public void onWorldPropertyChange(final MVWorldPropertyChangeEvent event) {
        plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                plugin.getWorldManager().setAlias(event.getWorld().getName(), event.getWorld().getColoredWorldString());
            }
        }, 1);
    }

}
