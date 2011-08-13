/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat.targets;

import java.util.ArrayList;
import java.util.List;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ChatPlayer;
import net.krinsoft.chat.interfaces.Target;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 *
 * @author Krin
 */
public class Channel implements Target {

    private List<String> occupants = new ArrayList<String>();
    private ChatCore plugin;
    private String name;

    public Channel(ChatCore plugin, String name) {
        this.plugin = plugin;
        this.name = name;
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

}
