package net.krinsoft.chatsuite;

import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 *
 * @author krinsdeath
 */

class Localization {
    private final static Pattern COLOR = Pattern.compile("(?i)&([0-F])");

	private static Configuration config;

	public static void init(Configuration conf) {
		config = conf;
	}

	public static String getString(String path, String tok) {
		String msg = config.getString(path);
		if (msg != null) {
			msg = msg.replaceAll("(<away>)", tok);
			msg = color(msg);
			return msg;
		} else {
			return null;
		}
	}

    public static void message(String path, Player p) {
        Object obj = config.getProperty(path);
        if (obj instanceof List) {
            List<String> lines = (List<String>) obj;
            for (String line : lines) {
                p.sendMessage(color(line));
            }
        } else if (obj instanceof String) {
            p.sendMessage(color(obj.toString()));
        }
    }

    private static String color(String message) {
        return COLOR.matcher(message).replaceAll("\u00A7$1");
    }
}
