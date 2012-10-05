package net.krinsoft.irc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.krinsoft.irc.events.IRCJoinEvent;
import net.krinsoft.irc.events.IRCMessageEvent;
import net.krinsoft.irc.events.IRCQuitEvent;

/**
 * @author krinsdeath
 */
public class Connection {
    /**
     * The IRC handler for this connection
     */
    private final IRCBot                  manager;

    /**
     * Nickname parser for String consts
     */
    private final static Pattern    NICK            = Pattern.compile("\\[nick\\]");

    /**
     * This connection's socket for I/O
     */
    private volatile Socket         connection;

    /**
     * Input Stream from Socket
     */
    private final BufferedReader          bReader;

    /**
     * Output stream from Socket
     */
    private final BufferedWriter          bWriter;

    /**
     * Reader thread for this connection
     */
    private volatile IRCReader      reader;

    /**
     * Writer thread for this connection
     */
    private volatile IRCWriter      writer;

    /**
     * The network name for this connection
     */
    private final String                  network;

    /**
     * This connection's hostname
     */
    private final String                  hostname;

    /**
     * This connection's port number
     */
    private final int                     port;

    /**
     * This connection's main channel (global)
     */
    private final String                  channel;

    /**
     * This connection's channel key (optional)
     */
    private String                  key;

    /**
     * A list of lines to be written to this connection's socket
     */
    private final List<String>            lines           = new ArrayList<String>();

    private class IRCReader extends Thread {
        private final Connection master;

        public IRCReader(final Connection conn, final String name) {
            super(name);
            master = conn;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    readIRC();
                    sleep(200);
                } catch (final InterruptedException e) {
                    throw new ThreadCleanupException("IRCReader cleaned up successfully!");
                }
            }
        }

        public void readIRC() {
            try {
                String line;
                while ((line = bReader.readLine()) != null) {
                    if (line.startsWith("PING")) {
                        writeLine("PONG " + line.substring(5));
                        continue;
                    }
                    String nick, message, target = null;
                    final Reply reply = Reply.get(line.split(" ")[1]);
                    if (reply == null) { continue; }
                    switch (reply) {
                        case RPL_MYINFO:
                            writeLine("JOIN " + channel + " " + key);
                            auth();
                            chanMsg(channel, manager.STARTUP);
                            break;
                        case JOIN:
                            nick = line.substring(1).split("!")[0];
                            if (nick.equalsIgnoreCase(manager.NICKNAME) || manager.isOnline(nick)) { break; }
                            manager.setOnline(nick, true);
                            final IRCJoinEvent join = new IRCJoinEvent(NICK.matcher(manager.PLAYER_JOIN_IRC).replaceAll(nick));
                            manager.getPlugin().getServer().getPluginManager().callEvent(join);
                            break;
                        case PART:
                            nick = line.substring(1).split("!")[0];
                            manager.setOnline(nick, false);
                            final IRCQuitEvent quit = new IRCQuitEvent(NICK.matcher(manager.PLAYER_QUIT_IRC).replaceAll(nick));
                            manager.getPlugin().getServer().getPluginManager().callEvent(quit);
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
                                final IRCMessageEvent msg = new IRCMessageEvent(nick, manager.IRC_TAG, message);
                                manager.getPlugin().getServer().getPluginManager().callEvent(msg);
                            }
                            break;
                        default:
                            break;
                    }
                }
            } catch (final IOException e) {
                e.printStackTrace();
                throw new ThreadCleanupException("Thread cleaned due to errors: use '/chat irc quit " + network + "'");
            }
        }

        public void kill() {
            readIRC();
            interrupt();
        }

    }

    private class IRCWriter extends Thread {
        private final Connection master;

        public IRCWriter(final Connection conn, final String name) {
            super(name);
            master = conn;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    writeIRC();
                    sleep(200);
                } catch (final InterruptedException e) {
                    throw new ThreadCleanupException("IRCWriter cleaned up successfully!");
                }
            }
        }

        public void writeIRC() {
            try {
                for (final String line : new ArrayList<String>(lines)) {
                    bWriter.write(line);
                }
                lines.clear();
                bWriter.flush();
            } catch (final IOException e) {
                e.printStackTrace();
                throw new ThreadCleanupException("Thread cleaned due to errors: use '/chat irc quit " + network + "'");
            }
        }

        public void kill() {
            writeIRC();
            interrupt();
        }
    }

    public Connection(final IRCBot bot, final String net, final String host, final int p, final String chan) throws IOException {
        manager     = bot;
        connection  = new Socket(host, p);
        bReader     = new BufferedReader(new InputStreamReader( connection.getInputStream()));
        bWriter     = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        network     = net;
        hostname    = host;
        port        = p;
        channel     = chan;
        key         = "";

        reader      = new IRCReader(this, "IRCReader: " + host);
        writer      = new IRCWriter(this, "IRCWriter: " + host);

        writeLine("NICK " + manager.NICKNAME);
        writeLine("USER " + manager.NICKNAME + " net.krinsoft.chatsuite " + manager.IDENTITY + " :" + manager.REALNAME);

        writer.start();
        reader.start();
    }

    public Connection(final IRCBot bot, final String net, final String host, final int port, final String channel, final String k) throws IOException {
        this(bot, net, host, port, channel);
        key         = k;
    }

    public String getName() {
        return network;
    }

    public String getInfo() {
        return hostname + ":" + port;
    }

    public void stop() {
        writeLine("QUIT :Connection closing...");
        reader.kill();
        writer.kill();
        try {
            bWriter.flush();
        } catch (final IOException e) {
            manager.getPlugin().warn("An error occurred while flushing the output stream for '" + hostname + ":" + port + "'");
        } finally {
            try {
                bWriter.close();
                bReader.close();
            } catch (final IOException e) {
                manager.getPlugin().warn("An error occured while closing the streams for '" + hostname + ":" + port + "'");
            } finally {
                try {
                    connection.close();
                } catch (final IOException e) {
                    manager.getPlugin().warn("An error occurred while closing the socket for '" + hostname + ":" + port + "'");
                }
            }
        }
    }

    public void writeLine(final String message) {
        lines.add(message + "\r\n");
    }

    public void chanMsg(String chan, final String message) {
        if (chan == null) { chan = channel; }
        writeLine("PRIVMSG " + chan + " :" + message);
    }

    private void auth() {
        for (final String auth : manager.getConfig().getString("networks." + network + ".auth").split("\n")) {
            writeLine(auth);
        }
    }

}
