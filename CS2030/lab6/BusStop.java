import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletionException;

/**
 * Encapsulate a bus stop with a unique String id, the location (in long, lat),
 * and human friendly name.
 */
class BusStop {
  /** A unique String id. */
  private final String id; // unique

  /** A human friendly name. */
  private String name;

  /** Static map from id of bus stop to bus stop objects. */
  // private static Map<String,BusStop> busStops;

  /**
   * Constructor for this bus stop.
   * @param   id     The id of this bus stop.
   * @param   name   A human friendly name of the bus stop.
   */
  public BusStop(String id, String name) {
    this.id = id;
    this.name = name;
  }

  /**
   * Constructor for this bus stop without name.
   * @param   id     The id of this bus stop.
   */
  public BusStop(String id) {
    this.id = id;
    this.name = "";
  }

  /**
   * Checks if the bus stop name matches the given string.
   * @param  name The string to match.
   * @return true if the name matches; false otherwise.
   */
  public boolean matchName(String name) {
    return this.name.toUpperCase().indexOf(name.toUpperCase()) != -1;
  }

  /**
   * Return the set of bus services that serve this bus stop as
   * a stream.  Query the web server if the bus services are not
   * retrieved before.
   * @return A set of BusService that serve this bus stop.
   */
  public Set<BusService> getBusServices() {
    URL url;
    try {
      Thread.sleep(200);
      url = new URL("https://cs2030-bus-api.herokuapp.com/bus_stops/" + id);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      if (connection.getResponseCode() != 200) {
        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
        return new HashSet<>();
      }

      Scanner s = new Scanner(connection.getInputStream());
      this.name = s.nextLine();
      String line = s.nextLine();
      String[] busIds = line.split(",");
      Set<BusService> tmpBusServices = new HashSet<>();
      for (String id : busIds) {
        tmpBusServices.add(new BusService(id));
      }
      return tmpBusServices;
    } catch (IOException e) {
      throw new CompletionException(e);
    } catch (InterruptedException e) {
      throw new CompletionException(e);
    }
  }

  /**
   * Checks of this bus stop equals to another bus stop -- two bus
   * stops are equal if their id is the same.
   * @param  o Another object to compare against.
   * @return  true if the two objects are equal, false otherwise.
   */
  @Override
  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof BusStop)) {
      return false;
    }
    return ((BusStop)o).id.equals(this.id);
  }

  /**
   * Return a hash code of the bus stop.
   * @return The hash code of this bus stop.
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Return a string representation of the bus stop.
   * @return Return the name of the bus stop.
   */
  @Override
  public String toString() {
    return id + " " + name;
  }
}
