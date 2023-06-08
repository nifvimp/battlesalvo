package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.ProxyController;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.ArtificialPlayer;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Player;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.net.Socket;

/**
 * This is the main driver of this project.
 */
public class Driver {
  /**
   * Project entry point.
   *
   * @param args - no command line args required
   */
  public static void main(String[] args) {
    GameView view = new TerminalView();
    BoardObserver observer = new BoardObserver();
    UserCommunicator requester = new UserCommunicator(view);
    Controller controller;
    if (args.length == 0) {
      Player player = new ArtificialPlayer(observer);
      Player opponent = new ArtificialPlayer(observer);
      controller = new LocalController(player, opponent, view, requester, observer);
    } else {
      double playerTotal = 0;
      double opponentTotal = 0;
      for (int i = 0; i < 5000; i++) {
        try {
          Socket socket = new Socket("0.0.0.0", 35001);
          controller = new ProxyController(socket, new ArtificialPlayer(observer), view, observer);
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
      System.out.println("Player hit rate: " + playerTotal / 200);
      System.out.println("Opponent hit rate: " + opponentTotal / 200);
    }
//    controller.run();
  }
}