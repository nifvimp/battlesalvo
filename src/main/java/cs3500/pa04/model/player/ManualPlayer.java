package cs3500.pa04.model.player;

import cs3500.pa03.controller.UserRequester;
import java.util.List;
import java.util.Random;
import kyle.pa03.model.BoardObserver;
import kyle.pa03.model.Coord;

/**
 * A manual player.
 */
public class ManualPlayer extends LocalPlayer {
  private final UserRequester input;

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
  public ManualPlayer(BoardObserver observer, UserRequester input) {
    super(observer);
    this.input = input;
  }

  /**
   * Creates a manual player for testing.
   *
   * @param observer observer of the game
   * @param random   random to randomize by
   * @param input    where to get input from
   */
  public ManualPlayer(BoardObserver observer, Random random, UserRequester input) {
    super(observer, random);
    this.input = input;
  }

  @Override
  protected void loadShots() {
    int numShots = Math.min(playerBoard.shipsLeft(), playerBoard.validShots().size());
    List<Coord> shots = input.requestShots(numShots);
    if (opponentBoard.validShots().containsAll(shots)) {
      salvo.addAll(shots);
    } else {
      input.signalInvalidShots();
      loadShots();
    }
  }
}