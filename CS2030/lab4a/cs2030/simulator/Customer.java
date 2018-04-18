package cs2030.simulator;

/**
 * The Customer class encapsulates information and methods pertaining to a
 * Customer in a simulation.  In Lab 4, we simplfied the class to maintaining
 * only two variables -- id and timeArrived.
 *
 * @author weitsang
 * @author atharvjoshi
 * @version CS2030 AY17/18 Sem 2 Lab 4b
 */
class Customer {
  /** The unique ID of the last created customer.  */
  private static int lastCustomerId = 0;

  /** The unique ID of this customer. */
  private final int id;

  /** The time this customer arrives. */
  private double timeArrived;

  /**
   * Create and initalize a new customer.
   * The {@code id} of the customer is set.
   *
   * @param timeArrived The time this customer arrived in the simulation.
   */
  public Customer(double timeArrived) {
    this.timeArrived = timeArrived;
    this.id = Customer.lastCustomerId;
    Customer.lastCustomerId++;
  }

  /**
   * Return the arrival time of this customer.
   * @return The arrival time of this customer.
   */
  double timeArrived() {
    return timeArrived;
  }

  /**
   * Return a string representation of this customer.
   * @return The id of the customer prefixed with "C"
   */
  public String toString() {
    return "C" + this.id;
  }
}
