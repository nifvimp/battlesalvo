package cs3500.pa04.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BoardTest {
  private Board testBoard;

  /**
   * Sets up a test board.
   */
  @BeforeEach
  public void setup() {
    List<Ship> ships = List.of(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.HORIZONTAL),
        new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL)
    );
    testBoard = new Board(5, 5, ships);
  }

  /**
   * Tests if the board representation gotten from the board is correct.
   */
  @Test
  public void getPlayerBoard() {
    assertArrayEquals(new char[][] {
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', 'U'},
            {'D', 'D', 'D', 'D', 'U'},
            {'~', '~', '~', '~', 'U'},
            {'~', '~', '~', '~', '~'}
        }, testBoard.getPlayerBoard());
  }

  /**
   * Tests if the board representation gotten from the board is correct.
   */
  @Test
  public void getOpponentKnowledge() {
    assertArrayEquals(
        new char[][] {
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', '~'},
        }, testBoard.getOpponentKnowledge());
  }

  /**
   * Tests if the board returns the correct valid shots remaining.
   */
  @Test
  public void validShots() {
    Board smallBoard = new Board(2, 2, Collections.emptyList());
    assertEquals(Set.of(
        new Coord(0, 0), new Coord(0, 1), new Coord(1, 0), new Coord(1, 1)
    ), smallBoard.validShots());
    smallBoard.markOpponent(new Coord(0, 1), false);
    smallBoard.markOpponent(new Coord(1, 0), false);
    assertEquals(Set.of(new Coord(0, 0), new Coord(1, 1)), smallBoard.validShots());
  }

  /**
   * Tests if the board takes damage correctly
   */
  @Test
  public void takeDamage() {
    assertFalse(testBoard.takeDamage(new Coord(0, 0)));
    assertFalse(testBoard.takeDamage(new Coord(0, 0)));
    assertTrue(testBoard.takeDamage(new Coord(0, 2)));
    assertTrue(testBoard.takeDamage(new Coord(0, 2)));
    testBoard.takeDamage(new Coord(4, 1));
    testBoard.takeDamage(new Coord(4, 2));
    testBoard.takeDamage(new Coord(4, 3));
    assertArrayEquals(
        new char[][] {
            {'X', '~', '~', '~', '~'},
            {'~', '~', '~', '~', 'S'},
            {'H', 'D', 'D', 'D', 'S'},
            {'~', '~', '~', '~', 'S'},
            {'~', '~', '~', '~', '~'},
        }, testBoard.getPlayerBoard());
  }

  /**
   * Tests if the board marks its knowledge about opponent board correctly
   */
  @Test
  public void markOpponent() {
    testBoard.markOpponent(new Coord(0, 0), true);
    testBoard.markOpponent(new Coord(1, 1), true);
    testBoard.markOpponent(new Coord(2, 2), false);
    assertArrayEquals(
        new char[][] {
            {'H', '~', '~', '~', '~'},
            {'~', 'H', '~', '~', '~'},
            {'~', '~', 'X', '~', '~'},
            {'~', '~', '~', '~', '~'},
            {'~', '~', '~', '~', '~'},
        }, testBoard.getOpponentKnowledge());
  }

  /**
   * Tests if the gets the correct number of ships left afloat.
   */
  @Test
  public void shipsLeft() {
    assertEquals(2, testBoard.shipsLeft());
    testBoard.takeDamage(new Coord(4, 1));
    testBoard.takeDamage(new Coord(4, 2));
    testBoard.takeDamage(new Coord(4, 2));
    assertEquals(2, testBoard.shipsLeft());
    testBoard.takeDamage(new Coord(4, 3));
    assertEquals(1, testBoard.shipsLeft());
    testBoard.takeDamage(new Coord(0, 2));
    testBoard.takeDamage(new Coord(1, 2));
    testBoard.takeDamage(new Coord(2, 2));
    testBoard.takeDamage(new Coord(3, 2));
    assertEquals(0, testBoard.shipsLeft());
  }
}