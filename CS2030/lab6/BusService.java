import java.lang.InterruptedException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Set;
import java.util.HashSet;
import java.util.Scanner;
import java.util.concurrent.CompletionException;


/**
 * BusService encapsulate a bus service with a String id
 * and a set of BusStop.
 */
class BusService {
  private final String id;

  /**
   * Construct a BusService object with a given id.  An empty
   * Set of bus stops is initialized.
   * @param id The id of this bus service.
   */
  public BusService(String id) {
    this.id = id;
  }

  /**
   * Get the current list of bus stops as a set.  Query the web server
   * if bus stops are not retrieved before.
   * @return A set of bus stops that this bus services serves.
   */
  public Set<BusStop> getBusStops() {
    URL url;
    try {
      Thread.sleep(200);
      url = new URL("https://cs2030-bus-api.herokuapp.com/bus_services/" + id);
      HttpURLConnection connection = (HttpURLConnection)url.openConnection();
      if (connection.getResponseCode() != 200) {
        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());
        return new HashSet<>();
      }

      Set<BusStop> busStops = new HashSet<>();
      Scanner s = new Scanner(connection.getInputStream());
      while (s.hasNextLine()) {
        String line = s.nextLine();
        String[] fields = line.split(",");
        BusStop stop = new BusStop(fields[0], fields[1]);
        busStops.add(stop);
      }
      return busStops;
    } catch (IOException e) {
      throw new CompletionException(e);
    } catch (InterruptedException e) {
      throw new CompletionException(e);
    }
  }

  /**
   * Return a list of bus stops matching a given name.
   * @param  name Name (possibly partial) of a bus stop.
   * @return A list of bus stops matching the given name.
   */
  public Set<BusStop> findStopsWith(String name) {
    Set<BusStop> stops = new HashSet<>();
    for (BusStop stop: this.getBusStops()) {
      if (stop.matchName(name)) {
        stops.add(stop);
      }
    }
    return stops;
  }

  /**
   * Checks if this bus service stops at the given bus stop.
   * @param  stop The bus stop to check.
   * @return Return true if this bus service stops at the given bus stop.
   *     Return false otherwise.
   */
  public boolean hasStopAt(BusStop stop) {
    return this.getBusStops().contains(stop);
  }

  /**
   * Return the hash code of this bus service.
   * @return The hash code.
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }

  /**
   * Return true if this bus service is equals to another bus service.
   * Two bus services are equal if they have the same id.
   * @param  busService another bus service to check for equality.
   * @return true if the bus servives are equal.
   */
  @Override
  public boolean equals(Object busService) {
    if (busService instanceof BusService) {
      return this.id.equals(((BusService)busService).id);
    } else {
      return false;
    }
  }

  /**
   * Convert this bus service to a string.
   * @return A string containing the id of this bus service.
   */
  @Override
  public String toString() {
    return id;
  }
}
