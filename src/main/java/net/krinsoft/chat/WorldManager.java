package net.krinsoft.chat;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Krin
 */
public class WorldManager {
    private ChatCore plugin;
    private MVWorldManager MVWorldManager;

    public WorldManager(ChatCore instance) {
        plugin = instance;
        fetchAliases();
    }

    public void clean() {
        MVWorldManager = null;
    }

    public String getAlias(String world) {
        if (MVWorldManager != null && MVWorldManager.isMVWorld(world)) {
            return MVWorldManager.getMVWorld(world).getColoredWorldString();
        }
        return world;
    }

    private void fetchAliases() {
        Plugin tmp = plugin.getServer().getPluginManager().getPlugin("Multiverse-Core");
        if (tmp != null) {
            plugin.debug("Found Multiverse-Core! Registering aliases...");
            MultiverseCore multiverse = (MultiverseCore) tmp;
            MVWorldManager = multiverse.getMVWorldManager();
        }
    }

}
