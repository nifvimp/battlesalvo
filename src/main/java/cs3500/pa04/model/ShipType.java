package cs3500.pa04.model;

/**
 * Represents the types of ships in the game.
 */
public enum ShipType {
  CARRIER(6, 'C'),
  BATTLESHIP(5, 'B'),
  DESTROYER(4, 'D'),
  SUBMARINE(3, 'U');

  private final int size;
  private final char character;

  /**
   * Constructor for ShipType.
   *
   * @param size      how many cells the ship takes up
   * @param character the character to represent a ship with
   */
  ShipType(int size, char character) {
    this.size = size;
    this.character = character;
  }

  /**
   * Gets the size of the ship.
   *
   * @return the size
   */
  public int getSize() {
    return this.size;
  }

  /**
   * Gets the character representation of the ship.
   *
   * @return the character representation
   */
  public char getCharacter() {
    return this.character;
  }

}
