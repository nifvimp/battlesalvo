package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import cs3500.pa04.TestOutputStream;
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
  private TestOutputStream testOut;
  private Player p1;
  private Player p2;

  /**
   * Sets up test output and players to test the observer.
   */
  @BeforeEach
  public void setup() {
    observer = new BoardObserver();
    p1Board = new Board(5, 5, List.of(
        new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL))
    );
    p2Board = new Board(5, 5, List.of(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.VERTICAL))
    );
    p1 = new ArtificialPlayer(observer);
    p2 = new ArtificialPlayer(observer);
    observer.registerBoard(p1, p1Board);
    testOut = new TestOutputStream();
    testView = new TerminalView(testOut.toPrintStream(), new StringReader(""));
  }

  /**
   * Tests if board registration registers correctly.
   */
  @Test
  public void registerBoard() {
    assertNull(observer.getBoard(p2));
    observer.registerBoard(p2, p2Board);
    assertEquals(p1Board, observer.getBoard(p2));
  }


  /**
   * Tests if getPlayerBoard() gets the right board.
   */
  @Test
  public void getBoard() {
    assertEquals(p1Board, observer.getBoard(p1));
    p1Board.takeDamage(new Coord(0, 0));
    p1Board.takeDamage(new Coord(1, 1));
    p1Board.takeDamage(new Coord(2, 2));
    assertEquals(p1Board, observer.getBoard(p1));
    String expected = """
        Your Board:
        \tX - - - -
        \t- X - - -
        \tD D H D -
        \t- - - - -
        \t- - - - -
        """;
    testView.displayPlayerBoard(observer.getBoard(p1).getPlayerBoard());
    assertEquals(expected, testOut.toString());
  }
}