package net.krinsoft.irc;

/**
 * @author krinsdeath
 */
public enum Reply {

    /**
     * Welcome Message
     * :Welcome, [nick]![user]@[host]
     */
    RPL_WELCOME("001"),

    /**
     * Server host information
     * :Your host is [server name], running version [version]
     */
    RPL_YOURHOST("002"),

    /**
     * Server creation date
     * :This server was created [date]
     */
    RPL_CREATED("003"),

    /**
     * Post registration greeting (Login successful)
     * [server name] [version] [user modes] [chan modes]
     */
    RPL_MYINFO("004"),

    /**
     * Server full
     * :Try server [server name], port [port]
     */
    RPL_BOUNCE("010"),

    /**
     * Nickname forcibly changed due to collision
     * :[info]
     */
    RPL_SAVENICK("043"),


    RPL_NOTOPIC("331"),
    RPL_TOPIC("332"),
    RPL_TOPICWHOTIME("333"),

    PART("PART"),
    JOIN("JOIN"),
    PRIVMSG("PRIVMSG"),
    ACTION("ACTION"),
    ;
    private String reply;

    Reply(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return this.reply;
    }

    public static Reply get(String numeric) {
        if (numeric != null) {
            for (Reply n : values()) {
                if (n.getReply().equalsIgnoreCase(numeric)) {
                    return n;
                }
            }
        }
        return null;
    }

}
