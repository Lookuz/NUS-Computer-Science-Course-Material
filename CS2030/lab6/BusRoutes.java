import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates the result of a query.  It takes in two bus stops
 * and keeps the set of bus services running from one bus stop to
 * the other.
 */
class BusRoutes {
  BusStop stop;
  String name;
  Map<BusService,Set<BusStop>> services;

  /**
   * Constructor for creating a bus route.
   * @param stop The first bus stop.
   * @param name The second bus stop.
   * @param services The set of bus services between the two stops.
   */
  BusRoutes(BusStop stop, String name, Map<BusService,Set<BusStop>> services) {
    this.stop = stop;
    this.name = name;
    this.services = services;
  }

  /**
   * Add a service and set of bus stops to this result, unless the
   * set of stops is empty. 
   * @param service The bus service to take
   * @param stops The stops matching the query reachable through
   *     the bus service. 
   * @return The resulting routes.
   */
  BusRoutes addRoute(BusService service, Set<BusStop> stops) {
    if (!stops.isEmpty()) {
      this.services.put(service, stops);
    }
    return this;
  }

  /**
   * Return a string representation of the bus route.
   * @return The first line contains the two bus stops.  The
   *     second line contains the list of unordered services.
   */
  public String toString() {
    String result = "Search for: " + stop + " <-> " + name + ":\n";
    result += "From " + stop + "\n";
    for (BusService service: services.keySet()) {
      result += "- Take " + service + " to:\n";
      for (BusStop s: services.get(service)) {
        if (!stop.equals(s)) {
          result += "  - " + s + "\n";
        }
      }
    }
    return result;
  }
}
