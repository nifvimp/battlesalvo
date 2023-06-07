package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests the ArtificialPlayer class.
 */
class ArtificialPlayerTest extends LocalPlayerTest {
  @Override
  protected LocalPlayer makeTestPlayer(BoardObserver observer, Random random) {
    return new ArtificialPlayer(observer, random);
  }

  /**
   * Tests if name gets the right name.
   */
  @Test
  public void testName() {
    assertEquals("Artificial Player", testPlayer.name());
  }

//  /**
//   * Tests if takeShots is based on the Random object.
//   */
//  @Test
//  public void testTakeShots() {
//    testPlayer.takeShots();
//    assertEquals(List.of(
//        new Coord(4, 2), new Coord(0, 2), new Coord(5, 5),
//        new Coord(3, 5), new Coord(2, 5), new Coord(5, 3)
//    ), testPlayer.salvo);
//  }
}