import cs2030.lambda.InfiniteList;
import cs2030.lambda.InfiniteListGrader;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Supplier;

/**
 * This class performs a series of tests to check if InfiniteList
 * is behaving lazily as well as returning the right values.
 */
class LabThree {

  /** Print when a test passes. */
  static void pass(String op, String diagnosis) {
    System.out.println(" 👍 " + op + ": Passed. " + diagnosis);
  }

  /** Print when a test fails. */
  static void fail(String op, String diagnosis) {
    System.out.println(" ❌ " + op + ": Failed. " + diagnosis);
  }

  /**
   * Test if given supplier of Boolean returns true.
   * @param toTest An expression to test
   * @param msg A description of the test
   */
  static void test(Supplier<Boolean> toTest, String msg) {
    try {
      if (toTest.get()) {
        pass(msg, "");
      } else {
        fail(msg, "");
      }
    } catch (Exception e) {
      fail(msg, "Exception thrown. " + e);
      e.printStackTrace();
    }
  }

  /**
   * Test if given InfiniteList expression returns the expected result.
   * Here, the expression must ends with `toArray`.  An array of expected
   * result and the expected number of evaluations are given for comparison.
   *
   * @param toTest An expression to test
   * @param expected The expected result
   * @param expectedNumOfEval The expected number of evaluations
   * @param op A description of the test
   */
  static void test(Supplier<Object[]> toTest, Object[] expected,
      int expectedNumOfEval, String op) {
    InfiniteListGrader.reset();
    try {
      Object[] result = toTest.get();
      if (!Arrays.equals(result, expected)) {
        fail(op, "Expected " + Arrays.toString(expected) + " but get "
            + Arrays.toString(result) + ".");
      }
      checkLazyEnough(expectedNumOfEval, op);
    } catch (Exception e) {
      fail(op, "Exception thrown. " + e);
      e.printStackTrace();
    }

  }

  /**
   * Test if given InfiniteList expression returns the expected result.
   * Here, the expression returns a value of type T.  The expected
   * result and the expected number of evaluations are given for comparison.
   *
   * @param toTest An expression to test
   * @param expected The expected result
   * @param expectedNumOfEval The expected number of evaluations
   * @param op A description of the test
   */
  static <T> void test(Supplier<T> toTest, T expected,
      int expectedNumOfEval, String op) {
    InfiniteListGrader.reset();
    try {
      T result = toTest.get();
      if (!result.equals(expected)) {
        fail(op, "Expected " + expected + " but get "
            + result + ".");
        return;
      }
      checkLazyEnough(expectedNumOfEval, op);
    } catch (Exception e) {
      fail(op, "Exception thrown. " + e);
      e.printStackTrace();
    }
  }

  /**
   * Test the expected number of evaluations for non-terminating
   * operations (no result is returned).
   *
   * @param toTest An expression to test
   * @param expectedNumOfEval The expected number of evaluations
   * @param op A description of the test
   */
  static <T> void test(Supplier<T> toTest, int expectedNumOfEval, String op) {
    InfiniteListGrader.reset();
    try {
      T result = toTest.get();
      checkLazyEnough(expectedNumOfEval, op);
    } catch (Exception e) {
      fail(op, "Exception thrown. " + e);
      e.printStackTrace();
    }
  }

  /**
   * Test if given InfiniteList expression has been evaluated no more than
   * the expected number of times.
   */
  public static void checkLazyEnough(int expectedNumOfEval, String op) {
    if (InfiniteListGrader.numOfEvals() > expectedNumOfEval) {
      fail(op, "(Took " + InfiniteListGrader.numOfEvals() + " > " + expectedNumOfEval + ")");
      return;
    }
    pass(op, "(Took " + InfiniteListGrader.numOfEvals() + " <= " + expectedNumOfEval + ")");
  }

  public static void main(String[] args) {

    InfiniteList<Integer> empty = InfiniteList.empty();
    InfiniteList<Integer> ones = InfiniteListGrader.generate(() -> 1);
    InfiniteList<Integer> natural = InfiniteListGrader.iterate(0, x -> x + 1);
    InfiniteList<String>  aaas = InfiniteListGrader.iterate("a", s -> s + "a");

    test(() -> empty.isEmpty(),
        "empty.isEmpty()");

    test(() -> empty.findFirst(x -> x == 0).equals(Optional.empty()),
        "empty.findFirst(x -> x == 0).equals(Optional.empty())");

    test(() -> empty.map(x -> x).isEmpty(),
        "empty.map(x -> x).isEmpty()");

    test(() -> empty.reduce(0, (x, y) -> x + y) == 0,
        "empty.reduce(0, (x, y) -> x + y) == 0");

    test(() -> empty.takeWhile(x -> x == 0).isEmpty(),
        "empty.takeWhile(x -> x == 0).isEmpty()");

    test(() -> empty.filter(x -> x > 1).isEmpty(),
        "empty.filter(x -> x > 1).isEmpty()");

    test(() -> empty.limit(1).isEmpty(),
        "empty.limit(1).isEmpty()");

    test(() -> empty.count() == 0,
        "empty.count() == 0");

    InfiniteListGrader.reset();
    InfiniteList<Integer> temp;

    temp = InfiniteList.iterate(0, x -> x + 1);
    test(() -> InfiniteListGrader.numOfEvals() == 0,
        "Should not have evaluated anything just by generating a list.");

    temp = InfiniteList.generate(() -> 10);
    test(() -> InfiniteListGrader.numOfEvals() == 0,
        "Should not have evaluated anything just by generating a list.");

    test(() -> natural.limit(1).filter(x -> x > 2).isEmpty(),
        "natural.limit(1).filter(x > 2).isEmpty()");

    test(() -> natural.limit(1).count(),
        1,
        0,
        "natural.limit(1).count() == 1");

    test(() -> natural.limit(4).count(),
        4,
        3,
        "natural.limit(4).count() == 4");

    test(() -> natural.takeWhile(x -> x < 2).count(),
        2,
        0,
        "natural.takeWhile(x < 2).count()");

    test(() -> natural.takeWhile(x -> x < 0).count(),
        0,
        0,
        "natural.takeWhile(x < 0).count()");

    test(() -> natural.map(x -> x).takeWhile(x -> x < 4).limit(1).toArray(),
        new Integer[] { 0 },
        0,
        "natural.map(x).takeWhile(x < 4).limit(1).toArray()");

    test(() -> natural.limit(4).takeWhile(x -> x < 2).count(),
        2,
        0,
        "natural.limit(4).takeWhile(x < 2).count() == 2");

    test(() -> natural.filter(x -> x < 10).limit(4).count(),
        4,
        0,
        "natural.filter(x < 10).limit(4).count() == 4");

    test(() -> natural.map(x -> x * x).findFirst(x -> x > 10),
        Optional.of(16),
        1,
        "natural.map(x*x).find1st(x > 10)");

    test(() -> natural.limit(5).toArray(),
        new Integer[] { 0, 1, 2, 3, 4 },
        0,
        "natural.limit(5).toArray()");

    test(() -> natural.map(x -> x * x).limit(5).toArray(),
        new Integer[] { 0, 1, 4, 9, 16 },
        0,
        "natural.map(x*x).limit(5).toArray()");

    test(() -> natural.map(x -> x * x).limit(5).reduce(0, (x, y) -> x + y),
        30,
        0,
        "natural.map(x*x).limit(5).reduce(x+y)");

    test(() -> natural.takeWhile(x -> x < 5).toArray(),
        new Integer[] { 0, 1, 2, 3, 4 },
        1,
        "natural.takeWhile(x < 5).toArray()");

    test(() -> natural.limit(4).takeWhile(x -> x < 2).toArray(),
        new Integer[] { 0, 1 },
        0,
        "natural.limit(4).takeWhile(x < 2).toArray()");


    test(() -> natural.map(x -> x * x).filter(x -> x % 2 == 1).limit(3).toArray(),
        new Integer[] { 1, 9, 25 },
        0,
        "natural.map(x*x).filter(x % 2 == 1).limit(3).toArray()");

    test(() -> natural.filter(x -> x > 6).limit(2).toArray(),
        new Integer[] { 7, 8 },
        3,
        "natural.filter(x -> x > 10).limit(1).toArray()");

    test(() -> natural.filter(x -> x > 10).filter(x -> x < 20).limit(1).toArray(),
        new Integer[] { 11 },
        3,
        "natural.filter(x -> x > 10).filter(x -> x < 20).limit(1).toArray()");

    test(() -> natural.limit(0).count(),
        0,
        0,
        "natural.limit(0).count() == 0");

    test(() -> natural.limit(4).filter(x -> x < 2).count(),
        2,
        0,
        "natural.limit(4).filter(x < 2).count() == 2");

    test(() -> !ones.isEmpty(),
        true,
        0,
        "ones.isEmpty()");

    test(() -> ones.findFirst(x -> x == 1),
        Optional.of(1),
        1,
        "ones.findFirst(x -> x == 1)");

    test(() -> ones.filter(i -> i < 0),
        1,
        "ones.filter(i < 0) should not go into infinite loop");

    test(() -> ones.takeWhile(i -> i > 0),
        0,
        "ones.takeWhile(i > 0) should not go into infinite loop");

    test(() -> ones.map(i -> i + 1),
        0,
        "ones.map(i + 1) should not go into infinite loop");

    test(() -> ones.limit(100),
        0,
        "ones.limit(100) should not go into infinite loop");

    test(() -> aaas.filter(s -> s.length() < 0),
        1,
        "aaas.filter(len < 0) should not go into infinite loop");

    test(() -> aaas.takeWhile(s -> s.length() > 0),
        0,
        "aaas.takeWhile(len > 0) should not go into infinite loop");

    test(() -> aaas.map(s -> s + "h!"),
        0,
        "aaas.map(s + h!) should not go into infinite loop");

    test(() -> aaas.limit(100),
        0,
        "aaas.limit(100) should not go into infinite loop");
  }
}
