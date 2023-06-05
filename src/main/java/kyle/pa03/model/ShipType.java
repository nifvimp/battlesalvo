package kyle.pa03.model;

/**
 * Enumeration of ships that can appear in a battle salvo game.
 */
public enum ShipType {
  CARRIER(6, 'C'),
  BATTLESHIP(5, 'B'),
  DESTROYER(4, 'D'),
  SUBMARINE(3, 'S');

  private final int size;
  private final char shortForm;

  ShipType(int size, char shortForm) {
    this.size = size;
    this.shortForm = shortForm;
  }

  /**
   * Gets the size of ship type.
   *
   * @return size of the ship type
   */
  public int getSize() {
    return this.size;
  }

  /**
   * Gets the board representation of the ship type.
   *
   * @return the board representation of the ship type.
   */
  public char getShortForm() {
    return this.shortForm;
  }
}
