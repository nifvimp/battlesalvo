package cs3500.pa04.controller;

import cs3500.pa03.model.Coord;
import cs3500.pa03.model.ShipType;
import cs3500.pa03.view.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Requests input from user.
 */
public class UserRequester {
  private static final Coord BOARD_LIMITS = new Coord(6, 15);
  private final GameView view;

  /**
   * Creates a new UserRequester object.
   *
   * @param view input of requester
   */
  public UserRequester(GameView view) {
    this.view = view;
  }

  /**
   * Requests valid board dimensions from the user.
   *
   * @return valid user response
   */
  public Coord requestBoardSize() {
    Coord size;
    try {
      String[] input = view.getBoardSize().split(" ");
      if (input.length != 2) {
        throw new IllegalArgumentException("Incorrect number of inputs.");
      }
      int width = Integer.parseInt(input[0]);
      int height = Integer.parseInt(input[1]);
      if (width < BOARD_LIMITS.x() || width > BOARD_LIMITS.y() || height < BOARD_LIMITS.x()
          || height > BOARD_LIMITS.y()) {
        throw new IllegalArgumentException("Dimensions are invalid.");
      }
      size = new Coord(width, height);
    } catch (IllegalArgumentException e) {
      view.invalidBoardSize(BOARD_LIMITS);
      size = requestBoardSize();
    }
    return size;
  }

  /**
   * Requests valid fleet specifications from the user.
   *
   * @param fleetSize the maximum number of ships the requested fleet can have
   * @return valid user response
   */
  public Map<ShipType, Integer> requestFleet(int fleetSize) {
    Map<ShipType, Integer> specification = new HashMap<>();
    try {
      String[] input = view.getFleet().split(" ");
      ShipType[] shipTypes = ShipType.values();
      if (input.length != shipTypes.length) {
        throw new IllegalArgumentException();
      }
      int currSize = 0;
      for (int i = 0; i < shipTypes.length; i++) {
        int numShips = Integer.parseInt(input[i]);
        if (numShips <= 0) {
          throw new IllegalArgumentException();
        }
        currSize += numShips;
        specification.put(shipTypes[i], numShips);
      }
      if (currSize > fleetSize) {
        throw new IllegalArgumentException();
      }
    } catch (IllegalArgumentException e) {
      view.invalidFleet(fleetSize);
      specification = requestFleet(fleetSize);
    }
    return specification;
  }

  /**
   * Requests board dimensions from the user.
   *
   * @param numShots number of shots to request from the user
   * @return valid user response
   */
  public List<Coord> requestShots(int numShots) {
    Set<Coord> shots = new HashSet<>(numShots);
    List<String> input = view.getShots(numShots);
    try {
      for (String str : input) {
        String[] toParse = str.trim().split(" ");
        if (toParse.length != 2) {
          throw new IllegalArgumentException("Input was formatted incorrectly");
        }
        int width = Integer.parseInt(toParse[0]);
        int height = Integer.parseInt(toParse[1]);
        if (!shots.add(new Coord(width, height))) {
          throw new IllegalArgumentException("Shot has already been entered.");
        }
      }
    } catch (IllegalArgumentException e) {
      view.invalidShots();
      return requestShots(numShots);
    }
    return new ArrayList<>(shots);
  }

  /**
   * Signals that some of the shots the user inputted were invalid.
   */
  public void signalInvalidShots() {
    view.invalidShots();
  }
}