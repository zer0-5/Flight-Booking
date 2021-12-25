package common.airport;

import java.util.UUID;

public class Reservation {
    private UUID reserveCode;
    private UUID client;
    private UUID flight;

    public Reservation(UUID reserveCode, UUID client, UUID flight) {
        this.reserveCode = reserveCode;
        this.client = client;
        this.flight = flight;
    }

    public UUID getClient() {
        return client;
    }

    public UUID getReserveCode() {
        return reserveCode;
    }

    public UUID getFlight() {
        return flight;
    }
}
