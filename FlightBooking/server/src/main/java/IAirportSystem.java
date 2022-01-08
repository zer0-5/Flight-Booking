import airport.Reservation;
import airport.Route;
import exceptions.*;
import users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IAirportSystem {

    /**
     * Adds a new route into the system.
     *
     * @param origin   the origin city.
     * @param destiny  the destiny city.
     * @param capacity the route capacity.
     * @exception RouteAlreadyExistsException is launched if this route already exists
     */
    void addRoute(String origin, String destiny, int capacity) throws RouteAlreadyExistsException;

    /**
     * Cancels a day. Preventing new reservations and canceling the remaining ones from that day.
     *
     * @param day the day.
     * @return all canceled @see airport.Reservation .
     */
    Set<Reservation> cancelDay(LocalDate day) throws DayAlreadyCanceledException;

    /**
     * Reserves a flight given the connections, in the time interval.
     *
     * @param userId the user's id.
     * @param cities the connections.
     * @param start  the start date of the interval.
     * @param end    the end date of the interval.
     * @return       the reservation's id.
     * @throws BookingFlightsNotPossibleException if there is no route possible.
     */
    UUID reserveFlight(UUID userId, List<String> cities, LocalDate start, LocalDate end)
            throws BookingFlightsNotPossibleException, FullFlightException;

    /**
     * Cancels a flight.
     *
     * @param userId                                        the id of the client
     * @param reservationId                                 the id of the reservation
     * @throws ReservationNotFoundException                 is launched if the reservation doesn't exist in the AirportSystem
     * @throws ReservationDoesNotBelongToTheClientException is launched if the reservation doesn't belong to the given
     * client
     * @return the deleted @see airport.Reservation .
     */
     Reservation cancelFlight(UUID userId, UUID reservationId) throws ReservationNotFoundException,
            ReservationDoesNotBelongToTheClientException;

    /**
     * Gets the existent routes.
     *
     * @return the list of the existent routes.
     */
    List<Route> getRoutes();


    /**
     * Registers a user into the system.
     *
     * @param user     the user
     */
    void register(User user) throws UsernameAlreadyExistsException;

    /**
     * Authenticates a user.
     *
     * @param name     the user's name.
     * @param password the user's password.
     * @return User
     */
    User authenticate(String name, String password) throws UserNotFoundException, InvalidCredentialsException;
}
