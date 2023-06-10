package cs3500.pa04.view;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.MockInputStream;
import cs3500.pa04.MockOutputStream;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.ShipType;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the TerminalView class.
 */
public class TerminalViewTest {
  private MockOutputStream testOut;
  private MockInputStream testIn;
  private GameView testView;

  /**
   * Sets up test output.
   */
  @BeforeEach
  public void setup() {
    testOut = new MockOutputStream();
    testIn = new MockInputStream();
    testView = new TerminalView(testOut.toPrintStream(), testIn.toReadable());
  }

  /**
   * Tests if message is displayed properly.
   */
  @Test
  public void testShowMessage() {
    testView.showMessage("Hello World!");
    assertEquals("Hello World!" + System.lineSeparator(), testOut.toString());
  }

  @Test
  public void testGreet() {
    testView.greet();
    assertEquals("""
            Hello! Welcome to the OOD BattleSalvo Game!
            """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if a board is displayed properly.
   */
  @Test
  public void testDisplayPlayerBoard() {
    char[][] fakeBoardRepresentation = new char[][] {
        {'B', 'B', 'B', 'B', 'B', '-', '-', '-'},
        {'-', 'M', 'C', '-', '-', 'D', '-', '-'},
        {'-', '-', 'H', '-', '-', 'D', 'M', '-'},
        {'-', '-', 'C', '-', '-', 'D', '-', '-'},
        {'-', '-', 'C', '-', '-', 'D', '-', '-'},
        {'-', '-', 'C', '-', '-', '-', 'M', '-'},
        {'-', '-', 'H', '-', '-', '-', '-', '-'},
        {'-', 'S', 'S', 'H', 'M', 'M', '-', '-'},
    };
    testView.displayPlayerBoard(fakeBoardRepresentation);
    assertEquals("""
            Your Board:
                    
                    0  1  2  3  4  5  6  7
                  0 B  B  B  B  B  -  -  -
                  1 -  M  C  -  -  D  -  -
                  2 -  -  H  -  -  D  M  -
                  3 -  -  C  -  -  D  -  -
                  4 -  -  C  -  -  D  -  -
                  5 -  -  C  -  -  -  M  -
                  6 -  -  H  -  -  -  -  -
                  7 -  S  S  H  M  M  -  -
            """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if a board is displayed properly.
   */
  @Test
  public void testDisplayOpponentBoard() {
    char[][] fakeBoardRepresentation = new char[][] {
        {'M', 'M', 'H', 'H', 'H', 'M', 'M', 'M'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'},
        {'-', '-', '-', '-', '-', '-', '-', '-'}
    };
    testView.displayOpponentBoard(fakeBoardRepresentation);
    assertEquals("""
            Opponent Board Data:
                    
                    0  1  2  3  4  5  6  7
                  0 M  M  H  H  H  M  M  M
                  1 -  -  -  -  -  -  -  -
                  2 -  -  -  -  -  -  -  -
                  3 -  -  -  -  -  -  -  -
                  4 -  -  -  -  -  -  -  -
                  5 -  -  -  -  -  -  -  -
                  6 -  -  -  -  -  -  -  -
                  7 -  -  -  -  -  -  -  -
            """ + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if the view prompts and gets the user input correctly.
   */
  @Test
  public void testGetBoardDimensions() {
    testIn.input("10 10");
    String input = testView.getBoardDimensions(new Coord(6, 15));
    assertEquals(input, "10 10");
    assertEquals("""
        Boards can be between sizes 6 and 15, and dimensions do not need to match.
        Please enter a valid height and width below:
        ------------------------------------------------------"""
        + System.lineSeparator(), testOut.toString());
  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidBoardDimensions() {
    testView.invalidBoardDimensions(new Coord(6, 15));
    assertEquals("Uh Oh! You've entered invalid dimensions. Please remember that "
            + "the height and width of the game must be in the range (6, 15), inclusive."
            + "\n------------------------------------------------------"
            + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if the view prompts and gets the user input correctly.
   */
  @Test
  public void testGetFleet() {
    testIn.input("1 2 2 1");
    String input = testView.getFleet(6);
    String shipOrder = Arrays.toString(ShipType.values());
    assertEquals("1 2 2 1", input);
    assertEquals(String.format("""
            Please enter your fleet in the order %s.
            Remember, your fleet may not exceed size 6.
            ------------------------------------------------------"""
            + System.lineSeparator(), shipOrder),
        testOut.toString());
  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidFleet() {
    testView.invalidFleet();
    assertEquals("Uh Oh! You've entered invalid fleet sizes."
        + System.lineSeparator(), testOut.toString());
  }

  /**
   * Tests if prompt shots prompts correctly.
   */
  @Test
  public void testPromptShots() {
    testView.promptShots(4);
    testView.promptShots(1);
    assertEquals("Please enter 4 shots:" + System.lineSeparator()
            + "Please enter 1 shot:" + System.lineSeparator(),
        testOut.toString());
  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidShots() {
    testView.invalidShots();
    assertEquals("""
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board."""
        + System.lineSeparator(), testOut.toString());
  }

  /**
   * Tests getting a shot from the user.
   */
  @Test
  public void testGetShot() {
    testIn.input("""
        0 0
        0 1
        """);
    assertEquals("0 0", testView.getShot());
    assertEquals("0 1", testView.getShot());
  }

  /**
   * Tests if the view shows the game result correctly.
   */
  @Test
  public void testShowResults() {
    testView.showResults(GameResult.WIN, "All opponent ships sunk.");
    assertEquals("""
            You Won!
            All opponent ships sunk."""
            + System.lineSeparator(),
        testOut.toString());
  }
}