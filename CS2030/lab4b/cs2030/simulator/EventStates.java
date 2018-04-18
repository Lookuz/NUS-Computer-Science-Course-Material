package cs2030.simulator;

import java.util.function.Supplier;

/**
 * Class that holds all the printable messages to be printed
 * when a new state of an Event is produced.
 */
public class EventStates {

  /**
   * Printable message called when a customer arrives.
   * @param time Time customer arrives
   * @param c Customer that arrives
   * @return Supplier of the printable message
   */
  public static Supplier<String> customerArrives(double time, Customer c) {
    return () -> String.format("%6.3f", time) + " " +
      c + " arrives\n";
  }

  /**
   * Printable message called when a customer waits.
   * @param time Time customer waits
   * @param s Server that is making the customer wait
   * @param c Customer that waits
   * @return Supplier of the printable message
   */
  public static Supplier<String> customerWaits(double time,
      Server s, Customer c) {
    return () -> String.format("%6.3f", time) + " " +
      c + " waits for " + s + "\n";
  }

  /**
   * Printable message called when a customer is served.
   * @param time Time customer is served
   * @param s Server that is serves the customer
   * @param c Customer that is being served
   * @return Supplier of the printable message
   */
  public static Supplier<String> customerServed(double time,
      Server s, Customer c) {
    return () -> String.format("%6.3f", time) + " " +
      c + " served by " + s + "\n";
  }

  /**
   * Printable message called when a customer is done.
   * @param time Time customer is done
   * @param s Server that has finished serving the customer
   * @param c Customer that is done
   * @return Supplier of the printable message
   */
  public static Supplier<String> customerDone(double time,
      Server s, Customer c) {
    return () -> String.format("%6.3f", time) + " " +
      c + " done served by " + s + "\n";
  }

  /**
   * Printable message called when a customer leaves.
   * @param time Time customer leaves
   * @param customer Customer that leaves
   * @return Supplier of the printable message
   */
  public static Supplier<String> customerLeaves(double time,
      Customer customer) {
    return () -> String.format("%6.3f", time) + " " +
      customer + " leaves\n";
  }
}
