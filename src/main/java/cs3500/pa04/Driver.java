package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.ProxyController;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.player.LinePlayer;
import cs3500.pa04.model.player.StackPlayer;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This is the main driver of this project.
 */
public class Driver {
  private static final boolean LOCAL = true;
  private static final Class<?> PLAYER_CLASS = StackPlayer.class;
  private static final Class<?> OPPONENT_CLASS = LinePlayer.class;
  private static final String DEFAULT_HOST = "0.0.0.0";
  private static final int DEFAULT_PORT = 35001;
  private static final int GAMES = 1000;
  private static final Random random = new Random();
  private static Socket socket;
  private static int wins = 0;
  private static double playerCumulativeHitRate = 0.0d;
  private static double opponentCumulativeHitRate = 0.0d;
  private static int runs = 0;

  /**
   * Project entry point.
   *
   * @param args - no command line args required
   */
  public static void main(String[] args)
      throws IOException, InterruptedException {
    if (!LOCAL) {
      if (args.length != 0) {
        socket = new Socket(args[0], Integer.parseInt(args[1]));
      } else {
        socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
      }
    }
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < GAMES; i++) {
      Thread thread = new GameSim();
      threads.add(thread);
      thread.start();
    }
    for (Thread thread : threads) {
      thread.join();
    }
    if (socket != null) {
      socket.close();
    }
    System.out.println("Player hit rate: " + playerCumulativeHitRate / GAMES);
    System.out.println("Opponent hit rate: " + opponentCumulativeHitRate / GAMES);
    System.out.println("Win Rate: " + (double) wins / GAMES);
  }

  private static class GameSim extends Thread {
    @Override
    public void run() {
      try {
        if (LOCAL) {
          simulateLocalGame(PLAYER_CLASS, OPPONENT_CLASS, false);
        } else {
          simulateServerGame(PLAYER_CLASS, socket, false);
        }
      } catch (NoSuchMethodException | IOException | IllegalAccessException |
               InstantiationException | InvocationTargetException e) {
        throw new RuntimeException("Something went horribly wrong. Probably constructing players",
            e);
      }
      runs++;
      if (runs % (GAMES / 100) == 0) {
        System.out.println("Simulations Completed: " + runs);
      }
    }
  }

  /**
   * Plays a game with the server using the specified player.
   *
   * @param playerType class of player
   * @param socket     socket to server
   * @param print      true if you want to print logs to terminal
   * @throws NoSuchMethodException     if creating a player throws
   * @throws InvocationTargetException if creating a player throws
   * @throws InstantiationException    if creating a player throws
   * @throws IllegalAccessException    if creating a player throws
   */
  private static void simulateServerGame(Class<?> playerType, Socket socket, boolean print)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException, IOException {
    GameView view =
        new TerminalView((!print) ? new PrintStream(new StubOutputStream()) : System.out,
            new StringReader(""));
    BoardObserver observer = new BoardObserver();
    Player player = (Player) playerType.getConstructor(BoardObserver.class).newInstance(observer);
    Controller controller = new ProxyController(socket, player, view, observer);
    if (controller.run("tell me if I won")) {
      wins++;
    }
    double[] hitRates = observer.getBoard(player).hitRate();
    playerCumulativeHitRate += hitRates[0];
    opponentCumulativeHitRate += hitRates[1];
  }

  /**
   * Simulates a local game with two of the specified players.
   *
   * @param player1 class of player 1
   * @param player2 class of player 2
   * @param print   true if you want to print logs to terminal
   * @throws NoSuchMethodException     if creating a player throws
   * @throws InvocationTargetException if creating a player throws
   * @throws InstantiationException    if creating a player throws
   * @throws IllegalAccessException    if creating a player throws
   */
  private static void simulateLocalGame(Class<?> player1, Class<?> player2, boolean print)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException,
      IllegalAccessException {
    String randomSetup = generateRandomSetup();
    Readable input = new StringReader(randomSetup);
    List<Integer> setup = Arrays.stream(randomSetup.split("[\s\n]"))
        .map(Integer::parseInt).toList();
    GameView view =
        new TerminalView((!print) ? new PrintStream(new StubOutputStream()) : System.out, input);
    BoardObserver observer = new BoardObserver();
    UserCommunicator communicator = new UserCommunicator(view);
    Player player = (Player) player1.getConstructor(BoardObserver.class).newInstance(observer);
    Player opponent = (Player) player2.getConstructor(BoardObserver.class).newInstance(observer);
    Controller controller = new LocalController(player, opponent, view, communicator, observer);
    controller.run();
    boolean playerLost = observer.getBoard(player).shipsLeft() <= 0;
    boolean opponentLost = observer.getBoard(opponent).shipsLeft() <= 0;
    if (!playerLost && opponentLost) {
      wins++;
    }
    double[] hitRates = observer.getBoard(player).hitRate();
    playerCumulativeHitRate += hitRates[0];
    opponentCumulativeHitRate += hitRates[1];
  }

  /**
   * A stub output stream.
   */
  private static class StubOutputStream extends OutputStream {
    @Override
    public void write(int b) {
      // Doesn't do anything
    }
  }

  /**
   * Generates a random board setup.
   *
   * @return string representation of setup
   */
  private static String generateRandomSetup() {
    int width = random.nextInt(6, 16);
    int height = random.nextInt(6, 16);
    int randomRange = Math.min(width, height) - 3;
    List<Integer> ships = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      int res = (int) (random.nextDouble() * randomRange);
      randomRange -= res;
      ships.add(res + 1);
    }
    Collections.shuffle(ships);
    return String.format("""
            %s %s
            %s %s %s %s
            """,
        width, height, ships.get(0), ships.get(1),
        ships.get(2), ships.get(3)
    );
  }
}