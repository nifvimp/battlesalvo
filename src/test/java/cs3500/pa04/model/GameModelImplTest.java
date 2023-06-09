package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.MockOutputStream;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the GameModelImpl class.
 */
class GameModelImplTest {
  private static final long SEED = 0;
  private final Map<ShipType, Integer> specifications = Map.of(
      ShipType.CARRIER, 2, ShipType.BATTLESHIP,
      2, ShipType.DESTROYER, 1, ShipType.SUBMARINE, 1
  );
  private BoardObserver observer;
  private Player player1;
  private Player player2;
  private GameView view;
  private MockOutputStream testOut;
  private GameModel model;

  /**
   * Sets up model and inputs.
   */
  @BeforeEach
  public void setup() {
    testOut = new MockOutputStream();
    view = new TerminalView(testOut.toPrintStream(), new StringReader("""
        0 0
        1 0
        2 0
        3 0
        4 0
        5 0
        """));
    observer = new BoardObserver();
    player1 = new ManualPlayer(observer, new UserCommunicator(view), new Random(SEED));
    player2 = new RandomPlayer(observer, new Random(SEED));
    model = new GameModelImpl(observer, player1, player2);
    model.setup(6, 6, specifications);
  }

  /**
   * Tests if setup sets up the players correctly.
   */
  @Test
  public void testSetup() {
    view.displayPlayerBoard(observer.getBoard(player1).getPlayerBoard());
    view.displayPlayerBoard(observer.getBoard(player2).getPlayerBoard());
    // state of both player boards are the same because they use the same seed for their random
    assertEquals("""
            Your Board:
            \tB - - B C C
            \tB - D B C C
            \tB U D B C C
            \tB U D B C C
            \tB U D B C C
            \t- - - - C C
            """
            .repeat(2),
        testOut.toString());
  }

  /**
   * tests if getter gets the right board.
   */
  @Test
  void getPlayerBoard() {
    view.displayPlayerBoard(model.getPlayerBoard());
    assertEquals("""
            Your Board:
            \tB - - B C C
            \tB - D B C C
            \tB U D B C C
            \tB U D B C C
            \tB U D B C C
            \t- - - - C C
            """,
        testOut.toString());
  }

  /**
   * tests if getter gets the right board.
   */
  @Test
  void getOpponentBoard() {
    view.displayPlayerBoard(model.getOpponentBoard());
    assertEquals("""
            Your Board:
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            """,
        testOut.toString());
  }

  /**
   * Tests if volley() starts a volley interaction between players.
   */
  @Test
  void volley() {
    model.volley();
    view.displayPlayerBoard(observer.getBoard(player1).getPlayerBoard());
    view.displayPlayerBoard(observer.getBoard(player2).getPlayerBoard());
    assertEquals("""
        Please Enter 6 Shots:
        Your Board:
        \tB - - B C C
        \tB - D B C C
        \tH S D B H C
        \tB S D B C H
        \tB S D B C C
        \t- - M M C H
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
//   * GameResult gets the correct game results.
//   */
//  @Test
//  void getGameResult() {
//    assertNull(model.getGameResult());
//    Board opponentBoard = observer.getBoard(player2.name());
//    Board playerBoard = observer.getBoard(player1.name());
//    final Set<Coord> allCoords = new HashSet<>(playerBoard.validShots());
//    opponentBoard.hits.addAll(opponentBoard.shotsLeft);
//    opponentBoard.shotsLeft.clear();
//    assertEquals(GameResult.WIN, model.getGameResult());
//    playerBoard.hits.addAll(playerBoard.shotsLeft);
//    playerBoard.shotsLeft.clear();
//    assertEquals(GameResult.TIE, model.getGameResult());
//    opponentBoard.hits.clear();
//    opponentBoard.shotsLeft.addAll(allCoords);
//    assertEquals(GameResult.LOSE, model.getGameResult());
//  }
}