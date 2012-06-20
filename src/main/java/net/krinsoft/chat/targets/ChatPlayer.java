package net.krinsoft.chat.targets;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import net.krinsoft.chat.PlayerManager;
import net.krinsoft.chat.api.Target;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author krinsdeath
 */
public class ChatPlayer implements Target {
    private final static Pattern SELF = Pattern.compile("(%name|%n)");
    private final static Pattern SELF_DISPLAY = Pattern.compile("(%display|%dn)");
    private final static Pattern TARGET = Pattern.compile("(%target|%t|%channel|%c)");
    private final static Pattern TARGET_DISPLAY = Pattern.compile("(%disp_target|%dt)");
    private final static Pattern PREFIX = Pattern.compile("(%prefix|%p)");
    private final static Pattern GROUP = Pattern.compile("(%group|%g)");
    private final static Pattern SUFFIX = Pattern.compile("(%suffix|%s)");
    private final static Pattern HEROES = Pattern.compile("(%hero|%h)");
    private final static Pattern AFK = Pattern.compile("(%afk)");
    private final static Pattern WORLD = Pattern.compile("(%world|%w)");
    private final static Pattern COLOR = Pattern.compile("(?i)&([0-F])");

    public enum Type {
        NORMAL("normal"),
        WHISPER_SEND("whisper_send"),
        WHISPER_RECEIVE("whisper_receive"),
        GLOBAL("global");

        private String type;

        private Type(String type) {
            this.type = type;
        }

        public String getName() {
            return this.type;
        }

    }

    // general stuff
    private PlayerManager manager;
    private String name;
    private String world; // the player's current world
    private String group; // the player's group
    private Target target;
    private Target reply;
    private boolean afk; // whether the player is afk or not
    private String afk_message;

    private List<String> auto_join;

    public ChatPlayer(PlayerManager man, Player p) {
        manager = man;
        name = p.getName();
        world = p.getWorld().getName();
        auto_join = manager.getConfig().getStringList(name + ".auto_join");
        if (manager.getConfig().get(name) != null) {
            String t = manager.getConfig().getString(getName() + ".target");
            if (t != null) {
                target = manager.getPlugin().getTarget(t);
            }
        }
        if (target == null) {
            target = manager.getPlugin().getChannelManager().getGlobalChannel();
        }
        getGroup();
        manager.getPlugin().debug("Player " + name + " set to group '" + group + "'");
    }

    @Override
    public String getName() {
        return name;
    }

    public String getGroup() {
        Player p = getPlayer();
        if (p == null) { return manager.getPlugin().getDefaultGroup(); }
        int weight = 0;
        for (String key : manager.getPlugin().getGroups()) {
            int i = manager.getPlugin().getGroupNode(key).getInt("weight");
            if ((p.hasPermission("chatsuite.groups." + key) || p.hasPermission("group." + key)) && i > weight) {
                weight = i;
                group = key;
            }
        }
        if (group == null) {
            group = p.isOp() ? manager.getPlugin().getOpGroup() : manager.getPlugin().getDefaultGroup();
        }
        manager.getPlugin().debug(name + ": Determined '" + group + "'.");
        return group;
    }

    @Override
    public void persist() {
        String t = (target instanceof Channel ? "c:" + target.getName() : "p:" + target.getName());
        manager.getConfig().set(getName() + ".target", t);
        manager.getConfig().set(getName() + ".auto_join", auto_join);
    }

    public Player getPlayer() {
        return manager.getPlugin().getServer().getPlayer(name);
    }

    public Target getTarget() {
        return target;
    }

    public void setTarget(Target t) {
        target = t;
        Player p = getPlayer();
        if (p != null) { p.sendMessage("[ChatSuite] Your target is now: " + target.getName()); }
    }

    public void setTarget(Target t, boolean val) {
        target = t;
    }

    public void join(Channel c) {
        if (!auto_join.contains(c.getName())) {
            sendMessage(c.getColoredName() + " added to Auto-Join list.");
        }
        auto_join.add(c.getName());
    }

    public void part(Channel c) {
        if (auto_join.contains(c.getName())) {
            sendMessage(c.getColoredName() + " removed from Auto-Join list.");
        }
        auto_join.remove(c.getName());
    }

    public List<String> getAutoJoinChannels() {
        return auto_join;
    }

    public String getAutoJoinChannelString() {
        StringBuilder ajoin = new StringBuilder();
        for (String ch : auto_join) {
            ajoin.append(ChatColor.AQUA).append(ch).append(ChatColor.WHITE).append(", ");
        }
        return ajoin.toString().substring(0, ajoin.toString().length()-2);
    }

    public void setWorld(String w) {
        world = w;
    }

    public String parse(String format) {
        ConfigurationSection node = manager.getPlugin().getGroupNode(group);
        format = parsePrefix(format, node.getString("prefix"));
        format = parseGroup(format, node.getString("group"));
        format = parseSuffix(format, node.getString("suffix"));
        format = parseHero(format);
        format = parseWorld(format, manager.getPlugin().getWorldManager().getAlias(world));
        format = parseTarget(format);
        format = parseSelf(format);
        format = parseAfk(format, node.getString("afk"));
        format = parseColors(format);
        return format;
    }

    private String parseAfk(String format, String afk) {
        if (this.afk) {
            format = AFK.matcher(format).replaceAll(afk);
            return format;
        } else {
            return AFK.matcher(format).replaceAll("");
        }
    }

    private String parsePrefix(String format, String prefix) {
        format = PREFIX.matcher(format).replaceAll(prefix);
        return format;
    }

    private String parseSuffix(String format, String suffix) {
        format = SUFFIX.matcher(format).replaceAll(suffix);
        return format;
    }

    private String parseHero(String format) {
        Plugin tmp = manager.getPlugin().getServer().getPluginManager().getPlugin("Heroes");
        if (tmp != null) {
            try {
                Heroes heroes = (Heroes) tmp;
                Player player = manager.getPlugin().getServer().getPlayer(getName());
                Hero hero = heroes.getHeroManager().getHero(player);
                String hero_name = hero.getHeroClass().getName();
                format = HEROES.matcher(format).replaceAll(hero_name);
            } catch (Exception e) {
                manager.getPlugin().warn("An error occurred while parsing a Hero class: " + e.getLocalizedMessage());
            }
        }
        return format;
    }

    private String parseGroup(String format, String group) {
        format = GROUP.matcher(format).replaceAll(group);
        return format;
    }

    private String parseWorld(String format, String world) {
        format = WORLD.matcher(format).replaceAll(world);
        return format;
    }

    private String parseSelf(String format) {
        format = SELF.matcher(format).replaceAll(getName());
        format = SELF_DISPLAY.matcher(format).replaceAll(getPlayer().getDisplayName());
        return format;
    }

    private String parseTarget(String format) {
        if (target != null) {
            if (target instanceof Channel) {
                format = TARGET.matcher(format).replaceAll(((Channel)target).getColoredName());
            } else {
                format = TARGET.matcher(format).replaceAll(target.getName());
            }
            Player p = getPlayer();
            if (p != null) {
                format = TARGET_DISPLAY.matcher(format).replaceAll(p.getDisplayName());
            }
        }
        return format;
    }

    private String parseColors(String format) {
        return COLOR.matcher(format).replaceAll("\u00A7$1");
    }

    public void toggleAfk(String message) {
        this.afk_message = message;
        this.afk = !this.afk;
    }

    ///////////////////////
    // MESSAGING METHODS //
    ///////////////////////

    public void sendMessage(String message) {
        Player p = getPlayer();
        if (p != null) { p.sendMessage(message); }
    }

    public void whisperTo(Target to, String message) {
        reply = to;
        String format = getFormattedWhisperTo(to);
        format = format.replaceAll("(%message|%m)", message);
        sendMessage(format);
        if (to instanceof ChatPlayer && ((ChatPlayer)to).afk) {
            sendMessage(to.getName() + " is afk: " + ((ChatPlayer)to).afk_message);
        }
    }

    public void whisperFrom(Target from, String message) {
        reply = from;
        String format = getFormattedWhisperFrom(from);
        format = format.replaceAll("(%message|%m)", message);
        sendMessage(format);
    }

    public void reply(String message) {
        if (reply == null) { return; }
        whisperTo(reply, message);
        ((ChatPlayer)reply).whisperFrom(this, message);
    }

    public String getFormattedWhisperTo(Target target) {
        String format = manager.getPlugin().getConfig().getString("groups." + ((ChatPlayer)target).getGroup() + ".format.to");
        if (format == null) {
            format = manager.getPlugin().getConfig().getString("format.to");
            if (format == null) {
                format = "&7[To] %t>>&F: %m";
            }
        }
        format = whisperParse(format);
        format = parse(format);
        return format;
    }

    public String getFormattedWhisperFrom(Target target) {
        String format = manager.getPlugin().getConfig().getString("groups." + ((ChatPlayer)target).getGroup() + ".format.from");
        if (format == null) {
            format = manager.getPlugin().getConfig().getString("format.from");
            if (format == null) {
                format = "&7[From] %t>>&F: %m";
            }
        }
        format = whisperParse(format);
        format = parse(format);
        return format;
    }

    protected String whisperParse(String format) {
        format = TARGET.matcher(format).replaceAll(reply.getName());
        format = TARGET_DISPLAY.matcher(format).replaceAll(reply.getName());
        return format;
    }

    public String getFormattedMessage() {
        String format = manager.getPlugin().getConfig().getString("groups." + group + ".format.message");
        if (format == null) {
            format = manager.getPlugin().getConfig().getString("format.message");
            if (format == null) {
                format = "[%t] %p %n&F: %m";
            }
        }
        format = parse(format);
        format = format.replaceAll("(%message|%m)", "%2\\$s");
        return format;
    }
}
