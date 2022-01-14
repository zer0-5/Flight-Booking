package airport;

import users.User;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents the reservation of one or multiple flights.
 */
public class Reservation {

    /**
     * Reservation code or id of this reservation.
     */
    public final UUID id;

    /**
     * Client that owns this reservation.
     */
    private final User client;

    /**
     * Flights with the connections of the reservation.
     * E.g. Lisbon -> Tokyo -> London
     */
    private final Set<Flight> flights;

    private final Lock readLockFlights;
    private final Lock writeLockFlights;

    /**
     * Constructor
     *
     * @param client     Client.
     * @param flightsIds a set of flight's id.
     */
    public Reservation(User client, Set<Flight> flightsIds) {
        this.id = UUID.randomUUID();
        this.client = client;
        this.flights = flightsIds;
        ReentrantReadWriteLock rwFlights = new ReentrantReadWriteLock();
        this.readLockFlights = rwFlights.readLock();
        this.writeLockFlights = rwFlights.writeLock();
    }


    /**
     * Cancel the reservation on all flights involved in the given reservation
     *
     */
    public void cancelReservation() {
        try {
            writeLockFlights.lock();
            for (Flight flight : flights) {
                if (flight != null)
                    flight.removeReservation(this);
            }
        } finally {
            writeLockFlights.unlock();
        }
    }

    /**
     * Checks if the given user made the reservation
     *
     * @param user User
     * @return true if are the same user
     */
    //public boolean checksUser(User user) {
    //    return client.equals(user);
    //}
}
