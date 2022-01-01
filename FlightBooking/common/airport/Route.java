package airport;

import java.util.UUID;

/**
 * This class stores the information about routes between cities.
 * The flights happen only if a route exists. If a route exists, but the flight don't, we create a flight.
 */
public class Route {
    //ID of the route.
    public final UUID id;
    //City of departure of the fligth.
    public final String origin;
    //City of arrival of the fligth.
    public final String destination;
    //Capacity of the airplane that does the connection.
    //The route as a fixed capacity.
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
