/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.HashMap;
import java.util.List;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Krin
 */
public class WorldManager {
    private ChatCore plugin;
    private HashMap<String, String> aliases = new HashMap<String, String>();
    private HashMap<String, ChatWorld> worlds = new HashMap<String, ChatWorld>();

    public WorldManager(ChatCore aThis) {
        plugin = aThis;
        fetchWorlds();
        fetchAliases();
    }

    public String getAlias(String world) {
        return (aliases.containsKey(world) ? aliases.get(world) : world);
    }

    public ChatWorld getWorld(String world) {
        return worlds.get(world);
    }

    public List<ChatWorld> getWorlds() {
        return (List<ChatWorld>) worlds.values();
    }

    private void fetchWorlds() {
        for (World w : plugin.getServer().getWorlds()) {
            worlds.put(w.getName(), new ChatWorld(plugin, w.getName()));
        }
    }

    private void fetchAliases() {
        Plugin tmp = plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (tmp != null) {
            plugin.debug("Found Multiverse-Core! Registering aliases...");
            MultiverseCore multiverse = (MultiverseCore) tmp;
            for (MVWorld mv : multiverse.getMVWorlds()) {
                aliases.put(mv.getName(), mv.getColoredWorldString());
            }
        }
    }

}
