package cs3500.pa04.view;

import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;

/**
 * Visual driver for a battle salvo game.
 */
public interface GameView {
  /**
   * Shows a message to the user.
   *
   * @param message message to show
   */
  void showMessage(String message);

  /**
   * Welcomes the user to BattleSalvo.
   */
  void greet();

  /**
   * Displays the user's board.
   *
   * @param board representation of the user's board
   */
  void displayPlayerBoard(char[][] board);

  /**
   * Displays what the user knows about the opponent's board.
   *
   * @param board representation of the user's knowledge on opponent's board.
   */
  void displayOpponentBoard(char[][] board);

  /**
   * Prompts the user for the board dimensions.
   *
   * @return the dimensions inputted
   */
  String getBoardDimensions(Coord limit);

  /**
   * Prompts the user that the dimensions they previously put in were invalid.
   *
   * @param limit the minimum and maximum dimensions the board can be
   */
  void invalidBoardDimensions(Coord limit);

  /**
   * Prompts the user for the fleet composition.
   *
   * @param maxSize max size of fleet
   * @return the fleet composition inputted
   */
  String getFleet(int maxSize);

  /**
   * Prompts the user that their previously entered fleet specifications were invalid.
   */
  void invalidFleet();

  /**
   * Prompts the user to enter their shots.
   * Doesn't prompt them, just gets their input.
   *
   * @param numShots the number of shots for them to enter
   */
  void promptShots(int numShots);

  /**
   * Gets one shot from the user.
   *
   * @return the shot inputted
   */
  String getShot();

  /**
   * Prompts the user that some of the shots they previously entered were invalid.
   */
  void invalidShots();

  /**
   * Informs the user that the game is over and why.
   *
   * @param result whether the user won, lost, or tied
   * @param reason the reason the game ended
   */
  void showResults(GameResult result, String reason);
}