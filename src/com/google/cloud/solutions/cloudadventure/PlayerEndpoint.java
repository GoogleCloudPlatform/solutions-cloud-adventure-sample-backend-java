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

package com.google.cloud.solutions.cloudadventure;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.cloud.solutions.cloudadventure.model.Game;
import com.google.cloud.solutions.cloudadventure.model.GameMessage;
import com.google.cloud.solutions.cloudadventure.model.Player;
import com.google.cloud.solutions.cloudadventure.model.world.Maze;
import com.google.cloud.solutions.cloudadventure.model.world.Tile;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An Endpoint class which exposes methods to manipulate primarily the Player model resource.
 */
@Api(name = "cloudadventure")
public class PlayerEndpoint {

  private static final Logger LOG = Logger.getLogger(PlayerEndpoint.class.getName());

  private ArrayList<String> getHandles(List<Player> players) {
    ArrayList<String> handles = new ArrayList<String>();
    for (Player player : players) {
      handles.add(player.getHandle());
    }
    return handles;
  }

  /**
   * Creates an instance of {@link Player} using the user's handle and links the new player to the
   * game specified by the game ID.
   * 
   * @param gameId the ID of the game that this {@link Player} will be joining
   * @param handle the handle of the user for which this player is being created
   * @return a new {@link Player} instance
   */
  @ApiMethod(path = "players/join/{gameId}", name = "players.joinGame",
      httpMethod = HttpMethod.POST)
  public Player joinGame(
      @Named("gameId") final String gameId, @Named("handle") final String handle) {
    Game game = StorageUtils.getGame(gameId);
    LOG.info(game != null ? game.getId() : "No Game with ID " + gameId + " could be found.");
    if (game == null || game.isRunning()) {
      return null;
    }
    Player player = Player.create(handle, gameId);
    insertPlayer(player);
    return player;
  }

  /**
   * Sends a message to everyone else who is already in the game that this player has joined.
   * 
   * @param gameId the ID of the game that is being joined
   * @param handle the handle of the player
   * @return the list of {@link Player}s who is being pinged
   */
  @ApiMethod(path = "players/notifyJoin/{gameId}", name = "players.notifyJoin",
      httpMethod = HttpMethod.POST)
  public List<Player> sendJoinNotification(
      @Named("gameId") final String gameId, @Named("handle") final String handle) {
    List<Player> players = getPlayersInGame(gameId);
    ArrayList<String> handles = getHandles(players);
    try {
      CloudMessenger.pingGamePlayerJoin(
          new GameMessage().setFrom(handle).setGameId(gameId).setTo(handles));
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
    return players;
  }

  /**
   * Removes the player from the game.
   * 
   * @param gameId the ID of the game that the player is leaving
   * @param handle the handle of the player that is leaving the game
   */
  @ApiMethod(path = "players/leave/{gameId}", name = "players.leaveGame",
      httpMethod = HttpMethod.POST)
  public void leaveGame(
      @Named("gameId") final String gameId, @Named("handle") final String handle) {
    removePlayer(handle);
    List<String> handles = getHandles(getPlayersInGame(gameId));
    handles.remove(handle); // just in case
    if (handles.isEmpty()) {
      StorageUtils.destroyGame(gameId);
    } else {
      try {
        CloudMessenger.pingGamePlayerLeave(
            new GameMessage().setFrom(handle).setTo(handles).setGameId(gameId));
      } catch (IOException e) {
        LOG.warning(e.getMessage());
      }
    }
  }

  /**
   * Updates the associated {@link GameUser} with the game statistics, and sends those statistics to
   * the other players in the game so that they can see your score in the final score page.
   * 
   * @param handle the handle of player that the scores belong to
   * @param gameId the ID of the game that this player is in
   * @param gemsCollected the number of gems collected in the game
   * @param mobsKilled the number of mobiles killed in the game
   * @param deaths the number of deaths the player accumulated in the game
   * @return the list of {@link Player}s that were in this game
   */
  @ApiMethod(path = "players.saveAndSendScores", name = "players.saveAndSendScores",
      httpMethod = HttpMethod.POST)
  public List<Player> saveScoresAndSend(@Named("handle") final String handle,
      @Named("gameId") final String gameId, @Named("gemsCollected") long gemsCollected,
      @Named("mobsKilled") long mobsKilled, @Named("deaths") long deaths) {
    GameUserEndpoint.updateUserWithPostGameScores(handle, gemsCollected, mobsKilled);

    List<Player> players = getPlayersInGame(gameId);
    ArrayList<String> handles = new ArrayList<String>();
    for (Player player : players) {
      player.setMaze(null); // unnecessary to send this blob
      handles.add(player.getHandle());
    }
    try {
      CloudMessenger.pingGamePlayerSendEndScore(
          new GameMessage().setGameId(gameId).setFrom(handle).setTo(handles),
          gemsCollected, mobsKilled, deaths);
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
    return players;
  }

  /**
   * Checks to see if this Player exists, and if the Game of this Player exists and is running.
   * 
   * @param handle the handle of the player
   * @return {@link Player} if Player and Game exist, and Game is running
   *         {@code null} otherwise
   */
  @ApiMethod(path = "players/{handle}", name = "players.checkGame", httpMethod = HttpMethod.POST)
  public Player checkIfPlayerInGame(@Named("handle") String handle) {
    Player player = StorageUtils.getPlayer(handle);
    if (player != null) {
      Game game = StorageUtils.getGame(player.getGameId());
      if (game == null || !game.isRunning()) {
        StorageUtils.destroyPlayer(handle);
        player = null;
      }
    }
    return player;
  }

  /**
   * Gets the player.
   * 
   * @param handle the handle of the player
   * @param gameId the ID of the game that the player is in
   * @return the {@link Player}
   */
  @ApiMethod(path = "players/{handle}", name = "players.get")
  public Player getPlayer(@Named("handle") String handle) {
    return StorageUtils.getPlayer(handle);
  }

  /**
   * Gets a list of players in a certain game.
   * 
   * @param gameId the ID of the game
   * @return a list of {@link Player}s in the game
   */
  @SuppressWarnings("unchecked")
  @ApiMethod(path = "players/game/{gameId}", name = "players.getFromGame")
  public static List<Player> getPlayersInGame(@Named("gameId") String gameId) {
    Filter userHandleFilter = new Query.FilterPredicate("game_id", FilterOperator.EQUAL, gameId);
    Query q = new Query("Player").setFilter(userHandleFilter);
    List<Entity> entities =
        StorageUtils.getDatastore().prepare(q).asList(FetchOptions.Builder.withDefaults());

    ArrayList<Player> handles = new ArrayList<Player>();
    for (Entity entity : entities) {
      Player player = new Player();
      player.setHandle((String) entity.getProperty("handle"));
      player.setGameId((String) entity.getProperty("game_id"));
      player.setMaze(StorageUtils.getUnindexedBlobValue(entity, "maze", Maze.class));
      player.setCurrentTile(StorageUtils.getUnindexedBlobValue(entity, "current_tile", Tile.class));
      player.setOrientation(Maze.Cardinal.valueOf((String) entity.getProperty("orientation")));
      player.setCurrentHP((Long) entity.getProperty("current_hp"));
      player.setMaxHP((Long) entity.getProperty("max_hp"));
      player.setGemsCollected((Long) entity.getProperty("gems_collected"));
      player.setMobsKilled((Long) entity.getProperty("mobs_killed"));
      player.setNumDeaths((Long) entity.getProperty("num_deaths"));
      player.setPickups(StorageUtils.getUnindexedBlobValue(entity, "pickups", ArrayList.class));
      player
          .setBaseItems(StorageUtils.getUnindexedBlobValue(entity, "base_items", ArrayList.class));
      handles.add(player);
    }

    return handles;
  }

  /**
   * Insert player.
   * 
   * @param player the {@link Player} to be inserted
   */
  @ApiMethod(path = "players", name = "players.insert")
  public void insertPlayer(Player player) {
    StorageUtils.writePlayer(player);
  }

  /**
   * Update the player of the same handle.
   * 
   * @param player the {@link Player} to be updated
   */
  @ApiMethod(path = "players", name = "players.update")
  public void updatePlayer(Player player) {
    StorageUtils.writePlayer(player);
  }

  /**
   * Remove the player.
   * 
   * @param handle the handle of the player to be removed
   * @param gameId the ID of the game that this player was in
   */
  @ApiMethod(path = "players/{handle}", name = "players.remove")
  public void removePlayer(@Named("handle") String handle) {
    String gameId = StorageUtils.getPlayer(handle).getGameId();
    StorageUtils.destroyPlayer(handle);
    List<String> handles = getHandles(getPlayersInGame(gameId));
    handles.remove(handle); // just in case
    if (handles.isEmpty()) {
      StorageUtils.destroyGame(gameId);
    }
  }
}
