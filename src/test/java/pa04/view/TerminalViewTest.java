package pa04.view;


import static org.junit.jupiter.api.Assertions.assertEquals;


import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pa04.TestInputStream;
import pa04.TestOutputStream;

/**
 * Tests the TerminalView class.
 */
public class TerminalViewTest {
  private TestOutputStream testOut;
  private TestInputStream testIn;
  private GameView testView;

  /**
   * Sets up test output.
   */
  @BeforeEach
  public void setup() {
    testOut = new TestOutputStream();
    testIn = new TestInputStream();
    testView = new TerminalView(testOut.toPrintStream(), testIn.toReadable());
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
        \tB B B B B - - -
        \t- M C - - D - -
        \t- - H - - D M -
        \t- - C - - D - -
        \t- - C - - D - -
        \t- - C - - - M -
        \t- - H - - - - -
        \t- S S H M M - -
        """, testOut.toString());
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
        \tM M H H H M M M
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
   * Tests if message is displayed properly.
   */
  @Test
  public void testShowMessage() {
    testView.showMessage("Hello World!");
    assertEquals("Hello World!\n", testOut.toString());
  }

  /**
   * Tests if the view prompts and gets the user input correctly.
   */
  @Test
  public void testGetBoardSize() {
    testIn.input("10 10");
    String input = testView.getBoardDimensions(new Coord(6, 15));
    assertEquals(input, "10 10");
    assertEquals("Please enter a valid height and width below:\n", testOut.toString());
  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidBoardSize() {
    testView.invalidBoardDimensions(new Coord(6, 15));
    assertEquals("The dimensions you entered were invalid dimensions. Please remember that "
            + "the height and width of the game must be in the range (6, 15), inclusive.\n",
        testOut.toString());
  }

//  /**
//   * Tests if the view prompts and gets the user input correctly.
//   */
//  @Test
//  public void testGetShots() {
//    testView = new TerminalView(testOut, new StringReader("""
//        0 0
//        1 3
//        3 2
//        5 4
//        5 5
//        never reach
//        """));
//    List<String> input = testView.getShots(5);
//    assertEquals(List.of("0 0", "1 3", "3 2", "5 4", "5 5"), input);
//    assertEquals("Please Enter 5 Shots:\n", testOut.toString());
//  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidShots() {
    testView.invalidShots();
    assertEquals("Some of the shots you entered were either invalid, out of bounds, "
        + "or has already been shot.\n", testOut.toString());
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
    assertEquals(String.format("Please enter your fleet in the order %s.\n", shipOrder),
        testOut.toString());
  }

  /**
   * Tests if the view notifies the user of an invalid input correctly.
   */
  @Test
  public void testInvalidFleet() {
    testView.invalidFleet();
    assertEquals("The fleet you entered is invalid. Remember, your fleet must "
        + "of size 6 and have at least one of each ship type.\n", testOut.toString());
  }

  /**
   * Tests if the view shows the game result correctly.
   */
  @Test
  public void testShowResults() {
    testView.showResults(GameResult.WIN, "All opponent ships sunk.");
    assertEquals("""
        You Won!
        All opponent ships sunk.
        """, testOut.toString());
  }
}