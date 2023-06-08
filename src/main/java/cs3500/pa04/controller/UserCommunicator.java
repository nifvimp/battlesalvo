package cs3500.pa04.controller;


import cs3500.pa04.model.Coord;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Collects input from user.
 */
public class UserCommunicator {
  private static final Coord BOARD_LIMITS = new Coord(6, 15);
  private final GameView view;

  /**
   * Creates a new UserRequester object.
   *
   * @param view input of requester
   */
  public UserCommunicator(GameView view) {
    this.view = view;
  }

  /**
   * Requests valid board dimensions from the user.
   *
   * @return valid user response
   */
  public Coord requestBoardDimensions() {
    Coord size;
    try {
      int[] input = numExtractor(2, view.getBoardDimensions(BOARD_LIMITS));
      int width = input[0];
      int height = input[1];
      if (width < BOARD_LIMITS.x() || width > BOARD_LIMITS.y()
          || height < BOARD_LIMITS.x() || height > BOARD_LIMITS.y()) {
        throw new IllegalArgumentException("Dimensions are invalid.");
      }
      size = new Coord(width, height);
    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
      view.invalidBoardDimensions(BOARD_LIMITS);
      size = requestBoardDimensions();
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
      ShipType[] shipTypes = ShipType.values();
      int[] input = numExtractor(shipTypes.length, view.getFleet(fleetSize));
      int currSize = 0;
      for (int i = 0; i < shipTypes.length; i++) {
        if (input[i] <= 0) {
          throw new IllegalArgumentException("There must be at least 1 of each ship type.");
        }
        specification.put(shipTypes[i], input[i]);
        currSize += input[i];
      }
      if (currSize > fleetSize) {
        throw new IllegalArgumentException("Current Fleet size exceeds the max fleet size.");
      }
    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
      view.invalidFleet();
      return requestFleet(fleetSize);
    }
    return specification;
  }

  /**
   * Requests shots from the client.
   *
   * @param numShots number of shots to request from the user
   * @return valid user response
   */
  public List<Coord> requestShots(int numShots) {
    Set<Coord> shots = new HashSet<>(numShots);
    view.promptShots(numShots);
    try {
      for (int i = 0; i < numShots; i++) {
        int[] input = numExtractor(2, view.getShot());
        int x = input[0];
        int y = input[1];
        if (!shots.add(new Coord(x, y))) {
          throw new IllegalArgumentException("Shot has already been entered.");
        }
      }
    } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
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

  /**
   * Extracts the specified number of integers from the given text.
   *
   * @param numsToExtract the number of integers to extract
   * @param text          the text containing the integers
   * @return an array containing the specified number of extracted integers
   * @throws IllegalArgumentException if there are more inputs than expected
   *                                  or if there is a problem extracting
   */
  private int[] numExtractor(int numsToExtract, String text) {
    text = text.trim();
    String[] split = text.split(" ");
    if (split.length > numsToExtract) {
      throw new IllegalArgumentException("more inputs than expected");
    }
    int[] nums = new int[numsToExtract];
    for (int i = 0; i < nums.length; i++) {
      int num = Integer.parseInt(split[i]);
      nums[i] = num;
    }
    return nums;
  }
}