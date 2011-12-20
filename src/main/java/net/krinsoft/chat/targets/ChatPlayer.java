package net.krinsoft.chat.targets;

import com.herocraftonline.dev.heroes.Heroes;
import com.herocraftonline.dev.heroes.persistence.Hero;
import java.util.regex.Pattern;
import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.ConfigManager;
import net.krinsoft.chat.interfaces.Target;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author krinsdeath
 */
public class ChatPlayer implements Target {
    private final static Pattern SELF = Pattern.compile("(%n)");
    private final static Pattern SELF_DISPLAY = Pattern.compile("(%dn)");
    private final static Pattern TARGET = Pattern.compile("(%t)");
    private final static Pattern TARGET_DISPLAY = Pattern.compile("(%dt)");
    private final static Pattern PREFIX = Pattern.compile("(%p)");
    private final static Pattern GROUP = Pattern.compile("(%g)");
    private final static Pattern SUFFIX = Pattern.compile("(%s)");
    private final static Pattern FACTION = Pattern.compile("(%f)");
    private final static Pattern HEROES = Pattern.compile("(%h)");
    private final static Pattern AFK = Pattern.compile("(%afk)");
    private final static Pattern WORLD = Pattern.compile("(%w)");
    private final static Pattern WHISPER_SEND = Pattern.compile("(%!ws|%!whisper_send)");
    private final static Pattern WHISPER_RECEIVE = Pattern.compile("(%!wr|%!whisper_receive)");
    private final static Pattern MESSAGE = Pattern.compile("(%m)");
    private final static Pattern CHANNEL = Pattern.compile("(%c)");
    private final static Pattern COLOR = Pattern.compile("(?i)&([0-F])");

    protected enum Type {
        NORMAL(0, "normal"),
        WHISPER_SEND(1, "whisper_send"),
        WHISPER_RECEIVE(2, "whisper_receive"),
        GLOBAL(3, "global");

        private int id;
        private String type;

        private Type(int id, String type) {
            this.id = id;
            this.type = type;
        }

        public String getName() {
            return this.type;
        }

        public static Type getTypeById(int id) {
            switch (id) {
                case 0: return NORMAL;
                case 1: return WHISPER_SEND;
                case 2: return WHISPER_RECEIVE;
                case 3: return GLOBAL;
                default: return NORMAL;
            }
        }

        public static Type getTypeByName(String name) {
            name = name.replaceAll("[\\s]", "_");
            for (Type type : Type.values()) {
                if (type.getName().equalsIgnoreCase(name)) {
                    return type;
                }
            }
            return null;
        }
    }

    private static ChatCore plugin; // instance of the main plugin
    private static Heroes heroes; // instance of the Heroes plugin

    // initialize the plugin instance
    public static void init(ChatCore aThis) {
        plugin = aThis; // initialize the plugin instance
        Plugin tmp = plugin.getServer().getPluginManager().getPlugin("Heroes");
        if (tmp != null) {
            plugin.debug("Found Heroes! Hooking...");
            heroes = (Heroes) tmp;
        }
    }

    // general stuff
    private String name; // name of the player
    private String world; // the player's current world
    private String group; // the player's group
    private String channel;
    private String global; // the player's raw global string
    private String format; // the player's raw format string
    private String send; // the player's raw whisper send string
    private String receive; // the player's raw whisper receive string

    private boolean afk; // whether the player is afk or not
    private String afkMessage; // the message the player set when they went afk

    private String locale;

    public ChatPlayer(Player p, String loc) {
        ConfigManager config = plugin.getConfigManager();
        this.name = p.getName();
        this.world = p.getWorld().getName();
        this.locale = loc;
        if (config.getPluginNode().getString("default_channel", "world").equalsIgnoreCase("world")) {
            this.channel = this.world;
        } else {
            this.channel = config.getPluginNode().getString("global_channel_name", "Global");
        }
        int weight = 0;
        for (String key : config.getGroups()) {
            int i = config.getGroupNode(key).getInt("weight", 1);
            if ((p.hasPermission("chatsuite.groups." + key) || p.hasPermission("group." + key)) && i > weight) {
                weight = i;
                this.group = key;
            }
        }
        plugin.debug("Player " + this.name + " set to group '" + this.group + "' (Weight: " + weight + ")");
        setGroup(this.group);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return this.channel;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public String getWorld() {
        return this.world;
    }

    public final void setGroup(String group) {
        this.group = group;
        updateFormat();
    }

    public final void updateFormat() {
        ConfigurationSection node = plugin.getConfigManager().getGroupNode(this.group);
        global = node.getString("format.global", "");
        format = node.getString("format.message", "");
        send = node.getString("format.whisper_send", "");
        receive = node.getString("format.whisper_receive", "");
    }

    public String getField(String field) {
        return plugin.getConfigManager().getGroupNode(this.group).getString(field);
    }

    protected String getFormat(Type t) {
        switch (t) {
            case NORMAL: return this.format;
            case WHISPER_SEND: return this.send;
            case WHISPER_RECEIVE: return this.receive;
            case GLOBAL: return this.global;
            default: return null;
        }
    }
    
    public String getFormat(String t) {
        Type type = Type.getTypeByName(t);
        return getFormat(type);
    }

    public String getFormat(int id) {
        Type type = Type.getTypeById(id);
        return getFormat(type);
    }

    protected boolean setFormat(Type t, String msg) {
        if (msg != null && (msg.contains("%n") || msg.contains("%d"))) {
            switch (t) {
                case NORMAL: return setRawMessageFormat(msg);
                case WHISPER_SEND: return setRawWhisperSend(msg);
                case WHISPER_RECEIVE: return setRawWhisperReceive(msg);
                case GLOBAL: return setRawGlobal(msg);
                default: return false;
            }
        } else {
            return false;
        }
    }

    public boolean setFormat(String name, String msg) {
        if (msg != null && (msg.contains("%n") || msg.contains("%d")) && (msg.contains("%m"))) {
            Type t = Type.getTypeByName(name);
            return setFormat(t, msg);
        } else {
            return false;
        }
    }

    public boolean setFormat(int id, String msg) {
        if (msg != null && (msg.contains("%n") || msg.contains("%d")) && (msg.contains("%m"))) {
            Type t = Type.getTypeById(id);
            return setFormat(t, msg);
        } else {
            return false;
        }
    }

    private boolean setRawMessageFormat(String msg) {
        this.format = msg;
        return true;
    }

    private boolean setRawWhisperSend(String msg) {
        this.send = msg;
        return true;
    }

    private boolean setRawWhisperReceive(String msg) {
        this.receive = msg;
        return true;
    }

    private boolean setRawGlobal(String msg) {
        this.global = msg;
        return true;
    }

    public String message(Target target, String message) {
        if (target instanceof Channel) {
            Channel channel = (Channel) target;
            if (channel.contains(this.name)) {
                plugin.debug("Channel: " + channel.getName());
                if (channel.getName().equals(plugin.getChannelManager().getGlobalChannel().getName())) {
                    return COLOR.matcher(channelMessage(Type.GLOBAL, channel, message)).replaceAll("\u00A7$1");
                } else {
                    return COLOR.matcher(channelMessage(Type.NORMAL, channel, message)).replaceAll("\u00A7$1");
                }
            } else {
                plugin.debug(this.name + " tried to chat on a channel they're not in!");
            }
        } else if (target instanceof ChatPlayer) {
            whisper(Type.WHISPER_SEND, ((ChatPlayer)target).getName(), message);
        }
        return null;
    }

    private String channelMessage(Type t, Channel chan, String message) {
        message = parse(getFormat(t), null, message);
        message = message.replaceAll("%c", plugin.getConfigManager().getGroupNode(this.group).getString("channel"));
        String rep = "";
        if (plugin.getWorldManager().getWorld(chan.getName()) != null) {
            rep = plugin.getWorldManager().getAlias(chan.getName());
        } else {
            rep = chan.getName();
        }
        message = message.replaceAll("%c", rep);
        return message;
    }

    protected void whisper(Type type, String target, String message) {
        String format = getFormat(type);
        String formatted = COLOR.matcher(parse(format, target, message)).replaceAll("\u00A7$1");
        plugin.getServer().getPlayer(this.name).sendMessage(formatted);
    }

    public void whisper(String type, String target, String message) {
        Type t = Type.getTypeByName(type);
        whisper(t, target, message);
    }

    public void whisper(int type, String target, String message) {
        Type t = Type.getTypeById(type);
        whisper(t, target, message);
    }

    private String parse(String format, String target, String message) {
        ConfigurationSection node = plugin.getConfigManager().getGroupNode(this.group);
        format = parsePrefix(format, node.getString("prefix"));
        format = parseGroup(format, node.getString("group"));
        format = parseSuffix(format, node.getString("suffix"));
        format = parseHero(format);
        format = parseWorld(format, plugin.getWorldManager().getAlias(this.world));
        format = parseTarget(format, target);
        format = parseSelf(format);
        format = parseAfk(format, node.getString("afk"));
        format = parseMessage(format, message);
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
        if (heroes != null) {
            try {
                Player p = plugin.getServer().getPlayer(name);
                Hero hero = heroes.getHeroManager().getHero(p);
                String hn = hero.getHeroClass().getName();
                format = HEROES.matcher(format).replaceAll(hn);
            } catch (NullPointerException e) {
                plugin.debug(e.getLocalizedMessage());
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
        format = SELF.matcher(format).replaceAll(this.name);
        format = SELF_DISPLAY.matcher(format).replaceAll(plugin.getServer().getPlayer(this.name).getDisplayName());
        return format;
    }

    private String parseTarget(String format, String target) {
        if (target != null) {
            format = TARGET.matcher(format).replaceAll(target);
            Player p = plugin.getServer().getPlayer(target);
            if (p != null) {
                format = TARGET_DISPLAY.matcher(format).replaceAll(p.getDisplayName());
            }
        }
        return format;
    }

    private String parseMessage(String format, String message) {
        format = MESSAGE.matcher(format).replaceAll(message);
        return format;
    }

    public boolean isAfk() {
        return this.afk;
    }

    public void toggleAfk(String msg) {
        this.afkMessage = msg;
        this.afk = !this.afk;
    }

    public String getAwayMessage() {
        return this.afkMessage;
    }

    public String getLocale() {
        return this.locale;
    }

    public void setLocale(String loc) {
        this.locale = loc;
    }
}
