import airport.Flight;
import airport.Reservation;
import airport.Route;
import exceptions.ReservationDoesNotBelongToTheClientException;
import exceptions.ReservationNotFoundException;
import exceptions.RouteDoesntExistException;

import java.time.LocalDate;
import java.util.*;

public class AirportSystem {

    /**
     * Associates each city, by name, with the flights that leave that city.
     */
    private final Map<String, Set<Route>> connectionsByCityOrig;

    /**
     * Associates each ID to the respective flight
     */
    private final Map<UUID, Flight> flightsById;

    /**
     * Associates each day to the flies that happen in that day.
     * If a connection exists, but the fly in that day doesn't, then the flight will be created.
     * We can only have one fligth by connection in each day.
     */
    private final Map<LocalDate, Set<Flight>> flightsByDate;

    /**
     * Days cancelled by the adminstrator.
     * This is used to avoid reservations in cancelled days.
     */
    private final Set<LocalDate> canceledDays;

    /**
     * Associates each reservation to his id.
     */
    private final Map<UUID, Reservation> reservationsById;

    /**
     * Constructor.
     * It starts with empty parameters because they are all inserted by the users.
     */
    public AirportSystem() {
        this.connectionsByCityOrig = new HashMap<>();
        this.flightsById = new HashMap<>();
        this.flightsByDate = new HashMap<>();
        this.canceledDays = new HashSet<>();
        this.reservationsById = new HashMap<>();
    }

    /**
     * Method to add a connection between two cities, with a given capacity.
     *
     * @param orig     the origin city.
     * @param dest     the destiny city.
     * @param capacity the capacity of each flight.
     */
    public void addRoute(String orig, String dest, int capacity) {
        Route newConn = new Route(orig, dest, capacity);
        if (connectionsByCityOrig.containsKey(orig)) connectionsByCityOrig.get(orig).add(newConn);
        else {
            Set<Route> toInsert = new HashSet<>();
            toInsert.add(new Route(orig, dest, capacity));
            connectionsByCityOrig.put(orig, toInsert);
        }
    }

    //public boolean addReservation(UUID client, LocalDateTime firstDate, LocalDateTime lastDate, List<String> cities) throws RouteDoesntExistException{
    //    // Routes that connect the cities given in argument.
    //    List<Route> routes;
    //    for (int i = 0; i < cities.size()-1; i++){
    //        //Routes that leave from current city.
    //        Set<Route> routeFromThisCity = connectionsByCityOrig.get(cities.get(i));
    //        if (routeFromThisCity == null) throw new RouteDoesntExistException();
    //        for (Route one : routeFromThisCity){
    //            if (one.destination.equals(cities.get(i+1)){
    //                routes.add(one);
    //                break;
    //            }
    //        }
    //    }
    //    //Now we got the routes that can make the connection.

    //}

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
    public Reservation cancelFlight(UUID userId, UUID reservationId) throws ReservationNotFoundException,
            ReservationDoesNotBelongToTheClientException{
        Reservation r = this.reservationsById.remove(reservationId);
        if (r == null)
            throw new ReservationNotFoundException();
        if (r.clientId != userId) {
            this.reservationsById.put(r.id, r);
            throw new ReservationDoesNotBelongToTheClientException();
        }

        for(UUID flightId : r.getFlightIds())
            flightsById.get(flightId).removeReservation(reservationId);
        return r;
    }

    /**
     * Cancels a day. Preventing new reservations and canceling the remaining ones from that day.
     *
     * @param day the day.
     * @return all canceled @see airport.Reservation .
     */
    public Set<Reservation> cancelDay(LocalDate day) {
        this.canceledDays.add(day);
        Set<Reservation> reservations = new HashSet<>();
        Set<Flight> flights = this.flightsByDate.remove(day);
        // Maybe thrown an exception when doesn't exist reservations in that day
        if (flights != null) {
            for(Flight flight : flights) {
                // Remove flight from flightsById
                this.flightsById.remove(flight.id);
                for(UUID reservationId : flight.reservations) {
                    // Remove reservation from reservationsById
                    Reservation reservation = reservationsById.remove(reservationId);
                    if (reservation != null)
                        reservations.add(reservation);
                }
            }
        }
        return reservations;
    }
}
