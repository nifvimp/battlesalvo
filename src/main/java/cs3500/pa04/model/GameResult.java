package cs3500.pa04.model;

/**
 * Represents the 3 possibilities for the game, a win, a loss, or a tie.
 */
public enum GameResult {
  WIN("You Won!"),
  LOSE("You Lost..."),
  TIE("You Tied.");

  // TODO: are messages necessary?
  private final String message;

  GameResult(String message) {
    this.message = message;
  }

  /**
   * Gets the message that is associated with a game result.
   *
   * @return the message to go along with the game result
   */
  public String message() {
    return message;
  }
}