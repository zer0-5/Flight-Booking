package exceptions;

/**
 * Thrown to indicate that the flight doesn't exist yet, but the route may exist.
 * So, the flight could still be possible.
 */
public class FlightDoesntExistYetException extends Exception{
}
