package exceptions;

/**
 * Thrown to indicate that the booking of flight isn't possible.
 * This can happen because the route doesn't exist, or because the possible flights are full.
 */
public class BookingFlightsNotPossibleException extends Exception{
}
