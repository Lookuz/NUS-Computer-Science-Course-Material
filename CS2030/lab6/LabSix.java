import java.util.concurrent.CompletableFuture;
import java.time.Instant;
import java.time.Duration;
import java.util.Scanner;
import java.util.Optional;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.List;

/**
 * LabSix finds different ways one can travel by bus (with a bit of walking)
 * from one bus stop to another.
 */
public class LabSix {
  /**
   * The main method takes in an argument, a li source bus stop id and
   * the dest bus stop id.  Both must be valid and found in bus-stops.csv.
   * Otherwise the program will quit.
   * @param args Command line arguments
   */

  public static void main(String[] args) {
    if (args.length != 2) {
      System.err.println("Usage: java LabSix <bus stop id> <name>");
      return;
    }
    BusStop src = new BusStop(args[0]);
    String name = args[1];
    Instant start = Instant.now();
    BusSg.findBusServicesBetween(src, name)
      .ifPresent(System.out::println);
    Instant stop = Instant.now();
    System.out.println("Took " + Duration.between(start, stop).toMillis() + "ms");
  }

  /**
   * Create and return a scanner. If a command line argument is given,
   * treat the argument as a file and open a scanner on the file. Else,
   * create a scanner that reads from standard input.
   *
   * @param args The arguments provided for simulation.
   * @return A scanner or {@code null} if a filename is provided but the file
   *     cannot be open.
   */
  private static Optional<Scanner> createScanner(String[] args) {
    Scanner scanner = null;
    try {
      // Read from stdin if no filename is given, otherwise read from the
      // given file.
      if (args.length == 0) {
        // If there is no argument, read from standard input.
        scanner = new Scanner(System.in);
      } else {
        // Else read from file
        FileReader fileReader = new FileReader(args[0]);
        scanner = new Scanner(fileReader);
      }
    } catch (FileNotFoundException exception) {
      System.err.println("Unable to open file " + args[0] + " "
          + exception);
    }
    return Optional.ofNullable(scanner);
  }
}
