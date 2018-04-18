package cs2030.simulator;

import cs2030.util.Pair;

/**
 * The Simulator class encapsulates information and methods pertaining to a
 * Simulator.
 *
 * @author atharvjoshi
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
public class Simulator {
  /** The time a server takes to serve a customer. */
  static final double SERVICE_TIME = 1.0;
  private SimState state;

  /**
   * Create a Simulator and initializes it.
   *
   * @param numOfServers Number of servers to be created for simulation.
   */
  public Simulator(int numOfServers) {
    state = new SimState(numOfServers);
  }

  public SimState getState() {
    return state;
  }

  public void setState(SimState state) {
    this.state = state;
  }

  /**
   * The main simulation loop.  Repeatedly get events from the event
   * queue, simulate and update the event.  Return the final simulation
   * state.
   * @return The final state of the simulation.
   */
  public SimState run() {
    Pair<Event, SimState> p = state.nextEvent();
    while (p.first != null) {
      p = p.first.simulate(p.second).nextEvent();
    }
    return p.second;
  }
}
