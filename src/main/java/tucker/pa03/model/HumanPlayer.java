package tucker.pa03.model;

import cs3500.pa03.controller.InputCollector;
import java.util.List;
import java.util.Random;

/**
 * A local player of BattleSalvo, controlled by a human.
 */
public class HumanPlayer extends LocalPlayer {
  private final InputCollector inputCollector;

  /**
   * Constructor for HumanPlayer, with a given seed.
   *
   * @param lostGame an array containing one boolean which is updated when the game is over
   * @param random the random seed to generate the board with
   * @param inputCollector retrieves the input for the player when asked
   */
  public HumanPlayer(boolean[] lostGame, Random random, InputCollector inputCollector) {
    super(lostGame, random);
    this.inputCollector = inputCollector;
  }

  /**
   * Constructor for HumanPlayer, without a given seed.
   * Generates a new random if one isn't passed in.
   *
   * @param lostGame an array containing one boolean which is updated when the game is over
   * @param inputCollector retrieves the input for the player when asked
   */
  public HumanPlayer(boolean[] lostGame, InputCollector inputCollector) {
    this(lostGame, new Random(), inputCollector);
  }

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Human Player";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    int validSum = 0;
    for (boolean[] b : validShots) {
      for (boolean ignored : b) {
        validSum++;
      }
    }
    //Doesn't check to make sure inputted shots are valid
    //However takes in only as many shots as there are valid shots left
    List<Coord> shots = inputCollector.getShots(Math.min(shipCount, validSum), width, height);
    addShotsFired(shots);
    for (Coord shot : shots) {
      validShots[shot.y()][shot.x()] = false;
    }
    return shots;
  }

  /**
   * Notifies the player that the game is over.
   * Win, lose, and draw should all be supported
   *
   * @param result if the player has won, lost, or forced a draw
   * @param reason the reason for the game ending
   */
  @Override
  public void endGame(GameResult result, String reason) {
  }

}
