package cs3500.pa04.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A local AI player of BattleSalvo that shoots randomly.
 */
public class RandomPlayer extends LocalPlayer {
  private final Random random;

  @Override
  public String name() {
    return "Random Player";
  }

  /**
   * Creates a new random player.
   *
   * @param observer board observer of the game
   */
  public RandomPlayer(BoardObserver observer) {
    super(observer);
    this.random = new Random();
  }

  /**
   * Creates a new random player for testing.
   *
   * @param observer board observer of the game
   * @param random   random to randomize by
   */
  public RandomPlayer(BoardObserver observer, Random random) {
    super(observer, random);
    this.random = random;
  }

  @Override
  protected List<Coord> loadShots() {
    List<Coord> shots = new ArrayList<>();
    List<Coord> validShots = new ArrayList<>(board.validShots());
    for (int i = 0; i < board.shipsLeft() && validShots.size() > 0; i++) {
      int randomShot = random.nextInt(validShots.size());
      shots.add(validShots.get(randomShot));
      validShots.remove(randomShot);
    }
    return shots;
  }
}