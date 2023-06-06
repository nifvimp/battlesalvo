package cs3500.pa04.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An abstract Battle Salvo game board.
 */
public class Board {
  private final char[][] opponentKnowledge;
  private final Cell[][] playerBoard;
  private final Set<Coord> shotsLeft;
  private final List<Ship> ships;
  private final int width;
  private final int height;

  /**
   * Creates an abstract battle salvo game board.
   *
   * @param height height of the board
   * @param width  width of the board
   */
  public Board(int height, int width, List<Ship> ships) {
    this.opponentKnowledge = new char[height][width];
    this.playerBoard = new Cell[height][width];
    this.ships = new ArrayList<>(ships);
    this.shotsLeft = new HashSet<>();
    this.width = width;
    this.height = height;
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        shotsLeft.add(new Coord(j, i));
        opponentKnowledge[i][j] = '~';
        playerBoard[i][j] = new Cell();
      }
    }
    for (Ship ship : ships) {
      for (Coord coord : ship.getOccupied()) {
        if(coord.y() >= height || coord.x() >= width) {
        }
        playerBoard[coord.y()][coord.x()] = new Cell(ship);
      }
    }
  }

  /**
   * Gets a representation of the player board.
   *
   * @return representation of the player board.
   */
  public char[][] getPlayerBoard() {
    char[][] board = new char[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        board[i][j] = playerBoard[i][j].getCharacter();
      }
    }
    return board;
  }

  /**
   * Gets a representation of opponent knowledge.
   *
   * @return representation of opponent knowledge.
   */
  public char[][] getOpponentKnowledge() {
    char[][] board = new char[height][width];
    for (int i = 0; i < height; i++) {
      board[i] = Arrays.copyOf(opponentKnowledge[i], width);
    }
    return board;
  }

  /**
   * Gets the available shots left on the board.
   *
   * @return valid shots left
   */
  public Set<Coord> validShots() {
    return new HashSet<>(shotsLeft);
  }

  /**
   * Returns true if the shot passed hits a ship on the board.
   *
   * @param coord position shot
   * @return true if the shot passed hits a ship on the board
   */
  public boolean takeDamage(Coord coord) {
    return playerBoard[coord.y()][coord.x()].takeDamage(coord);
  }

  /**
   * Mark a coord on the opponent knowledge board as a hit.
   *
   * @param coord position to mark
   * @param hit   true if shot on opponent was a hit
   */
  public void markOpponent(Coord coord, boolean hit) {
    shotsLeft.remove(coord);
    opponentKnowledge[coord.y()][coord.x()] = hit ? 'H' : 'X';
  }

  /**
   * returns the number of ships left on the board.
   *
   * @return number of ships left on the board
   */
  public int shipsLeft() {
    ships.removeIf(Ship::isSunk);
    return ships.size();
  }
}