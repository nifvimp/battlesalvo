package tucker.pa03.view;

import cs3500.pa03.model.GameResult;
import java.io.PrintStream;
import java.util.Scanner;

/**
 * A view that communicates to the user through the terminal.
 */
public class TerminalView implements View {

  private final PrintStream output;
  private final Scanner scanner;

  /**
   * Constructor for TerminalView.
   *
   * @param input  a Readable input object.
   * @param output a PrintStream object for output.
   */
  public TerminalView(Readable input, PrintStream output) {
    this.output = output;
    this.scanner = new Scanner(input);
  }


  /**
   * Welcomes the user to BattleSalvo.
   */
  @Override
  public void greet() {
    output.println("Hello! Welcome to the OOD BattleSalvo Game!\n");
  }

  /**
   * Prompts the user for the board dimensions.
   *
   * @param retry whether an error message should be printed before getting input
   * @return the dimensions inputted
   */
  @Override
  public String getParameters(boolean retry) {
    if (!retry) {
      output.println("Boards can be between sizes 6 and 15, and dimensions do not need to match.");
      output.println("Please enter a valid height and width below:");
      output.println("------------------------------------------------------");
    } else {
      output.println("------------------------------------------------------");
      output.println(
          "Uh Oh! You've entered invalid dimensions. Please remember that the height and width\n"
              + "of the game must be in the range (6, 15), inclusive. Try again!");
      output.println("------------------------------------------------------");
    }
    return scanner.nextLine();
  }

  /**
   * Prompts the user for the fleet composition.
   *
   * @param retry whether an error message should be printed before getting input
   * @return the fleet composition inputted
   */
  @Override
  public String getFleet(boolean retry, int maxFleetSize) {
    if (!retry) {
      output.println(
          "Please enter your fleet in the order [Carrier, Battleship, Destroyer, Submarine].\n"
              + "Remember, your fleet may not exceed size " + maxFleetSize + ".");
      output.println("------------------------------------------------------");
    } else {
      output.println("------------------------------------------------------");
      output.println("""
          Uh Oh! You've entered invalid fleet sizes.
          Please enter your fleet in the order [Carrier, Battleship, Destroyer, Submarine].
          Remember, your fleet may not exceed size""" + " " + maxFleetSize + ".");
      output.println("------------------------------------------------------");
    }
    return scanner.nextLine();
  }

  /**
   * Gets one shot from the user.
   * Doesn't prompt them, just gets their input.
   *
   * @return the shot inputted
   */
  @Override
  public String getShot() {
    return scanner.nextLine();
  }

  /**
   * Prompts the user to enter their shots.
   *
   * @param numShots the number of shots for them to enter
   * @param retry    whether an error message should be printed
   */
  @Override
  public void promptShots(int numShots, boolean retry) {
    if (retry) {
      output.println("------------------------------------------------------");
      output.println("Please try again. Shots must be a valid coordinate on the board.");
    }
    output.println("Please enter: " + numShots + (numShots > 1 ? " shots:" : " shot:"));
    output.println("------------------------------------------------------");


  }

  /**
   * Informs the user that the game is over and why.
   *
   * @param gameResult whether the user won or lost
   * @param reason     the reason the game ended, such as no more ships
   */
  @Override
  public void endGame(GameResult gameResult, String reason) {
    switch (gameResult) {
      case LOSS -> output.println("You lost.");
      case TIE -> output.println("You tied.");
      case WIN -> output.println("You won!");
      default -> throw new IllegalStateException("this shouldn't be reached");
    }
    output.println("Reason: " + reason);
  }

  /**
   * Shows the board of the user.
   *
   * @param board a board representation
   */
  @Override
  public void showBoard(char[][] board) {
    output.println("Your Board:\n");
    for (char[] chars : board) {
      output.print("   ");
      for (char c : chars) {
        output.print(" " + c);
      }
      output.println();
    }
    output.println();
  }

  /**
   * Shows the shots the user has fired.
   *
   * @param shots the shots as a boolean 2d array
   */
  @Override
  public void showShotBoard(Boolean[][] shots) {
    output.println("Opponent Board Data:\n");
    output.print("   ");
    for (int i = 0; i < shots[0].length; i++) {
      output.print(" " + i);
    }
    output.println();
    for (int i = 0; i < shots.length; i++) {
      output.print(" " + (i >= 10 ? i : " " + i));
      for (int j = 0; j < shots[i].length; j++) {
        output.print(
            (j >= 10 ? "  " : " ") + (shots[i][j] == null ? '~' : shots[i][j] ? 'H' : 'X'));
      }
      output.println();
    }
    output.println();
  }
}
