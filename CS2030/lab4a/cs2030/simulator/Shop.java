package cs2030.simulator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A shop object maintains the list of servers and support queries
 * for server.
 *
 * @author weitsang
 * @author atharvjoshi
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
class Shop {
  /** List of servers. */
  private final List<Server> servers;

  /**
   * Constructor method that creates a copy of the Shop class
   * with a copy of the serverList argument.
   * @param serverList List to be copied into the new shop class
   */
  Shop(List<Server> serverList) {
    this.servers = new ArrayList<Server>(serverList);
  }

  /**
   * Create a new shop with a given number of servers.
   * @param numOfServers The number of servers.
   */
  Shop(int numOfServers) {
    this.servers = new ArrayList<>(numOfServers);
    for (int i = 0; i < numOfServers; i++) {
      this.servers.add(new Server());
    }
  }

  /**
   * Getter method that retrieves the server list in the Shop.
   * @return the list of servers in the shop
   */
  public List<Server> getServers() {
    return servers;
  }

  /**
   * Method that returns a Shop instance with the updated server list.
   * @param updated server with updated state
   * @return new Shop instance with updated server list
   */
  public Shop updateServer(Server updated) {
    List<Server> newList = new ArrayList<Server>(this.servers);
    for (Server server : newList) {
      if (server.equals(updated)) {
        newList.remove(server);
        newList.add(updated);
        Collections.sort(newList);
        break;
      }
    }
    return new Shop(newList);
  }

  /**
   * Return the first server in the list that fulfils based on the predicate argument.
   * @param find Function that evaluates if the server is available for return
   * @return An Optional instance of an idle server, or empty Optional if every server is busy.
   */
  public Optional<Server> findServer(Predicate<Server> find) {
    return this.servers.stream()
            .filter(find)
            .findFirst();
  }

  /**
   * Return a string representation of this shop.
   * @return A string representation of this shop.
   */
  public String toString() {
    return servers.toString();
  }
}
