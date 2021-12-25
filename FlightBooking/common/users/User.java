package common.users;

import java.util.UUID;

public abstract class User {
  private UUID id;
  private String username;
  private String password;

  public User(String username, String password) {
    this.password = password;
    this.username = username;
    //this.id = new UUID(  password,username);
  }

  //abstract void login();
}
