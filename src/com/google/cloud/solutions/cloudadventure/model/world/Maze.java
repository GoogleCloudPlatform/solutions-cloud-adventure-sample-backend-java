/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.google.cloud.solutions.cloudadventure.model.world;

import static com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal.EAST;
import static com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal.NORTH;
import static com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal.SOUTH;
import static com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal.WEST;

import com.google.cloud.solutions.cloudadventure.model.world.Tile.Coordinates;
import com.google.cloud.solutions.cloudadventure.util.WorldGenerator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class represents the player map, in the form of a maze. It keeps track of the player world,
 * including the locations of the various object in it.
 */
public class Maze implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The type of this maze.
   */
  private MazeType type;

  /**
   * The grid of {@link Tile}s that compose the body of the maze.
   * <p>
   * grid[0][0] represents the bottom left-hand corner.
   */
  private Tile[][] grid;

  /**
   * The {@link Coordinates} of any remaining gems on the maze.
   */
  private ArrayList<Coordinates> gemsRemaining;

  /**
   * The {@link Coordinates} of for this maze.
   */
  private Coordinates startingCoordinates;

  /**
   * This class enumerates the types of cardinal directions available.
   * <p>
   * NOTE: Updates to this enum need to be reflected in the corresponding enum class in the client.
   *
   */
  public enum Cardinal {
    NORTH, EAST, SOUTH, WEST
  }

  /**
   * This class enumerates the types of mazes available.
   * <p>
   * NOTE: Updates to this enum need to be reflected in the corresponding enum class in the client.
   *
   */
  public enum MazeType {
    TEST(15), RANDOM(20), LABYRINTH(25);

    /**
     * The maximum hit points that a player may have at any given time on this map.
     */
    final int maxHP;

    MazeType(int maxHP) {
      this.maxHP = maxHP;
    }

    public int getMaxHP() {
      return this.maxHP;
    }
  }

  public Maze() {}

  /**
   * Creates a {@link Maze} instance based on the specified {@link MazeType}.
   *
   * @param mazeType
   * @return a new Maze
   */
  public Maze(final MazeType mazeType) {
    switch (mazeType) {
      case TEST:
        generateTestMaze();
        break;
      case RANDOM:
        generateRandomMaze();
        break;
      case LABYRINTH:
        generateLabyrinth();
        break;
      default:
        generateTestMaze();
    }
  }

  /**
   * This map is consists of one tile with one gem on it. This is for developers to test with.
   */
  private void generateTestMaze() {
    this.type = MazeType.TEST;
    this.gemsRemaining = new ArrayList<Coordinates>();
    this.startingCoordinates = new Coordinates(0, 0);

    Set<Creature> creatures = new HashSet<Creature>();
    creatures.add(new CreatureTypes.Newt());
    Set<Pickup> potentialPickups = new HashSet<Pickup>();
    potentialPickups.add(new PickupTypes.Gem());

    grid = new Tile[1][1];
    Tile[] col = new Tile[1];
    col[0] = new Tile(0, 0, null, creatures, potentialPickups, NORTH, EAST, SOUTH, WEST);
    recordGemPresence(col[0]);
    grid[0] = col;
  }

  /**
   * This map is an open grid of Tiles, 5x5. There is a random assortment of items on each Tile. The
   * result is different each time this method is called.
   */
  private void generateRandomMaze() {
    this.type = MazeType.RANDOM;
    this.gemsRemaining = new ArrayList<Coordinates>();

    generateOpenGrid(5, 5);
  }

  /**
   */
  private void generateLabyrinth() {
    this.type = MazeType.LABYRINTH;
    this.gemsRemaining = new ArrayList<Coordinates>();

    generateOpenGrid(5, 5);
  }

  private void recordGemPresence(Tile tile) {
    for (Pickup pickup : tile.getPickups()) {
      if (new PickupTypes.Gem().getName().equalsIgnoreCase(pickup.getName())) {
        this.gemsRemaining.add(tile.getCoord());
      }
      return;
    }
  }

  private Set<Creature> getRandomCreatures(Map<Creature, Integer> potentialCreatures) {
    Set<Creature> creatures = new HashSet<Creature>();
    for (Creature creature : potentialCreatures.keySet()) {
      if (potentialCreatures.get(creature) > WorldGenerator.getRandom(100)) {
        creatures.add(creature);
      }
    }
    return creatures;  // empty set is OK
  }

  private Set<Pickup> getRandomPickups(Map<Pickup, Integer> potentialPickups) {
    Set<Pickup> pickups = new HashSet<Pickup>();
    for (Pickup pickup : potentialPickups.keySet()) {
      if (potentialPickups.get(pickup) > WorldGenerator.getRandom(100)) {
        pickups.add(pickup);
      }
    }
    return pickups;  // empty set is OK
  }

  /**
   * This generates an open grid (the walls are only around the perimeter of the rectangle). There
   * is a random set of items on each Tile.
   *
   * @param mapWidth
   * @param mapHeight
   */
  private void generateOpenGrid(int mapWidth, int mapHeight) {
    grid = new Tile[mapWidth][mapHeight];

    Coordinates coord = new Coordinates();
    coord.setX(WorldGenerator.getRandom(mapWidth));
    coord.setY(WorldGenerator.getRandom(mapHeight));
    this.startingCoordinates = coord;

    Map<Creature, Integer> potentialCreatures = new HashMap<Creature, Integer>();
    potentialCreatures.put(new CreatureTypes.Dragon(), 10);
    potentialCreatures.put(new CreatureTypes.Spider(), 10);
    potentialCreatures.put(new CreatureTypes.Sprite(), 10);
    potentialCreatures.put(new CreatureTypes.Newt(), 10);
    potentialCreatures.put(new CreatureTypes.Hippogriff(), 10);
    potentialCreatures.put(new CreatureTypes.Troll(), 10);
    potentialCreatures.put(new CreatureTypes.Faerie(), 10);

    Map<Pickup, Integer> potentialPickups = new HashMap<Pickup, Integer>();
    potentialPickups.put(new PickupTypes.Gem(), 10);
    potentialPickups.put(new PickupTypes.Broadsword(), 10);
    potentialPickups.put(new PickupTypes.Scimitar(), 10);
    potentialPickups.put(new PickupTypes.GreaterPot(), 20);
    potentialPickups.put(new PickupTypes.LesserPot(), 30);
    potentialPickups.put(new PickupTypes.MagicWand(), 10);
    potentialPickups.put(new PickupTypes.Battleaxe(), 10);

    // leftmost column
    Set<Pickup> firstPickups = new HashSet<Pickup>();
    firstPickups.add(new PickupTypes.Gem()); // ensure at least one gem on map
    Tile[] col_0 = new Tile[mapHeight];
    col_0[0] = new Tile(0, 0, null, getRandomCreatures(potentialCreatures), firstPickups, NORTH,
        EAST);
    recordGemPresence(col_0[0]);
    col_0[mapHeight - 1] = new Tile(0, mapHeight - 1, null, getRandomCreatures(potentialCreatures),
        getRandomPickups(potentialPickups), EAST, SOUTH);
    recordGemPresence(col_0[mapHeight - 1]);
    for (int y = 1; y < mapHeight - 1; y++) {
      col_0[y] = new Tile(0, y, null, getRandomCreatures(potentialCreatures),
          getRandomPickups(potentialPickups), NORTH, EAST, SOUTH);
      recordGemPresence(col_0[y]);
    }
    grid[0] = col_0;
    // middle columns
    for (int x = 1; x < mapWidth - 1; x++) {
      Tile[] col = new Tile[mapHeight];
      col[0] = new Tile(x, 0, null, getRandomCreatures(potentialCreatures),
          getRandomPickups(potentialPickups), NORTH, EAST, WEST);
      recordGemPresence(col[0]);
      col[mapHeight - 1] = new Tile(x, mapHeight - 1, null, getRandomCreatures(potentialCreatures),
          getRandomPickups(potentialPickups), EAST, SOUTH, WEST);
      recordGemPresence(col[mapHeight - 1]);
      for (int y = 1; y < mapHeight - 1; y++) {
        col[y] = new Tile(x, y, null, getRandomCreatures(potentialCreatures),
            getRandomPickups(potentialPickups), NORTH, EAST, SOUTH, WEST);
        recordGemPresence(col[y]);
      }
      grid[x] = col;
    }
    // rightmost column
    Tile[] col_x = new Tile[mapHeight];
    col_x[0] = new Tile(mapWidth - 1, 0, null, getRandomCreatures(potentialCreatures),
        getRandomPickups(potentialPickups), NORTH, WEST);
    recordGemPresence(col_x[0]);
    col_x[mapHeight - 1] = new Tile(mapWidth - 1, mapHeight - 1, null,
        getRandomCreatures(potentialCreatures), getRandomPickups(potentialPickups), SOUTH, WEST);
    recordGemPresence(col_x[mapHeight - 1]);
    for (int y = 1; y < mapHeight - 1; y++) {
      col_x[y] = new Tile(mapWidth - 1, y, null, getRandomCreatures(potentialCreatures),
          getRandomPickups(potentialPickups), NORTH, SOUTH, WEST);
      recordGemPresence(col_x[y]);
    }
    grid[mapWidth - 1] = col_x;
  }

  public Tile[][] getGrid() {
    return grid;
  }

  public void setGrid(Tile[][] grid) {
    this.grid = grid;
  }

  public MazeType getType() {
    return type;
  }

  public void setType(MazeType type) {
    this.type = type;
  }

  public ArrayList<Coordinates> getGemsRemaining() {
    return gemsRemaining;
  }

  public void setGemsRemaining(ArrayList<Coordinates> gemsRemaining) {
    this.gemsRemaining = gemsRemaining;
  }

  public Coordinates getStartingCoordinates() {
    return startingCoordinates;
  }

  public void setStartingCoordinates(Coordinates coordinates) {
    this.startingCoordinates = coordinates;
  }
}
