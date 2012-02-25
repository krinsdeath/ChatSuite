package net.krinsoft.chat.irc;

/**
 * @author krinsdeath
 */
public class ThreadCleanupException extends RuntimeException {
    private String                  message;

    public ThreadCleanupException() {
        super();
        this.message    = "Thread Cleaned";
    }

    public ThreadCleanupException(String message) {
        super(message);
        this.message    = message;
    }

    @Override
    public String getLocalizedMessage() {
        return message;
    }

}
