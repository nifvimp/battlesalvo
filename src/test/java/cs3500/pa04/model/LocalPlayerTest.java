package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import cs3500.pa04.MockOutputStream;

/**
 * Abstract test class for Local Players.
 */
public abstract class LocalPlayerTest {
  private static final long SEED = 0;
  protected LocalPlayer testPlayer;
  protected BoardObserver observer;
  protected GameView view;
  protected MockOutputStream testOut;

  /**
   * Sets up a test output and player for tests.
   */
  @BeforeEach
  public void setup() {
    testOut = new MockOutputStream();
    view = new TerminalView(testOut.toPrintStream(), new StringReader(""));
    observer = new BoardObserver();
    testPlayer = makeTestPlayer(observer, new Random(SEED));
    testPlayer.setup(6, 6,
        Map.of(ShipType.CARRIER, 2, ShipType.BATTLESHIP, 2, ShipType.DESTROYER, 1,
            ShipType.SUBMARINE, 1));
  }

  /**
   * Makes a player to run the tests inside this abstract test class on.
   *
   * @param observer test observer
   * @param random   random to randomize by
   * @return player to test
   */
  protected abstract LocalPlayer makeTestPlayer(BoardObserver observer, Random random);

  /**
   * Tests if setup of player sets up correctly.
   */
  @Test
  public void testSetup() {
    view.displayPlayerBoard(observer.getBoard(testPlayer).getPlayerBoard());
    view.displayOpponentBoard(observer.getBoard(testPlayer).getOpponentKnowledge());
    assertEquals("""
        Your Board:
                
                0  1  2  3  4  5
              0 B  ~  ~  B  C  C
              1 B  ~  D  B  C  C
              2 B  U  D  B  C  C
              3 B  U  D  B  C  C
              4 B  U  D  B  C  C
              5 ~  ~  ~  ~  C  C
        """ + System.lineSeparator() + """
        Opponent Board Data:
                
                0  1  2  3  4  5
              0 ~  ~  ~  ~  ~  ~
              1 ~  ~  ~  ~  ~  ~
              2 ~  ~  ~  ~  ~  ~
              3 ~  ~  ~  ~  ~  ~
              4 ~  ~  ~  ~  ~  ~
              5 ~  ~  ~  ~  ~  ~
        """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if setup is able to handle setting up a board where there is not much room
   * to work with.
   */
  @Test
  public void testSetupTight() {
    observer = new BoardObserver();
    testPlayer = makeTestPlayer(observer, new Random(SEED));
    testPlayer.setup(12, 8,
        Map.of(ShipType.CARRIER, 12, ShipType.BATTLESHIP, 1, ShipType.DESTROYER, 1,
            ShipType.SUBMARINE, 1));
    view.displayPlayerBoard(observer.getBoard(testPlayer).getPlayerBoard());
    view.displayOpponentBoard(observer.getBoard(testPlayer).getOpponentKnowledge());
    assertEquals("""
        Your Board:
                
                0  1  2  3  4  5  6  7
              0 C  C  C  C  C  C  ~  D
              1 C  C  C  C  C  C  ~  D
              2 C  C  C  C  C  C  ~  D
              3 ~  U  U  U  ~  ~  C  D
              4 C  C  C  C  C  C  C  ~
              5 C  C  C  C  C  C  C  C
              6 C  C  C  C  C  C  C  C
              7 C  C  C  C  C  C  C  C
              8 C  C  C  C  C  C  C  C
              9 C  C  C  C  C  C  ~  C
             10 ~  C  C  C  C  C  C  C
             11 B  B  B  B  B  ~  ~  ~
        """ + System.lineSeparator() + """
        Opponent Board Data:
                
                0  1  2  3  4  5  6  7
              0 ~  ~  ~  ~  ~  ~  ~  ~
              1 ~  ~  ~  ~  ~  ~  ~  ~
              2 ~  ~  ~  ~  ~  ~  ~  ~
              3 ~  ~  ~  ~  ~  ~  ~  ~
              4 ~  ~  ~  ~  ~  ~  ~  ~
              5 ~  ~  ~  ~  ~  ~  ~  ~
              6 ~  ~  ~  ~  ~  ~  ~  ~
              7 ~  ~  ~  ~  ~  ~  ~  ~
              8 ~  ~  ~  ~  ~  ~  ~  ~
              9 ~  ~  ~  ~  ~  ~  ~  ~
             10 ~  ~  ~  ~  ~  ~  ~  ~
             11 ~  ~  ~  ~  ~  ~  ~  ~
        """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if the player reports their damage correctly.
   */
  @Test
  public void testReportDamage() {
    assertEquals(
        List.of(
            new Coord(0, 0), new Coord(3, 0), new Coord(4, 0),
            new Coord(5, 0)),
        testPlayer.reportDamage(
            List.of(
                new Coord(0, 0), new Coord(1, 0), new Coord(2, 0),
                new Coord(3, 0), new Coord(4, 0), new Coord(5, 0)))
    );
    view.displayPlayerBoard(observer.getBoard(testPlayer).getPlayerBoard());
    assertEquals("""
        Your Board:
                
                0  1  2  3  4  5
              0 H  X  X  H  H  H
              1 B  ~  D  B  C  C
              2 B  U  D  B  C  C
              3 B  U  D  B  C  C
              4 B  U  D  B  C  C
              5 ~  ~  ~  ~  C  C
        """ + System.lineSeparator(),
        testOut.toString());
  }
}