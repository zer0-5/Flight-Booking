package airport;

import java.util.UUID;

public class Route {
    public final UUID id;
    public final String origin;
    public final String destination;
    public final int capacity;

    /**
     * Constructor
     *
     * @param origin      the origin city.
     * @param destination the destiny city.
     * @param capacity    the capacity of each flight.
     */
    public Route(String origin, String destination, int capacity) {
        this.id = UUID.fromString(origin + destination + capacity);
        this.origin = origin;
        this.destination = destination;
        this.capacity = capacity;
    }
}
