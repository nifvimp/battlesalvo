package pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cs3500.pa04.model.Board;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.Orientation;
import cs3500.pa04.model.Ship;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pa04.TestOutputStream;

/**
 * Tests the PlayerBoard class.
 */
public class PlayerBoardTest {
  private Board testBoard;
  private GameView testView;
  private TestOutputStream testOut;

  /**
   * Sets up a test board and test output.
   */
  @BeforeEach
  public void setup() {
    List<Ship> ships = List.of(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.HORIZONTAL),
        new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL)
    );
    testBoard = new Board(5, 5, ships);
    testOut = new TestOutputStream();
    testView = new TerminalView(testOut.toPrintStream(), new StringReader(""));
  }

  /**
   * Tests if the board representation gotten from the board is correct.
   */
  @Test
  public void testGetBoardScene() {
    String expected = """
        Your Board:
        \t- - - - -
        \t- - - - S
        \tD D D D S
        \t- - - - S
        \t- - - - -
        """;
    testView.displayPlayerBoard(testBoard.getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Tests if shipsLeft() returns correctly.
   */
  @Test
  public void testShipsLeft() {
    assertEquals(2, testBoard.shipsLeft());
    testBoard.takeDamage(new Coord(4, 1));
    testBoard.takeDamage(new Coord(4, 2));
    testBoard.takeDamage(new Coord(4, 3));
    assertEquals(1, testBoard.shipsLeft());
    String expected = """
        Your Board:
        \t- - - - -
        \t- - - - H
        \tD D D D H
        \t- - - - H
        \t- - - - -
        """;
    testView.displayPlayerBoard(testBoard.getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Tests if hit(Coord shot) return and registers to the board correctly.
   */
  @Test
  public void testTakeDamage() {
    Coord coord1 = new Coord(4, 2);
    Coord coord2 = new Coord(0, 0);
    assertTrue(testBoard.takeDamage(coord1));
    assertFalse(testBoard.takeDamage(coord2));
    String expected = """
        Your Board:
        \tM - - - -
        \t- - - - S
        \tD D D D H
        \t- - - - S
        \t- - - - -
        """;
    testView.displayPlayerBoard(testBoard.getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Tests if validShots() returns the correct coords left to available shoot.
   */
  @Test
  public void testValidShots() {
    for (int i = 0; i < 5; i++) {
      for (int j = 1; j < 5; j++) {
        testBoard.takeDamage(new Coord(j, i));
      }
    }
    Set<Coord> expectedValid = new HashSet<>();
    for (int i = 0; i < 5; i++) {
      expectedValid.add(new Coord(0, i));
    }
    assertEquals(expectedValid, testBoard.validShots());
  }
}