package kyle.pa03.view;

import cs3500.pa03.model.Coord;
import cs3500.pa03.model.GameResult;
import java.util.List;

// TODO: Don't Necessarily need invalid methods
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
   * Shows the board of the user.
   *
   * @param board representation of the user's board
   */
  void displayPlayerBoard(char[][] board);

  /**
   * Displays what the user knows about the opponent's board.
   *
   * @param board representation of user's knowledge on opponent board.
   */
  void displayOpponentBoard(char[][] board);

  /**
   * Prompts the user for the board dimensions.
   *
   * @return user response
   */
  String getBoardDimensions();

  /**
   * Prompts the user to input the fleet specifications they want to have for the game.
   *
   * @return user response
   */
  String getFleet();

  /**
   * Prompts the user that the dimensions they previously put in were invalid.
   *
   * @param limit the minimum and maximum dimensions the board can be
   */
  void invalidBoardSize(Coord limit);

  /**
   * Prompts the user that their previously entered fleet specifications were invalid.
   *
   * @param maxSize the maximum number of ships the fleet can have
   */
  void invalidFleet(int maxSize);

  /**
   * Prompts the user to input the shots they want to make on the opponent.
   *
   * @param numShots number of shots to get from the user
   * @return user response
   */
  List<String> getShots(int numShots);

  /**
   * Prompts the user that some of the shots they previously entered were invalid.
   */
  void invalidShots();

  /**
   * Shows the result of the game.
   *
   * @param result result of the game
   * @param reason reason for game result
   */
  void showResults(GameResult result, String reason);
}