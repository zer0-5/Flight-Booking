package common.airport;

import java.util.UUID;

public class Route {
  final UUID id;
  final String origin;
  final String destination;
  final int capacity;

  /**
   * Construtor
   * @param origin the origin city.
   * @param destination the destiny city.
   * @param capacity the capacity of each flight.
   */
  public Route(String origin, String destination, int capacity) {
    this.id = UUID.fromString(origin + destination + capacity);
    this.origin = origin;
    this.destination = destination;
    this.capacity = capacity;
  }
}
