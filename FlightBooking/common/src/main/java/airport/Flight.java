package airport;

import exceptions.FullFlightException;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a flight.
 */
public class Flight {

    /**
     * Id of the flight.
     */
    public final UUID id;

    /**
     * Route of the flight.
     */
    public final Route route;

    /**
     * Date of the flight.
     */
    public final LocalDate date;

    /**
     * Association between each reservation code to the flight.
     * <p>
     * This is necessary because when a flight is canceled, we need
     * to know all reservations associated to cancel then.
     * Like, if a connection flight is canceled, we need to cancel the flights associated to that connection
     */
    private final Set<Reservation> reservations;

    private final ReadWriteLock rwReservation ;

    // Get reservations
    private final Lock readLockReservation;

    //Adicionar a reserva depois de ter sido bloqueado.
    private final Lock writeLockReservation;


    /**
     * Constructor of the flight.
     *
     * @param route the route.
     * @param date  the date.
     */
    public Flight(Route route, LocalDate date) {
        this.id = UUID.randomUUID();
        this.route = route;
        this.date = date;
        this.reservations = new HashSet<>();
        this.rwReservation = new ReentrantReadWriteLock();
        this.readLockReservation = rwReservation.readLock();
        this.writeLockReservation = rwReservation.writeLock();
    }

    /**
     * Adds a reservation to this flight.
     *
     * @param reservation the id of the reservation.
     * @return true if this set did not already contain the specified element.
     * @throws FullFlightException is launched if aren't seats available.
     */
    public boolean addReservation(Reservation reservation) throws FullFlightException {
        try {
            writeLockReservation.lock();
            if (route.capacity > reservations.size())
                return this.reservations.add(reservation);
            throw new FullFlightException();
        }finally{
            writeLockReservation.unlock();
        }
    }

    /**
     * Removes a reservation to this flight.
     *
     * @param reservation the id of the reservation.
     * @return true if this set contained the specified element.
     */
    public boolean removeReservation(Reservation reservation) {
        try {
            writeLockReservation.lock();
            return this.reservations.remove(reservation);
        } finally {
            writeLockReservation.unlock();
        }
    }

    /**
     * Get all reservation's ids on the flight
     *
     * @return reservation's ids
     */
    public Set<Reservation> getReservations() {
        try{
            readLockReservation.lock();
            return new HashSet<>(reservations);
        }
        finally{
            readLockReservation.unlock();
        }
    }

    /**
     * Checks if there are available seats.
     *
     * @return true if there is a seat.
     */
    public boolean seatAvailable() {
        // T
        return route.capacity > reservations.size();
    }

    // TODO
    public void preReservationSeat() throws FullFlightException {
        if (seatAvailable())
            return;
        else
            throw new FullFlightException();
    }

    public void cancelSeat() {
        //TODO
    }

    public void cancelFlight() {
        for (Reservation reservation : reservations) {
            reservation.cancelReservation();
        }
    }

    public void unlock() {
        writeLockReservation.unlock();
    }

    public void lock() {
        //writeLockReservation.tryLock();
        writeLockReservation.lock();
    }
}
