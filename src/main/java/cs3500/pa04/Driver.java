package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.ProxyController;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.ArtificialPlayer;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.ManualPlayer;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.RandomPlayer;
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
    UserCommunicator communicator = new UserCommunicator(view);
    Controller controller;
    if (args.length == 0) {
      Player player = new ManualPlayer(observer, communicator);
      Player opponent = new ArtificialPlayer(observer);
      controller = new LocalController(player, opponent, view, communicator, observer);
    } else {
      try {
        Socket socket = new Socket(args[0], Integer.parseInt(args[1]));
        controller = new ProxyController(socket, new RandomPlayer(observer), view, observer);
      } catch (IOException | NumberFormatException e) {
        System.err.println(
            "There was an error connecting to the server, or host and port were incorrect."
        );
        return;
      }
    }
    controller.run();
  }
}