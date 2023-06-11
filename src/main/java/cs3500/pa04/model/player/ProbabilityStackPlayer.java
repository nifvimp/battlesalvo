package cs3500.pa04.model.player;

import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.Orientation;
import cs3500.pa04.model.Ship;
import cs3500.pa04.model.ShipType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class ProbabilityStackPlayer extends LocalPlayer {
  private static final int MILLISECONDS_THREAD_INTERRUPT = 50;
  private static final int THREADS = 50;
  private double[][] probabilityDistribution;
  private Map<ShipType, Integer> specifications;
  private Map<ShipType, List<Ship>> shipLocations;
  private Map<Ship, Set<Ship>> incompatible;
  private final Set<Coord> misses;
  private final Set<Coord> hits;
  private Random random;
  private long count;
  private int runs;

  private final Stack<Coord> shotStack = new Stack<>();


  public ProbabilityStackPlayer(BoardObserver observer) {
    super(observer);
    this.hits = new HashSet<>();
    this.misses = new HashSet<>();
  }

  @Override
  public String name() {
    return "Probability Player";
  }

  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> result = super.setup(height, width, specifications);
    this.specifications = specifications;
    this.random = new Random();
    return result;
  }

  private void calculateDistribution() {
    this.probabilityDistribution = new double[height][width];
    this.shipLocations = new HashMap<>();
    this.incompatible = new HashMap<>();
    this.count = 0;
    getPossibleShipLocations();
    getIncompatibleLocations();

    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < THREADS; i++) {
      Thread thread = new Simulation();
      thread.start();
      threads.add(thread);
    }
    try {
      TimeUnit.MILLISECONDS.sleep(MILLISECONDS_THREAD_INTERRUPT);
    } catch (InterruptedException ignored) {
      // An empty catch block
    }
    for (Thread thread : threads) {
      thread.interrupt();
    }
    System.out.println("Attempts: " + runs);
    System.out.println("Sims Complete: " + count);
//    for (int i = 0; i < SIMULATION_ATTEMPTS; i++) {
//      simulateShipPlacement();
//    }
    for (double[] row : probabilityDistribution) {
      row = Arrays.stream(row).map(num -> (num / count) * 100).toArray();
      System.out.println(Arrays.stream(row)
          .mapToObj(num -> String.format("%3.2f", num))
          .toList());
    }
  }

  private void getPossibleShipLocations() {
    for (ShipType shipType : specifications.keySet()) {
      List<Ship> possible = new ArrayList<>();
      int size = shipType.getSize();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          Coord startingCoord = new Coord(j, i);
          if (i + size <= height) {
            Ship potential = new Ship(shipType, startingCoord, Orientation.VERTICAL);
            if (potential.getOccupied().stream().noneMatch(misses::contains)) {
              possible.add(potential);
            }
          }
          if (j + size <= width) {
            Ship potential = new Ship(shipType, startingCoord, Orientation.HORIZONTAL);
            if (potential.getOccupied().stream().noneMatch(misses::contains)) {
              possible.add(potential);
            }
          }
        }
      }
      shipLocations.put(shipType, possible);
    }
  }

  private void getIncompatibleLocations() {
    List<Ship> ships = new ArrayList<>();
    for (List<Ship> possible : shipLocations.values()) {
      ships.addAll(possible);
    }
    for (Ship ship : ships) {
      Set<Ship> conflicts = new HashSet<>();
      Set<Coord> occupied = ship.getOccupied();
      for (Ship other : ships) {
        if (other.getOccupied().stream().anyMatch(occupied::contains)) {
          conflicts.add(other);
        }
      }
      conflicts.remove(ship);
      incompatible.put(ship, conflicts);
    }
  }

  // TODO: extract inner class outside
  private class Simulation extends Thread {
    @Override
    public void run() {
      while(!this.isInterrupted()) {
        try {
          simulateShipPlacement();
          runs++;
        } catch (NullPointerException ignored) {
          // An empty catch block
        }
      }
    }
  }

  private void simulateShipPlacement() {
    List<Ship> placed = new ArrayList<>();
    for (ShipType shipType : specifications.keySet()) {
      for (int i = 0; i < specifications.get(shipType); i++) {
        List<Ship> possible = shipLocations.get(shipType);
        Ship toPlace = possible.get(random.nextInt(0, possible.size()));
        if (placed.contains(toPlace)) {
          return;
        }
        Set<Ship> conflict = incompatible.get(toPlace);
        if (placed.stream().anyMatch(conflict::contains)) {
          return;
        }
        placed.add(toPlace);
      }
    }
    Set<Coord> occupied = new HashSet<>();
    for (Ship ship : placed) {
      occupied.addAll(ship.getOccupied());
    }
    if (!hits.isEmpty() && !occupied.containsAll(hits)) {
      return;
    }
    for (Coord coord : occupied) {
      probabilityDistribution[coord.y()][coord.x()]++;
    }
    count++;
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord hit : shotsThatHitOpponentShips) {
      addAdjacentShots(hit);
      board.markOpponent(hit, true);
      hits.add(hit);
    }
    lastTurnShots.removeAll(shotsThatHitOpponentShips);
    for (Coord miss : lastTurnShots) {
      board.markOpponent(miss, false);
      misses.add(miss);
    }
    lastTurnShots.clear();
  }

  @Override
  public List<Coord> takeShots() {
    calculateDistribution();
    return super.takeShots();
  }

  @Override
  protected List<Coord> loadShots() {
    List<Coord> shots = new ArrayList<>();
    if (count > 0) {
      shootProbability(shots);
    } else {
      shootStack(shots);
    }
    return shots;
  }

  private void shootProbability(List<Coord> shots) {
    Set<Coord> validShots = board.validShots();
    Map<Coord, Double> possibleShots = new HashMap<>();
    PriorityQueue<Coord> queue =
        new PriorityQueue<>(Comparator.comparingDouble(possibleShots::get).reversed());
    for (Coord coord : validShots) {
      Double probability = probabilityDistribution[coord.y()][coord.x()];
      possibleShots.put(coord, probability);
      queue.add(coord);
    }
    for (int i = Math.min(board.shipsLeft(), validShots.size()); i > 0; i--) {
      shots.add(queue.poll());
    }
  }

  private void shootStack(List<Coord> shots) {
    List<Coord> validShots = new ArrayList<>(board.validShots());
    int validSum = validShots.size();
    for (int i = Math.min(board.shipsLeft(), validSum); i > 0; i--) {
      Coord shot;
      do {
        if (!shotStack.empty()) {
          shot = shotStack.pop();
        } else {
          int randShot = random.nextInt(validShots.size());
          shot = validShots.get(randShot);
        }
      } while (!validShots.contains(shot));
      validShots.remove(shot);
      shots.add(shot);
    }
  }

  /**
   * Adds any valid shot next to a coordinate to the shot stack.
   *
   * @param coord the coordinate to add around
   */
  private void addAdjacentShots(Coord coord) {
    List<Coord> coords = new ArrayList<>();
    coords.add(new Coord(coord.x() + 1, coord.y()));
    coords.add(new Coord(coord.x() - 1, coord.y()));
    coords.add(new Coord(coord.x(), coord.y() + 1));
    coords.add(new Coord(coord.x(), coord.y() - 1));
    for (Coord adjacentCoord : coords) {
      if ((adjacentCoord.x() < width && adjacentCoord.x() >= 0)
          && adjacentCoord.y() < height && adjacentCoord.y() >= 0) {
        if (board.validShots().contains(adjacentCoord)) {
          shotStack.push(adjacentCoord);
        }
      }
    }
  }
}