package cs2030.lambda;

import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.List;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A InfiniteList is a list that supports functional operations
 * generate, iterate, map, filter, reduce, findFirst, limit, count,
 * and takeWhile.   An InfiniteLIst is immutable and is _lazily_ evaluated.
 */
public class InfiniteList<T> {
  /** The supplier of the head. */
  private Supplier<T> headSupplier;

  /** The supplier of the tail (rest of the list). */
  private Supplier<InfiniteList<T>> tailSupplier;

  /** A cached value of the head. */
  private Optional<T> headValue;

  /** A cached value of the tail. */
  private Optional<InfiniteList<T>> tailValue;

  private boolean filtered = false;

  /**
   * InfiniteList has a private constructor to prevent the list
   * from created directly.
   */
  private InfiniteList() { }

  /**
   * Empty is a special private subclass of InfiniteList that
   * corresponds to an empty InfiniteList.  We intentionally
   * violate LSP here, so that it throws an error if we try
   * to use an empty list like a normal list.
   */
  private static class Empty<T> extends InfiniteList<T> {
    @Override
    public T head() {
      throw new IllegalStateException("calling head() on empty list");
    }

    @Override
    public InfiniteList<T> tail() {
      throw new IllegalStateException("calling tail() on empty list");
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public <R> InfiniteList<R> map(Function<? super T, ? extends R> mapper) {
      return InfiniteList.empty();
    }

    @Override
    public InfiniteList<T> limit(int n) {
      return this;
    }

    @Override
    public InfiniteList<T> takeWhile(Predicate<T> predicate) {
      return this;
    }

    @Override
    public InfiniteList<T> filter(Predicate<T> predicate) {
      return this;
    }

    @Override
    public String toString() {
      return "empty";
    }
  }

  // TODO: Add private methods for constructing the list here.

  /**
   * A private constructor that takes in two suppliers.
   * @param head The supplier for the head of the list.
   * @param tail The supplier for the tail of the list.
   */
  InfiniteList(Supplier<T> head, Supplier<InfiniteList<T>> tail) {
    this.headSupplier = head;
    this.headValue = Optional.empty();
    this.tailValue = Optional.empty();
    this.tailSupplier = tail;
  }

  /**
   * A private constructor that takes in a tail supplier and a head
   * value.
   * @param head The value of the head of the List.
   * @param tail The supplier of the tail of the list.
   */
  InfiniteList(T head, Supplier<InfiniteList<T>> tail) {
    this.headSupplier = () -> head;
    this.headValue = Optional.empty();
    this.tailValue = Optional.empty();
    this.tailSupplier = tail;
  }

  /**
   * A private constructor that takes in 2 suppliers, and a boolean flag
   * for filtered that is set to true.
   * @param head The supplier for the head of the list.
   * @param tail The supplier of the tail of the list.
   * @param filtered Boolean flag that determines if item is filtered
   */
  InfiniteList(Supplier<T> head, Supplier<InfiniteList<T>> tail,
      boolean filtered) {
    this(head, tail);
    this.filtered = true;
  }

  /**
   * Return the head of the list.  If the head is not evaluated yet,
   * the supplier is called and the value is cached.
   * @return The head of the list.
   */
  public T head() {
    return this.headValue.orElseGet(() -> {
      T head = this.headSupplier.get();
      this.headValue = Optional.of(head);
      return head;
    });
  }

  /**
   * Return the tail of the list, which is another InfiniteList.
   * If the tail is not evaluated yet, the supplier is called and
   * the value is cached.
   * @return The tail of the list.
   */
  public InfiniteList<T> tail() {
    InfiniteList<T> list = this.tailValue.orElseGet(this.tailSupplier);
    this.tailValue = Optional.of(list);
    return list;
  }

  /**
   * Create an empty InfiniteList.
   * @param <T> The type of the elements in the list.
   * @return An empty InfiniteList.
   */
  public static <T> InfiniteList<T> empty() {
    return new Empty<T>();
  }

  /**
   * Checks if the list is empty.
   * @return true if the list is empty; false otherwise.
   */
  public boolean isEmpty() {
    if (this.filtered) {
      return this.tail().isEmpty();
    }
    return false;
  }

  /**
   * Generate an infinite list of elements, each is generated with
   * the given supplier.
   * @param <T> The type of elements to generate.
   * @param supply A supplier function to generate the elements.
   * @return The new list generated.
   */
  public static <T> InfiniteList<T> generate(Supplier<T> supply) {
    return new InfiniteList<T>(supply, () -> InfiniteList.generate(supply));
  }

  /**
   * Generate an infinite list of elements, starting with {@code init}
   * and with the next element computed with the {@code next} function.
   * @param <T> The type of elements to generate.
   * @param init The value of the head.
   * @param next A function to generate the next element.
   * @return The new list generated.
   */
  public static <T> InfiniteList<T> iterate(T init, Function<T, T> next) {
    return new InfiniteList<T>(init,
        () -> InfiniteList.iterate(next.apply(init), next));
  }

  /**
   * Return the first element that matches the given predicate, or
   * Optional.empty() if none of the elements matches.
   * @param  predicate A predicate to apply to each element to determine
   *     if it should be returned.
   * @return An Optional object containing either the first element
   *     that matches, or is empty if none of the element matches.
   */
  public Optional<T> findFirst(Predicate<T> predicate) {
    InfiniteList<T> list = this;

    while (!list.isEmpty()) {
      if (!list.filtered) {
        T next = list.head();
        if (predicate.test(next)) {
          return Optional.of(next);
        }
      }
      list = list.tail();
    }
    return Optional.empty();
  }

  /**
   * Returns a list consisting of the results of applying the given function
   * to the elements of this list.
   * @param <R> The type of elements returned.
   * @param mapper The function to apply to each element.
   * @return The new list.
   */
  public <R> InfiniteList<R> map(Function<? super T, ? extends R> mapper) {
    InfiniteList<T> list = this;
    if (!list.isEmpty()) {
      if (!list.filtered) {
        return new InfiniteList<R>(mapper.apply(list.head()),
            () -> list.tail().map(mapper));
      } else {
        return new InfiniteList<R>(() -> mapper.apply(list.head()),
            () -> list.tail().map(mapper), true);
      }
    }
    return InfiniteList.empty();
  }

  /**
   * Reduce the elements of this stream to a single value, by successively
   * "accumulating" the elements using the given accumulation function.
   *
   * @param <U> The type of the value the list is reduced into.
   * @param identity The identity (initial accumulated values)
   * @param accumulator A function that accumulates elements in the stream.
   * @return The accumulated value.
   */
  public <U> U reduce(U identity, BiFunction<U, ? super T, U> accumulator) {
    InfiniteList<T> list = this;
    // Recursively perform reduce by accumulating the identity
    // and the current head
    if (!list.isEmpty()) {
      T head = list.head();
      if (!list.filtered) {
        return list.tail().reduce(accumulator.apply(identity, head),
            accumulator);
      } else {
        return list.tail().reduce(identity, accumulator);
      }
    }
    return identity;
  }

  /**
   * Truncate the list to up to n elements.  If the list has less than n
   * elements, then the original list is returned.
   * @param n The number of items to limit the list to.
   * @return The truncated list.
   */
  public InfiniteList<T> limit(int n) {
    InfiniteList<T> list = this;
    if (n == 0 || list.isEmpty()) {
      return InfiniteList.empty();
    } else { // Recursively limit the tail size of the list
      if (list.filtered) {
        return new InfiniteList<T>(list.headSupplier,
            () -> list.tail().limit(n), true);
      } else if (n == 1) {
        return new InfiniteList<T>(list.headSupplier,
            () -> InfiniteList.empty());
      }
      return new InfiniteList<T>(list.headSupplier,
          () -> list.tail().limit(n - 1));
    }
  }

  /**
   * Return a new list consisting of elements from this list
   * by successively copying the elements, until the predicate
   * becomes false.  All elements in the returned list passed
   * the predicate.
   * @param predicate A predicate where elements in the returned
   *     list must satisfied.
   * @return The new list.
   */
  public InfiniteList<T> takeWhile(Predicate<T> predicate) {
    InfiniteList<T> list = this;
    if (!list.isEmpty()) {
      if (list.filtered) {
        return new InfiniteList<T>(list.headSupplier,
            () -> list.tail().takeWhile(predicate), true);
      } else if (predicate.test(list.head())) {
        // If current head passes predicate, take it
        // and test predicate on the tail of the list
        return new InfiniteList<T>(list.headSupplier,
            () -> list.tail().takeWhile(predicate));
      }
    } // Else return Empty<T>
    return InfiniteList.empty();
  }

  /**
   * Returns a list consisting of the elements of this list that
   * match the given predicate.
   * @param  predicate A predicate to apply to each element to
   *     determine if it should be included
   * @return The new list.
   */
  public InfiniteList<T> filter(Predicate<T> predicate) {
    InfiniteList<T> list = this;
    if (!list.isEmpty()) {
      // if current head fulfils the predicate, take it
      // and filter the elements in the tail
      if (predicate.test(list.head()) && !list.filtered) {
        return new InfiniteList<T>(list.head(),
            () -> list.tail().filter(predicate));
      } else { // Ignore the current head and filter the tail
        return new InfiniteList<T>(() -> list.head(),
            () -> list.tail().filter(predicate), true);
      }
    }
    return InfiniteList.empty();
  }

  /**
   * Return the number of elements in this list.
   * @return The number of elements in the list.
   */
  public int count() {
    InfiniteList<T> list = this;
    if (!list.isEmpty()) {
      if (list.filtered) {
        return list.tail().count();
      } else {
        return 1 + list.tail().count();
      }
    }
    return 0;
  }

  /**
   * Return an array containing the elements in the list.
   * @return The array containing the elements in the list.
   */
  public Object[] toArray() {
    List<Object> list = new ArrayList<>();
    InfiniteList<T> iterList = this;
    while (!iterList.isEmpty()) {
      if (!iterList.filtered) {
        T head = iterList.head();
        list.add(head);
      }
      iterList = iterList.tail();
    }
    return list.toArray();
  }

  /**
   * Return this infinite list in string format.
   */
  public String toString() {
    if (this.isEmpty()) {
      return "-";
    }

    String tail = this.tailValue
        .map(x -> x.toString())
        .orElse("?");
    String head = this.headValue
        .map(x -> x.toString())
        .orElse("?");

    return head + "," + tail;
  }
}
