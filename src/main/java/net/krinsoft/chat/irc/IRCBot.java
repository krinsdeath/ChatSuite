package net.krinsoft.chat.irc;

import net.krinsoft.chat.ChatCore;
import net.krinsoft.chat.api.Manager;
import net.krinsoft.chat.events.IRCMessageEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author krinsdeath
 */
public class IRCBot implements Manager {
    private final static Pattern    NICK                    = Pattern.compile("\\[nick\\]");

    private ChatCore                plugin;
    private FileConfiguration       configuration;
    private File                    config;

    private volatile Socket         connection;
    private volatile BufferedReader reader;
    private volatile SocketReader   socketReader;
    private volatile BufferedWriter writer;
    private volatile SocketWriter   socketWriter;
    private String                  server;
    private int                     port;

    private String                  nickname;
    private String                  identity;
    private String                  realname;

    private String                  channel;

    private List<String>            lines                   = new ArrayList<String>();

    // messages
    private String                  STARTUP;
    private String                  IRC_TAG;
    private String                  PLAYER_JOIN_IRC;
    private String                  PLAYER_JOIN_MINECRAFT;
    private String                  PLAYER_QUIT_IRC;
    private String                  PLAYER_QUIT_MINECRAFT;

    public IRCBot(ChatCore instance) throws Exception {
        plugin = instance;
        registerConfiguration();
        registerIRCConstants();
        createSocket();
        createThreads();
    }

    private class SocketReader extends Thread {

        public SocketReader(String name) {
            super(name);
        }

        public void run() {
            while (!isInterrupted()) {
                try {
                    read();
                    sleep(200);
                } catch (IOException e) {
                    plugin.warn("An error occurred while reading from the Socket.");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new ThreadCleanupException("Reader Thread interrupted by kill()... done!");
                }
            }
        }

        void read() throws IOException {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("PING")) {
                    plugin.debug("Ping? Pong!");
                    add("PONG " + line.substring(5));
                    continue;
                }
                String nick, message, target = null;
                Reply reply = Reply.get(line.split(" ")[1]);
                if (reply == null) { continue; }
                switch (reply) {
                    case RPL_MYINFO:
                        add("JOIN " + channel);
                        msg(STARTUP);
                        break;
                    case JOIN:
                        nick = line.substring(1).split("!")[0];
                        msg(NICK.matcher(PLAYER_JOIN_IRC).replaceAll(nick));
                        break;
                    case PART:
                        nick = line.substring(1).split("!")[0];
                        msg(NICK.matcher(PLAYER_QUIT_IRC).replaceAll(nick));
                        break;
                    case PRIVMSG:
                        nick = line.substring(1).split("!")[0];
                        message = line.substring(line.substring(1).indexOf(":", 1)+2);
                        if (message.startsWith(".say")) {
                            target = channel;
                            message = message.substring(5);
                        } else if (message.startsWith(".msg")) {
                            target = message.split(" ")[1];
                            message = message.substring(message.indexOf(" ", message.indexOf(target)));
                        }
                        if (target != null) {
                            IRCMessageEvent event = new IRCMessageEvent(nick, IRC_TAG, message);
                            plugin.getServer().getPluginManager().callEvent(event);
                        }
                        break;
                    default:
                        break;
                }
                plugin.debug(line);
            }
        }

        void kill() throws IOException {
            read();
            interrupt();
        }

    }

    private class SocketWriter extends Thread {

        public SocketWriter(String name) {
            super(name);
        }

        public void run() {
            while (!isInterrupted()) {
                try {
                    write();
                    sleep(200);
                } catch (IOException e) {
                    plugin.warn("An error occurred while writing to the Socket.");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    throw new ThreadCleanupException("Writer Thread Interrupted by kill()... done!");
                }
            }
        }

        void write() throws IOException {
            for (String line : lines) {
                writer.write(line);
            }
            writer.flush();
            lines.clear();
        }

        void kill() throws IOException {
            write();
            interrupt();
        }

    }

    public void clean() {
        try {
            add("QUIT :Server is stopping...");
            socketWriter.kill();
            socketReader.kill();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public FileConfiguration getConfig() {
        if (configuration == null) {
            configuration = YamlConfiguration.loadConfiguration(config);
            configuration.setDefaults(YamlConfiguration.loadConfiguration(config));
        }
        return configuration;
    }

    public void saveConfig() {
        try {
            getConfig().save(config);
        } catch (Exception e) {
            plugin.warn("An error occurred while saving 'irc.yml'");
        }
    }

    public ChatCore getPlugin() {
        return plugin;
    }

    public void registerConfiguration() {
        config = new File(plugin.getDataFolder(), "irc.yml");
        if (!config.exists()) {
            getConfig().setDefaults(YamlConfiguration.loadConfiguration(plugin.getClass().getResourceAsStream("/defaults/irc.yml")));
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
    }

    public void registerIRCConstants() {
        // connection settings
        server      = getConfig().getString("server");
        port        = getConfig().getInt("port");
        // bot information
        nickname    = getConfig().getString("nickname");
        identity    = getConfig().getString("ident");
        realname    = getConfig().getString("realname");
        // channels
        channel     = getConfig().getString("channel");

        // messages
        STARTUP                 = getConfig().getString("messages.startup", "ChatSuite IRC Bot Initialized.");
        IRC_TAG                 = getConfig().getString("messages.tag", "[IRC]");
        PLAYER_JOIN_IRC         = getConfig().getString("messages.irc.join", "[nick] is now on IRC.");
        PLAYER_JOIN_MINECRAFT   = getConfig().getString("messages.mc.join", "[nick] has logged in.");
        PLAYER_QUIT_IRC         = getConfig().getString("messages.irc.quit", "[nick] has quit IRC.");
        PLAYER_QUIT_MINECRAFT   = getConfig().getString("messages.mc.quit", "[nick] has logged out.");
    }

    public void createSocket() throws Exception {
        // authenticate the connection
        add("NICK " + nickname);
        add("USER " + nickname + " net.krinsoft.chat.IRCBot " + identity + " :" + realname);
        // open the socket
        connection = new Socket(server, port);
        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
    }

    public void createThreads() {
        socketReader = new SocketReader("ChatSuite IRC Reader");
        socketWriter = new SocketWriter("ChatSuite IRC Writer");

        socketReader.start();
        socketWriter.start();
    }

    // get instance variables
    public String getChannel() {
        return channel;
    }


    // IRC methods
    // add lines to the socket writer
    public void add(String line) {
        lines.add(line + "\r\n");
    }

    public void msg(String line) {
        add("PRIVMSG " + channel + " :" + line);
    }

    public void join(String name) {
        msg(NICK.matcher(PLAYER_JOIN_MINECRAFT).replaceAll(name));
    }

    public void quit(String name) {
        msg(NICK.matcher(PLAYER_QUIT_MINECRAFT).replaceAll(name));
    }
}
