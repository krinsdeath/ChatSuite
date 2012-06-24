package net.krinsoft.chat.api;

/**
 * @author krinsdeath
 */
public interface Group {

    public String getName();

    public String getPrefix();

    public String getSuffix();

    public int maxChannels();

}
