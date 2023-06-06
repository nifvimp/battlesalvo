package pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;


import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.LocalPlayer;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pa04.TestOutputStream;

/**
 * Abstract test class for Local Players.
 */
public abstract class LocalPlayerTest {
  private static final long SEED = 0;
  protected LocalPlayer testPlayer;
  protected BoardObserver observer;
  protected GameView view;
  protected TestOutputStream testOut;

  /**
   * Sets up a test output and player for tests.
   */
  @BeforeEach
  public void setup() {
    testOut = new TestOutputStream();
    view = new TerminalView(testOut.toPrintStream(), new StringReader(""));
    observer = new BoardObserver();
    testPlayer = makeTestPlayer(observer, new Random(SEED));
    testPlayer.setup(6, 6,
        Map.of(ShipType.CARRIER, 2, ShipType.BATTLESHIP, 2, ShipType.DESTROYER, 1,
            ShipType.SUBMARINE, 1));
  }

  /**
   * Makes a player to run tests on.
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
    view.displayPlayerBoard(observer.getBoard(testPlayer.name()).getPlayerBoard());
    view.displayOpponentBoard(observer.getBoard(testPlayer.name()).getOpponentKnowledge());
    assertEquals("""
        Your Board:
        \tB - - B C C
        \tB - D B C C
        \tB S D B C C
        \tB S D B C C
        \tB S D B C C
        \t- - - - C C
        Opponent Board Data:
        \t- - - - - -
        \t- - - - - -
        \t- - - - - -
        \t- - - - - -
        \t- - - - - -
        \t- - - - - -
        """, testOut.toString());
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
    view.displayPlayerBoard(observer.getBoard(testPlayer.name()).getPlayerBoard());
    view.displayOpponentBoard(observer.getBoard(testPlayer.name()).getOpponentKnowledge());
    assertEquals("""
        Your Board:
        \tC C C C C C - -
        \tC C C C C C - -
        \tC C C C C C - S
        \tB B B B B - C S
        \tC C C C C C C S
        \tC C C C C C C C
        \tC C C C C C C C
        \tC C C C C C C C
        \tC C C C C C C C
        \tC C C C C C - C
        \t- C C C C C C C
        \t- D D D D - - -
        Opponent Board Data:
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        \t- - - - - - - -
        """, testOut.toString());
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
    view.displayPlayerBoard(observer.getBoard(testPlayer.name()).getPlayerBoard());
    assertEquals("""
        Your Board:
        \tH M M H H H
        \tB - D B C C
        \tB S D B C C
        \tB S D B C C
        \tB S D B C C
        \t- - - - C C
        """, testOut.toString());
  }

//  /**
//   * Tests if the player marks their board correctly based on the successful hits
//   * reported.
//   */
//  @Test
//  public void testSuccessfulHits() {
//    testPlayer.salvo.addAll(
//        List.of(
//            new Coord(0, 0), new Coord(1, 0), new Coord(2, 0),
//            new Coord(3, 0), new Coord(4, 0), new Coord(5, 0)));
//    testPlayer.successfulHits(
//        List.of(new Coord(0, 0), new Coord(3, 0), new Coord(4, 0)));
//    view.displayPlayerBoard(observer.getBoard(testPlayer.name()).getOpponentKnowledge());
//    assertEquals("""
//        Your Board:
//        \tH M M H H M
//        \t- - - - - -
//        \t- - - - - -
//        \t- - - - - -
//        \t- - - - - -
//        \t- - - - - -
//        """, testOut.toString());
//  }
}