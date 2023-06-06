package pa04.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pa04.TestInputStream;
import pa04.TestOutputStream;

/**
 * Tests the InputCollector class.
 */
public class InputCollectorTest {
  private TestOutputStream testOut;
  private TestInputStream testIn;
  private GameView testView;
  private UserCommunicator collector;

  /**
   * Sets up the test output.
   */
  @BeforeEach
  public void setup() {
    testOut = new TestOutputStream();
    testIn = new TestInputStream();
    testView = new TerminalView(testOut.toPrintStream(), testIn.toReadable());
    collector = new UserCommunicator(testView);
  }

  /**
   * Tests if a request returns correctly.
   */
  @Test
  public void testRequestBoardSize() {
    testIn.input("6 8");
    Coord size = collector.requestBoardDimensions();
    assertEquals(new Coord(6, 8), size);
    assertEquals("Please enter a valid height and width below:\n", testOut.toString());
  }

  /**
   * Tests if request handles invalid inputs.
   */
  @Test
  public void testRequestBoardSizeRetries() {
    testIn.input("""
        0 0
        1 2 3 4
        8 20
        1 10
        42 65
        4 53
        bad input
        10
                
        13  12
        6 8
        """);
    Coord size = collector.requestBoardDimensions();
    String invalid = "The dimensions you entered were invalid dimensions. Please remember that "
        + "the height and width of the game must be in the range (6, 15), inclusive.\n";
    String prompt = "Please enter a valid height and width below:\n";
    assertEquals(new Coord(6, 8), size);
    assertEquals((prompt + invalid).repeat(10) + prompt, testOut.toString());
  }

  /**
   * Tests if a request returns correctly.
   */
  @Test
  public void testRequestFleet() {
    testIn.input("1 2 2 1");
    Map<ShipType, Integer> specifications = collector.requestFleet(6);
    assertEquals(Map.of(ShipType.CARRIER, 1, ShipType.BATTLESHIP, 2, ShipType.DESTROYER, 2,
        ShipType.SUBMARINE, 1), specifications);
    assertEquals("""
        Please enter your fleet in the order [CARRIER, BATTLESHIP, DESTROYER, SUBMARINE].
        """, testOut.toString());
  }

  /**
   * Tests if request handles invalid inputs.
   */
  @Test
  public void testRequestFleetRetries() {
    testIn.input("""
        1 3 2 0
        1  2 2 1
        10 3 42 1
        bad input
        1 2 1
                
        1 1 1 1 1 1 1 1 1
        1 2 2 1
        """);
    Map<ShipType, Integer> specifications = collector.requestFleet(6);
    assertEquals(Map.of(
            ShipType.CARRIER, 1,
            ShipType.BATTLESHIP, 2,
            ShipType.DESTROYER, 2,
            ShipType.SUBMARINE, 1),
        specifications);
    String invalid = "The fleet you entered is invalid. Remember, your fleet must of size 6 "
        + "and have at least one of each ship type.\n";
    String prompt = "Please enter your fleet in the order [CARRIER, BATTLESHIP, DESTROYER, "
        + "SUBMARINE].\n";
    assertEquals((prompt + invalid).repeat(7) + prompt, testOut.toString());
  }

  /**
   * Tests if a request returns correctly.
   */
  @Test
  public void testRequestShots() {
    testIn.input("""
        0 0
        1 3
        3 2
        5 4
        5 5
        """);
    List<Coord> input = collector.requestShots(5);
    assertEquals(
        Set.of(new Coord(0, 0), new Coord(1, 3),
              new Coord(3, 2), new Coord(5, 4),
              new Coord(5, 5)), new HashSet<>(input));
    assertEquals("Please Enter 5 Shots:\n", testOut.toString());
  }

  /**
   * Tests if request handles invalid inputs.
   */
  @Test
  public void testRequestShotsRetries() {
    testIn.input("""
        0 0
        bad input
        1 2 3
        2 2
        0  0
        1 1
        0 0
        1 1
        """);
    List<Coord> input = collector.requestShots(2);
    assertEquals(Set.of(new Coord(0, 0), new Coord(1, 1)), new HashSet<>(input));
    assertEquals("""
        Please Enter 2 Shots:
        Some of the shots you entered were either invalid, out of bounds, or has already been shot.
        Please Enter 2 Shots:
        Some of the shots you entered were either invalid, out of bounds, or has already been shot.
        Please Enter 2 Shots:
        Some of the shots you entered were either invalid, out of bounds, or has already been shot.
        Please Enter 2 Shots:
        """, testOut.toString());
  }

  /**
   * Tests if signal gets to the view.
   */
  @Test
  public void testSignalInvalidShots() {
    UserCommunicator collector = new UserCommunicator(testView);
    collector.signalInvalidShots();
    assertEquals("Some of the shots you entered were either invalid, out of bounds, or has "
        + "already been shot.\n", testOut.toString());
  }
}