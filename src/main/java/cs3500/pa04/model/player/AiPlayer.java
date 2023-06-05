package cs3500.pa04.model.player;

import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * A local player of BattleSalvo, controlled by an advanced AI.
 */
public class AiPlayer extends LocalPlayer {
  private final Stack<Coord> shotStack = new Stack<>();

  private final Random random;

  /**
   * Constructor for AiPlayer, with a given seed.
   *
   * @param lostGame an array containing one boolean which is updated when the game is over
   * @param random the random seed to used to generate the board and get random shots
   */
  public AiPlayer(boolean[] lostGame, Random random) {
    super(lostGame, random);
    this.random = random;
  }

  /**
   * Constructor for HumanPlayer, without a given seed.
   * Generates a new random if one isn't passed in.
   *
   * @param lostGame an array containing one boolean which is updated when the game is over
   */
  public AiPlayer(boolean[] lostGame) {
    this(lostGame, new Random());
  }

  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    return "Ai Player";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   */
  @Override
  public List<Coord> takeShots() {
    //Works by getting a random shot or a shot from the stack
    //When a hit is found adds all adjacent positions to the stack
    ArrayList<Coord> shots = new ArrayList<>();
    int validSum = 0;
    for (boolean[] b : validShots) {
      for (boolean ignored : b) {
        validSum++;
      }
    }
    for (int i = Math.min(shipCount, validSum); i > 0; i--) {
      Coord shot;
      if (!shotStack.empty()) {
        shot = shotStack.pop();
      } else {
        shot = randomShot();
      }
      shots.add(shot);
      validShots[shot.y()][shot.x()] = false;
    }
    addShotsFired(shots);
    return shots;
  }

  /**
   * Generates a new valid shot randomly.
   *
   * @return a coordinate to shoot
   */
  private Coord randomShot() {
    ArrayList<Coord> validCoords = new ArrayList<>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        if (validShots[i][j]) {
          validCoords.add(new Coord(j, i));
        }
      }
    }
    Collections.shuffle(validCoords, random);
    return validCoords.get(0);
  }

  /**
   * Reports to this player what shots in their previous volley returned from takeShots()
   * successfully hit an opponent's ship.
   *
   * @param shotsThatHitOpponentShips the list of shots that successfully hit the opponent's ships
   */
  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    super.successfulHits(shotsThatHitOpponentShips);
    for (Coord coord : shotsThatHitOpponentShips) {
      addAdjacentShots(coord);
    }

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

  /**
   * Adds any valid shot next to a coordinate to the shot stack.
   *
   * @param coord the coordinate to add around
   */
  private void addAdjacentShots(Coord coord) {
    List<Coord> coords = new ArrayList<>();
    coords.add(new Coord(coord.x() + 1, coord.y()));
    coords.add(new Coord(coord.x() - 1, coord.y()));
    coords.add(new Coord(coord.x(), coord.y() + 1));
    coords.add(new Coord(coord.x(), coord.y() - 1));
    for (Coord adjacentCoord : coords) {
      if ((adjacentCoord.x() < width && adjacentCoord.x() >= 0)
          && adjacentCoord.y() < height && adjacentCoord.y() >= 0) {
        if (validShots[adjacentCoord.y()][adjacentCoord.x()]) {
          shotStack.push(adjacentCoord);
          validShots[adjacentCoord.y()][adjacentCoord.x()] = false;
        }
      }
    }
  }

}
