package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Tests the ArtificialPlayer class.
 */
class RandomPlayerTest extends LocalPlayerTest {
  @Override
  protected LocalPlayer makeTestPlayer(BoardObserver observer, Random random) {
    return new RandomPlayer(observer, random);
  }

  /**
   * Tests if name gets the right name.
   */
  @Test
  public void testName() {
    assertEquals("Random Player", testPlayer.name());
  }

  /**
   * Tests if takeShots is based on the Random object.
   */
  @Test
  public void testTakeShots() {
    random.useInjected();
    // tells the random player to shoot the first row
    random.inject(0, 0, 0, 0, 0, 0, 0);
    assertEquals(List.of(
        new Coord(0, 0), new Coord(1, 0), new Coord(2, 0),
        new Coord(3, 0), new Coord(4, 0), new Coord(5, 0)
    ), testPlayer.takeShots());
  }
}