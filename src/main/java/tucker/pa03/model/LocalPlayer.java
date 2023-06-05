package tucker.pa03.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Abstract class for a local player.
 * Common functionality for local players such as creating a board is implemented here.
 */
public abstract class LocalPlayer implements Player {
  private Board board;
  protected int width;
  protected int height;
  protected int shipCount;

  protected boolean[][] validShots;


  private final boolean[] lostGame;

  private final Random random;

  private final List<Coord> lastTurnShots = new ArrayList<>();

  /**
   * Constructor for LocalPlayer.
   *
   * @param lostGame an array containing one boolean which is updated when the game is over
   */
  public LocalPlayer(boolean[] lostGame, Random random) {
    this.lostGame = lostGame;
    this.random = random;
  }

  /**
   * Given the specifications for a BattleSalvo board, return a list of ships with their locations
   * on the board. Generates board according to a set random.
   *
   * @param height         the height of the board, range: [6, 15] inclusive
   * @param width          the width of the board, range: [6, 15] inclusive
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return the placements of each ship on the board
   */
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    board = new Board(height, width);
    this.height = height;
    this.width = width;
    List<Ship> placedShips = board.placeShips(specifications, random);
    shipCount = placedShips.size();

    //starts every possible shot on the board as valid
    validShots = new boolean[height][width];
    for (boolean[] arr : validShots) {
      Arrays.fill(arr, true);
    }
    return placedShips;
  }

  /**
   * Gets the character representation of the board.
   *
   * @return the character representation, with S for a sunk ship,
   *        H if the segment is damaged, or the character representing ship type.
   */
  public char[][] getBoardRepresentation() {
    return board.getBoardRepresentation();
  }

  /**
   * Gets the board containing shots fired, with true for a hit and false for a miss.
   * A cell contains null if no shot has been fired in that location.
   *
   * @return the 2d array of shots fired
   */
  public Boolean[][] getShotsResults() {
    return board.getShotResults();
  }

  /**
   * Adds every shot fired this turn to the list of shots fired.
   *
   * @param shotsFired the shots fired
   */
  protected void addShotsFired(List<Coord> shotsFired) {
    lastTurnShots.clear();
    lastTurnShots.addAll(shotsFired);
  }

  /**
   * Given the list of shots the opponent has fired on this player's board, report which
   * shots hit a ship on this player's board.
   *
   * @param opponentShotsOnBoard the opponent's shots on this player's board
   * @return a filtered list of the given shots that contain all locations of shots that hit a
   *        ship on this board
   */
  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    List<Coord> hits = new ArrayList<>();
    for (Coord coord : opponentShotsOnBoard) {
      Ship ship = board.getLocation(coord);
      if (ship != null) {
        if (!ship.isSegmentDamaged(coord)) {
          hits.add(coord);
          ship.takeDamage(coord);
          if (ship.isSunk()) {
            shipCount--;
          }
        }
      } else {
        board.placeOpponentMiss(coord);
      }
    }
    if (shipCount == 0) {
      lostGame[0] = true;
    }
    return hits;
  }

  /**
   * Reports to this player what shots in their previous volley returned from takeShots()
   * successfully hit an opponent's ship.
   *
   * @param shotsThatHitOpponentShips the list of shots that successfully hit the opponent's ships
   */
  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord coord : lastTurnShots) {
      if (!shotsThatHitOpponentShips.contains(coord)) {
        board.placeShotResult(coord, false);
      }
    }
    for (Coord coord : shotsThatHitOpponentShips) {
      board.placeShotResult(coord, true);
    }
  }

}
