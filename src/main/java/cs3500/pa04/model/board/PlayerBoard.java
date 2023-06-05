package cs3500.pa04.model.board;

import cs3500.pa04.model.Coord;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The game board of a player.
 */
public class PlayerBoard extends AbstractBoard {
  private final List<Ship1> ships;
  private final Map<Coord, Ship1> shipPlacement;

  /**
   * Creates a new player board.
   *
   * @param height    height of the board
   * @param width     width of the board
   * @param hitsToWin number of distinct hits to win the game
   * @param ships     ships to place on the board
   */
  public PlayerBoard(int height, int width, int hitsToWin, List<Ship1> ships) {
    super(height, width, hitsToWin);
    this.ships = new ArrayList<>(ships);
    shipPlacement = new HashMap<>();
    for (Ship1 ship : ships) {
      for (Coord coord : ship.getOccupied()) {
        shipPlacement.put(coord, ship);
      }
    }
  }

  @Override
  protected char translateCoord(Coord coord) {
    if (hits.contains(coord)) {
      return 'H';
    } else if (misses.contains(coord)) {
      return 'M';
    } else if (shipPlacement.get(coord) != null) {
      return shipPlacement.get(coord).shipType().getCharacter();
    } else {
      return '-';
    }
  }

  /**
   * returns the number of ships left on the board.
   *
   * @return number of ships left on the board
   */
  public int shipsLeft() {
    return ships.size();
  }

  /**
   * Returns true if the shot passed hits a ship on the board.
   *
   * @param shot position to shot
   * @return true if the shot passed hits a ship on the board
   */
  public boolean hit(Coord shot) {
    boolean result;
    if (shipPlacement.get(shot) != null) {
      hits.add(shot);
      updateShips();
      result = true;
    } else {
      misses.add(shot);
      result = false;
    }
    shotsLeft.remove(shot);
    return result;
  }

  /**
   * Removes ships from the ship list that have sunk.
   */
  private void updateShips() {
    List<Ship1> toRemove = new ArrayList<>();
    for (Ship1 ship : ships) {
      if (hits.containsAll(ship.getOccupied())) {
        toRemove.add(ship);
      }
    }
    ships.removeAll(toRemove);
  }
}