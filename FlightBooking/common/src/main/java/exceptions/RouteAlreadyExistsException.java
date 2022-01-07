package exceptions;

/**
 * Thrown to indicate that the route between the two given airports is already registered.
 * We compare the names with case insensitivity.
 */
public class RouteAlreadyExistsException extends Exception{
}
