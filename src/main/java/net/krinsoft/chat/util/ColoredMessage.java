package net.krinsoft.chat.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author krinsdeath
 */
public class ColoredMessage {
    private final static Pattern COLOR = Pattern.compile("(?i)&(0-F)");

    private List<String> contents = new ArrayList<String>();
    public ColoredMessage(List<String> lines) {
        for (String line : lines) {
            this.contents.add(COLOR.matcher(line).replaceAll("\u00A7$1"));
        }
    }

    public List<String> getContents() {
        return this.contents;
    }
}
