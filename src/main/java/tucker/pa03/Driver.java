package tucker.pa03;

import cs3500.pa03.controller.Controller;
import cs3500.pa03.controller.LocalGameController;
import java.io.InputStreamReader;

/**
 * This is the main driver of this project.
 */
public class Driver {
  /**
   * Project entry point
   *
   * @param args - no command line args required
   */
  public static void main(String[] args) {
    try {
      Controller controller =
          new LocalGameController(new InputStreamReader(System.in), System.out);
      controller.run();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}