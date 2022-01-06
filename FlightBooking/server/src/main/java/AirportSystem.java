import airport.Flight;
import airport.Reservation;
import airport.Route;
import exceptions.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class AirportSystem implements IAirportSystem{

    /**
     * Associates each city, by name, with the flights that leave that city.
     */
    private final Map<String, Map<String, Route>> connectionsByCityOrig;

    /**
     * Associates each ID to the respective flight
     */
    private final Map<UUID, Flight> flightsById;

    /**
     * Associates each day to the flies that happen in that day.
     * If a connection exists, but the fly in that day doesn't, then the flight will be created.
     * We can only have one flight by connection in each day.
     */
    private final Map<LocalDate, Set<Flight>> flightsByDate;

    /**
     * Days cancelled by the administrator.
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
     * @exception RouteAlreadyExistsException is launched if this route already exists
     */
    public void addRoute(String orig, String dest, int capacity) throws RouteAlreadyExistsException {
        String origUpperCase = orig.toUpperCase();
        String destUpperCase = dest.toUpperCase();
        Route newRoute = new Route(orig, dest, capacity);
        if (connectionsByCityOrig.containsKey(origUpperCase))
            if (connectionsByCityOrig.get(origUpperCase).putIfAbsent(destUpperCase,newRoute) != null)
                throw new RouteAlreadyExistsException();
        else {
            HashMap<String,Route> toInsert = new HashMap<>();
            toInsert.put(destUpperCase ,new Route(orig, dest, capacity));
            connectionsByCityOrig.put(origUpperCase, toInsert);
        }
    }

    /**
     * Method to get a connection between two cities.
     *
     * @param orig     the origin city.
     * @param dest     the destiny city.
     * @exception RouteDoesntExistException is launched if this route doesn't exist.
     */
    public Route getRoute(String orig, String dest) throws RouteDoesntExistException {
        String origUpperCase = orig.toUpperCase();
        String destUpperCase = dest.toUpperCase();
        Map<String,Route> routesByDestCity = connectionsByCityOrig.get(origUpperCase);
        if (routesByDestCity == null || routesByDestCity.isEmpty())
            throw new RouteDoesntExistException();
        Route route = routesByDestCity.get(destUpperCase);
        if (route == null)
            throw new RouteDoesntExistException();
        return route;
    }

    /**
     * Checks if a route is valid.
     *
     * @param orig City of departure of the flight.
     * @param dest City of arrival of the flight.
     * @return true if this route exists.
     */
    private boolean validRoute(String orig, String dest){
        String origUpperCase = orig.toUpperCase();
        String destUpperCase = dest.toUpperCase();
        Map<String,Route> routes = connectionsByCityOrig.get(origUpperCase);
        if (routes != null ){
            return routes.containsKey(destUpperCase);
        }
        return false;
    }

    /**
     * Verifies if a given route can be made through the possible routes.
     *
     * @param cities All cities in order of passage
     * @return true if it's possible to make this trip
     */
    private boolean validRoutes(final List<String> cities) {
        String origCity = null;
        String destCity;
        for (String city: cities){
           if(origCity == null) {
               origCity = city;
               continue;
           }
           destCity = city;
           if(!validRoute(origCity,destCity))
               return false;
            origCity = city;
        }
        return true;
    }

    /**
     * Get a valid Flight on the given day, departing from the origin city to the destination city.
     *
     * @param date Date we want
     * @param orig Origin City
     * @param dest Destination City
     * @return a Flight
     * @throws FullFlightException Is launched if the flight is full
     * @throws FlightDoesntExistYetException Is launched if the flight doesn't exist yet
     */
    private Flight getValidFlight(LocalDate date, String orig, String dest)
            throws FullFlightException, FlightDoesntExistYetException {
        String origUpperCase = orig.toUpperCase();
        String destUpperCase = dest.toUpperCase();

        Set<Flight> flightsByCityOrig = this.flightsByDate.get(date);

        for ( Flight flight : flightsByCityOrig ) {
            if (flight.route.origin.equals(origUpperCase) &&
                flight.route.destination.equals(destUpperCase)){
                if (flight.seatAvailable())
                    return flight;
                else
                    throw new FullFlightException();
            }
        }
        throw new FlightDoesntExistYetException();
    }

    /**
     * Returns a set of flights that make the trip possible.
     *
     * @param cities the connections.
     * @param start  the start date of the interval.
     * @param end    the end date of the interval.
     * @return The available flights
     * @throws BookingFlightsNotPossibleException if there is no route possible.
     */
    private Set<Flight> getRouteFlights(List<String> cities, LocalDate start, LocalDate end)
            throws BookingFlightsNotPossibleException {
        String origCity = null;
        String destCity;
        int numberFlights = cities.size() - 1;
        int i = 0;
        LocalDate dateToSearch = start;
        Set<Flight> flights = new HashSet<>();

        while( i < numberFlights ) {
            if (dateToSearch.isAfter(end))
                throw new BookingFlightsNotPossibleException();
            if(canceledDays.contains(dateToSearch)) {
                dateToSearch = dateToSearch.plusDays(1);
                continue;
            }

            if (origCity == null) {
                origCity = cities.get(i); i++;
                continue;
            }
            destCity = cities.get(i);

            try {
                Flight flight = getValidFlight(dateToSearch,origCity,destCity);
                flights.add(flight);
            } catch (FullFlightException e) {
                dateToSearch = dateToSearch.plusDays(1);
                continue;
            } catch (FlightDoesntExistYetException e) {
                try {
                    Route route = getRoute(origCity,destCity);
                    Flight flight = addFlight(route,dateToSearch);
                    flights.add(flight);
                } catch (RouteDoesntExistException ignored) {
                    // FUTURE ATTENTION: Before throw this we need to unlock the flights
                    throw new BookingFlightsNotPossibleException();
                }
            }
            origCity = destCity; i++;
        }
        return flights;
    }

    /**
     * Creates and add a flight
     *
     * @param route the connection
     * @param date  the day
     * @return      the flight
     */
    private Flight addFlight(Route route, LocalDate date) {
        Flight flight = new Flight(route, date);
        flightsById.putIfAbsent(flight.id,flight);
        Set<Flight> flights = flightsByDate.get(date);
        if (flights == null) {
            flights = new HashSet<>(); flights.add(flight);
            flightsByDate.put(date, flights);
        } else
            flights.add(flight);
        return flight;
    }

    /**
     * Reserves a flight given the connections, in the time interval.
     *
     * @param userId the user's id.
     * @param cities the connections.
     * @param start  the start date of the interval.
     * @param end    the end date of the interval.
     * @return       the reservation's id.
     */
    public UUID reserveFlight(UUID userId, List<String> cities, LocalDate start, LocalDate end)
            throws BookingFlightsNotPossibleException {
        Set<Flight> flights = getRouteFlights(cities, start, end);
        Set<UUID> flightIds = flights.stream().map(e->e.id).collect(Collectors.toSet());
        Reservation reservation = new Reservation(userId,flightIds);
        for( Flight flight : flights) {
            try {
                flight.addReservation(reservation.id);
            } catch (FullFlightException ignored) {
                // Shouldn't happen
                throw new BookingFlightsNotPossibleException();
            }
        }
        return reservation.id;
    }

    //public boolean addReservation(UUID client, LocalDateTime firstDate, LocalDateTime lastDate, List<String> cities) throws RouteDoesntExistException{
    /* FIXME We need to check if that dates aren't canceled. */
    //    // Routes that connect the cities given in argument.
    //    List<Route> routes;
    //    for (int i = 0; i < cities.size()-1; i++){
    //        //Routes that leave from current city.
    //        Set<Route> routeFromThisCity = connectionsByCityOrig.get(cities.get(i).toUpperCase());
    //        if (routeFromThisCity == null) throw new RouteDoesntExistException();
    //        for (Route one : routeFromThisCity){
    //            if (one.destination.equals(cities.get(i+1).toUpperCase()){
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

    /**
     * Gets the existent routes.
     *
     * @return the list of the existent routes.
     */
    public List<Route> getRoutes() {
        return this.connectionsByCityOrig.values()
                .stream()
                .flatMap(e -> e.values().stream())
                .collect(Collectors.toList());
    }
}
