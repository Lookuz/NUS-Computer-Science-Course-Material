package cs2030.simulator;

/**
 * Encapsulates information and methods pertaining to a "done" event,
 * which is when a server finishes serving a customer.
 * A DoneEvent remembers which customer is served by which server.
 *
 * @author Ooi Wei Tsang
 * @author Evan Tay
 * @version CS2030 AY17/18 Sem 2 Lab 1b
 */
public class DoneEvent extends Event {
  /** The server that finishes serving the customer. */
  private final Server server;

  /** The customer being served. */
  private final Customer customer;

  /**
   * Create a new DoneEvent to be executed at time {@code time}.
   *
   * @param time The time a customer is done being served.
   * @param server The server who serves.
   * @param customer The customer being served.
   */
  DoneEvent(double time, Server server, Customer customer) {
    super(time);
    this.server = server;
    this.customer = customer;
  }

  /**
   * Ask the simulator to simulate this done event.
   *
   * @param sim The simulator.
   */
  public SimState simulate(SimState sim) {
    return sim.simulateDone(time, server, customer);
  }
}
