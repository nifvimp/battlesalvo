package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import cs3500.pa04.MockOutputStream;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
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
                        
                    0  1  2  3  4  5
                  0 B  ~  ~  B  C  C
                  1 B  ~  D  B  C  C
                  2 B  U  D  B  C  C
                  3 B  U  D  B  C  C
                  4 B  U  D  B  C  C
                  5 ~  ~  ~  ~  C  C
                        
            Your Board:
                        
                    0  1  2  3  4  5
                  0 B  ~  ~  B  C  C
                  1 B  ~  D  B  C  C
                  2 B  U  D  B  C  C
                  3 B  U  D  B  C  C
                  4 B  U  D  B  C  C
                  5 ~  ~  ~  ~  C  C
                        
            """,
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
                        
                    0  1  2  3  4  5
                  0 B  ~  ~  B  C  C
                  1 B  ~  D  B  C  C
                  2 B  U  D  B  C  C
                  3 B  U  D  B  C  C
                  4 B  U  D  B  C  C
                  5 ~  ~  ~  ~  C  C
                        
            """,
        testOut.toString());
  }

  /**
   * tests if getter gets the right board.
   */
  @Test
  void getOpponentBoard() {
    view.displayOpponentBoard(model.getOpponentBoard());
    assertEquals("""
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  ~  ~
                  1 ~  ~  ~  ~  ~  ~
                  2 ~  ~  ~  ~  ~  ~
                  3 ~  ~  ~  ~  ~  ~
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
                        
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
        Please enter 6 shots:
        Your Board:
                
                0  1  2  3  4  5
              0 B  ~  ~  B  C  C
              1 B  ~  D  B  C  C
              2 H  U  D  B  H  C
              3 B  U  D  B  C  H
              4 B  U  D  B  C  C
              5 ~  ~  X  X  C  H
                
        Your Board:
                
                0  1  2  3  4  5
              0 H  X  X  H  H  H
              1 B  ~  D  B  C  C
              2 B  U  D  B  C  C
              3 B  U  D  B  C  C
              4 B  U  D  B  C  C
              5 ~  ~  ~  ~  C  C
                      
        """, testOut.toString());
  }

  /**
   * GameResult gets the correct game results.
   */
  @Test
  void getGameResult() {
    assertNull(model.getGameResult());
    Board opponentBoard = observer.getBoard(player2);
    Board playerBoard = observer.getBoard(player1);
    Set<Coord> allCoords = new HashSet<>(playerBoard.validShots());
    allCoords.forEach(opponentBoard::takeDamage);
    assertEquals(GameResult.WIN, model.getGameResult());
    allCoords = opponentBoard.validShots();
    allCoords.forEach(playerBoard::takeDamage);
    assertEquals(GameResult.DRAW, model.getGameResult());
    setup();
    playerBoard = observer.getBoard(player1);
    opponentBoard = observer.getBoard(player2);
    allCoords = opponentBoard.validShots();
    allCoords.forEach(playerBoard::takeDamage);
    assertEquals(GameResult.LOSE, model.getGameResult());
  }
}