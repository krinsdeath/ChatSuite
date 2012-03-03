package net.krinsoft.chat.irc;

/**
 * @author krinsdeath
 */
public class InvalidIRCBotException extends RuntimeException {
    private String message;

    public InvalidIRCBotException(String msg) {
        super(msg);
        message = msg;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }

}
