package common.airport;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Flight {
  private final UUID id;
  private final Connection connection;
  private final LocalDate date;
  private Set<UUID> clientsId;

  public Flight(UUID id, Connection connection, LocalDate date) {
    this.id = id;
    this.connection = connection;
    this.date = date;

    this.clientsId = new HashSet<>();
  }
}
