package airport;

import java.util.Set;
import java.util.UUID;

public class Reservation {
    public final UUID reserveCode;
    public final UUID client;
    public final Set<UUID> flights;
    // TODO: Adicionar capacidade aos flights depois de apagar a reservation

    public Reservation(UUID reserveCode, UUID client, Set<UUID> flights) {
        this.reserveCode = reserveCode;
        this.client = client;
        this.flights = flights;
    }
}
