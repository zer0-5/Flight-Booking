package common.airport;

import java.util.UUID;

public class Connection {
  private final UUID id;
  private final String originCity;
  private final String destinationCity;
  private final int capacity;

  /**
   * Construtor
   * @param originCity the origin city.
   * @param destinationCity the destiny city.
   * @param capacity the capacity of each flight.
   */
  public Connection(String originCity, String destinationCity, int capacity) {
    this.id = UUID.fromString(originCity + destinationCity + capacity);
    this.originCity = originCity;
    this.destinationCity = destinationCity;
    this.capacity = capacity;
  }
}
