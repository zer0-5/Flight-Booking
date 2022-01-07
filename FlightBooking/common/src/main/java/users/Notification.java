package users;

import java.time.LocalDateTime;

public class Notification {
    private final LocalDateTime date;
    private final String message;
    // TODO: We can add more things to this as we see fit.

    public Notification(String message) {
        this.message = message;
        this.date = LocalDateTime.now();
    }
}
