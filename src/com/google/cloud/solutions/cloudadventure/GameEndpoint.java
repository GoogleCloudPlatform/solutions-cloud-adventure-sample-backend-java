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
import com.google.cloud.solutions.cloudadventure.model.Game;
import com.google.cloud.solutions.cloudadventure.model.GameMessage;
import com.google.cloud.solutions.cloudadventure.model.Player;
import com.google.cloud.solutions.cloudadventure.model.world.Maze;
import com.google.cloud.solutions.cloudadventure.model.world.Maze.MazeType;
import com.google.cloud.solutions.cloudadventure.util.StorageUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An Endpoint class which exposes methods to manipulate primarily the Game model resource.
 */
@Api(name = "cloudadventure")
public class GameEndpoint {

  private static final Logger LOG = Logger.getLogger(GameEndpoint.class.getName());

  private ArrayList<String> getHandles(List<Player> players) {
    ArrayList<String> handles = new ArrayList<String>();
    for (Player player : players) {
      handles.add(player.getHandle());
    }
    return handles;
  }

  /**
   * Creates a new game with a map using the given MazeType.
   * 
   * @param mazeTypeName the name of the {@link MazeType} which the game will take place on
   * @return the {@link Game} created
   */
  @ApiMethod(path = "games/{mazeType}", name = "games.create", httpMethod = HttpMethod.POST)
  public Game createGame(@Named("mazeType") final String mazeTypeName) {
    MazeType mazeType = MazeType.valueOf(mazeTypeName);
    Game game = Game.create(UUID.randomUUID().toString().replaceAll("[^A-Za-z0-9]", ""));
    Maze gameMaze = new Maze(mazeType);
    game.setMaze(gameMaze);
    StorageUtils.writeGame(game);
    return game;
  }

  /**
   * Sends an invitation to a user to join a newly-created game.
   * 
   * @param msg contains the information needed for sending and processing the invite
   */
  @ApiMethod(path = "games/invite", name = "games.invite", httpMethod = HttpMethod.POST)
  public void sendGameInvite(final GameMessage msg) {
    try {
      CloudMessenger.pingGameInvite("Game invite from " + msg.getFrom() + ".", msg);
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
  }

  /**
   * Starts the given game and sends all players who are in the game a relevant notification.
   * 
   * @param gameId the ID of the game
   * @param from user handle of the {@link GameUser} who created the game
   */
  @ApiMethod(path = "games/start", name = "games.start", httpMethod = HttpMethod.POST)
  public void start(@Named("gameId") final String gameId, @Named("from") final String from) {
    Game game = StorageUtils.getGame(gameId);
    game.start();
    StorageUtils.writeGame(game);
    List<String> to = getHandles(PlayerEndpoint.getPlayersInGame(gameId));
    try {
      CloudMessenger.pingGameStarted(new GameMessage().setGameId(gameId).setFrom(from).setTo(to));
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
  }

  /**
   * Cancels the given game and sends all players who are in the game a relevant notification.
   * Cleans up the game and player resources on the server.
   * 
   * @param gameId the ID of the game
   * @param from user handle of the {@link GameUser} who created the game
   */
  @ApiMethod(path = "games/cancel", name = "games.cancel", httpMethod = HttpMethod.POST)
  public void cancelGame(@Named("gameId") final String gameId, @Named("from") final String from) {
    StorageUtils.destroyGame(gameId);
    List<Player> toPlayers = PlayerEndpoint.getPlayersInGame(gameId);
    List<String> handles = getHandles(toPlayers);
    try {
      CloudMessenger.pingGameDestroyed(
          "The creator of this game " + from + " has abandoned the game.",
          new GameMessage().setGameId(gameId).setFrom(from).setTo(handles));
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
    for (String handle : handles) {
      StorageUtils.destroyPlayer(handle);
    }
  }

  /**
   * Ends the given game and sends all players who are in the game a relevant notification.
   * 
   * @param gameId the ID of the game
   * @param from user handle of the {@link GameUser} who created the game
   */
  @ApiMethod(path = "games/end", name = "games.end", httpMethod = HttpMethod.PUT)
  public void end(@Named("gameId") final String gameId, @Named("from") final String from) {
    ArrayList<String> handles = getHandles(PlayerEndpoint.getPlayersInGame(gameId));
    try {
      CloudMessenger.pingGameEnded(
          new GameMessage().setGameId(gameId).setFrom(from).setTo(handles));
    } catch (IOException e) {
      LOG.warning(e.getMessage());
    }
  }
}
