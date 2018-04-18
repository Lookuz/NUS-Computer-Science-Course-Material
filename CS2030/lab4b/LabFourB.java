import cs2030.simulator.Event;
import cs2030.simulator.SimState;
import cs2030.simulator.Simulator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Optional;
import java.util.Scanner;

/**
 * The LabOFourA class is the entry point into Lab 4a.
 *
 * @author atharvjoshi
 * @author weitsang
 * @version CS2030 AY17/18 Sem 2 Lab 4a
 */
class LabFourB {
  /**
   * The main method for Lab 4b. Reads data from file and
   * then run a simulation based on the input data.
   *
   * @param args two arguments, first an integer specifying number of servers
   *     in the shop. Second a file containing a sequence of double values, each
   *     being the arrival time of a customer (in any order).
   */
  public static void main(String[] args) {
    Optional<Scanner> hasScanner = createScanner(args);
    if (!hasScanner.isPresent()) {
      return;
    }

    Scanner scanner = hasScanner.get();

    // Read the first line of input as number of servers in the shop
    int numOfServers = scanner.nextInt();
    Simulator sim = new Simulator(numOfServers);

    int customerId = 0;
    while (scanner.hasNextDouble()) {
      double arrivalTime = scanner.nextDouble();
      sim = new Simulator(sim.getState()
          .addEvent(new Event(arrivalTime, customerId++)));

    }

    scanner.close();

    // After data input is handled, run the simulator
    SimState result = sim.run();

    System.out.println(result);
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
    Optional<Scanner> scanner = Optional.empty();

    try {
      // Read from stdin if no filename is given, otherwise read from the
      // given file.
      if (args.length == 0) {
        // If there is no argument, read from standard input.
        scanner = Optional.of(new Scanner(System.in));
      } else {
        // Else read from file
        scanner = Optional.of(new Scanner(new FileReader(args[0])));
      }
    } catch (FileNotFoundException exception) {
      System.err.println("Unable to open file " + args[0] + " "
          + exception);
    }
    return scanner;
  }
}
