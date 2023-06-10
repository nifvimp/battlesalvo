package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cs3500.pa04.MockInputStream;
import cs3500.pa04.MockOutputStream;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the GameModelImpl class.
 */
public class GameModelTest {
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
  private MockInputStream testIn1;
  private MockInputStream testIn2;
  private GameModel model;

  /**
   * Sets up model and inputs.
   */
  @BeforeEach
  public void setup() {
    testOut = new MockOutputStream();
    testIn1 = new MockInputStream();
    testIn2 = new MockInputStream();
    view = new TerminalView(testOut.toPrintStream(), testIn1.toReadable());
    observer = new BoardObserver();
    player1 = new ManualPlayer(observer, new UserCommunicator(view), new Random(SEED));
    player2 = new ManualPlayer(observer, new UserCommunicator(
        new TerminalView(new PrintStream(new ByteArrayOutputStream()),
            testIn2.toReadable())), new Random(SEED));
    model = new GameModel(observer, player1, player2);
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
    assertEquals(("""
            Your Board:
                        
                    0  1  2  3  4  5
                  0 B  ~  ~  B  C  C
                  1 B  ~  D  B  C  C
                  2 B  U  D  B  C  C
                  3 B  U  D  B  C  C
                  4 B  U  D  B  C  C
                  5 ~  ~  ~  ~  C  C
            """ + System.lineSeparator())
            .repeat(2),
        testOut.toString());
  }

  /**
   * tests if getter gets the right board.
   */
  @Test
  public void testGetPlayerBoard() {
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
            """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * tests if getter gets the right board.
   */
  @Test
  public void testGetOpponentBoard() {
    view.displayPlayerBoard(model.getOpponentBoard());
    assertEquals("""
            Your Board:
                        
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
   * Tests if volley() starts a volley interaction between players.
   */
  @Test
  public void testVolley() {
    testIn1.input("""
        0 0
        1 0
        2 0
        3 0
        4 0
        5 0
        """);
    testIn2.input("""
        0 1
        1 1
        2 1
        3 1
        4 1
        5 1
        """);
    model.volley();
    view.displayPlayerBoard(observer.getBoard(player1).getPlayerBoard());
    view.displayPlayerBoard(observer.getBoard(player2).getPlayerBoard());
    assertEquals("Please enter 6 shots:" + System.lineSeparator()
            + """
            Your Board:
                    
                    0  1  2  3  4  5
                  0 B  ~  ~  B  C  C
                  1 H  X  H  H  H  H
                  2 B  U  D  B  C  C
                  3 B  U  D  B  C  C
                  4 B  U  D  B  C  C
                  5 ~  ~  ~  ~  C  C
            """ + System.lineSeparator()
            + """
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

  /**
   * GameResult gets the correct game results.
   */
  @Test
  public void testGetGameResultWin() {
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    Board opponentBoard = observer.getBoard(player2);
    Board playerBoard = observer.getBoard(player1);
    playerBoard.takeDamage(new Coord(0, 0));
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    obliterate(opponentBoard);
    assertEquals(GameResult.WIN, model.getGameResult());
  }

  /**
   * GameResult gets the correct game results.
   */
  @Test
  public void testGetGameResultLoss() {
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    Board opponentBoard = observer.getBoard(player2);
    Board playerBoard = observer.getBoard(player1);
    playerBoard.takeDamage(new Coord(0, 0));
    opponentBoard.takeDamage(new Coord(5, 5));
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    obliterate(playerBoard);
    assertEquals(GameResult.LOSE, model.getGameResult());
  }

  /**
   * GameResult gets the correct game results.
   */
  @Test
  public void testGetGameResultDraw() {
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    Board opponentBoard = observer.getBoard(player2);
    Board playerBoard = observer.getBoard(player1);
    playerBoard.takeDamage(new Coord(0, 0));
    assertThrows(IllegalStateException.class, () -> model.getGameResult());
    obliterate(opponentBoard);
    obliterate(playerBoard);
    assertEquals(GameResult.DRAW, model.getGameResult());
  }

  @Test
  public void testEndGame() {
    model.endGame(GameResult.WIN, "You Won!");
    model.endGame(GameResult.LOSE, "You Lost.");
    model.endGame(GameResult.DRAW, "You Tied.");
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