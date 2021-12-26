package airport;

import java.util.UUID;

public class Reservation {
    public final UUID reserveCode;
    public final UUID client;
    public final UUID flight;

    public Reservation(UUID reserveCode, UUID client, UUID flight) {
        this.reserveCode = reserveCode;
        this.client = client;
        this.flight = flight;
    }
}
