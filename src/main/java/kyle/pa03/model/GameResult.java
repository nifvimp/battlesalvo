package kyle.pa03.model;

/**
 * Results of a Battle Salvo game.
 */
public enum GameResult { // TODO: might want to move reasons here
  WIN("You Won!"),
  LOSE("You Lost..."),
  TIE("You Tied.");

  private final String message;

  GameResult(String message) {
    this.message = message;
  }

  /**
   * Gets the message that is associated with a game result
   *
   * @return the message to go along with the game result
   */
  public String message() {
    return message;
  }
}