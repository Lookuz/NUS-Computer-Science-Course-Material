package cs2030.util;

import java.util.Optional;

public class PriorityQueue<T> {
  java.util.PriorityQueue<T> pq;

  public PriorityQueue() {
    pq = new java.util.PriorityQueue<T>();
  }

  /**
   * Copy constructor that creates a deep copy of the PriorityQueue queue.
   * @param queue PriorityQueue that is to be copied
   */
  public PriorityQueue(PriorityQueue<T> queue) {
    this.pq = new java.util.PriorityQueue<T>(queue.pq);
  }
  
  /**
   * Method that adds a an Object object in this
   * PriorityQueue instance.
   * @param object Object to be added in this PriorityQueue Object
   * @return a new PriorityQueue Object with the new added object
   */
  public PriorityQueue<T> add(T object) {
    PriorityQueue<T> newPq = new PriorityQueue<T>(this);
    newPq.pq.add(object);
    return newPq;
  }

  /**
   * Method that returns a new Pair Object containing
   * the next object in this PriorityQueue,
   * and the new PriorityQueue object after the first item has ben removed.
   * @return a new Pair Object where the first value is the first event
   *     of the PriorityQueue, and the second event is the new state of
   *     PriorityQueue after the first item has been removed
   */
  public Pair<Optional<T>, PriorityQueue<T>> poll() {
    PriorityQueue<T> newPq = new PriorityQueue<>(this);
    if (newPq.pq.isEmpty()) {
      return new Pair<>(Optional.empty(), newPq);
    } else {
      Optional<T> t = Optional.of(newPq.pq.poll());
      return new Pair<>(t, newPq);
    }
  }
}
