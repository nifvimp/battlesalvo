package cs3500.pa04.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.TestInputStream;
import cs3500.pa04.TestOutputStream;
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
    assertEquals("""
        Boards can be between sizes 6 and 15, and dimensions do not need to match.
        Please enter a valid height and width below:
        ------------------------------------------------------
        """, testOut.toString(), "");
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
    String invalid = "Uh Oh! You've entered invalid dimensions. Please remember that the "
        + "height and width of the game must be in the range (6, 15), inclusive."
        + "\n------------------------------------------------------\n";
    String prompt = """
        Boards can be between sizes 6 and 15, and dimensions do not need to match.
        Please enter a valid height and width below:
        ------------------------------------------------------
        """;
    assertEquals((prompt + invalid).repeat(10) + prompt, testOut.toString());
    assertEquals(new Coord(6, 8), size);
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
        Remember, your fleet may not exceed size 6.
        ------------------------------------------------------
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
    String invalid = """
        Uh Oh! You've entered invalid fleet sizes.
        """;
    String prompt = """
        Please enter your fleet in the order [CARRIER, BATTLESHIP, DESTROYER, SUBMARINE].
        Remember, your fleet may not exceed size 6.
        ------------------------------------------------------
        """;
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
    assertEquals("""
        Please enter 5 shots:
        """, testOut.toString());
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
        Please enter 2 shots:
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board.
        Please enter 2 shots:
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board.
        Please enter 2 shots:
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board.
        Please enter 2 shots:
        """, testOut.toString());
  }

  /**
   * Tests if signal gets to the view.
   */
  @Test
  public void testSignalInvalidShots() {
    UserCommunicator collector = new UserCommunicator(testView);
    collector.signalInvalidShots();
    assertEquals("""
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board.
        """, testOut.toString());
  }
}