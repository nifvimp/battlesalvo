package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import cs3500.pa04.controller.UserCommunicator;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * tests the ManualPlayer class.
 */
class ManualPlayerTest extends LocalPlayerTest {
  @Override
  protected LocalPlayer makeTestPlayer(BoardObserver observer, Random random) {
    return new ManualPlayer(observer, new UserCommunicator(view), random);
  }

  /**
   * Tests if name gets the right name.
   */
  @Test
  public void testName() {
    assertEquals("Manual Player", testPlayer.name());
  }

  /**
   * Tests if takeShots is input.
   */
  @Test
  public void testTakeShots() {
    testIn.input("""
        0 0
        1 0
        2 0
        3 0
        10 0
        5 0
        0 0
        1 0
        2 0
        3 0
        4 0
        5 0
        """);
    assertEquals(
        Set.of(
            new Coord(0, 0), new Coord(1, 0), new Coord(2, 0),
            new Coord(3, 0), new Coord(4, 0), new Coord(5, 0)),
        new HashSet<>(testPlayer.takeShots()));
  }

  /**
   * Tests if the player marks their board correctly based on the successful hits
   * reported.
   */
  @Test
  public void testSuccessfulHits() {
    testIn.input("""
        0 0
        1 0
        2 0
        3 0
        4 0
        5 0
        """);
    testPlayer.takeShots();
    testPlayer.successfulHits(
        List.of(new Coord(0, 0), new Coord(3, 0), new Coord(4, 0))
    );
    view.displayPlayerBoard(observer.getBoard(testPlayer).getOpponentKnowledge());
    assertEquals("Please enter 6 shots:" + System.lineSeparator() + """
            Your Board:
                    
                    0  1  2  3  4  5
                  0 H  X  X  H  H  X
                  1 ~  ~  ~  ~  ~  ~
                  2 ~  ~  ~  ~  ~  ~
                  3 ~  ~  ~  ~  ~  ~
                  4 ~  ~  ~  ~  ~  ~
                  5 ~  ~  ~  ~  ~  ~
            """ + System.lineSeparator(),
        testOut.toString());
  }
}