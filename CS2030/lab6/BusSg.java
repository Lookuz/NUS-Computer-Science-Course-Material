import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A BusSg class encapsulate the data related to the bus services and
 * bus stops in Singapore, and supports queries to the data.
 */
class BusSg {

  /**
   * Given a bus stop and a name, find the bus services that serve between
   * the given stop and any bus stop with matching mame.
   * @param  stop The bus stop
   * @param  name The (partial) name of other bus stops.
   * @return The (optional) bus routes between the stops.
   */
  public static Optional<BusRoutes> findBusServicesBetween(BusStop stop, String name) {
    if (stop == null || name == null) {
      return Optional.empty();
    }
     return CompletableFuture.supplyAsync(() -> stop.getBusServices())
     .thenApply(stops -> stops.stream()
             .parallel()
             .filter(service -> !service.findStopsWith(name).isEmpty())
             .collect(Collectors.toMap(service -> service,
                     service -> service.findStopsWith(name))))
     .handle((services, e) -> {
       if (services != null) {
         return services;
       } else {
         System.err.println("Unable to complete query: " + e);
         return new HashMap<BusService, Set<BusStop>>();
       }
     })
     .thenApply(services -> Optional.of(new BusRoutes(stop, name, services)))
     .join();

  }
}
