package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.ProxyController;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.player.ProbabilityStackPlayer;
import cs3500.pa04.model.player.StackPlayer;
import cs3500.pa04.model.player.CheckerLinePlayer;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.player.ProbabilityPlayer;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.io.StringReader;
import java.net.Socket;
import java.util.Random;

/**
 * This is the main driver of this project.
 */
public class Driver {

  private static final int GAMES = 10;
  private static final Random random = new Random();

  /**
   * Project entry point.
   *
   * @param args - no command line args required
   */
  public static void main(String[] args) {
    GameView view = new TerminalView();
    BoardObserver observer = new BoardObserver();
    UserCommunicator requester = new UserCommunicator(view);
    int wins = 0;
    Controller controller;
    if (args.length == 0) {
      for (int i = 0; i < GAMES; i++) {
        Readable input = new StringReader(String.format("""
          %s %s
          %s %s %s %s
          """,
            random.nextInt(6, 16), random.nextInt(6, 16),
            1, 2, 2, 1
        ));
        view = new TerminalView(System.out, input);
        observer = new BoardObserver();
        requester = new UserCommunicator(view);
        Player player = new ProbabilityStackPlayer(observer);
        Player opponent = new StackPlayer(observer);
        controller = new LocalController(player, opponent, view, requester, observer);
        controller.run();
        if (observer.getBoard(player).shipsLeft() > 0 && observer.getBoard(opponent).shipsLeft() <= 0) {
          wins++;
        }
      }
    } else {
      double playerTotal = 0;
      double opponentTotal = 0;
      for (int i = 0; i < GAMES; i++) {
        try {
          Socket socket = new Socket("0.0.0.0", 35001);
          controller = new ProxyController(socket, new CheckerLinePlayer(observer), view, observer);
          double[] thing = controller.run(1);
          playerTotal += thing[0];
          opponentTotal += thing[1];
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
          System.err.println("There was an error connecting to the server.");
          return;
        }
      }
      System.out.println("Player hit rate: " + playerTotal / GAMES);
      System.out.println("Opponent hit rate: " + opponentTotal / GAMES);
    }
    System.out.println("Wins: " + wins);
//    controller.run();
  }
}