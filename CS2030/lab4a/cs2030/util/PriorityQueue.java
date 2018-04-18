package cs2030.util;

public class PriorityQueue<T> {
  java.util.PriorityQueue<T> pq;

  public PriorityQueue() {
    pq = new java.util.PriorityQueue<T>();
  }

  /**
   * Copy constructor that creates a deep copy of the PriorityQueue queue
   * @param queue PriorityQueue that is to be copied
   */
  public PriorityQueue(PriorityQueue<T> queue) {
    this.pq = new java.util.PriorityQueue<T>(queue.pq);
  }

  public PriorityQueue<T> add(T object) {
    PriorityQueue<T> newPq = new PriorityQueue<T>(this);
    newPq.pq.add(object);
    return newPq;
  }

  public Pair<T, PriorityQueue<T>> poll() {
    PriorityQueue<T> newPq = new PriorityQueue<T>(this);
    T t = newPq.pq.poll();
    return new Pair<>(t, newPq);
  }
}
