package pa04.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.model.ArtificialPlayer;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.ManualPlayer;
import cs3500.pa04.model.Player;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.StringReader;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pa04.TestOutputStream;

/**
 * Tests the LocalController class.
 */
public class LocalControllerTest {
  private static final long SEED = 0;
  private TestOutputStream testOut;
  private Controller controller;

  /**
   * Sets up the input for a game.
   */
  @BeforeEach
  public void setup() {
    testOut = new TestOutputStream();
    GameView view = new TerminalView(testOut.toPrintStream(),
        new StringReader("""
        6 6
        1 1 1 3
        0 0
        1 0
        2 0
        1 3
        2 3
        3 3
        1 4
        2 4
        3 4
        0 2
        1 2
        2 2
        3 2
        5 0
        5 1
        5 2
        5 3
        5 4
        4 0
        4 1
        4 2
        4 3
        4 4
        4 5
        """));
    BoardObserver observer = new BoardObserver();
    Player player1 = new ManualPlayer(observer, new UserCommunicator(view), new Random(SEED));
    Player player2 = new ArtificialPlayer(observer, new Random(SEED));
    controller = new LocalController(player1, player2, view, new UserCommunicator(view), observer);
  }

  /**
   * Tests if a single game run.
   */
  @Test
  public void testRun() {
    controller.run();
    assertEquals("""
            Please enter a valid height and width below:
            Please enter your fleet in the order [CARRIER, BATTLESHIP, DESTROYER, SUBMARINE].
            Opponent Board Data:
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            \t- - - - - -
            Your Board:
            \tS S S - C B
            \t- - - - C B
            \tD D D D C B
            \t- S S S C B
            \t- S S S C B
            \t- - - - C -
            Please Enter 6 Shots:
            Opponent Board Data:
            \tH H H - - -
            \t- - - - - -
            \t- - - - - -
            \t- H H H - -
            \t- - - - - -
            \t- - - - - -
            Your Board:
            \tH H S M C B
            \t- - - - C B
            \tD D D D C B
            \t- S S H C B
            \t- S S S H B
            \t- - - M C -
            Please Enter 6 Shots:
            Opponent Board Data:
            \tH H H - - -
            \t- - - - - -
            \tH H H - - -
            \t- H H H - -
            \t- H H H - -
            \t- - - - - -
            Your Board:
            \tH H S M C B
            \t- M - - C B
            \tD D D D C B
            \t- S S H C B
            \t- S H S H B
            \tM - M M C -
            Please Enter 6 Shots:
            Opponent Board Data:
            \tH H H - - H
            \t- - - - - H
            \tH H H H - H
            \t- H H H - H
            \t- H H H - H
            \t- - - - - -
            Your Board:
            \tH H S M C B
            \t- M - M C B
            \tD H D D C B
            \t- S S H C H
            \t- S H S H B
            \tM - M M C -
            Please Enter 6 Shots:
            Opponent Board Data:
            \tH H H - H H
            \t- - - - H H
            \tH H H H H H
            \t- H H H H H
            \t- H H H H H
            \t- - - - H -
            Your Board:
            \tH H S M C B
            \t- M - M C B
            \tD H D D C B
            \t- S S H C H
            \t- S H S H B
            \tM M M M C -
            You Won!
            All opponent ships sunk.
            """,
        testOut.toString()
    );
  }
}