package common.airport;

import java.util.UUID;

public class Reservation {
    final UUID reserveCode;
    final UUID client;
    final UUID flight;

    public Reservation(UUID reserveCode, UUID client, UUID flight) {
        this.reserveCode = reserveCode;
        this.client = client;
        this.flight = flight;
    }
}
