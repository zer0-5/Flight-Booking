package airport;

import java.util.Set;
import java.util.UUID;

public record Reservation(UUID reserveCode, UUID client, Set<UUID> flights) {
    // TODO: Adicionar capacidade aos flights depois de apagar a reservation

}
