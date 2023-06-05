package cs3500.pa04.model.board;

import cs3500.pa04.model.Coord;
import cs3500.pa04.model.ShipType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * One player's board in BattleSalvo. Tracks both their ships and the shots on the opponent.
 */
public class Board {
  private final Ship[][] shipBoard;
  private final Boolean[][] shotResultBoard;

  private final boolean[][] opponentMissesBoard;

  private final int width;

  private final int height;


  /**
   * Constructor for Board.
   *
   * @param height the height of the board
   * @param width  the width of the board
   */
  public Board(int height, int width) {
    shipBoard = new Ship[height][width];
    shotResultBoard = new Boolean[height][width];
    opponentMissesBoard = new boolean[height][width];
    this.width = width;
    this.height = height;
  }

  /**
   * Places ships according to the specified number.
   *
   * @param specifications the map containing ship type and number of ships to place
   * @param random the Random object for generating the board
   * @return a list of the ships placed
   */
  public List<Ship> placeShips(Map<ShipType, Integer> specifications, Random random) {
    //Keeps trying to place ships until successful
    while (true) {
      try {
        ArrayList<Ship> placedShips = new ArrayList<>();
        List<ShipType> types = new ArrayList<>(specifications.keySet().stream().toList());
        Collections.sort(types);
        for (ShipType type : types) {
          for (int i = 0; i < specifications.get(type); i++) {
            placedShips.add(placeShipRandomly(type, random));
          }
        }
        return placedShips;
      } catch (Exception ignored) {
        // Empty catch block: intentionally left blank
      }
    }
  }

  /**
   * Gets the ship at the specified location.
   *
   * @param coord the coordinate of the board to get from
   * @return the Ship if one is present, if none are returns null
   */
  public Ship getLocation(Coord coord) {
    return shipBoard[coord.y()][coord.x()];
  }

  /**
   * Gets the character representation of the board.
   *
   * @return the character representation, with S for a sunk ship,
   *        H if the segment is damaged, or the character representing ship type.
   */
  public char[][] getBoardRepresentation() {
    char[][] boardRepresentation = new char[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        Coord coord = new Coord(j, i);
        Ship ship = getLocation(coord);
        if (ship == null) {
          boardRepresentation[i][j] = opponentMissesBoard[i][j] ? 'X' : '~';
        } else {
          boardRepresentation[i][j] =
              ship.isSunk() ? 'S' :
                  ship.isSegmentDamaged(coord) ? 'H' : ship.getType().getCharacter();
        }
      }
    }
    return boardRepresentation;
  }

  /**
   * Gets the board containing shots fired, with true for a hit and false for a miss.
   * A cell contains null if no shot has been fired in that location.
   *
   * @return the 2d array of shots fired
   */
  public Boolean[][] getShotResults() {
    return shotResultBoard;
  }

  /**
   * Marks the shot result board at the coordinate as either a hit or a miss.
   *
   * @param coord the coordinate to mark on the board
   * @param hit   whether the shot hit or missed
   */
  public void placeShotResult(Coord coord, boolean hit) {
    if (shotResultBoard[coord.y()][coord.x()] == null) {
      shotResultBoard[coord.y()][coord.x()] = hit;
    }
  }

  /**
   * Marks the specified coordinate as an opponent miss.
   *
   * @param coord the coord to mark
   */
  public void placeOpponentMiss(Coord coord) {
    opponentMissesBoard[coord.y()][coord.x()] = true;
  }

  /**
   * Randomly places a ship of the specified type in the 2d array.
   *
   * @param type the type of ship to create
   * @return the placed ship
   */
  private Ship placeShipRandomly(ShipType type, Random random) {
    Coord coord;
    boolean vertical;
    do {
      vertical = random.nextBoolean();
      coord = new Coord(random.nextInt(0, width), random.nextInt(0, height));
    } while (!canFit(coord, type.getSize(), vertical));
    Ship ship = new Ship(type, coord);
    for (int i = 0; i < type.getSize(); i++) {
      if (vertical) {
        shipBoard[i + coord.y()][coord.x()] = ship;
      } else {
        shipBoard[coord.y()][i + coord.x()] = ship;
      }
    }
    return ship;
  }

  /**
   * Checks if there is room for a ship.
   *
   * @param coord    the starting coordinate of the ship
   * @param size     the size of the ship
   * @param vertical if the ship is vertical or horizontal
   * @return whether the ship can fit or not
   */
  private boolean canFit(Coord coord, int size, boolean vertical) {
    if (vertical) {
      for (int i = coord.y(); i < coord.y() + size; i++) {
        if (i > height - 1 || shipBoard[i][coord.x()] != null) {
          return false;
        }
      }
    } else {
      for (int i = coord.x(); i < coord.x() + size; i++) {
        if (i > width - 1 || shipBoard[coord.y()][i] != null) {
          return false;
        }
      }
    }
    return true;
  }
}
