package cs2030.lambda;

import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.function.Function;

/**
 * InfiniteListGrader extends InfiniteList by augmenting
 * the head supplier with a counter to count how many times
 * the head is evaluated.  It is used to check if the
 * implementation of InfiniteList is fully lazy.  It
 * maintains a counter internally.  The counter can be
 * retrieved using the `InfiniteListGrader.numOfEvals()`
 * method and reset back to 0 using the `InfiniteListGrader.reset()`
 * method.
 */
public class InfiniteListGrader<T> extends InfiniteList<T> {

  /**
   * The number of times the supplier/function of interest has been
   * evaluated.
   */
  private static int numOfEvals = 0;

  /**
   * Constructor for an InfiniteListGrader.  This is private and not
   * intended to be called.
   * @param init The value of the head of this List.
   * @param supplier The supplier of the tail of this list.
   */
  private InfiniteListGrader(T init, Supplier<InfiniteList<T>> supplier) {
    super(init, supplier);
  }

  /**
   * Generate an infinite list of elements, each is generated with
   * the given supplier.  The supplier is augmented with a counter.
   * @param <T> The type of elements to generate.
   * @param supply A supplier function to generate the elements.
   * @return The new list generated.
   */
  public static <T> InfiniteList<T> generate(Supplier<T> supply) {
    Supplier<T> headSupplierWithCounter = () -> {
      numOfEvals++;
      return supply.get();
    };
    return InfiniteList.generate(headSupplierWithCounter);
  }

  /**
   * Generate an infinite list of elements, starting with {@code init}
   * and with the next element computed with the {@code next} function.
   * The {@code next} method is augmented with a counter.
   * @param <T> The type of elements to generate.
   * @param init The value of the head.
   * @param next A function to generate the next element.
   * @return The new list generated.
   */
  public static <T> InfiniteList<T> iterate(T init, Function<T,T> next) {
    Function<T,T> nextWithCounter = x -> {
      numOfEvals++;
      return next.apply(x);
    };
    return InfiniteList.iterate(init, nextWithCounter);
  }

  public static int numOfEvals() {
    return numOfEvals;
  }

  public static void reset() {
    numOfEvals = 0;
  }
}
