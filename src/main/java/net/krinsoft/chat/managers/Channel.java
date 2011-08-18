package net.krinsoft.chat.managers;

import java.util.ArrayList;
import java.util.List;
import net.krinsoft.chat.ChatCore;

/**
 *
 * @author krinsdeath
 */
final class Channel implements Target {
    private ChatCore plugin;
    private String name;

    private List<String> occupants = new ArrayList<String>();

    public Channel(ChatCore plugin, String name, String player) {
        this.plugin = plugin;
        this.name = name;
        this.addPlayer(player);
    }

    @Override
    public String getName() {
        return this.name;
    }

    protected List<String> getOccupants() {
        return this.occupants;
    }

    protected void addPlayer(String player) {
        if (!this.occupants.contains(player)) {
            this.occupants.add(player);
            plugin.debug(player + " added to '" + this.name + "'");
        }
    }

    protected void removePlayer(String player) {
        if (this.occupants.contains(player)) {
            this.occupants.remove(player);
            plugin.debug(player + " removed from '" + this.name + "'");
        }
    }

}
