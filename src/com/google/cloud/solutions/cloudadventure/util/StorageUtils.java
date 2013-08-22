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

package com.google.cloud.solutions.cloudadventure.util;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.google.cloud.solutions.cloudadventure.model.Game;
import com.google.cloud.solutions.cloudadventure.model.Player;
import com.google.cloud.solutions.cloudadventure.model.world.Maze;
import com.google.cloud.solutions.cloudadventure.model.world.Tile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class which takes care of some of the getting and storing resources in Datastore and
 * Memcache. Direct interactions to Datastore are implemented in the resources' respective Endpoint
 * classes, but those that are more involved, for example with Memcache, are implemented here.
 * <p>
 * For example, most {@link GameUser} storage is handled by its endpoint class. {@link Player} is
 * more complicated, using both Memcache and Datastore, and so is grouped and implemented here.
 */
public class StorageUtils {

  private static final Logger LOG = Logger.getLogger(StorageUtils.class.getName());

  private static MemcacheService memecache = initMemcache();
  private static DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

  /*
   * Persistence methods.
   */

  /**
   * @return {@code null} if a {@link Game} with gameId cannot be found in either
   *         Datastore or Memcache
   */
  public static Game getGame(final String gameId) {
    Game game = getGameFromMemcache(gameId);
    if (game == null) {
      game = getGameFromDatastore(gameId);
    }
    return game;
  }

  /**
   * Updates an existing game on the server. This is not threadsafe, so use only when certain of
   * having no other concurrent updates. For thread safety, use getIdentifiable and putIfUntouched
   * from {@link MemcacheService} as well as the other utility methods in this class for getting the
   * Datastore and Memcache keys.
   * 
   * @param game the game to write to storage
   */
  public static void writeGame(final Game game) {
    writeGameToMemcache(game);
    writeGameToDatastore(game);
  }

  public static void destroyGame(final String gameId) {
    removeGameFromMemcache(gameId);
    removeGameFromDatastore(gameId);
  }

  /**
   * @return {@code null} if a {@link Player} with handle cannot be found in either
   *         Datastore or Memcache
   */
  public static Player getPlayer(final String handle) {
    Player player = getPlayerFromMemcache(handle);
    if (player == null) {
      player = getPlayerFromDatastore(handle);
    }
    return player;
  }

  public static void writePlayer(final Player player) {
    writePlayerToMemcache(player);
    writePlayerToDatastore(player);
  }

  public static void destroyPlayer(final String handle) {
    removePlayerFromMemcache(handle);
    removePlayerFromDatstore(handle);
  }

  /*
   * Memcache persistence methods.
   */

  private static MemcacheService initMemcache() {
    MemcacheService m = MemcacheServiceFactory.getMemcacheService();
    m.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
    return m;
  }

  private static MemcacheService getSynchronousMemcache() {
    return memecache;
  }

  private static String getGameMemcacheKeyString(final String gameId) {
    return gameId;
  }

  private static String getPlayerMemcacheKeyString(final String handle) {
    return handle;
  }

  /**
   * @return {@code null} if a {@link Game} with gameId cannot be found in Memcache
   */
  private static Game getGameFromMemcache(final String gameId) {
    String key = getGameMemcacheKeyString(gameId);
    return (Game) getSynchronousMemcache().get(key);
  }

  private static void writeGameToMemcache(final Game game) {
    String key = getGameMemcacheKeyString(game.getId());
    getSynchronousMemcache().put(key, game);
  }

  private static void removeGameFromMemcache(final String gameId) {
    String key = getGameMemcacheKeyString(gameId);
    getSynchronousMemcache().delete(key);
  }

  /**
   * @return {@code null} if a {@link Player} with handle cannot be found in Memcache
   */
  private static Player getPlayerFromMemcache(final String handle) {
    String key = getPlayerMemcacheKeyString(handle);
    return (Player) getSynchronousMemcache().get(key);
  }

  private static void writePlayerToMemcache(final Player player) {
    String key = getPlayerMemcacheKeyString(player.getHandle());
    getSynchronousMemcache().put(key, player);
  }

  private static void removePlayerFromMemcache(final String handle) {
    String key = getPlayerMemcacheKeyString(handle);
    getSynchronousMemcache().delete(key);
  }

  /*
   * Datastore persistence methods.
   */

  public static DatastoreService getDatastore() {
    return datastore;
  }

  public static Key getUserDatastoreKey(final String userAccount) {
    return KeyFactory.createKey("User", userAccount);
  }

  public static Key getHandleDatastoreKey(final String handle) {
    return KeyFactory.createKey("Handle", handle);
  }

  public static Key getDeviceDatastoreKey(final String userHandle) {
    return KeyFactory.createKey("DeviceInfo", userHandle);
  }

  private static Key getGameDatastoreKey(final String gameId) {
    return KeyFactory.createKey("Game", gameId);
  }

  private static Key getPlayerDatastoreKey(final String handle) {
    return KeyFactory.createKey("Player", handle);
  }

  public static <T> byte[] serialize(T object) throws IOException {
    ByteArrayOutputStream b = new ByteArrayOutputStream();
    ObjectOutput o = null;
    try {
      o = new ObjectOutputStream(b);
      o.writeObject(object);
      return b.toByteArray();
    } finally {
      o.close();
      b.close();
    }
  }

  @SuppressWarnings("unchecked")
  public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException,
      ClassNotFoundException {
    ByteArrayInputStream b = new ByteArrayInputStream(bytes);
    ObjectInput o = new ObjectInputStream(b);
    return (T) o.readObject();
  }

  public static <T> T getUnindexedBlobValue(Entity entity, String propertyName, Class<T> clazz) {
    T value = null;
    try {
      Blob blob = (Blob) entity.getProperty(propertyName);
      value = deserialize(blob.getBytes(), clazz);
    } catch (IOException e) {
      LOG.warning("Unable to deserialize value in " + propertyName + " from bytes."
          + e.getMessage());
    } catch (ClassNotFoundException e) {
      LOG.warning(e.getMessage());
    }
    return value;
  }

  public static <T> void setUnindexedBlobProperty(Entity entity, String propertyName, T value) {
    try {
      entity.setUnindexedProperty(propertyName, new Blob(serialize(value)));
    } catch (IOException e) {
      LOG.warning("Unable to serialize value in " + propertyName + " to bytes." + e.getMessage());
    } catch (IllegalArgumentException e) {
      LOG.warning(e.getMessage());
    }
  }

  /**
   * @return {@code null} if a {@link Game} with gameId cannot be found in the Datastore
   */
  private static Game getGameFromDatastore(final String gameId) {
    Key key = getGameDatastoreKey(gameId);
    Entity entity;
    try {
      entity = getDatastore().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    Game game = new Game();
    game.setId(key.getName());
    game.setMaze(getUnindexedBlobValue(entity, "original_maze", Maze.class));
    game.setRunning((Boolean) entity.getProperty("is_running"));
    return game;
  }

  private static Game writeGameToDatastore(Game game) {
    Key key = getGameDatastoreKey(game.getId());
    Entity entity = new Entity(key);
    setUnindexedBlobProperty(entity, "original_maze", game.getMaze());
    entity.setProperty("is_running", game.isRunning());
    getDatastore().put(entity);
    return game;
  }

  private static void removeGameFromDatastore(final String gameId) {
    Key key = getGameDatastoreKey(gameId);
    getDatastore().delete(key);
  }

  /**
   * @return {@code null} if a {@link Player} with handle cannot be found in the Datastore
   */
  @SuppressWarnings("unchecked")
  private static Player getPlayerFromDatastore(final String handle) {
    Key key = getPlayerDatastoreKey(handle);
    Entity entity;
    try {
      entity = getDatastore().get(key);
    } catch (EntityNotFoundException e) {
      return null;
    }
    Player player = new Player();
    player.setHandle((String) entity.getProperty("handle"));
    player.setGameId((String) entity.getProperty("game_id"));
    player.setMaze(getUnindexedBlobValue(entity, "maze", Maze.class));
    player.setCurrentTile(getUnindexedBlobValue(entity, "current_tile", Tile.class));
    player.setOrientation(Maze.Cardinal.valueOf((String) entity.getProperty("orientation")));
    player.setCurrentHP((Long) entity.getProperty("current_hp"));
    player.setMaxHP((Long) entity.getProperty("max_hp"));
    player.setGemsCollected((Long) entity.getProperty("gems_collected"));
    player.setMobsKilled((Long) entity.getProperty("mobs_killed"));
    player.setNumDeaths((Long) entity.getProperty("num_deaths"));
    player.setPickups(getUnindexedBlobValue(entity, "pickups", ArrayList.class));
    player.setBaseItems(getUnindexedBlobValue(entity, "base_items", ArrayList.class));
    return player;
  }

  private static void writePlayerToDatastore(final Player player) {
    Key key = getPlayerDatastoreKey(player.getHandle());
    Entity entity = new Entity(key);
    entity.setProperty("handle", player.getHandle());
    entity.setProperty("game_id", player.getGameId());
    setUnindexedBlobProperty(entity, "maze", player.getMaze());
    setUnindexedBlobProperty(entity, "current_tile", player.getCurrentTile());
    entity.setUnindexedProperty("orientation", player.getOrientation().toString());
    entity.setUnindexedProperty("current_hp", player.getCurrentHP());
    entity.setUnindexedProperty("max_hp", player.getMaxHP());
    entity.setUnindexedProperty("gems_collected", player.getGemsCollected());
    entity.setUnindexedProperty("mobs_killed", player.getMobsKilled());
    entity.setUnindexedProperty("num_deaths", player.getNumDeaths());
    setUnindexedBlobProperty(entity, "pickups", player.getPickups());
    setUnindexedBlobProperty(entity, "base_items", player.getBaseItems());
    getDatastore().put(entity);
  }

  private static void removePlayerFromDatstore(final String handle) {
    Key key = getPlayerDatastoreKey(handle);
    getDatastore().delete(key);
  }
}
