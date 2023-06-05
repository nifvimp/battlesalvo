package kyle.pa03.view;

import cs3500.pa03.model.Coord;
import cs3500.pa03.model.GameResult;
import cs3500.pa03.model.ShipType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Terminal view of a battle salvo game.
 */
public class TerminalView implements GameView {
  private final Appendable output;
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
   * @param input test input
   */
  public TerminalView(Appendable output, Readable input) {
    this.output = output;
    this.input = new Scanner(input);
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

  private String getBoardString(char[][] board) {
    StringBuilder boardString = new StringBuilder();
    for (char[] row : board) {
      boardString.append("\t");
      List<String> cells = new ArrayList<>();
      for (char cell : row) {
        cells.add(String.valueOf(cell));
      }
      boardString.append(String.join(" ", cells)).append("\n");
    }
    return boardString.toString().stripTrailing();
  }

  @Override
  public void showMessage(String message) {
    try {
      output.append(message).append("\n");
    } catch (IOException e) {
      System.err.println("Error outputting to output.");
    }
  }

  @Override
  public String getBoardSize() {
    showMessage("Please enter a valid height and width below:");
    return input.nextLine();
  }

  @Override
  public void invalidBoardSize(Coord limit) {
    showMessage(String.format(
        "The dimensions you entered were invalid dimensions. Please remember that "
            + "the height and width of the game must be in the range %s, inclusive.",
        limit
        )
    );
  }

  @Override
  public List<String> getShots(int numShots) {
    showMessage(String.format("Please Enter %s Shots:", numShots));
    List<String> shots = new ArrayList<>();
    for (int i = 0; i < numShots; i++) {
      shots.add(input.nextLine());
    }
    return shots;
  }

  @Override
  public void invalidShots() {
    showMessage("Some of the shots you entered were either invalid, out of bounds, or has "
        + "already been shot.");
  }

  @Override
  public String getFleet() {
    showMessage(
        String.format("Please enter your fleet in the order %s.",
        Arrays.stream(ShipType.values()).map(Enum::toString).toList()
    ));
    return input.nextLine();
  }

  @Override
  public void invalidFleet(int maxSize) {
    showMessage(
        String.format("The fleet you entered is invalid. Remember, your fleet must "
            + "of size %s and have at least one of each ship type.", maxSize
    ));
  }

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
