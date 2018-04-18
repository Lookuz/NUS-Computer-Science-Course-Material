package cs2030.simulator;

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
public abstract class Event implements Comparable<Event> {
  /** The time this event occurs at. */
  protected double time;

  /**
   * Creates an event and initializes it.
   *
   * @param time The time of occurrence.
   */
  public Event(double time) {
    this.time = time;
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
   * The abstract method that simulates this event.
   *
   * @param sim The simulator.
   * @return The updated state after simulating this event.
   */
  abstract SimState simulate(SimState sim);
}
