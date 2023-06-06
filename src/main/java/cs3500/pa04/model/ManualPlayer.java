package cs3500.pa04.model;

import cs3500.pa04.controller.UserCommunicator;
import java.util.List;
import java.util.Random;

/**
 * A manual player.
 */
public class ManualPlayer extends LocalPlayer {
  private final UserCommunicator input;

  @Override
  public String name() {
    return "Manual Player";
  }

  /**
   * Creates a new manual player.
   *
   * @param observer observer of the game
   * @param input    where to get input from
   */
  public ManualPlayer(BoardObserver observer, UserCommunicator input) {
    super(observer);
    this.input = input;
  }

  /**
   * Creates a manual player for testing.
   *
   * @param observer observer of the game
   * @param input    where to get input from
   * @param random   random to randomize by
   */
  public ManualPlayer(BoardObserver observer, UserCommunicator input, Random random) {
    super(observer, random);
    this.input = input;
  }

  protected List<Coord> loadShots() {
    int numShots = Math.min(board.shipsLeft(), board.validShots().size());
    List<Coord> shots = input.requestShots(numShots);
    if (board.validShots().containsAll(shots)) {
      return shots;
    } else {
      input.signalInvalidShots();
      return loadShots();
    }
  }
}