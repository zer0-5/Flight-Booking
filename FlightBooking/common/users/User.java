package users;

import java.util.UUID;

public abstract class User {
    final UUID id;
    final String username;
    final String password;

    public User(String username, String password) {
        this.password = password;
        this.username = username;
        this.id = UUID.randomUUID();
    }

    //abstract void login();
}
