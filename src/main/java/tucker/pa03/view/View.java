package tucker.pa03.view;

import cs3500.pa03.model.GameResult;


/**
 * Displays program output to the user.
 */
public interface View {

  /**
   * Welcomes the user to BattleSalvo.
   */
  void greet();

  /**
   * Prompts the user for the board dimensions.
   *
   * @param retry whether an error message should be printed before getting input
   * @return the dimensions inputted
   */
  String getParameters(boolean retry);

  /**
   * Prompts the user for the fleet composition.
   *
   * @param retry whether an error message should be printed before getting input
   * @return the fleet composition inputted
   */
  String getFleet(boolean retry, int maxSize);

  /**
   * Informs the user that the game is over and why.
   *
   * @param gameResult whether the user won or lost
   * @param reason the reason the game ended, such as no more ships
   */
  void endGame(GameResult gameResult, String reason);

  /**
   * Shows the board of the user.
   *
   * @param board a board representation
   */
  void showBoard(char[][] board);

  /**
   * Shows the shots the user has fired.
   *
   * @param shots the shots as a boolean 2d array
   */
  void showShotBoard(Boolean[][] shots);

  /**
   * Gets one shot from the user.
   *
   * @return the shot inputted
   */
  String getShot();

  /**
   * Prompts the user to enter their shots.
   * Doesn't prompt them, just gets their input.
   *
   * @param numShots the number of shots for them to enter
   * @param retry whether an error message should be printed before getting input
   */
  void promptShots(int numShots, boolean retry);
}

