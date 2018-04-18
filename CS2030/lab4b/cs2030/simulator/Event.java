package cs2030.simulator;

import java.util.function.Function;

/**
 * The Event class encapsulates information and methods pertaining to a
 * Simulator event.  This is an abstract class that should be subclassed
 * into a specific event in the simulator.  The {@code simulate} method
 * must be written.
 *
 * @author Ooi Wei Tsang
 * @author Evan Tay
 * @version CS2030 AY17/18 Sem 2 Lab 1b
 */
public class Event implements Comparable<Event> {
  /** The time this event occurs at. */
  private double time;

  /** 
   * Function that determines the action to be taken.
   * when event is simulated */
  private Function<SimState, SimState> action;

  /**
   * Creates an event and initializes it.
   * Sets default action to simulateArrival
   * @param time The time of occurrence.
   * @param customerId ID to be assigned tothis customer.
   */
  public Event(double time, int customerId) {
    this.time = time;
    this.action = state -> state.simulateArrival(this.time, customerId);
  }

  /**
   * Secondary constructor that takes in 3 parameters
   * and sets the default action to simulateDone.
   * @param time The time that the event occurs
   * @param server Server that finished serving the customer
   * @param customer Customer that is being finished served
   */
  public Event(double time, Server server, Customer customer) {
    this.time = time;
    this.action = state -> state.simulateDone(this.time, server, customer);
  }

  /**
   * Defines natural ordering of events by their time.
   * Events ordered in ascending order of their timestamps.
   *
   * @param other Another event to compare against.
   * @return 0 if two events occur at same time, a positive number if
   *     this event has later than other event, a negative number otherwise.
   */
  public int compareTo(Event other) {
    return (int)Math.signum(this.time - other.time);
  }


  /**
   * Method that simulates this event.
   * @param sim The simulator.
   * @return The updated state after simulating this event.
   */
  SimState simulate(SimState sim) {
    return this.action.apply(sim);
  }
}
