package cs3500.pa04.model;

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

public class ProbabilityPlayer extends LocalPlayer {
  private static final int SIMULATION_ATTEMPTS = 1000000;
  private double[][] probabilityDistribution;
  private Map<ShipType, Integer> specifications;
  private Map<ShipType, List<Ship>> shipLocations;
  private Map<Ship, Set<Ship>> incompatible;
  private final Set<Coord> misses;
  private final Set<Coord> hits;
  private Random random;
  private long count;

  public ProbabilityPlayer(BoardObserver observer) {
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
    for (int i = 0; i < SIMULATION_ATTEMPTS; i++) {
      simulateShipPlacement();
    }
    for (double[] row : probabilityDistribution) {
      row = Arrays.stream(row).map(num -> (num / count) * 100).toArray();
      System.out.println(Arrays.stream(row)
          .mapToObj(num -> String.format("%3.2f", num))
          .toList());
    }
  }

  // TODO: Possible to multi-thread for each row or cell or something
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

  // TODO: can multi-thread for each ship as a thread (1 for loop instead of 2)
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
    return shots;
  }
}
