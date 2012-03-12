package net.krinsoft.irc;

/**
 * @author krinsdeath
 */
public class ThreadCleanupException extends RuntimeException {
    private String                  message;

    public ThreadCleanupException(String message) {
        super(message);
        this.message    = message;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }

}
