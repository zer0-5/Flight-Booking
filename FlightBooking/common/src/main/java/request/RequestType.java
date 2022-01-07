package request;

public enum RequestType {
    // Any user
    REGISTER,
    LOGIN,
    EXIT,

    // Admin commands
    INSERT_ROUTE,
    CANCEL_DAY,

    // User commands
    RESERVATION,
    CANCEL_RESERVATION,
    GET_ROUTES,
}
