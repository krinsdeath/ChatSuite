package net.krinsoft.chat.targets;

import java.util.ArrayList;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.interfaces.Target;
import org.bukkit.entity.Player;

/**
 *
 * @author krinsdeath (Jeff Wardian)
 */
public class Channel implements Target {

    private List<String> occupants = new ArrayList<String>();
    private ChatCore plugin;
    private String name;
    private String type;

    public Channel(ChatCore plugin, String name, String type) {
        this.plugin = plugin;
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public String toNode() {
        return this.name.replaceAll("\\s", "_").toLowerCase();
    }

    public List<String> getOccupants() {
        return occupants;
    }

    public void addPlayer(String player) {
        occupants.add(player);
    }

    public void removePlayer(String player) {
        occupants.remove(player);
    }

    public void message(String sender, String message) {
        for (String player : occupants) {
            Player p = plugin.getServer().getPlayer(player);
            p.sendMessage(message);
        }
    }

    public boolean contains(String player) {
        return occupants.contains(player);
    }

    public boolean isPrivate() {
        return this.type.equalsIgnoreCase("private");
    }
}
