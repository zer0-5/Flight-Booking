public interface IUsersSystem {

  /**
   * Registers a user into the system.
   *
   * @param name     the user's name.
   * @param password the user's password.
   */
  void register(String name, String password);

  /**
   * Authenticates a user.
   *
   * @param name     the user's name.
   * @param password the user's password.
   */
  void authenticate(String name, String password);

  /**
   * Authenticates an admin.
   *
   * @param name     the admin's name.
   * @param password the admin's password.
   */
  void authenticateAdmin(String name, String password);
}
