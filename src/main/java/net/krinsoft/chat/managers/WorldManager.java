package net.krinsoft.chat.managers;

import com.onarandombox.MultiverseCore.MVWorld;
import com.onarandombox.MultiverseCore.MultiverseCore;
import java.util.HashMap;
import net.krinsoft.chat.ChatCore;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author krinsdeath
 */
public class WorldManager {
    private ChatCore plugin;

    private HashMap<String, ChatWorld> worlds = new HashMap<String, ChatWorld>();
    private HashMap<String, String> aliases = new HashMap<String, String>();

    public WorldManager(ChatCore plugin) {
        this.plugin = plugin;
        this.buildWorldMap();
    }

    private void buildWorldMap() {
        Plugin tmp = plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (tmp != null) {
            plugin.debug("Found Multiverse! Registering aliases...");
            MultiverseCore multiverse = (MultiverseCore) tmp;
            for (MVWorld mvworld : multiverse.getMVWorlds()) {
                plugin.debug("Registering alias for " + mvworld.getName());
                aliases.put(mvworld.getName(), mvworld.getColoredWorldString());
            }
        }
        for (World world : plugin.getServer().getWorlds()) {
            if (aliases.containsKey(world.getName())) {
                continue;
            }
            aliases.put(world.getName(), world.getName());
        }
    }

    public String getAlias(String world) {
        return aliases.get(world);
    }
}
