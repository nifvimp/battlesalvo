package cs3500.pa04.model;

public class Cell {
  private final Ship ship;
  private boolean shot;

  Cell(Ship ship) {
    this.ship = ship;
  }

  Cell() {
    this.ship = null;
  }

  public char getCharacter() {
    if (ship != null) {
      return (ship.isSunk() ? 'S' : (shot) ? 'H' : ship.getType().getCharacter());
    }
    return (shot) ? 'X' : '~';
  }

  /**
   * Returns true if the shot passed hits a ship on the board.
   *
   * @param coord position shot
   * @return true if the shot passed hits a ship on the board
   */
  public boolean takeDamage(Coord coord) {
    this.shot = true;
    if (ship != null) {
      ship.takeDamage(coord);
    }
    return ship != null;
  }
}