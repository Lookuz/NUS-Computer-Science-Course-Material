package cs2030.simulator;

/**
 * This is an immutable class that stores stats about the simulation.
 * In particular, the average * waiting time, the number of customer
 * who left, and the number of customers who are served, are stored.
 *
 * @author Ooi Wei Tsang
 * @version CS2030 AY17/18 Sem 2 Lab 4a
 */
class Statistics {
  /** Sum of time spent waiting for all customers. */
  private double totalWaitingTime;

  /** Total number of customers who were served. */
  private int totalNumOfServedCustomers;

  /** Total number of customers who left without being served. */
  private int totalNumOfLostCustomers;

  public Statistics() {
    this.totalWaitingTime = 0;
    this.totalNumOfServedCustomers = 0;
    this.totalNumOfLostCustomers = 0;
  }

  /**
   * Secondary constructor class that copies data from
   * another Statistics class.
   * @param statistics Other Statistics class to copy data from.
   */
  public Statistics(Statistics statistics) {
    this.totalNumOfLostCustomers = statistics.totalNumOfLostCustomers;
    this.totalNumOfServedCustomers = statistics.totalNumOfServedCustomers;
    this.totalWaitingTime = statistics.totalWaitingTime;
  }

  /**
   * Secondary Constructor that manually initializes each field based on
   * the argument parameters.
   * @param numLost Number of customers lost
   * @param numServed Number of customers served
   * @param totalWait Total waiting time
   */
  public Statistics(int numLost, int numServed, double totalWait) {
    this.totalNumOfLostCustomers = numLost;
    this.totalNumOfServedCustomers = numServed;
    this.totalWaitingTime = totalWait;
  }

  /**
   * Mark that a customer is served.
   * @return A new Statistics object with updated stats
   */
  public Statistics serveOneCustomer() {
    return new Statistics(this.totalNumOfLostCustomers,
            this.totalNumOfServedCustomers + 1, this.totalWaitingTime);
  }

  /**
   * Mark that a customer is lost.
   * @return A new Statistics object with updated stats
   */
  public Statistics lostOneCustomer() {
    return new Statistics(this.totalNumOfLostCustomers + 1,
            this.totalNumOfServedCustomers, this.totalWaitingTime);
  }

  /**
   * Accumulate the waiting time of a customer.
   * @param time The time a customer waited.
   * @return A new Statistics object with updated stats
   */
  public Statistics customerWaitedFor(double time) {
    return new Statistics(this.totalNumOfLostCustomers,
            this.totalNumOfServedCustomers, this.totalWaitingTime + time);
  }

  /**
   * Return a string representation of the staistics collected.
   * @return A string containing three numbers: the average
   *     waiting time, followed by the number of served customer,
   *     followed by the number of lost customer.
   */
  public String toString() {
    return String.format("%.3f %d %d",
        totalWaitingTime / totalNumOfServedCustomers,
        totalNumOfServedCustomers, totalNumOfLostCustomers);
  }
}
