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
    RESERVE,
    CANCEL_RESERVATION,
    GET_ROUTES,
    ;


    public static RequestType getRequestType(int opcode) {
        return RequestType.values()[opcode];
    }

    public static String getMenu() {
        var arr = RequestType.values();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < arr.length; i++) {
            builder.append(i);
            builder.append(". ");
            String str = arr[i].toString();
            builder.append(Character.toUpperCase(str.charAt(0))).
                    append(str.substring(1).toLowerCase().replace('_', ' '));
            builder.append('\n');
        }

        return builder.append("\n> ").toString();
    }
}
