package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cs3500.pa04.MockOutputStream;

/**
 * Tests the PlayerBoard class.
 */
public class BoardTest {
  private Board testBoard;
  private GameView testView;
  private MockOutputStream testOut;

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
    testOut = new MockOutputStream();
    testView = new TerminalView(testOut.toPrintStream(), new StringReader(""));
  }

  /**
   * Tests if the board representation gotten from the board is correct.
   */
  @Test
  public void testGetPlayerBoard() {
    String expected = """
        Your Board:
                
                0  1  2  3  4
              0 ~  ~  ~  ~  ~
              1 ~  ~  ~  ~  U
              2 D  D  D  D  U
              3 ~  ~  ~  ~  U
              4 ~  ~  ~  ~  ~
                
        """;
    testView.displayPlayerBoard(testBoard.getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Tests if the board representation gotten from the board is correct.
   */
  @Test
  public void testGetOpponentBoard() {
    String expected = """
        Opponent Board Data:
                
                0  1  2  3  4
              0 ~  ~  ~  ~  ~
              1 ~  ~  ~  ~  U
              2 D  D  D  D  U
              3 ~  ~  ~  ~  U
              4 ~  ~  ~  ~  ~
                
        """;
    testView.displayOpponentBoard(testBoard.getPlayerBoard());
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
                
                0  1  2  3  4
              0 ~  ~  ~  ~  ~
              1 ~  ~  ~  ~  S
              2 D  D  D  D  S
              3 ~  ~  ~  ~  S
              4 ~  ~  ~  ~  ~
                
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
                
                0  1  2  3  4
              0 X  ~  ~  ~  ~
              1 ~  ~  ~  ~  U
              2 D  D  D  D  H
              3 ~  ~  ~  ~  U
              4 ~  ~  ~  ~  ~
                
        """;
    testView.displayPlayerBoard(testBoard.getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Tests if marking the opponent board registers correctly.
   */
  @Test
  void markOpponentTest() {
    Coord coord1 = new Coord(4, 2);
    Coord coord2 = new Coord(0, 0);
    assertTrue(testBoard.validShots().contains(coord1));
    assertTrue(testBoard.validShots().contains(coord2));
    testBoard.markOpponent(coord1, true);
    testBoard.markOpponent(coord2, false);
    assertFalse(testBoard.validShots().contains(coord1));
    assertFalse(testBoard.validShots().contains(coord2));
  }

  /**
   //   * Tests if validShots() returns the correct coords left to available shoot.
   //   */
  @Test
  public void validShots() {
    for (int i = 0; i < 5; i++) {
      for (int j = 1; j < 5; j++) {
        if ((i + j) % 3 == 0) {
          testBoard.markOpponent(new Coord(j, i), true);
        } else {
          testBoard.markOpponent(new Coord(j, i), false);
        }
      }
    }
    Set<Coord> expectedValid = new HashSet<>();
    for (int i = 0; i < 5; i++) {
      expectedValid.add(new Coord(0, i));
    }
    assertEquals(expectedValid, testBoard.validShots());
  }
}