/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.krinsoft.chat;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import com.onarandombox.MultiverseCore.api.MultiverseWorld;
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
    private MVWorldManager MVWorldManager;

    public WorldManager(ChatCore aThis) {
        plugin = aThis;
        fetchWorlds();
        fetchAliases();
    }

    public String getAlias(String world) {
        return (aliases.containsKey(world) ? aliases.get(world) : world);
    }

    public void setAlias(String world, String alias) {
        aliases.put(world, alias);
    }

    public ChatWorld getWorld(String world) {
        String a = world;
        for (String w : aliases.keySet()) {
            if (aliases.get(w).equals(world)) {
                a = w;
            }
        }
        return worlds.get(a);
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
            MVWorldManager = multiverse.getMVWorldManager();
            for (MultiverseWorld mv : MVWorldManager.getMVWorlds()) {
                aliases.put(mv.getName(), mv.getColoredWorldString());
            }
        }
    }

}
