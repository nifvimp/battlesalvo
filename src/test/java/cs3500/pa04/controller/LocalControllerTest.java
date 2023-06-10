package cs3500.pa04.controller;


import static org.junit.jupiter.api.Assertions.assertEquals;

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
import cs3500.pa04.MockOutputStream;


// TODO: fix. Broken because of artificial player

/**
 * Tests the LocalController class.
 */
public class LocalControllerTest {
  private static final long SEED = 0;
  private MockOutputStream testOut;
  private Controller controller;

  /**
   * Sets up the input for a game.
   */
  @BeforeEach
  public void setup() {
    testOut = new MockOutputStream();
    GameView view = new TerminalView(testOut.toPrintStream(),
        new StringReader("""
        6 6
        1 1 1 3
        4 0
        5 0
        0 1
        1 1
        2 1
        4 1
        5 1
        0 2
        1 2
        2 2
        3 2
        4 2
        5 2
        1 3
        2 3
        3 3
        4 3
        5 3
        1 4
        2 4
        3 4
        4 4
        5 4
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
            Hello! Welcome to the OOD BattleSalvo Game!
            """ + System.lineSeparator() + """
            Boards can be between sizes 6 and 15, and dimensions do not need to match.
            Please enter a valid height and width below:
            ------------------------------------------------------""" + System.lineSeparator()
            + """
            Please enter your fleet in the order [CARRIER, BATTLESHIP, DESTROYER, SUBMARINE].
            Remember, your fleet may not exceed size 6.
            ------------------------------------------------------""" + System.lineSeparator()
            + """
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  ~  ~
                  1 ~  ~  ~  ~  ~  ~
                  2 ~  ~  ~  ~  ~  ~
                  3 ~  ~  ~  ~  ~  ~
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
            """ + System.lineSeparator() + """
            Your Board:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  C  B
                  1 U  U  U  ~  C  B
                  2 D  D  D  D  C  B
                  3 ~  U  U  U  C  B
                  4 ~  U  U  U  C  B
                  5 ~  ~  ~  ~  C  ~
            """ + System.lineSeparator() + "Please enter 6 shots:"
            + System.lineSeparator() + """
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  H  H
                  1 H  H  H  ~  H  ~
                  2 ~  ~  ~  ~  ~  ~
                  3 ~  ~  ~  ~  ~  ~
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
            """ + System.lineSeparator() + """
            Your Board:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  C  H
                  1 U  U  U  ~  C  B
                  2 D  D  H  D  C  B
                  3 ~  U  U  U  C  H
                  4 ~  U  U  U  C  H
                  5 ~  X  ~  X  C  ~
            """ + System.lineSeparator() + "Please enter 6 shots:"
            + System.lineSeparator() + """
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  H  H
                  1 H  H  H  ~  H  H
                  2 H  H  H  H  H  ~
                  3 ~  ~  ~  ~  ~  ~
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
            """ + System.lineSeparator() + """
            Your Board:
                        
                    0  1  2  3  4  5
                  0 X  ~  ~  ~  C  H
                  1 U  U  H  ~  C  H
                  2 D  D  H  D  C  B
                  3 ~  U  H  U  H  H
                  4 ~  U  U  U  C  H
                  5 ~  X  ~  X  C  ~
            """ + System.lineSeparator() + "Please enter 6 shots:"
            + System.lineSeparator() + """
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  H  H
                  1 H  H  H  ~  H  H
                  2 H  H  H  H  H  H
                  3 ~  H  H  H  H  H
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
            """ + System.lineSeparator() + """
            Your Board:
                        
                    0  1  2  3  4  5
                  0 X  ~  ~  ~  H  H
                  1 U  U  H  X  C  H
                  2 H  D  H  D  C  B
                  3 ~  U  H  U  H  H
                  4 ~  U  U  H  C  H
                  5 ~  X  ~  X  C  ~
            """ + System.lineSeparator() + "Please enter 6 shots:"
            + System.lineSeparator() + """
            Opponent Board Data:
                        
                    0  1  2  3  4  5
                  0 ~  ~  ~  ~  H  H
                  1 H  H  H  ~  H  H
                  2 H  H  H  H  H  H
                  3 ~  H  H  H  H  H
                  4 ~  H  H  H  H  H
                  5 ~  ~  ~  ~  H  ~
            """ + System.lineSeparator() + """
            Your Board:
                        
                    0  1  2  3  4  5
                  0 X  ~  ~  X  H  S
                  1 U  U  H  X  C  S
                  2 H  D  H  D  C  S
                  3 ~  U  H  U  H  S
                  4 ~  U  U  H  C  S
                  5 ~  X  ~  X  C  X
            """ + System.lineSeparator() + """
            You Won!
            All opponent ships sunk."""
            + System.lineSeparator(),
        testOut.toString()
    );
  }
}