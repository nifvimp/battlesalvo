//package pa04.model;
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//import cs3500.pa04.model.Board;
//import cs3500.pa04.model.Coord;
//import cs3500.pa04.view.GameView;
//import cs3500.pa04.view.TerminalView;
//import java.io.StringReader;
//import java.util.HashSet;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
///**
// * Tests the GuessBoard class.
// */
//class GuessBoardTest {
//
//  private Board testBoard;
//  private GameView testView;
//  private Appendable testOut;
//
//  /**
//   * Sets up the guess board and test output.
//   */
//  @BeforeEach
//  public void setup() {
//    testBoard = new GuessBoard(5, 5, 7);
//    testOut = new StringBuilder();
//    testView = new TerminalView(testOut, new StringReader(""));
//  }
//
//  /**
//   * Tests if the board representation gotten from the board is correct.
//   */
//  @Test
//  public void testGetBoardScene() {
//    for (int i = 0; i < 5; i++) {
//      for (int j = 0; j < 5; j++) {
//        if ((i + j) % 3 == 0) {
//          testBoard.hit(new Coord(j, i));
//        } else if ((i + j) % 4 == 0) {
//          testBoard.miss(new Coord(j, i));
//        }
//      }
//    }
//    String expected = """
//        Your Board:
//        \tH - - H M
//        \t- - H M -
//        \t- H M - H
//        \tH M - H -
//        \tM - H - M
//        """;
//    testView.displayPlayerBoard(testBoard.getBoardScene());
//    assertEquals(expected, testOut.toString());
//  }
//
//  /**
//   * Tests if hit registers on the board correctly.
//   */
//  @Test
//  void hit() {
//    Coord coord1 = new Coord(4, 2);
//    Coord coord2 = new Coord(0, 0);
//    assertFalse(testBoard.hits.contains(coord1));
//    assertFalse(testBoard.hits.contains(coord2));
//    testBoard.hit(coord1);
//    testBoard.hit(coord2);
//    assertTrue(testBoard.hits.contains(coord1));
//    assertTrue(testBoard.hits.contains(coord2));
//  }
//
//  /**
//   * Tests if miss registers on the board correctly.
//   */
//  @Test
//  void miss() {
//    Coord coord1 = new Coord(4, 2);
//    Coord coord2 = new Coord(0, 0);
//    assertFalse(testBoard.misses.contains(coord1));
//    assertFalse(testBoard.misses.contains(coord2));
//    testBoard.miss(coord1);
//    testBoard.miss(coord2);
//    assertTrue(testBoard.misses.contains(coord1));
//    assertTrue(testBoard.misses.contains(coord2));
//  }
//
//  /**
//   * Tests if validShots() returns the correct coords left to available shoot.
//   */
//  @Test
//  public void validShots() {
//    for (int i = 0; i < 5; i++) {
//      for (int j = 1; j < 5; j++) {
//        if ((i + j) % 3 == 0) {
//          testBoard.hit(new Coord(j, i));
//        } else {
//          testBoard.miss(new Coord(j, i));
//        }
//      }
//    }
//    Set<Coord> expectedValid = new HashSet<>();
//    for (int i = 0; i < 5; i++) {
//      expectedValid.add(new Coord(0, i));
//    }
//    assertEquals(expectedValid, testBoard.validShots());
//  }
//}