package cs2030.simulator;

import cs2030.util.Pair;

import java.util.Optional;

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

  /**
   * Constructor that initializes a new Simulator from an existing SimState.
   * @param simState The SimState to be assigned to the new Simulator.
   */
  public Simulator(SimState simState) {
    this.state = new SimState(simState);
  }

  public SimState getState() {
    return state;
  }

  /**
   * The main simulation loop.  Repeatedly get events from the event
   * queue, simulate and update the event.  Return the final simulation
   * state.
   * @return The final state of the simulation.
   */
  public SimState run() {
    Pair<Optional<Event>, SimState> p = state.nextEvent();
    while (p.first.isPresent()) {
//      final SimState newState = new SimState(p.second);
      // p.first.get().simulate(state).nextEvent();
      p = p.first.get()
              .simulate(p.second)
              .nextEvent();
    }
    return p.second;
  }
}
