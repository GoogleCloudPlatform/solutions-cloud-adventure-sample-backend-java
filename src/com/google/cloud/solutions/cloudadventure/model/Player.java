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

package com.google.cloud.solutions.cloudadventure.model;

import com.google.cloud.solutions.cloudadventure.model.world.Maze;
import com.google.cloud.solutions.cloudadventure.model.world.Maze.Cardinal;
import com.google.cloud.solutions.cloudadventure.model.world.Pickup;
import com.google.cloud.solutions.cloudadventure.model.world.PickupTypes;
import com.google.cloud.solutions.cloudadventure.model.world.Tile;
import com.google.cloud.solutions.cloudadventure.model.world.Tile.Coordinates;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class represents the Player object in the model, which keeps track of player-specific
 * statistics.
 */
public class Player implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * The unique handle of this Player instance.
   * <p>
   * Derived from the {@link GameUser} handle.
   */
  private String handle;

  /**
   * The ID of the game which this Player is part of.
   */
  private String gameId;

  /**
   * The personal {@link Maze} for this Player.
   */
  private Maze maze;

  /**
   * The {@link Tile} of the map which this player is occupying.
   */
  private Tile currentTile;

  /**
   * The cardinal direction the player is currently facing.
   */
  private Cardinal orientation;

  /**
   * The current hit points (health) of this player.
   */
  private long currentHP;

  /**
   * The maximum possible hit points (health) of this player.
   */
  private long maxHP;

  /**
   * The number of gems this player currently holds in the game.
   */
  private long gemsCollected;

  /**
   * The number of mobs this player has defeated in battle in the game.
   */
  private long mobsKilled;

  /**
   * The number of times this player has died in the game.
   */
  private long numDeaths;

  /**
   * A list of things the player is carrying. Available types are specified in {@link Pickup}.
   */
  private List<Pickup> pickups;

  /**
   * A list of things the player will always spawn with. Available types are specified in
   * {@link Pickup}.
   */
  private List<Pickup> baseItems;

  /**
   * Creates a new {@link Player}. Static factory method.
   * 
   * @param handle the userhandle of the user for whom this player is being created
   * @param gameId the ID of the game this player will join
   * @return the new instance of {@link Player}
   */
  public static Player create(final String handle, final String gameId) {
    Maze maze = StorageUtils.getGame(gameId).getMaze();
    Player player = new Player();
    player.setHandle(handle);
    player.setGameId(gameId);
    player.setMaze(maze);
    player.setCurrentHP(maze.getType().getMaxHP());
    player.setMaxHP(maze.getType().getMaxHP());

    Coordinates startCoordinates = maze.getStartingCoordinates();
    Tile startTile = maze.getGrid()[startCoordinates.x][startCoordinates.y];
    List<Pickup> pickups = new ArrayList<Pickup>();
    pickups.add(new PickupTypes.Broadsword());
    pickups.add(new PickupTypes.LesserPot());
    pickups.add(new PickupTypes.GreaterPot());

    player.setCurrentTile(startTile);
    Iterator<Cardinal> iter = startTile.getOpenTo().iterator();
    if (iter.hasNext()) {
      player.setOrientation(startTile.getOpenTo().iterator().next());
    } else {
      player.setOrientation(Cardinal.NORTH);
    }
    player.setPickups(pickups);
    player.setBaseItems(new ArrayList<Pickup>(pickups));
    return player;
  }

  public String getHandle() {
    return handle;
  }

  public void setHandle(String handle) {
    this.handle = handle;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(final String gameId) {
    this.gameId = gameId;
  }

  public Maze getMaze() {
    return maze;
  }

  public void setMaze(final Maze maze) {
    this.maze = maze;
  }

  public Tile getCurrentTile() {
    return currentTile;
  }

  public void setCurrentTile(Tile currentTile) {
    this.currentTile = currentTile;
  }

  public Cardinal getOrientation() {
    return orientation;
  }

  public void setOrientation(Cardinal orientation) {
    this.orientation = orientation;
  }

  public long getCurrentHP() {
    return currentHP;
  }

  public void setCurrentHP(long currentHP) {
    this.currentHP = currentHP;
  }

  public long getMaxHP() {
    return maxHP;
  }

  public void setMaxHP(long maxHP) {
    this.maxHP = maxHP;
  }

  public long getGemsCollected() {
    return gemsCollected;
  }

  public void setGemsCollected(long gemsCollected) {
    this.gemsCollected = gemsCollected;
  }

  public long getMobsKilled() {
    return mobsKilled;
  }

  public void setMobsKilled(long mobsKilled) {
    this.mobsKilled = mobsKilled;
  }

  public long getNumDeaths() {
    return numDeaths;
  }

  public void setNumDeaths(long numDeaths) {
    this.numDeaths = numDeaths;
  }

  public List<Pickup> getPickups() {
    return pickups;
  }

  public void setPickups(List<Pickup> pickups) {
    this.pickups = pickups;
  }

  public List<Pickup> getBaseItems() {
    return baseItems;
  }

  public void setBaseItems(List<Pickup> baseItems) {
    this.baseItems = baseItems;
  }
}
