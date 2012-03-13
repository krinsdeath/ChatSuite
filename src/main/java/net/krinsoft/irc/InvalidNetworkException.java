package net.krinsoft.irc;

/**
 * @author krinsdeath
 */
public class InvalidNetworkException extends RuntimeException {
    private String message;

    public InvalidNetworkException(String msg) {
        message = msg;
    }

    public String getLocalizedMessage() {
        return message;
    }
}
