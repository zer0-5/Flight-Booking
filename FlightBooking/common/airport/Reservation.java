package airport;

import java.util.Set;
import java.util.UUID;

public class Reservation {
    public final UUID reserveCode;
    public final UUID client;
    public final Set<UUID> flights; // Flights with the connections of the reservation e.g. Lisbon -> Toquio -> London
    // TODO: Adicionar capacidade aos flights depois de apagar a reservation

    /**
     * Constructor
     * @param reserveCode a reserve code.
     * @param client a client.
     * @param flights set of flight's id.
     */
    public Reservation(UUID reserveCode, UUID client, Set<UUID> flights) {
        this.reserveCode = reserveCode;
        this.client = client;
        this.flights = flights;
    }
}
