package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cs3500.pa04.MockOutputStream;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the BoardObserver class.
 */
public class BoardObserverTest {
  private BoardObserver observer;
  private Board p1Board;
  private Board p2Board;
  private GameView testView;
  private MockOutputStream testOut;
  private Player p1;
  private Player p2;

  /**
   * Sets up test output and players to test the observer.
   */
  @BeforeEach
  public void testSetup() {
    observer = new BoardObserver();
    p1Board = new Board(6, 6, List.of(
        new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL))
    );
    p2Board = new Board(6, 6, List.of(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.VERTICAL))
    );
    p1 = new RandomPlayer(observer);
    p2 = new RandomPlayer(observer);
    observer.registerBoard(p1, p1Board);
    testOut = new MockOutputStream();
    testView = new TerminalView(testOut.toPrintStream(), new StringReader(""));
  }

  /**
   * Tests if board registration registers correctly.
   */
  @Test
  public void testRegisterBoard() {
    assertNull(observer.getBoard(p2));
    observer.registerBoard(p2, p2Board);
    assertEquals(p2Board, observer.getBoard(p2));
  }


  /**
   * Tests if getPlayerBoard() gets the right board.
   */
  @Test
  public void testGetBoard() {
    assertEquals(p1Board, observer.getBoard(p1));
    p1Board.takeDamage(new Coord(0, 0));
    p1Board.takeDamage(new Coord(1, 1));
    p1Board.takeDamage(new Coord(2, 2));
    assertEquals(p1Board, observer.getBoard(p1));
    String expected = """
        Your Board:
                
                0  1  2  3  4  5
              0 X  ~  ~  ~  ~  ~
              1 ~  X  ~  ~  U  ~
              2 ~  ~  X  ~  U  ~
              3 ~  ~  ~  ~  U  ~
              4 ~  ~  ~  ~  ~  ~
              5 ~  ~  ~  ~  ~  ~
        """ + System.lineSeparator();
    testView.displayPlayerBoard(observer.getBoard(p1).getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }

  /**
   * Test if game over works correctly
   */
  @Test
  public void testIsGameOver() {
    observer.registerBoard(p2, p2Board);
    assertFalse(observer.isGameOver());
    obliterate(p2Board);
    assertTrue(observer.isGameOver());
    obliterate(p1Board);
    assertTrue(observer.isGameOver());
  }

  /**
   * Shoots every spot on the board.
   */
  private void obliterate(Board board) {
    char[][] player = board.getPlayerBoard();
    for (int i = 0; i < player.length; i++) {
      for (int j = 0; j < player[i].length; j++) {
        board.takeDamage(new Coord(j, i));
      }
    }
  }
}