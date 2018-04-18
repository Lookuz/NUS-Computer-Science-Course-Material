import java.time.Duration;
import java.time.Instant;
import java.util.Random;
import java.util.function.Supplier;

/**
 * LabFive is the main driver class for testing matrix multiplication.
 * Usage: java LabFive n
 * 2^n is the dimension of the square matrixOne
 */
class LabFive {
  public static void main(String[] args) {
    if (args.length != 1) {
      System.err.println("Incorrect number of arguments. " +
          "Usage: java LabFive n");
      return;
    }

    int n = Integer.parseInt(args[0]);

    Random random = new Random(1); // random number generator to fill matrix
    int dimension = 1 << n; // dimension of square matrix 2^n;
    System.out.println("dimension " + dimension);

    // fill two matrices of size 2^n x 2^n with random numbers.
    Matrix matrixOne = Matrix.generate(dimension, () -> random.nextDouble());
    Matrix matrixTwo = Matrix.generate(dimension, () -> random.nextDouble());

    Matrix result1 = Matrix.multiplyNonRecursively(matrixOne, matrixTwo);
    Matrix result2 = Matrix.multiplyParallely(matrixOne, matrixTwo);
    boolean match = Matrix.equals(result1, result2);
    if (!match) {
      System.out.println("ERROR: matrix multiplication gives inconsistent " +
          "result when multiplying sequentially and parallel.");
      return;
    }

    double d1 = measureTimeToRun(() -> Matrix.multiplyNonRecursively(matrixOne,
            matrixTwo));
    double d2 = measureTimeToRun(() -> Matrix.multiplyParallely(matrixOne, matrixTwo));
    System.out.printf("Parallel %.3f ms Sequential %.3f ms Speedup %.3f times\n", d2, d1, d1 / d2);
  }

  /**
   * Return the average time needed to run the task over five runs.
   * @param  task A lambda expression for the task to be run
   * @return The average time taken in ms.
   */
  private static double measureTimeToRun(Supplier<Matrix> task) {
    final int numOfTimes = 3;
    double sum = 0;
    for (int i = 0; i < numOfTimes; i++) {
      Instant start = Instant.now();
      Matrix m = task.get();
      Instant stop = Instant.now();
      sum += Duration.between(start, stop).toMillis();
    }
    return sum / numOfTimes;
  }
}
