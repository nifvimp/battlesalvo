package cs3500.pa04.view;


import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.ShipType;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


/**
 * Terminal view of a battle salvo game.
 */
public class TerminalView implements GameView {
  private final PrintStream output;
  private final Scanner input;

  /**
   * Creates a new terminal view with the default system input and output streams as
   * the input and output.
   */
  public TerminalView() {
    this.output = System.out;
    this.input = new Scanner(System.in);
  }

  /**
   * Creates a new terminal view for testing.
   *
   * @param output test output
   * @param input  test input
   */
  public TerminalView(PrintStream output, Readable input) {
    this.output = output;
    this.input = new Scanner(input);
  }

  @Override
  public void showMessage(String message) {
    output.println(message);
  }

  @Override
  public void greet() {
    output.println("Hello! Welcome to the OOD BattleSalvo Game!\n");
  }

  @Override
  public void displayPlayerBoard(char[][] board) {
    showMessage(String.format("""
            Your Board:
                    
            %s""", getBoardString(board)
        )
    );
  }

  @Override
  public void displayOpponentBoard(char[][] board) {
    showMessage(String.format("""
            Opponent Board Data:
                    
            %s""", getBoardString(board)
        )
    );
  }

  // TODO: fix output

  /**
   * Converts the 2d character array representation of a board to a string.
   *
   * @param board board representation to convert
   * @return string representation of the board
   */
  private String getBoardString(char[][] board) {
    StringBuilder boardString = new StringBuilder();
    boardString.append("      ");
    for (int i = 0; i < board[0].length; i++) {
      boardString.append("  ").append(i);
    }
    boardString.append('\n');
    for (int i = 0; i < board.length; i++) {
      boardString.append("    ");
      boardString.append(" ").append(i >= 10 ? i : " " + i);
      List<String> cells = new ArrayList<>();
      for (int j = 0; j < board[i].length; j++) {
        cells.add((j >= 10 ? "  " : " ") + board[i][j]);
      }
      boardString.append(String.join(" ", cells)).append("\n");
    }
    return boardString.toString();
  }

  @Override
  public String getBoardDimensions(Coord limit) {
    showMessage(String.format("""
            Boards can be between sizes %s and %s, and dimensions do not need to match.
            Please enter a valid height and width below:
            ------------------------------------------------------""",
        limit.x(), limit.y()));
    return input.nextLine();
  }

  @Override
  public void invalidBoardDimensions(Coord limit) {
    showMessage(String.format(
            "Uh Oh! You've entered invalid dimensions. Please remember that "
                + "the height and width of the game must be in the range %s, inclusive."
                + "\n------------------------------------------------------",
            limit
        )
    );
  }

  @Override
  public String getFleet(int maxSize) {
    showMessage(String.format("""
            Please enter your fleet in the order %s.
            Remember, your fleet may not exceed size %s.
            ------------------------------------------------------""",
        Arrays.stream(ShipType.values()).map(Enum::toString).toList(),
        maxSize
    ));
    return input.nextLine();
  }

  @Override
  public void invalidFleet() {
    showMessage("Uh Oh! You've entered invalid fleet sizes.");
  }

  @Override
  public void promptShots(int numShots) {
    showMessage(String.format("""
        Please enter %s
        """, numShots + (numShots > 1 ? " shots:" : " shot:")
    ));
  }

  @Override
  public void invalidShots() {
    showMessage("""
        ------------------------------------------------------
        Please try again. Shots must be a valid coordinate on the board."""
    );
  }

  @Override
  public String getShot() {
    return input.nextLine();
  }

  // TODO: remove result?
  @Override
  public void showResults(GameResult result, String reason) {
    showMessage(String.format("""
            %s
            %s""",
        result.message(),
        reason
    ));
    input.close();
  }
}
