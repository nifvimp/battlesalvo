package cs3500.pa04.model.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import kyle.pa03.model.BoardObserver;
import kyle.pa03.model.Coord;

/**
 * A local player of BattleSalvo, controlled by an advanced AI.
 */
public class ArtificialPlayer extends LocalPlayer {
  private final Random random;

  @Override
  public String name() {
    return "Artificial Player";
  }

  /**
   * Creates a new artificial player.
   *
   * @param observer board observer of the game
   */
  public ArtificialPlayer(BoardObserver observer) {
    super(observer);
    this.random = new Random();
  }

  /**
   * Creates a new artificial player for testing.
   *
   * @param observer board observer of the game
   * @param random   random to randomize by
   */
  public ArtificialPlayer(BoardObserver observer, Random random) {
    super(observer, random);
    this.random = random;
  }

  @Override
  protected void loadShots() {
    List<Coord> shots = new ArrayList<>();
    List<Coord> validShots = new ArrayList<>(opponentBoard.validShots());
    for (int i = 0; i < playerBoard.shipsLeft() && validShots.size() > 0; i++) {
      int randomShot = random.nextInt(validShots.size());
      shots.add(validShots.get(randomShot));
      validShots.remove(randomShot);
    }
    // TODO: fix up to be better
    salvo.addAll(shots);
  }
}
